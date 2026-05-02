param(
    [string]$Jar = "target\XellObfuscator-1.0-SNAPSHOT-jar-with-dependencies.jar",
    [string]$WorkDir = "target\java-installer-exe-work",
    [string]$OutputDir = "target\java-installer-exe",
    [string]$OutputName = "XellObfuscatorSetup.exe"
)

$ErrorActionPreference = "Stop"

if (-not (Get-Command iexpress.exe -ErrorAction SilentlyContinue)) {
    throw "iexpress.exe was not found. It is normally included with Windows."
}

if (Test-Path -LiteralPath $WorkDir) {
    throw "Work folder already exists: $WorkDir. Choose another -WorkDir path or remove it yourself."
}
if (Test-Path -LiteralPath $OutputDir) {
    throw "Output folder already exists: $OutputDir. Choose another -OutputDir path or remove it yourself."
}

$installerApp = Join-Path $WorkDir "app"
$installerInput = Join-Path $WorkDir "input"
$payload = Join-Path $WorkDir "payload"
$payloadInput = Join-Path $WorkDir "payload-input"
$bundle = Join-Path $WorkDir "bundle"
$zip = Join-Path $bundle "XellObfuscatorJavaInstaller.zip"
$bootstrap = Join-Path $bundle "launch-installer.cmd"
$sed = Join-Path $WorkDir "iexpress.sed"
$outputPath = Join-Path $OutputDir $OutputName

New-Item -ItemType Directory -Force -Path $bundle | Out-Null
New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null

$javaInstallerScript = Join-Path $PSScriptRoot "package-java-installer.ps1"
& $javaInstallerScript -Jar $Jar -Dest $installerApp -InputDir $installerInput -PayloadDest $payload -PayloadInputDir $payloadInput
if ($LASTEXITCODE -ne 0) {
    throw "Java installer app build failed with exit code $LASTEXITCODE."
}

Compress-Archive -LiteralPath (Join-Path $installerApp "XellObfuscatorJavaInstaller") -DestinationPath $zip -Force

@'
@echo off
setlocal
set "TARGET=%TEMP%\XellObfuscatorJavaInstaller-%RANDOM%%RANDOM%"
mkdir "%TARGET%" >nul 2>nul
powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -LiteralPath '%~dp0XellObfuscatorJavaInstaller.zip' -DestinationPath '%TARGET%' -Force"
if errorlevel 1 (
  echo Unable to unpack the Java installer.
  pause
  exit /b 1
)
start "" /wait "%TARGET%\XellObfuscatorJavaInstaller\XellObfuscatorJavaInstaller.exe"
endlocal
'@ | Set-Content -LiteralPath $bootstrap -Encoding ASCII

$outputPathEscaped = (Resolve-Path -LiteralPath $OutputDir).Path + "\" + $OutputName
$bundleEscaped = (Resolve-Path -LiteralPath $bundle).Path

@"
[Version]
Class=IEXPRESS
SEDVersion=3
[Options]
PackagePurpose=InstallApp
ShowInstallProgramWindow=0
HideExtractAnimation=1
UseLongFileName=1
InsideCompressed=0
CAB_FixedSize=0
CAB_ResvCodeSigning=0
RebootMode=N
InstallPrompt=
DisplayLicense=
FinishMessage=
TargetName=$outputPathEscaped
FriendlyName=Xell Obfuscator Setup
AppLaunched=launch-installer.cmd
PostInstallCmd=<None>
AdminQuietInstCmd=
UserQuietInstCmd=
SourceFiles=SourceFiles
[Strings]
FILE0="launch-installer.cmd"
FILE1="XellObfuscatorJavaInstaller.zip"
[SourceFiles]
SourceFiles0=$bundleEscaped
[SourceFiles0]
%FILE0%=
%FILE1%=
"@ | Set-Content -LiteralPath $sed -Encoding ASCII

Start-Process -FilePath "iexpress.exe" -ArgumentList @("/N", "/Q", $sed) -Wait -NoNewWindow

if (-not (Test-Path -LiteralPath $outputPath)) {
    throw "iexpress finished but did not create $outputPath."
}

Write-Host "Single-file Java installer created at $outputPath"
