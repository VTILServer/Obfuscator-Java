param(
    [string]$Jar = "target\XellObfuscator-1.0-SNAPSHOT-jar-with-dependencies.jar",
    [string]$Dest = "target\java-installer-app",
    [string]$InputDir = "target\java-installer-input",
    [string]$PayloadDest = "target\java-installer-payload",
    [string]$PayloadInputDir = "target\java-installer-payload-input",
    [string]$Name = "XellObfuscatorJavaInstaller",
    [string]$PayloadName = "XellObfuscator"
)

$ErrorActionPreference = "Stop"

if (-not (Get-Command jpackage -ErrorAction SilentlyContinue)) {
    throw "jpackage was not found. Use JDK 14+; this workspace currently uses JDK 21."
}

if (-not (Test-Path -LiteralPath $Jar)) {
    throw "Missing jar: $Jar. Run mvn package first."
}

foreach ($path in @($Dest, $InputDir, $PayloadDest, $PayloadInputDir)) {
    if (Test-Path -LiteralPath $path) {
        throw "Folder already exists: $path. Choose another path or remove it yourself."
    }
}

$desktopScript = Join-Path $PSScriptRoot "package-desktop.ps1"
& $desktopScript -Jar $Jar -Dest $PayloadDest -InputDir $PayloadInputDir -Name $PayloadName
if ($LASTEXITCODE -ne 0) {
    throw "Desktop payload build failed with exit code $LASTEXITCODE."
}

New-Item -ItemType Directory -Force -Path $InputDir | Out-Null
Copy-Item -LiteralPath $Jar -Destination $InputDir

$payloadSource = Join-Path $PayloadDest $PayloadName
$payloadTarget = Join-Path $InputDir "payload"
New-Item -ItemType Directory -Force -Path $payloadTarget | Out-Null
Copy-Item -LiteralPath $payloadSource -Destination $payloadTarget -Recurse

jpackage `
    --type app-image `
    --dest $Dest `
    --input $InputDir `
    --name $Name `
    --app-version "1.0.0" `
    --vendor "Xell" `
    --description "Java based Xell Obfuscator installer" `
    --main-jar (Split-Path -Leaf $Jar) `
    --main-class "icu.Xell.Mainline.obfuscator.XellJavaInstallerApp"

if ($LASTEXITCODE -ne 0) {
    throw "jpackage failed with exit code $LASTEXITCODE."
}

Write-Host "Java installer app created at $(Join-Path $Dest $Name)"
