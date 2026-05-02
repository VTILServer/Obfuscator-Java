package icu.Xell.Mainline.obfuscator;

import org.junit.Assume;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class GeneratedOutputTest {

    @Test
    public void obfuscateWithoutDarkluaProducesPatchedHeaderlessVm() throws Exception {
        String output = Obfuscator.obfuscate("x=1\nprint(\"test int:\", x)", false);

        assertFalse(output.contains("%%"));
        assertFalse(output.contains("\\27XEL"));
        assertFalse(output.contains("Xell bytecode expected"));
        assertFalse(output.contains("LUAC"));
        assertTrue(output.contains("getBytecodeText"));
        assertTrue(output.contains("DecodeChunk(getBytecodeText())"));
    }

    @Test
    public void finalNumberEncoderRewritesIntegersAndSkipsStringsCommentsAndFloats() {
        String encoded = Obfuscator.encodeFinalNumbersAsHex(
                "local a=15 local b=0x10 local c=12.5 local d=1e6 -- 123\nlocal s='456' local t=[[789]] local z=0");

        assertTrue(encoded.contains("local a=0xF"));
        assertTrue(encoded.contains("local b=0x10"));
        assertTrue(encoded.contains("local c=12.5"));
        assertTrue(encoded.contains("local d=1e6"));
        assertTrue(encoded.contains("-- 123"));
        assertTrue(encoded.contains("'456'"));
        assertTrue(encoded.contains("[[789]]"));
        assertTrue(encoded.contains("local z=0x0"));
    }

    @Test
    public void finalOutputDoesNotContainPlainDecimalIntegerTokens() throws Exception {
        String output = Obfuscator.obfuscate("x=1\nprint(\"test int:\", x)", false);

        assertFalse(Pattern.compile("(?<![A-Za-z0-9_\\.])\\d+(?![A-Za-z0-9_\\.])").matcher(output).find());
        assertTrue(output.contains("0x"));
    }

    @Test
    public void generatedOutputRunsInLuaWhenLuaIsAvailable() throws Exception {
        Assume.assumeTrue(commandExists("lua"));
        String output = Obfuscator.obfuscate("x=1\nprint(\"test int:\", x)", false);
        Path file = Files.createTempFile("xell-output-", ".lua");
        try {
            Files.write(file, output.getBytes(StandardCharsets.UTF_8));
            Process process = new ProcessBuilder("lua", file.toString()).start();
            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            assertTrue("lua process timed out", finished);

            String stdout = readAll(process.getInputStream());
            String stderr = readAll(process.getErrorStream());
            assertEquals(stderr, 0, process.exitValue());
            assertTrue(stdout.contains("test int:"));
            assertTrue(stdout.contains("1"));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    private static boolean commandExists(String command) {
        try {
            Process process = new ProcessBuilder(command, "-v").start();
            process.waitFor(5, TimeUnit.SECONDS);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static String readAll(java.io.InputStream input) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = input.read(buffer)) >= 0) {
            out.write(buffer, 0, read);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}
