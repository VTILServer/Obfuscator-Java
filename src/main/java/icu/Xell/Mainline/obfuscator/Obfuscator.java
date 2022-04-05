package icu.Xell.Mainline.obfuscator;

import icu.Xell.obfuscator.impl.CompilerOptions;
import org.apache.commons.io.IOUtils;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.compiler.XellDumpState;
import org.luaj.vm2.compiler.XellLuaCompiler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static icu.Xell.Mainline.obfuscator.minify.Minifier.minify;
import static spark.Spark.*;

public class Obfuscator {
    static String[] base;
    static AtomicBoolean hasErrored;
    static int zeroCount;

    private boolean isCTO = false;
    private boolean isDebug = true;
    private boolean isPublic = false;

    static {
        Obfuscator.base = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        Obfuscator.hasErrored = new AtomicBoolean(false);
        Obfuscator.zeroCount = 0;
    }

    public static StringBuilder toBase(int number) {
        if (number == 0) {
            final StringBuilder dab = new StringBuilder();
            final Random random = new Random();
            dab.append("G" + (int) Math.floor(random.nextInt(9)));
            ++Obfuscator.zeroCount;
            return dab;
        }
        final List<String> buffer = new ArrayList<String>();
        final boolean isNegative = number < 0;
        String stuff;
        for (number = (int) Math.abs(Math.floor(number)); number != 0; number = (int) Math.floor(number / 16)) {
            stuff = Obfuscator.base[number % 16];
            buffer.add(stuff);
        }
        if (isNegative) {
            buffer.add("-");
            System.out.println("NEGATIVE");
        }
        StringBuilder ret = new StringBuilder();
        ret.append(String.join("", buffer));
        ret = ret.reverse();
        if (ret.length() == 1) {
            ret.append("!");
        }
        return ret;
    }

    private static String processScript(final InputStream script, final OutputStream out, final CompilerOptions opts) throws Exception {
        Label_0087:
        {
            try {
                final Prototype chunk = XellLuaCompiler.compile(script, "=Xell", opts);
                XellDumpState.dump(chunk, out, true, 0, true, opts);
            } catch (Exception e) {
                if (e instanceof LuaError) {
                    System.out.println("ERROR: " + e.getMessage());
                    return e.getMessage();
                }
                break Label_0087;
            } finally {
                script.close();
            }
            script.close();
        }
        script.close();
        return "";
    }


    private static String obfuscate(final String input, final boolean minify) throws Exception {
        final InputStream in = IOUtils.toInputStream(input, "UTF-8");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String out = "";
        final CompilerOptions opts = new CompilerOptions();
        try {
            out = processScript(in, outputStream, opts);
            if (out != "") {
                Obfuscator.hasErrored.set(true);
                return out;
            }
        } finally {
            outputStream.close();
        }
        outputStream.close();
        final StringBuilder buffer = new StringBuilder();
        final Random random = new Random();
        final char[] chars = "*&^%#@!)(".toCharArray();
        final StringBuilder sb = new StringBuilder(20);
        for (int i = 0; i < 2; ++i) {
            final char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        final String sep1 = "&^";
        final String sep2 = "(*";
        final String sep3 = "@#";
        final int concetrations = 0;
        int obf = 0;
        byte[] byteArray;
        for (int length = (byteArray = outputStream.toByteArray()).length, j = 0; j < length; ++j) {
            final byte c2 = byteArray[j];
            ++obf;
            final int b = c2 & 0xFF;
            buffer.append((CharSequence) toBase(b));
        }
        String template = new String(Files.readAllBytes(Paths.get("VM.Xell", new String[0])));
        template = template.replaceAll("%%_INFOWATERMARK%%", "Obfuscated with Xell");
        template = template.replaceAll("%%_Version%%", "1.0.4");

        template = template.replaceAll("%%SEPARATOR_1%%", sep1);
        template = template.replaceAll("%%SEPARATOR_2%%", sep2);
        template = template.replaceAll("%%SEPARATOR_3%%", sep3);

        template = opts.patchTemplate(template);

        template = template.replaceAll("%%BYTECODEMARK%%", "XEL");
        template = template.replaceAll("%%BYTECODE%%", buffer.toString());

        template = template.replaceAll("%%CHUNKRANDOM%%", opts.dumpCustomStack());

        template = template.replaceAll("%%DATASTACK%%", opts.dumpDataStack());
        if (minify)
            template = minify(template);
        return template;
    }

    public static void main(String[] args) throws Exception {

        String s = new String(Files.readAllBytes(Paths.get("Xell.in.lua")));
        PrintWriter writer = new PrintWriter("Xell.out.lua");


        String source = obfuscate(s, true);


        writer.println(source);
        writer.close();


        ipAddress("127.0.0.1");

        port(8491);
        exception(Exception.class, (e, request, response) -> {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw, true);
            e.printStackTrace(pw);
            System.err.println(sw.getBuffer().toString());
        });
        AtomicBoolean obfuscating = new AtomicBoolean(false);
        post("/obfuscate", (req, res) -> {
            String scriptObf = "";
            if (obfuscating.get()) {
                res.status(400);
                return "Error: There's already a script being obfuscated, please wait.";
            }

            obfuscating.set(true);
            try {
                scriptObf = obfuscate(req.body(), true);

                res.status(200);
                obfuscating.set(false);
                return scriptObf;
            } catch (Exception e) {
                obfuscating.set(false);
                if (e instanceof LuaError) {
                    scriptObf = e.getMessage().toString();
                    res.status(400);
                    return scriptObf;
                }
                return e.getMessage();
            }
        });
    }
}
