param(
    [string]$Jar = "target\XellObfuscator-1.0-SNAPSHOT-jar-with-dependencies.jar",
    [string]$Dest = "target\desktop-app",
    [string]$InputDir = "target\desktop-input",
    [string]$Name = "XellObfuscator",
    [ValidateSet("app-image", "exe")]
    [string]$Type = "app-image",
    [switch]$Installer,
    [switch]$InstallWiX,
    [string]$WiXUrl = "https://github.com/wixtoolset/wix3/releases/download/wix3141rtm/wix314.exe",
    [string]$WiXDownload = "target\downloads\wix314.exe"
)

$ErrorActionPreference = "Stop"

function Test-WiXAvailable {
    return [bool]((Get-Command candle.exe -ErrorAction SilentlyContinue) -and (Get-Command light.exe -ErrorAction SilentlyContinue))
}

function Get-WiXInstaller {
    param(
        [string]$Url,
        [string]$Path
    )

    $directory = Split-Path -Parent $Path
    if ($directory -and -not (Test-Path -LiteralPath $directory)) {
        New-Item -ItemType Directory -Force -Path $directory | Out-Null
    }

    if (-not (Test-Path -LiteralPath $Path)) {
        Write-Host "WiX tools were not found. Downloading WiX Toolset to $Path"
        Invoke-WebRequest -Uri $Url -OutFile $Path
    } else {
        Write-Host "Using existing WiX download at $Path"
    }

    return (Resolve-Path -LiteralPath $Path).Path
}

function Ensure-WiXForInstaller {
    if (Test-WiXAvailable) {
        return
    }

    $installerFile = Get-WiXInstaller -Url $WiXUrl -Path $WiXDownload
    if ($InstallWiX) {
        Write-Host "Starting WiX installer. Finish the installer, then this script will continue."
        try {
            Start-Process -FilePath $installerFile -Wait
        } catch {
            throw "Downloaded WiX to $installerFile, but could not start it. Run that file manually, then rerun this installer build."
        }

        $env:Path = [System.Environment]::GetEnvironmentVariable("Path", "Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path", "User") + ";" + $env:Path
        if (Test-WiXAvailable) {
            return
        }
        throw "WiX was downloaded and the installer was started, but candle.exe and light.exe are still not on PATH. Run $installerFile manually if installation did not finish, then rerun this build."
    }

    throw "Can not build a jpackage .exe installer because WiX is missing. WiX was downloaded to $installerFile. Install it manually, or rerun with -InstallWiX to start the WiX installer from this script."
}

if (-not (Get-Command jpackage -ErrorAction SilentlyContinue)) {
    throw "jpackage was not found. Use JDK 14+; this workspace currently uses JDK 21."
}

if (-not (Test-Path -LiteralPath $Jar)) {
    throw "Missing jar: $Jar. Run mvn package first."
}

if ($Installer) {
    $Type = "exe"
    if ($Dest -eq "target\desktop-app") {
        $Dest = "target\desktop-installer"
    }
    if ($InputDir -eq "target\desktop-input") {
        $InputDir = "target\desktop-installer-input"
    }
}

if ($Type -eq "exe") {
    Ensure-WiXForInstaller
}

if (Test-Path -LiteralPath $InputDir) {
    throw "Input folder already exists: $InputDir. Choose another -InputDir path or remove it yourself."
}
if (Test-Path -LiteralPath $Dest) {
    throw "Destination folder already exists: $Dest. Choose another -Dest path or remove it yourself."
}

New-Item -ItemType Directory -Force -Path $InputDir | Out-Null
Copy-Item -LiteralPath $Jar -Destination $InputDir

$darklua = Get-Command darklua -ErrorAction SilentlyContinue
if ($darklua) {
    Copy-Item -LiteralPath $darklua.Source -Destination (Join-Path $InputDir "darklua.exe")
}

$args = @(
    "--type", $Type,
    "--dest", $Dest,
    "--input", $InputDir,
    "--name", $Name,
    "--app-version", "1.0.0",
    "--vendor", "Xell",
    "--description", "Xell Lua obfuscator desktop app",
    "--main-jar", (Split-Path -Leaf $Jar),
    "--main-class", "icu.Xell.Mainline.obfuscator.XellDesktopApp"
)

if ($Type -eq "exe") {
    $args += @(
        "--win-menu",
        "--win-shortcut",
        "--win-dir-chooser"
    )
}

& jpackage @args
if ($LASTEXITCODE -ne 0) {
    throw "jpackage failed with exit code $LASTEXITCODE."
}

if ($Type -eq "exe") {
    $installerPath = Join-Path $Dest "$Name-1.0.0.exe"
    Write-Host "Desktop installer created at $installerPath"
} else {
    Write-Host "Desktop app created at $(Join-Path $Dest $Name)"
}
