param(
    [string]$Jar = "target\XellObfuscator-1.0-SNAPSHOT-jar-with-dependencies.jar",
    [string]$Dest = "target\desktop-installer",
    [string]$InputDir = "target\desktop-installer-input",
    [string]$Name = "XellObfuscator",
    [switch]$InstallWiX,
    [string]$WiXUrl = "https://github.com/wixtoolset/wix3/releases/download/wix3141rtm/wix314.exe",
    [string]$WiXDownload = "target\downloads\wix314.exe"
)

$ErrorActionPreference = "Stop"

$script = Join-Path $PSScriptRoot "package-desktop.ps1"
& $script -Jar $Jar -Dest $Dest -InputDir $InputDir -Name $Name -Installer -InstallWiX:$InstallWiX -WiXUrl $WiXUrl -WiXDownload $WiXDownload
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}
