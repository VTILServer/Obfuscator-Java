# Xell Obfuscator Documentation

This project builds a Java-based Lua/Luau obfuscator that compiles input Lua source into a protected Lua 5.1 VM runner. The output is intended to run as Lua 5.1-compatible code, including in Luau environments that still support the Lua 5.1 APIs used by the VM.

## Requirements

- Java JDK 8 or newer.
- Maven.
- `darklua` available on `PATH`.
- Optional runtime checks:
  - `lua` for Lua 5.1 validation.
  - `luau` for Luau validation.

The current workspace has Maven installed at:

```powershell
C:\Tools\apache-maven-3.9.15\bin
```

If Maven is not already on your `PATH`, prefix commands with:

```powershell
$env:Path='C:\Tools\apache-maven-3.9.15\bin;' + $env:Path
```

## Build

From the repository root:

```powershell
mvn package
```

The runnable jar is produced at:

```text
target\XellObfuscator-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## File Mode

The default `main` method reads:

```text
Xell.in.lua
```

and writes:

```text
Xell.out.lua
```

Run it with:

```powershell
java -jar target\XellObfuscator-1.0-SNAPSHOT-jar-with-dependencies.jar
```

The process also starts the Eclipse RAP server after writing `Xell.out.lua`. If you only need the file output, run `--once` or stop the process after the output is written.

## RAP UI

The obfuscator serves a local Eclipse RAP UI:

```text
http://127.0.0.1:8491/xell
```

The frontend is Java RAP/RWT widget code, not a separate HTML/CSS/JS or JSP page:

```text
src\main\java\icu\Xell\Mainline\obfuscator\XellRapEntryPoint.java
```

The RAP application and embedded server are configured in:

```text
src\main\java\icu\Xell\Mainline\obfuscator\XellRapApplication.java
src\main\java\icu\Xell\Mainline\obfuscator\XellRapServer.java
```

If that port is already occupied, choose another one:

```powershell
java -Dxell.port=8492 -jar target\XellObfuscator-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Or:

```powershell
$env:XELL_PORT=8492
java -jar target\XellObfuscator-1.0-SNAPSHOT-jar-with-dependencies.jar
```

The RAP frontend calls the obfuscator directly from Java selection listeners, so there is no separate browser-side API layer to keep in sync.

The right panel exposes runtime settings:

- Source column span.
- Final column span.
- Darklua path.

The RAP version accepts the Darklua path as text because the browser-hosted RAP widget set cannot open a real local file chooser on the client machine.

## Desktop App

The project also includes a Swing desktop frontend:

```text
src\main\java\icu\Xell\Mainline\obfuscator\XellDesktopApp.java
```

Build the runnable jar and Windows app image with:

```powershell
mvn package
.\scripts\package-desktop.ps1
```

The generated app launcher is:

```text
target\desktop-app\XellObfuscator\XellObfuscator.exe
```

The desktop launcher shows a short splash screen before opening the main workspace. For automation or smoke testing you can skip it with:

```powershell
target\desktop-app\XellObfuscator\XellObfuscator.exe --no-splash
```

Build a small Windows installer instead of a folder app image with:

```powershell
.\scripts\package-desktop.ps1 -Installer
```

Or use the installer-only wrapper:

```powershell
.\scripts\package-installer.ps1
```

The installer output is:

```text
target\desktop-installer\XellObfuscator-1.0.0.exe
```

`jpackage --type exe` requires the Windows packaging toolchain on the machine running the build. If it reports a missing WiX Toolset, install WiX and run the command again.

The packaging scripts can fetch WiX Toolset 3.14.1 from the official WiX GitHub release when the build machine does not have `candle.exe` and `light.exe` on `PATH`.

Default behavior downloads WiX and stops with the exact downloaded file path:

```powershell
.\scripts\package-installer.ps1
```

Default WiX download path:

```text
target\downloads\wix314.exe
```

To let the script start the downloaded WiX installer before retrying the Xell installer build, run:

```powershell
.\scripts\package-installer.ps1 -InstallWiX
```

If WiX installation needs admin approval or another manual step, finish the WiX installer yourself, reopen the terminal if needed, and rerun `.\scripts\package-installer.ps1`.

## Java Installer

The project also has a Java/Swing installer that does not require WiX. It is packaged as a Java app image and carries the desktop app image as its payload.

Build it with:

```powershell
mvn package
.\scripts\package-java-installer.ps1
```

The installer launcher is:

```text
target\java-installer-app\XellObfuscatorJavaInstaller\XellObfuscatorJavaInstaller.exe
```

The Java installer:

- Shows a splash screen.
- Lets you choose an install folder.
- Copies the full `XellObfuscator.exe` desktop app image to that folder.
- Can create a desktop shortcut.
- Writes launch and uninstall helper `.cmd` files into the install folder.

To produce a single setup `.exe` that extracts and starts the Java installer UI, run:

```powershell
.\scripts\package-java-installer-exe.ps1
```

The single-file installer is:

```text
target\java-installer-exe\XellObfuscatorSetup.exe
```

This single-file wrapper uses Windows IExpress only to unpack the Java installer app to a temp folder. The actual install wizard and splash screen are still the Java/Swing installer.

The packaging script copies `darklua.exe` into the app image when it is available on `PATH`. You can also point the app at a specific Darklua executable with:

```powershell
$env:XELL_DARKLUA_PATH='C:\path\to\darklua.exe'
```

The desktop app also exposes the same source column span, final column span, and Darklua path settings in its controls panel. The desktop path field includes a native Browse button.

## Tests

The project includes JUnit tests for:

- Bytecode container wrapping, checksums, masked chunks, and header stripping.
- Lua quoted and long-bracket literal scanning.
- Source global localization around strings, comments, and member access.
- Darklua column span configuration.
- Generated VM output smoke checks.
- RAP frontend wiring.
- Desktop frontend wiring.

Run them with:

```powershell
mvn test
```

The runtime smoke test uses `lua` when it is available on `PATH`.

## Benchmarks

Lua benchmark cases live in:

```text
benchmarks\lua
```

Current cases cover:

- `controlflow.lua`: branches, loops, nested conditionals.
- `arithmetic.lua`: numeric operators, modulo, exponentiation, floating-point churn.
- `tables_strings.lua`: table mutation, string concatenation, string length, byte access.
- `calls_closures.lua`: closure state, repeated function calls.
- `varargs_returns.lua`: varargs, `select`, multiple returns.
- `mixed_vm_stress.lua`: mixed tables, closures, loops, arithmetic, and calls.
- `vm_stats.lua`: static stats for generated VM output.

Run all benchmark cases with:

```powershell
.\benchmarks\run-benchmarks.ps1 -Runtime lua -Iterations 1
```

The runner:

1. Runs each benchmark as plain Lua.
2. Uses the jar `--once` mode to obfuscate each benchmark.
3. Runs the obfuscated benchmark.
4. Writes CSV timing/checksum results to:

```text
benchmarks\results\last.csv
```

It also writes VM output stats to:

```text
benchmarks\results\last-vm-stats.txt
```

Useful options:

```powershell
.\benchmarks\run-benchmarks.ps1 -Runtime luau -Iterations 3
.\benchmarks\run-benchmarks.ps1 -SkipObfuscate
```

The benchmark scripts print lines in this format:

```text
BENCH_RESULT <case> <seconds> <checksum>
```

Checksums help catch behavior drift between plain and obfuscated runs.

The jar also supports one-shot generation without starting the HTTP server:

```powershell
java -jar target\XellObfuscator-1.0-SNAPSHOT-jar-with-dependencies.jar --once .\benchmarks\lua\controlflow.lua .\benchmarks\results\controlflow.obf.lua
```

## Pipeline

The obfuscation pipeline is:

1. Convert/minify the input source with Darklua.
2. Localize known globals so Darklua can rename their local aliases.
3. Inject small literal decoders for source strings and numbers.
4. Run Darklua again on the transformed source.
5. Compile the source to Lua bytecode with the bundled LuaJ compiler.
6. Wrap the bytecode in a custom encoded container with checksum validation.
7. Patch `VM.Xell` with VM handlers, bytecode payload, and custom constants.
8. Compress string literals in the final VM source.
9. Run Darklua on the final obfuscated VM output.
10. Rewrite final integer literals into hexadecimal form.

Darklua is the default minifier for source and final output.

## Darklua Column Span

Darklua uses the dense generator. The default `column_span` is:

```text
2
```

You can configure it globally:

```powershell
java -Dxell.darklua.column_span=120 -jar target\XellObfuscator-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Or per stage:

```powershell
java -Dxell.darklua.source.column_span=80 -Dxell.darklua.final.column_span=120 -jar target\XellObfuscator-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Environment variables are also supported:

```powershell
$env:XELL_DARKLUA_COLUMN_SPAN=120
$env:XELL_DARKLUA_SOURCE_COLUMN_SPAN=80
$env:XELL_DARKLUA_FINAL_COLUMN_SPAN=120
```

Priority order:

1. `xell.darklua.<stage>.column_span`
2. `XELL_DARKLUA_<STAGE>_COLUMN_SPAN`
3. `xell.darklua.column_span`
4. `XELL_DARKLUA_COLUMN_SPAN`
5. Default `2`

Stages are:

- `source`
- `final`

The RAP and desktop frontends expose these same per-run settings directly in the controls panel, so you do not need to restart the jar just to try a different column span or Darklua executable.

## Darklua Source Conversion

The source pass is configured to remove or rewrite Luau features before Lua 5.1 compilation, including:

- Type annotations.
- Luau number syntax.
- Interpolated strings.
- `continue`.
- Compound assignment.
- Floor division.
- If-expressions.
- Local function declarations.

This allows Luau-like input to be lowered before it reaches the Lua 5.1 compiler.

## VM Internals

The VM stores its internal chunk and instruction data in numeric slots instead of named table fields. This avoids leaking table-field names that Darklua cannot rename safely.

Examples of names that should not appear in final output:

```text
XellRun
DecodeChunk
ByteString
Instr
Proto
Chunk
Stack
Args
Vargs
Value
```

The final Darklua pass is responsible for renaming local variables and local functions. The obfuscator should not manually randomize variables after Darklua.

## Roblox Notes

Roblox/Luau does not always expose the full execution environment through `_G`. The generated VM uses:

```lua
getfenv and getfenv() or _G
```

This lets aliases such as `string`, `table`, and `math` resolve from the real script or command environment when running in Roblox Studio.

If you see an error like:

```text
attempt to index nil with 'byte'
```

it usually means the VM tried to resolve `string.byte` from the wrong environment. Regenerate with the current build, which uses `getfenv()` fallback behavior.

## Verification

After building and generating output, run:

```powershell
lua .\Xell.out.lua
luau .\Xell.out.lua
```

For the default sample input:

```lua
print(_VERSION)
```

expected output is:

```text
Lua 5.1
Luau
```

Useful scans:

```powershell
$text = Get-Content .\Xell.out.lua -Raw
($text.ToCharArray() | Where-Object { $_ -eq '"' -or $_ -eq [char]39 }).Count
$text.Contains('__xell_')
$text.Contains('XellRun')
$text.Contains('Stack')
```

Expected results for the current final output:

- Quote count: `0`
- `__xell_`: `False`
- `XellRun`: `False`
- `Stack`: `False`

Final integer literals are emitted as hexadecimal tokens such as `0x1F` after Darklua finishes. Floating-point literals are left alone when they appear, because Lua 5.1-compatible hexadecimal float syntax is not portable across every target runtime.

## Troubleshooting

### `darklua` Not Found

Make sure `darklua.exe` is on `PATH`:

```powershell
Get-Command darklua
```

### Maven Not Found

Use the bundled Maven path:

```powershell
$env:Path='C:\Tools\apache-maven-3.9.15\bin;' + $env:Path
mvn package
```

### Server Already Running

The HTTP server binds to:

```text
127.0.0.1:8491
```

If that port is occupied, stop the existing Java process or configure another port with `xell.port` or `XELL_PORT`.

### Output Has Long Lines

Increase the final column span:

```powershell
java -Dxell.darklua.final.column_span=120 -jar target\XellObfuscator-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Output Has Too Many Lines

Lower the final column span or use the default:

```powershell
java -Dxell.darklua.final.column_span=2 -jar target\XellObfuscator-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Important Limitations

- Obfuscation raises reverse-engineering cost, but it is not encryption.
- Any script that runs on a client can eventually be inspected by a determined analyst.
- The VM relies on Lua 5.1-style APIs such as `getfenv` and `loadstring` behavior.
- Darklua can rename variables and functions, but it cannot safely rename arbitrary table keys. Internal VM data avoids this by using numeric slots.
