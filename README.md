# Obfuscator-Java
AxonObfuscator Fork

## This obfuscator website and installer and ALL ps1 stuff is written by AI, please keep that in mind, most of the actual obfuscator parts are written either by the original creators or me,,,

## Documentation

See [docs/OBFUSCATOR.md](docs/OBFUSCATOR.md) for build instructions, Darklua configuration, usage, verification, and troubleshooting.

## Web UI

Run the jar and open http://127.0.0.1:8491/xell to use the Eclipse RAP frontend.
The UI is built with Java RAP/RWT widgets in `src/main/java/icu/Xell/Mainline/obfuscator/XellRapEntryPoint.java`.
The right side of the UI exposes settings for source `column_span`, final `column_span`, and a custom `darklua` executable path.

If port `8491` is taken, start it with another port:

```powershell
java -Dxell.port=8492 -jar target\XellObfuscator-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Desktop App

Build a Windows app image with an `.exe` launcher:

```powershell
mvn package
.\scripts\package-desktop.ps1
```

The launcher is written to:

```text
target\desktop-app\XellObfuscator\XellObfuscator.exe
```

The desktop app has the same source, output, controls, and settings layout as the RAP UI, plus a native Browse button for selecting `darklua.exe`.
It shows a short splash screen when launched.

Build a Windows installer `.exe` instead of a folder app image:

```powershell
.\scripts\package-desktop.ps1 -Installer
```

The installer is written under `target\desktop-installer`.
You can also run `.\scripts\package-installer.ps1`, which is a small wrapper around that installer build.
If WiX is missing, the script downloads WiX Toolset to `target\downloads\wix314.exe` and tells you to install it. To let the script start the WiX installer for you, run:

```powershell
.\scripts\package-installer.ps1 -InstallWiX
```

Build a Java-based installer with its own splash screen and installer UI:

```powershell
mvn package
.\scripts\package-java-installer.ps1
```

The Java installer app is written to:

```text
target\java-installer-app\XellObfuscatorJavaInstaller\XellObfuscatorJavaInstaller.exe
```

Build a single-file Java installer wrapper:

```powershell
.\scripts\package-java-installer-exe.ps1
```

That writes:

```text
target\java-installer-exe\XellObfuscatorSetup.exe
```

## Tests

Run the unit and smoke tests with:

```powershell
mvn test
```

## Benchmarks

Run Lua benchmark cases against plain and obfuscated output:

```powershell
.\benchmarks\run-benchmarks.ps1 -Runtime lua -Iterations 1
```

Results are written to `benchmarks\results\last.csv`.
