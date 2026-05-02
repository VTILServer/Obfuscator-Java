param(
    [string]$Runtime = "lua",
    [string]$Jar = "target\XellObfuscator-1.0-SNAPSHOT-jar-with-dependencies.jar",
    [string]$CasesDir = "benchmarks\lua",
    [string]$OutDir = "benchmarks\results",
    [int]$Iterations = 1,
    [switch]$SkipObfuscate
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path -LiteralPath $OutDir)) {
    New-Item -ItemType Directory -Path $OutDir | Out-Null
}

if (-not (Get-Command $Runtime -ErrorAction SilentlyContinue)) {
    throw "Runtime '$Runtime' was not found on PATH."
}

if (-not $SkipObfuscate -and -not (Test-Path -LiteralPath $Jar)) {
    throw "Jar not found at '$Jar'. Run mvn package first."
}

$resultsPath = Join-Path $OutDir "last.csv"
$statsPath = Join-Path $OutDir "last-vm-stats.txt"
"case,mode,iteration,seconds,checksum,exit_code" | Set-Content -Path $resultsPath -Encoding ASCII

$cases = Get-ChildItem -Path $CasesDir -Filter "*.lua" |
    Where-Object { $_.Name -notin @("vm_stats.lua") } |
    Sort-Object Name

function Invoke-BenchCase {
    param(
        [string]$CaseName,
        [string]$Mode,
        [string]$File,
        [int]$Iteration
    )

    $output = & $Runtime $File 2>&1
    $exit = $LASTEXITCODE
    $line = $output | Where-Object { $_ -match "^BENCH_RESULT\s+" } | Select-Object -Last 1
    if (-not $line) {
        $safeOutput = ($output -join " ").Replace('"', '""')
        Add-Content -Path $resultsPath -Encoding ASCII -Value "$CaseName,$Mode,$Iteration,ERROR,""$safeOutput"",$exit"
        return
    }

    $parts = $line -split "\s+"
    Add-Content -Path $resultsPath -Encoding ASCII -Value "$CaseName,$Mode,$Iteration,$($parts[2]),$($parts[3]),$exit"
}

foreach ($case in $cases) {
    for ($i = 1; $i -le $Iterations; $i++) {
        Invoke-BenchCase -CaseName $case.BaseName -Mode "plain" -File $case.FullName -Iteration $i
    }

    if (-not $SkipObfuscate) {
        $obfuscated = Join-Path $OutDir ($case.BaseName + ".obf.lua")
        & java -jar $Jar --once $case.FullName $obfuscated | Out-Null
        if ($LASTEXITCODE -ne 0) {
            throw "Obfuscation failed for $($case.Name)."
        }

        for ($i = 1; $i -le $Iterations; $i++) {
            Invoke-BenchCase -CaseName $case.BaseName -Mode "obfuscated" -File $obfuscated -Iteration $i
        }
    }
}

if (Test-Path -LiteralPath (Join-Path $CasesDir "vm_stats.lua")) {
    Remove-Item -LiteralPath $statsPath -ErrorAction SilentlyContinue
    $statsTargets = Get-ChildItem -Path $OutDir -Filter "*.obf.lua" -ErrorAction SilentlyContinue | Sort-Object Name
    if (-not $statsTargets -and (Test-Path -LiteralPath "Xell.out.lua")) {
        $statsTargets = @(Get-Item -LiteralPath "Xell.out.lua")
    }
    foreach ($target in $statsTargets) {
        & $Runtime (Join-Path $CasesDir "vm_stats.lua") $target.FullName | Add-Content -Path $statsPath -Encoding ASCII
        Add-Content -Path $statsPath -Encoding ASCII -Value ""
    }
}

Write-Output "Benchmark results: $resultsPath"
Write-Output "VM stats: $statsPath"
