package icu.Xell.Mainline.obfuscator;

import icu.Xell.obfuscator.impl.CompilerOptions;
import org.apache.commons.io.IOUtils;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.compiler.XellDumpState;
import org.luaj.vm2.compiler.XellLuaCompiler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static icu.Xell.Mainline.obfuscator.minify.Minifier.minifyFinalWithDarklua;
import static icu.Xell.Mainline.obfuscator.minify.Minifier.minifySourceWithDarklua;

public class Obfuscator {
    static String[] base;
    static AtomicBoolean hasErrored;
    static int zeroCount;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int[] STRING_BASES = new int[]{16, 24, 30, 32, 56};
    static final Set<String> SOURCE_GLOBALS = new LinkedHashSet<String>(Arrays.asList(
            "_G", "_VERSION", "assert", "bit", "bit32", "Color3", "coroutine", "CFrame", "debug", "delay",
            "Enum", "error", "game", "getfenv", "Instance", "ipairs", "loadstring", "math", "next", "os",
            "owner", "pairs", "pcall", "print", "require", "script", "select", "setfenv", "shared", "spawn",
            "string", "table", "task", "tick", "tonumber", "tostring", "type", "UDim2", "unpack", "Vector3",
            "wait", "warn", "workspace", "xpcall"
    ));
    static final Set<String> VM_GLOBALS = new LinkedHashSet<String>(Arrays.asList(
            "bit", "error", "getfenv", "loadstring", "math", "select", "setmetatable", "string", "table",
            "tonumber", "tostring", "type", "unpack"
    ));

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
            dab.append("G").append(RANDOM.nextInt(9));
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
        try {
            final Prototype chunk = XellLuaCompiler.compile(script, "=Xell", opts);
            XellDumpState.dump(chunk, out, true, 0, true, opts);
        } catch (Exception e) {
            if (e instanceof LuaError) {
                return e.getMessage();
            }
            throw e;
        } finally {
            script.close();
        }
        return "";
    }


    public static String obfuscate(final String input) throws Exception {
        return obfuscate(input, true);
    }

    static String obfuscate(final String input, final boolean minify) throws Exception {
        final String lua51Source = minify ? minifySourceWithDarklua(input) : input;
        final String localizedSource = localizeKnownGlobals(lua51Source, SOURCE_GLOBALS);
        final String injectedSource = injectSourceLiteralVMs(localizedSource);
        final String source = minify ? minifySourceWithDarklua(injectedSource) : injectedSource;
        final InputStream in = IOUtils.toInputStream(source, "UTF-8");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String out = "";
        final CompilerOptions opts = new CompilerOptions();
        try {
            out = processScript(in, outputStream, opts);
            if (!out.isEmpty()) {
                Obfuscator.hasErrored.set(true);
                return out;
            }
        } finally {
            outputStream.close();
        }
        outputStream.close();
        final StringBuilder buffer = new StringBuilder();
        final String sep1 = "&^";
        final String sep2 = "(*";
        final String sep3 = "@#";
        final int concetrations = 0;
        int obf = 0;
        final int bytecodeKey = RANDOM.nextInt(255) + 1;
        final int bytecodeSalt = RANDOM.nextInt(256);
        final int bytecodeMultiplier = randomOdd16();
        final int bytecodeIncrement = RANDOM.nextInt(65536);
        byte[] byteArray = wrapLuaBytecode(stripLuaHeader(outputStream.toByteArray()), bytecodeKey, bytecodeSalt, bytecodeMultiplier, bytecodeIncrement);
        for (int length = byteArray.length, j = 0; j < length; ++j) {
            final byte c2 = byteArray[j];
            ++obf;
            final int b = c2 & 0xFF;
            buffer.append((CharSequence) toBase(b));
        }
        String template = loadVmTemplate();
        template = template.replace("%%_INFOWATERMARK%%", "");
        template = template.replace("%%_Version%%", "1.0.4");

        template = template.replace("%%SEPARATOR_1%%", sep1);
        template = template.replace("%%SEPARATOR_2%%", sep2);
        template = template.replace("%%SEPARATOR_3%%", sep3);

        template = opts.patchTemplate(template);

        final int[] keyTerms = expressionTerms(bytecodeKey - 1, 255);
        final int[] saltTerms = expressionTerms(bytecodeSalt, 256);
        template = template.replace("%%BYTECODE_KEY_A%%", Integer.toString(keyTerms[0]));
        template = template.replace("%%BYTECODE_KEY_B%%", Integer.toString(keyTerms[1]));
        template = template.replace("%%BYTECODE_KEY_C%%", Integer.toString(keyTerms[2]));
        template = template.replace("%%BYTECODE_SALT_A%%", Integer.toString(saltTerms[0]));
        template = template.replace("%%BYTECODE_SALT_B%%", Integer.toString(saltTerms[1]));
        template = template.replace("%%BYTECODE_SALT_C%%", Integer.toString(saltTerms[2]));
        template = template.replace("%%BYTECODE_LCG_MULTIPLIER%%", Integer.toString(bytecodeMultiplier));
        template = template.replace("%%BYTECODE_LCG_INCREMENT%%", Integer.toString(bytecodeIncrement));

        final int chunkMultiplier = randomOdd8();
        final int chunkIncrement = RANDOM.nextInt(256);
        final int orderSeed = RANDOM.nextInt(256);
        final int orderMultiplier = randomOdd8();
        final int orderIncrement = RANDOM.nextInt(256);
        final BytecodeChunks bytecodeChunks = splitBytecode(buffer.toString(), chunkMultiplier, chunkIncrement,
                orderSeed, orderMultiplier, orderIncrement);
        template = template.replace("%%BYTECODE_CHUNKS%%", bytecodeChunks.assignments);
        template = template.replace("%%BYTECODE_ORDER_DATA%%", bytecodeChunks.order);
        template = template.replace("%%BYTECODE_CHUNK_MULTIPLIER%%", Integer.toString(chunkMultiplier));
        template = template.replace("%%BYTECODE_CHUNK_INCREMENT%%", Integer.toString(chunkIncrement));
        template = template.replace("%%BYTECODE_ORDER_SEED%%", Integer.toString(orderSeed));
        template = template.replace("%%BYTECODE_ORDER_MULTIPLIER%%", Integer.toString(orderMultiplier));
        template = template.replace("%%BYTECODE_ORDER_INCREMENT%%", Integer.toString(orderIncrement));

        template = template.replace("%%CHUNKRANDOM%%", opts.dumpCustomStack());

        template = template.replace("%%DATASTACK%%", opts.dumpDataStack());
        template = localizeKnownGlobals(template, VM_GLOBALS);
        template = encodeLuaStrings(template);
        if (minify)
            template = minifyFinalWithDarklua(template);
        return encodeFinalNumbersAsHex(template);
    }

    static String localizeKnownGlobals(String source, Set<String> globals) {
        final Map<String, String> aliases = new LinkedHashMap<String, String>();
        final String transformed = replaceGlobalReads(source, globals, aliases);
        if (aliases.isEmpty()) {
            return source;
        }

        final String envName = "__xell_env";
        final StringBuilder prefix = new StringBuilder(aliases.size() * 48);
        prefix.append("local ").append(envName).append("=(getfenv and getfenv() or _G)\n");
        for (Map.Entry<String, String> entry : aliases.entrySet()) {
            prefix.append("local ").append(entry.getValue()).append("=")
                    .append(envName).append("[\"").append(entry.getKey()).append("\"]\n");
        }
        return prefix + transformed;
    }

    static String replaceGlobalReads(String source, Set<String> globals, Map<String, String> aliases) {
        StringBuilder out = new StringBuilder(source.length());
        int index = 0;
        while (index < source.length()) {
            char current = source.charAt(index);
            if (current == '-' && index + 1 < source.length() && source.charAt(index + 1) == '-') {
                int commentEnd = skipLuaComment(source, index);
                out.append(source, index, commentEnd);
                index = commentEnd;
                continue;
            }
            if (current == '\'' || current == '"') {
                LuaStringLiteral literal = readLuaString(source, index);
                out.append(source, index, literal.endIndex);
                index = literal.endIndex;
                continue;
            }
            if (current == '[') {
                LuaStringLiteral literal = readLuaLongBracket(source, index);
                if (literal != null) {
                    out.append(source, index, literal.endIndex);
                    index = literal.endIndex;
                    continue;
                }
            }
            if (isIdentifierStart(current)) {
                int end = readIdentifierEnd(source, index);
                String identifier = source.substring(index, end);
                if (globals.contains(identifier) && canLocalizeGlobalRead(source, index, end)) {
                    String alias = aliases.get(identifier);
                    if (alias == null) {
                        alias = "__xell_global_" + aliases.size();
                        aliases.put(identifier, alias);
                    }
                    out.append(alias);
                } else {
                    out.append(identifier);
                }
                index = end;
                continue;
            }
            out.append(current);
            index++;
        }
        return out.toString();
    }

    private static boolean canLocalizeGlobalRead(String source, int start, int end) {
        int previous = previousNonWhitespace(source, start - 1);
        if (previous >= 0) {
            char previousChar = source.charAt(previous);
            if (previousChar == '.' || previousChar == ':') {
                return false;
            }
        }
        int next = nextNonWhitespace(source, end);
        if (next >= 0 && source.charAt(next) == '=' && !(next + 1 < source.length() && source.charAt(next + 1) == '=')) {
            return false;
        }
        return true;
    }

    private static int previousNonWhitespace(String source, int index) {
        while (index >= 0 && Character.isWhitespace(source.charAt(index))) {
            index--;
        }
        return index;
    }

    private static int nextNonWhitespace(String source, int index) {
        while (index < source.length() && Character.isWhitespace(source.charAt(index))) {
            index++;
        }
        return index < source.length() ? index : -1;
    }

    static String injectSourceLiteralVMs(String source) {
        final String stringVm = "__xell_string_vm";
        final String numberVm = "__xell_number_vm";
        final SourceTransformResult transformed = transformSourceLiterals(source, stringVm, numberVm);
        if (!transformed.changed) {
            return source;
        }

        String prefix = "local function " + stringVm + "(t,k)local r={}for i=1,#t do r[i]=string.char((t[i]-k-i)%256)end return table.concat(r)end\n"
                + "local function " + numberVm + "(t,k)local r={}for i=1,#t do r[i]=string.char((t[i]-k-i)%256)end return tonumber(table.concat(r))end\n";
        return prefix + transformed.source;
    }

    static SourceTransformResult transformSourceLiterals(String source, String stringVm, String numberVm) {
        StringBuilder out = new StringBuilder(source.length() * 2);
        boolean changed = false;
        int index = 0;
        while (index < source.length()) {
            char current = source.charAt(index);
            if (current == '-' && index + 1 < source.length() && source.charAt(index + 1) == '-') {
                int commentEnd = skipLuaComment(source, index);
                out.append(source, index, commentEnd);
                index = commentEnd;
                continue;
            }
            if (current == '\'' || current == '"') {
                LuaStringLiteral literal = readLuaString(source, index);
                out.append(encodeSourceStringLiteral(literal.value, stringVm));
                index = literal.endIndex;
                changed = true;
                continue;
            }
            if (current == '[') {
                LuaStringLiteral literal = readLuaLongBracket(source, index);
                if (literal != null) {
                    out.append(encodeSourceStringLiteral(literal.value, stringVm));
                    index = literal.endIndex;
                    changed = true;
                    continue;
                }
            }
            if (isNumberStart(source, index)) {
                int end = readLuaNumberEnd(source, index);
                if (end > index) {
                    String number = source.substring(index, end);
                    out.append(encodeSourceNumberLiteral(number, numberVm));
                    index = end;
                    changed = true;
                    continue;
                }
            }
            out.append(current);
            index++;
        }
        return new SourceTransformResult(out.toString(), changed);
    }

    private static boolean isNumberStart(String source, int index) {
        char current = source.charAt(index);
        char previous = index > 0 ? source.charAt(index - 1) : '\0';
        char next = index + 1 < source.length() ? source.charAt(index + 1) : '\0';
        if (isIdentifierPart(previous) || previous == '.') {
            return false;
        }
        if (current >= '0' && current <= '9') {
            return true;
        }
        return current == '.' && next >= '0' && next <= '9';
    }

    private static int readLuaNumberEnd(String source, int start) {
        int index = start;
        if (source.charAt(index) == '.') {
            index++;
            while (index < source.length() && Character.isDigit(source.charAt(index))) {
                index++;
            }
            return readExponentEnd(source, index);
        }

        if (index + 1 < source.length() && source.charAt(index) == '0' && (source.charAt(index + 1) == 'x' || source.charAt(index + 1) == 'X')) {
            return 0;
        }

        while (index < source.length() && Character.isDigit(source.charAt(index))) {
            index++;
        }
        if (index < source.length() && source.charAt(index) == '.' && !(index + 1 < source.length() && source.charAt(index + 1) == '.')) {
            index++;
            while (index < source.length() && Character.isDigit(source.charAt(index))) {
                index++;
            }
        }
        return readExponentEnd(source, index);
    }

    private static int readExponentEnd(String source, int index) {
        if (index < source.length() && (source.charAt(index) == 'e' || source.charAt(index) == 'E')) {
            int exponent = index + 1;
            if (exponent < source.length() && (source.charAt(exponent) == '+' || source.charAt(exponent) == '-')) {
                exponent++;
            }
            int digits = exponent;
            while (digits < source.length() && Character.isDigit(source.charAt(digits))) {
                digits++;
            }
            if (digits > exponent) {
                return digits;
            }
        }
        return index;
    }

    private static boolean isIdentifierPart(char value) {
        return (value >= 'a' && value <= 'z')
                || (value >= 'A' && value <= 'Z')
                || (value >= '0' && value <= '9')
                || value == '_';
    }

    private static boolean isIdentifierStart(char value) {
        return (value >= 'a' && value <= 'z')
                || (value >= 'A' && value <= 'Z')
                || value == '_';
    }

    private static int readIdentifierEnd(String source, int start) {
        int index = start + 1;
        while (index < source.length() && isIdentifierPart(source.charAt(index))) {
            index++;
        }
        return index;
    }

    private static String encodeSourceStringLiteral(String value, String vmName) {
        return encodeSourceByteVmCall(value.getBytes(java.nio.charset.StandardCharsets.UTF_8), vmName);
    }

    private static String encodeSourceNumberLiteral(String value, String vmName) {
        return encodeSourceByteVmCall(value.getBytes(java.nio.charset.StandardCharsets.UTF_8), vmName);
    }

    private static String encodeSourceByteVmCall(byte[] bytes, String vmName) {
        int key = RANDOM.nextInt(199) + 17;
        StringBuilder encoded = new StringBuilder(bytes.length * 5 + 32);
        encoded.append(vmName).append("({");
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) {
                encoded.append(',');
            }
            encoded.append(((bytes[i] & 0xFF) + key + i + 1) & 0xFF);
        }
        encoded.append("},").append(key).append(')');
        return encoded.toString();
    }

    static byte[] wrapLuaBytecode(byte[] bytecode, int key, int salt, int multiplier, int increment) {
        byte[] wrapped = new byte[bytecode.length + 12];
        for (int i = 0; i < 4; i++) {
            wrapped[i] = (byte) RANDOM.nextInt(256);
        }
        int length = bytecode.length;
        int state = bytecodeState(key, salt, wrapped);
        for (int i = 0; i < 4; i++) {
            state = nextBytecodeState(state, multiplier, increment);
            int lengthByte = (length >> (i * 8)) & 0xFF;
            wrapped[4 + i] = (byte) (lengthByte ^ bytecodeMask(state, i, salt));
        }
        int checksum = 0;
        for (int i = 0; i < bytecode.length; i++) {
            checksum = (checksum + ((bytecode[i] & 0xFF) * (i + 1))) & 0x7FFFFFFF;
        }
        for (int i = 0; i < 4; i++) {
            state = nextBytecodeState(state, multiplier, increment);
            int checksumByte = (checksum >> (i * 8)) & 0xFF;
            wrapped[8 + i] = (byte) (checksumByte ^ bytecodeMask(state, i + 4, salt));
        }
        for (int i = 0; i < bytecode.length; i++) {
            int sourceIndex = bytecode.length - 1 - i;
            state = nextBytecodeState(state, multiplier, increment);
            int mask = bytecodeMask(state, i + 8, salt);
            int encoded = (bytecode[sourceIndex] & 0xFF) ^ mask;
            encoded = (encoded + ((mask + i + salt) & 0xFF)) & 0xFF;
            wrapped[12 + i] = (byte) encoded;
        }
        return wrapped;
    }

    static byte[] stripLuaHeader(byte[] bytecode) {
        final int headerSize = 12;
        if (bytecode.length <= headerSize) {
            throw new IllegalArgumentException("compiled bytecode is too short to strip header");
        }
        return Arrays.copyOfRange(bytecode, headerSize, bytecode.length);
    }

    static int bytecodeState(int key, int salt, byte[] header) {
        int state = (key * 257 + salt * 17) & 0xFFFF;
        for (int i = 0; i < 4; i++) {
            state = (state + ((header[i] & 0xFF) * (i * 29 + 13))) & 0xFFFF;
        }
        return state;
    }

    static int nextBytecodeState(int state, int multiplier, int increment) {
        return (state * multiplier + increment) & 0xFFFF;
    }

    static int bytecodeMask(int state, int index, int salt) {
        return (((state / 257) + state + salt + index * 73 + (index % 19) * 41) & 0xFF);
    }

    private static int randomOdd16() {
        return (RANDOM.nextInt(32768) << 1) | 1;
    }

    private static int randomOdd8() {
        return (RANDOM.nextInt(128) << 1) | 1;
    }

    static int[] expressionTerms(int value, int modulus) {
        int a = RANDOM.nextInt(modulus * 16) + 1;
        int b = RANDOM.nextInt(modulus * 16) + 1;
        int product = (int) (((long) a * b) % modulus);
        int c = (value - product) % modulus;
        if (c < 0) {
            c += modulus;
        }
        c += modulus * (RANDOM.nextInt(9) + 1);
        return new int[]{a, b, c};
    }

    static BytecodeChunks splitBytecode(String bytecode, int chunkMultiplier, int chunkIncrement,
                                                int orderSeed, int orderMultiplier, int orderIncrement) {
        List<String> chunks = new ArrayList<String>();
        int index = 0;
        while (index < bytecode.length()) {
            int remaining = bytecode.length() - index;
            int chunkLength = Math.min(remaining, RANDOM.nextInt(241) + 180);
            chunks.add(bytecode.substring(index, index + chunkLength));
            index += chunkLength;
        }

        List<Integer> slots = new ArrayList<Integer>();
        for (int i = 0; i < chunks.size(); i++) {
            slots.add(i + 1);
        }
        Collections.shuffle(slots, RANDOM);

        StringBuilder assignments = new StringBuilder(bytecode.length() + chunks.size() * 24);
        StringBuilder order = new StringBuilder(chunks.size() * 4);
        int orderState = orderSeed;
        for (int i = 0; i < chunks.size(); i++) {
            int slot = slots.get(i);
            if (i > 0) {
                order.append(',');
            }
            orderState = (orderState * orderMultiplier + orderIncrement) & 0xFF;
            order.append(slot + orderState + i + 1);
            assignments.append("bytetbl[").append(slot).append("]=")
                    .append(maskedBytecodeChunk(chunks.get(i), chunkMultiplier, chunkIncrement)).append('\n');
        }
        return new BytecodeChunks(assignments.toString(), order.toString());
    }

    static String maskedBytecodeChunk(String value, int multiplier, int increment) {
        int state = RANDOM.nextInt(256);
        StringBuilder out = new StringBuilder(value.length() * 4 + 12);
        out.append('{').append(state);
        for (int i = 0; i < value.length(); i++) {
            state = (state * multiplier + increment) & 0xFF;
            out.append(',').append((value.charAt(i) + state + i + 2) & 0xFF);
        }
        out.append('}');
        return out.toString();
    }

    private static String encodeLuaStrings(String lua) {
        final String decoderName = "__xell_string_decode";
        final String charName = "__xell_string_char";
        final StringBuilder out = new StringBuilder(lua.length() * 2);
        int index = 0;
        while (index < lua.length()) {
            char current = lua.charAt(index);
            if (current == '-' && index + 1 < lua.length() && lua.charAt(index + 1) == '-') {
                int commentEnd = skipLuaComment(lua, index);
                out.append(lua, index, commentEnd);
                index = commentEnd;
                continue;
            }
            if (current == '\'' || current == '"') {
                LuaStringLiteral literal = readLuaString(lua, index);
                out.append(encodeLuaString(literal.value, decoderName));
                index = literal.endIndex;
                continue;
            }
            if (current == '[') {
                LuaStringLiteral literal = readLuaLongBracket(lua, index);
                if (literal != null) {
                    out.append(encodeLuaString(literal.value, decoderName));
                    index = literal.endIndex;
                    continue;
                }
            }
            out.append(current);
            index++;
        }

        String decoder = "local " + charName + "=string.char\nlocal function " + decoderName + "(t,b)local r=" + charName + "() for i=1,#t,2 do r=r.." + charName + "(t[i]*b+t[i+1])end return r end\n";
        return decoder + out;
    }

    static int skipLuaComment(String lua, int start) {
        if (start + 2 < lua.length() && lua.charAt(start + 2) == '[') {
            int contentStart = longBracketContentStart(lua, start + 2);
            if (contentStart >= 0) {
                int end = longBracketEnd(lua, start + 2, contentStart);
                return end >= 0 ? end : lua.length();
            }
        }
        int lineEnd = lua.indexOf('\n', start + 2);
        return lineEnd >= 0 ? lineEnd : lua.length();
    }

    static LuaStringLiteral readLuaLongBracket(String lua, int start) {
        int contentStart = longBracketContentStart(lua, start);
        if (contentStart < 0) {
            return null;
        }
        int end = longBracketEnd(lua, start, contentStart);
        if (end < 0) {
            return new LuaStringLiteral(lua.substring(longBracketValueStart(lua, contentStart)), lua.length());
        }
        int closeLength = longBracketCloseLength(contentStart - start - 2);
        return new LuaStringLiteral(lua.substring(longBracketValueStart(lua, contentStart), end - closeLength), end);
    }

    static int longBracketContentStart(String lua, int start) {
        if (start >= lua.length() || lua.charAt(start) != '[') {
            return -1;
        }
        int index = start + 1;
        while (index < lua.length() && lua.charAt(index) == '=') {
            index++;
        }
        return index < lua.length() && lua.charAt(index) == '[' ? index + 1 : -1;
    }

    static int longBracketEnd(String lua, int start, int contentStart) {
        int equals = contentStart - start - 2;
        StringBuilder close = new StringBuilder(longBracketCloseLength(equals));
        close.append(']');
        for (int i = 0; i < equals; i++) {
            close.append('=');
        }
        close.append(']');
        int closeStart = lua.indexOf(close.toString(), contentStart);
        return closeStart >= 0 ? closeStart + close.length() : -1;
    }

    static int longBracketValueStart(String lua, int contentStart) {
        if (contentStart >= lua.length()) {
            return contentStart;
        }
        char first = lua.charAt(contentStart);
        if (first == '\r') {
            return contentStart + 1 < lua.length() && lua.charAt(contentStart + 1) == '\n'
                    ? contentStart + 2
                    : contentStart + 1;
        }
        return first == '\n' ? contentStart + 1 : contentStart;
    }

    static int longBracketCloseLength(int equals) {
        return equals + 2;
    }

    static LuaStringLiteral readLuaString(String lua, int start) {
        char quote = lua.charAt(start);
        StringBuilder value = new StringBuilder();
        int index = start + 1;
        while (index < lua.length()) {
            char current = lua.charAt(index++);
            if (current == quote) {
                return new LuaStringLiteral(value.toString(), index);
            }
            if (current != '\\') {
                value.append(current);
                continue;
            }
            if (index >= lua.length()) {
                value.append('\\');
                break;
            }
            char escaped = lua.charAt(index++);
            switch (escaped) {
                case 'a':
                    value.append((char) 7);
                    break;
                case 'b':
                    value.append('\b');
                    break;
                case 'f':
                    value.append('\f');
                    break;
                case 'n':
                    value.append('\n');
                    break;
                case 'r':
                    value.append('\r');
                    break;
                case 't':
                    value.append('\t');
                    break;
                case 'v':
                    value.append((char) 11);
                    break;
                case '\\':
                case '"':
                case '\'':
                    value.append(escaped);
                    break;
                case 'z':
                    while (index < lua.length() && Character.isWhitespace(lua.charAt(index))) {
                        index++;
                    }
                    break;
                default:
                    if (escaped >= '0' && escaped <= '9') {
                        int escapeValue = escaped - '0';
                        int digits = 1;
                        while (digits < 3 && index < lua.length()) {
                            char digit = lua.charAt(index);
                            if (digit < '0' || digit > '9') {
                                break;
                            }
                            escapeValue = escapeValue * 10 + (digit - '0');
                            index++;
                            digits++;
                        }
                        value.append((char) (escapeValue & 0xFF));
                    } else {
                        value.append(escaped);
                    }
                    break;
            }
        }
        return new LuaStringLiteral(value.toString(), index);
    }

    private static String encodeLuaString(String value, String decoderName) {
        int stringBase = STRING_BASES[RANDOM.nextInt(STRING_BASES.length)];
        byte[] bytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        StringBuilder encoded = new StringBuilder(bytes.length * 5 + 32);
        encoded.append(decoderName).append("({");
        for (int i = 0; i < bytes.length; i++) {
            int current = bytes[i] & 0xFF;
            if (i > 0) {
                encoded.append(',');
            }
            encoded.append(current / stringBase).append(',').append(current % stringBase);
        }
        encoded.append("},").append(stringBase).append(')');
        return encoded.toString();
    }

    static String encodeFinalNumbersAsHex(String lua) {
        StringBuilder out = new StringBuilder(lua.length());
        int index = 0;
        while (index < lua.length()) {
            char current = lua.charAt(index);
            if (current == '-' && index + 1 < lua.length() && lua.charAt(index + 1) == '-') {
                int commentEnd = skipLuaComment(lua, index);
                out.append(lua, index, commentEnd);
                index = commentEnd;
                continue;
            }
            if (current == '\'' || current == '"') {
                LuaStringLiteral literal = readLuaString(lua, index);
                out.append(lua, index, literal.endIndex);
                index = literal.endIndex;
                continue;
            }
            if (current == '[') {
                LuaStringLiteral literal = readLuaLongBracket(lua, index);
                if (literal != null) {
                    out.append(lua, index, literal.endIndex);
                    index = literal.endIndex;
                    continue;
                }
            }
            if (isHexableNumberStart(lua, index)) {
                int end = readHexableIntegerEnd(lua, index);
                if (end > index) {
                    String number = lua.substring(index, end);
                    out.append("0x").append(Long.toHexString(Long.parseLong(number)).toUpperCase());
                    index = end;
                    continue;
                }
            }
            out.append(current);
            index++;
        }
        return out.toString();
    }

    private static boolean isHexableNumberStart(String lua, int index) {
        char current = lua.charAt(index);
        if (current < '0' || current > '9') {
            return false;
        }
        char previous = index > 0 ? lua.charAt(index - 1) : '\0';
        if (isIdentifierPart(previous) || previous == '.') {
            return false;
        }
        if (index + 1 < lua.length() && current == '0' && (lua.charAt(index + 1) == 'x' || lua.charAt(index + 1) == 'X')) {
            return false;
        }
        return true;
    }

    private static int readHexableIntegerEnd(String lua, int start) {
        int index = start;
        while (index < lua.length() && Character.isDigit(lua.charAt(index))) {
            index++;
        }
        if (index < lua.length()) {
            char next = lua.charAt(index);
            if (next == '.' || next == 'e' || next == 'E' || isIdentifierStart(next)) {
                return start;
            }
        }
        return index;
    }

    private static String randomAscii(int length) {
        final char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder value = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            value.append(chars[RANDOM.nextInt(chars.length)]);
        }
        return value.toString();
    }

    static final class LuaStringLiteral {
        final String value;
        final int endIndex;

        private LuaStringLiteral(String value, int endIndex) {
            this.value = value;
            this.endIndex = endIndex;
        }
    }

    static final class SourceTransformResult {
        final String source;
        final boolean changed;

        private SourceTransformResult(String source, boolean changed) {
            this.source = source;
            this.changed = changed;
        }
    }

    static final class BytecodeChunks {
        final String assignments;
        final String order;

        private BytecodeChunks(String assignments, String order) {
            this.assignments = assignments;
            this.order = order;
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && "--once".equals(args[0])) {
            PathPair paths = oncePaths(args);
            String input = new String(Files.readAllBytes(Paths.get(paths.input)));
            try (PrintWriter writer = new PrintWriter(paths.output)) {
                writer.println(obfuscate(input, true));
            }
            return;
        }

        String s = new String(Files.readAllBytes(Paths.get("Xell.in.lua")));
        PrintWriter writer = new PrintWriter("Xell.out.lua");


        String source = obfuscate(s, true);


        writer.println(source);
        writer.close();


        XellRapServer.start(configuredPort());
    }

    public static ObfuscationResult runObfuscation(String source, AtomicBoolean obfuscating) {
        return runObfuscation(source, obfuscating, XellObfuscatorSettings.defaults());
    }

    static ObfuscationResult runObfuscation(String source, AtomicBoolean obfuscating, XellObfuscatorSettings settings) {
        if (!obfuscating.compareAndSet(false, true)) {
            return new ObfuscationResult(429, "Error: There's already a script being obfuscated, please wait.");
        }
        try {
            XellObfuscatorSettings activeSettings = settings == null ? XellObfuscatorSettings.defaults() : settings;
            String scriptObf = activeSettings.runWithProperties(() -> obfuscate(source, true));
            return new ObfuscationResult(200, scriptObf);
        } catch (Exception e) {
            return new ObfuscationResult(e instanceof LuaError ? 400 : 500, e.getMessage());
        } finally {
            obfuscating.set(false);
        }
    }

    static String loadVmTemplate() throws IOException {
        Path file = Paths.get("VM.Xell");
        if (Files.exists(file)) {
            return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        }
        try (InputStream input = Obfuscator.class.getResourceAsStream("/VM.Xell")) {
            if (input == null) {
                throw new FileNotFoundException("Missing VM.Xell template");
            }
            return new String(IOUtils.toByteArray(input), StandardCharsets.UTF_8);
        }
    }

    public static final class ObfuscationResult {
        public final int status;
        public final String body;

        ObfuscationResult(int status, String body) {
            this.status = status;
            this.body = body == null ? "" : body;
        }

        public boolean ok() {
            return status == 200;
        }
    }

    static int configuredPort() {
        String value = firstNonEmpty(System.getProperty("xell.port"), System.getenv("XELL_PORT"));
        if (value == null) {
            return 8491;
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed > 0 && parsed <= 65535 ? parsed : 8491;
        } catch (NumberFormatException ignored) {
            return 8491;
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

    static PathPair oncePaths(String[] args) {
        String input = args.length > 1 ? args[1] : "Xell.in.lua";
        String output = args.length > 2 ? args[2] : "Xell.out.lua";
        return new PathPair(input, output);
    }

    static final class PathPair {
        final String input;
        final String output;

        private PathPair(String input, String output) {
            this.input = input;
            this.output = output;
        }
    }
}
