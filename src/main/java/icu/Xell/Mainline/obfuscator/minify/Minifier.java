package icu.Xell.Mainline.obfuscator.minify;

import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import org.apache.commons.io.IOUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * A central location for the minification of Lua Files. This will likely
 * be expanded in the future, or at the least an API will be provided allowing for
 * independent Minifiers to be created and used as needed.
 *
 * @author Glossawy
 */
public final class Minifier {

    public static final Path SCRIPT_DIR = Paths.get("scripts");

    private static final String HEADER_COMMENT = "";

    /**
     * Nashorn JavaScript Engine -- {@link ScriptEngine} Object
     */
    private static final ScriptEngine nashornJS = new ScriptEngineManager().getEngineByName("nashorn");
    /**
     * Nashorn JavaScript Engine as Invocable -- {@link Invocable} Object
     */
    private static final Invocable nashorn = nashornJS instanceof Invocable ? (Invocable) nashornJS : null;

    private static final Object luaparseObj, luaminObj;
    // Pre-compiled Regex Pattern for Single or Double Quoted String
    private static Pattern singleQuotePattern = Pattern.compile("('(?:[^']+|'')')", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
    private static Pattern doublequotePattern = Pattern.compile("(\"(?:[^\"]+|\"\")\")", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

    /**
     * Minify contents of file the standard way and then write out minified contents
     * to a temporary file.
     *
     * @param file Path to file to minify
     * @return Minified File
     * @throws IOException If failed to create or write to temporary file
     * @throws ScriptException If failed to minify
     */
    private static Pattern ccLineEndingPattern = Pattern.compile("\n");
    private static final int DEFAULT_DARKLUA_COLUMN_SPAN = 2;

    private static String darkluaSourceConfig() {
        return "{\n" +
            "  generator: { name: \"dense\", column_span: " + darkluaColumnSpan("source") + " },\n" +
            "  rules: [\n" +
            "    \"remove_comments\",\n" +
            "    \"remove_types\",\n" +
            "    \"convert_luau_number\",\n" +
            "    { rule: \"remove_interpolated_string\", strategy: \"string\" },\n" +
            "    \"remove_continue\",\n" +
            "    \"remove_compound_assignment\",\n" +
            "    \"remove_floor_division\",\n" +
            "    \"remove_if_expression\",\n" +
            "    \"convert_local_function_to_assign\",\n" +
            "    \"filter_after_early_return\",\n" +
            "    \"remove_empty_do\",\n" +
            "    \"remove_nil_declaration\",\n" +
            "    \"remove_spaces\",\n" +
            "    \"group_local_assignment\",\n" +
            "    { rule: \"rename_variables\", include_functions: true, globals: [\"$default\", \"$roblox\"] }\n" +
            "  ]\n" +
            "}\n";
    }

    private static String darkluaFinalConfig() {
        return "{\n" +
            "  generator: { name: \"dense\", column_span: " + darkluaColumnSpan("final") + " },\n" +
            "  rules: [\n" +
            "    \"remove_comments\",\n" +
            "    \"convert_local_function_to_assign\",\n" +
            "    \"remove_spaces\",\n" +
            "    \"group_local_assignment\",\n" +
            "    { rule: \"rename_variables\", include_functions: true, globals: [\"$default\", \"$roblox\"] }\n" +
            "  ]\n" +
            "}\n";
    }

    private static int darkluaColumnSpan(String stage) {
        String value = firstNonEmpty(
                System.getProperty("xell.darklua." + stage + ".column_span"),
                System.getenv("XELL_DARKLUA_" + stage.toUpperCase() + "_COLUMN_SPAN"),
                System.getProperty("xell.darklua.column_span"),
                System.getenv("XELL_DARKLUA_COLUMN_SPAN")
        );
        if (value == null) {
            return DEFAULT_DARKLUA_COLUMN_SPAN;
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed > 0 ? parsed : DEFAULT_DARKLUA_COLUMN_SPAN;
        } catch (NumberFormatException ignored) {
            return DEFAULT_DARKLUA_COLUMN_SPAN;
        }
    }

    private static String firstNonEmpty(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }
        return null;
    }

    static {
        Object luaparse = null;
        Object luamin = null;
        try {
            if (nashornJS != null) {
                // Load luaparse requirement, then load luamin
                nashornJS.eval(Files.newBufferedReader(SCRIPT_DIR.resolve(Paths.get("luamin", "node_modules", "luaparse", "luaparse.js"))));
                nashornJS.eval(Files.newBufferedReader(SCRIPT_DIR.resolve(Paths.get("luamin", "luamin.js"))));

                // Store global Parse Options as JavaScript Object
                nashornJS.eval("var _PARSE_VARS = {scope: true}");

                // Retrieve LuaParse and LuaMin Objects on which to execute Lua Methods from
                luaparse = nashornJS.get("luaparse");
                luamin = nashornJS.get("luamin");
            }
        } catch (IOException | ScriptException e) {
            throw new ExceptionInInitializerError(e);
        }
        luaparseObj = luaparse;
        luaminObj = luamin;
    }

    /**
     * Reads in the entire file and passes to LuaMin for minification.
     *
     * @param path File Path
     * @return Minified Code as String
     * @throws IOException     If luamin or luaparse is corrupted in some way
     * @throws ScriptException If an error occurs during evaluation
     */
    public static String minifyFile(Path path) throws IOException, ScriptException {
        String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        return minifyFinalWithDarklua(content);
    }

    /**
     * Use LuaMin to minify a specific piece of code (as a string).
     *
     * @param code Code to minify
     * @return Minified Code
     * @throws ScriptException
     */
    public static String minify(String code) throws ScriptException {
        return minifyFinalWithDarklua(code);
    }

    public static String minifySourceWithDarklua(String code) throws ScriptException {
        return processWithDarklua(code, darkluaSourceConfig(), "source");
    }

    public static String minifyFinalWithDarklua(String code) throws ScriptException {
        return processWithDarklua(code, darkluaFinalConfig(), "final");
    }

    private static String processWithDarklua(String code, String config, String stage) throws ScriptException {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("xell-darklua-");
            Path input = tempDir.resolve(stage + ".lua");
            Path output = tempDir.resolve(stage + ".out.lua");
            Path configPath = tempDir.resolve(stage + ".darklua.json5");
            Files.write(input, code.getBytes(StandardCharsets.UTF_8));
            Files.write(configPath, config.getBytes(StandardCharsets.UTF_8));

            ProcessBuilder builder = new ProcessBuilder(
                    darkluaCommand(),
                    "process",
                    "--config",
                    configPath.toString(),
                    input.toString(),
                    output.toString()
            );
            builder.directory(Paths.get("").toAbsolutePath().toFile());
            Process process = builder.start();
            boolean finished = process.waitFor(120, TimeUnit.SECONDS);
            String stdout = new String(IOUtils.toByteArray(process.getInputStream()), StandardCharsets.UTF_8);
            String stderr = new String(IOUtils.toByteArray(process.getErrorStream()), StandardCharsets.UTF_8);
            if (!finished) {
                process.destroyForcibly();
                throw new ScriptException("darklua timed out while processing " + stage + " Lua");
            }
            if (process.exitValue() != 0) {
                String message = stderr.isEmpty() ? stdout : stderr;
                throw new ScriptException("darklua failed while processing " + stage + " Lua: " + message);
            }
            return new String(Files.readAllBytes(output), StandardCharsets.UTF_8);
        } catch (IOException e) {
            ScriptException scriptException = new ScriptException("Unable to run darklua for " + stage + " Lua: " + e.getMessage());
            scriptException.initCause(e);
            throw scriptException;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ScriptException scriptException = new ScriptException("Interrupted while running darklua for " + stage + " Lua");
            scriptException.initCause(e);
            throw scriptException;
        } finally {
            if (tempDir != null) {
                try (Stream<Path> paths = Files.walk(tempDir)) {
                    paths.sorted(Comparator.reverseOrder())
                         .forEach(path -> {
                             try {
                                 Files.deleteIfExists(path);
                             } catch (IOException ignored) {
                             }
                         });
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static String darkluaCommand() {
        String configured = firstNonEmpty(
                System.getProperty("xell.darklua.path"),
                System.getenv("XELL_DARKLUA_PATH")
        );
        if (configured != null) {
            return configured;
        }

        for (Path candidate : darkluaCandidates()) {
            if (Files.isRegularFile(candidate)) {
                return candidate.toAbsolutePath().toString();
            }
        }
        return "darklua";
    }

    private static List<Path> darkluaCandidates() {
        List<Path> candidates = new ArrayList<Path>();
        String exe = isWindows() ? "darklua.exe" : "darklua";
        candidates.add(Paths.get(exe));
        candidates.add(Paths.get("app", exe));

        Path codeLocation = codeLocation();
        if (codeLocation != null) {
            candidates.add(codeLocation.resolve(exe));
            candidates.add(codeLocation.resolve("app").resolve(exe));
        }
        return candidates;
    }

    private static Path codeLocation() {
        try {
            URI uri = Minifier.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path location = Paths.get(uri);
            return Files.isRegularFile(location) ? location.getParent() : location;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    private static String minifyWithNode(String code) throws ScriptException {
        ProcessBuilder builder = new ProcessBuilder(
                "node",
                "-e",
                "const fs=require('fs');" +
                        "const luamin=require('./scripts/luamin/luamin.js');" +
                        "let input='';" +
                        "process.stdin.setEncoding('utf8');" +
                        "process.stdin.on('data', chunk => input += chunk);" +
                        "process.stdin.on('end', () => {" +
                        "  try { process.stdout.write(luamin.minify(input)); }" +
                        "  catch (err) { console.error(err && err.stack || err); process.exit(1); }" +
                        "});"
        );
        builder.directory(Paths.get("").toAbsolutePath().toFile());

        try {
            Process process = builder.start();
            try (Writer writer = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(code);
            }

            String output = new String(IOUtils.toByteArray(process.getInputStream()), StandardCharsets.UTF_8);
            String error = new String(IOUtils.toByteArray(process.getErrorStream()), StandardCharsets.UTF_8);
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new ScriptException(error.isEmpty() ? "Node luamin exited with code " + exitCode : error);
            }

            return HEADER_COMMENT + output.replace("\n", ";");
        } catch (IOException e) {
            ScriptException scriptException = new ScriptException("Unable to run Node.js luamin fallback: " + e.getMessage());
            scriptException.initCause(e);
            throw scriptException;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ScriptException scriptException = new ScriptException("Interrupted while running Node.js luamin fallback");
            scriptException.initCause(e);
            throw scriptException;
        }
    }

    // Fast Hash Function at 32 bits. We don't need a high quality hash function. Minimal bit count.

    // FIXME The current regular expression of the type:
    /*
     "This is aa string \"containing
     \" another escaped string"

     The line ending after 'containing' will not be fixed and the LuaJ Interpreter will fail
     claiming the string is unterminated.

     This can possibly be fixed by placing an exception for [\"] and [\'] in the regexs.
     */

    /**
     * Repair Lua Strings that are broken by LuaMin during minification. This induces some overhead
     * but attempts to use pre-compiled regular expression.
     * <p>
     * Specifically this checks if a line contains any line endings. If they do, a '\' is inserted
     * before the line ending to allow Lua to continue to the next line. This is done for each
     * line ending in the string. <br />
     * <br />
     * This method makes extensive use of Regular Expressions. Any patterns are pre-compiled and thus
     * cached for performance later on.
     *
     * @param s String to repair
     * @return String repaired as described above
     */
    private static String repairBrokenLuaStrings(String s) {
        Matcher singleMatcher = singleQuotePattern.matcher(s);
        Matcher doubleMatcher = doublequotePattern.matcher(s);
        Set<HashCode> hashes = Sets.newHashSetWithExpectedSize(singleMatcher.groupCount() + doubleMatcher.groupCount());

        // First fix Single Quote Strings, then fix Double Quote Strings
        s = applyLineEndingFix(s, singleMatcher, hashes);
        s = applyLineEndingFix(s, doubleMatcher, hashes);

        return s;
    }


    /**
     * Applies the line ending fix which will replace any line ending characters ('\n' for CC) with
     * '\\\n' which inserts a backslash before the line ending, this is so Lua will move on to the next line
     * without claiming something like a String is unterminated when the end quote is in the proceeding lines. <br />
     * <br />
     * If a HashSet is passed in, the HashSet will be populated with any HashCodes generated in this method
     * as a side effect. <br />
     * <br />
     * If no HashSet is provided, a new one is created for temporary use.
     *
     * @param text             Text to Scan
     * @param matcher          Regular Expression Matcher to use
     * @param usedStringHashes HashSet to store HashCodes so that we do not evaluate the same capture over again
     * @return The String with any captures containing '\n' being fixed with '\\\n'
     */
    private static String applyLineEndingFix(String text, Matcher matcher, Set<HashCode> usedStringHashes) {
        if (usedStringHashes == null)
            usedStringHashes = Sets.newHashSetWithExpectedSize(matcher.groupCount());

        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                // Get the next capture and generate it's accompanying GoodFastHash
                String capture = matcher.group(i);

                // Only apply fix if '\n' exists in the capture.
                Matcher cMatcher = ccLineEndingPattern.matcher(capture);
                if (cMatcher.find())
                    text = text.replace(capture, cMatcher.replaceAll("\\\\\n"));

            }
        }

        return text;
    }
}
