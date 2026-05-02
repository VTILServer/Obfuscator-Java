package icu.Xell.Mainline.obfuscator;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class BytecodeContainerTest {

    @Test
    public void stripLuaHeaderRemovesTheTwelveByteSignatureBlock() {
        byte[] bytecode = new byte[18];
        for (int i = 0; i < bytecode.length; i++) {
            bytecode[i] = (byte) i;
        }

        byte[] stripped = Obfuscator.stripLuaHeader(bytecode);

        assertArrayEquals(new byte[]{12, 13, 14, 15, 16, 17}, stripped);
    }

    @Test(expected = IllegalArgumentException.class)
    public void stripLuaHeaderRejectsTinyPayloads() {
        Obfuscator.stripLuaHeader(new byte[12]);
    }

    @Test
    public void wrappedBytecodeRoundTripsWithCustomPrngConstants() {
        byte[] payload = "not-a-luac-header".getBytes(StandardCharsets.UTF_8);
        int key = 73;
        int salt = 141;
        int multiplier = 51873;
        int increment = 35261;

        byte[] wrapped = Obfuscator.wrapLuaBytecode(payload, key, salt, multiplier, increment);
        byte[] unwrapped = unwrap(wrapped, key, salt, multiplier, increment);

        assertArrayEquals(payload, unwrapped);
        assertFalse(startsWithLuaSignature(unwrapped));
    }

    @Test
    public void splitBytecodeUsesMaskedNumericChunksAndMaskedOrder() {
        String payload = "0123456789abcdef0123456789abcdef0123456789abcdef";
        int chunkMultiplier = 37;
        int chunkIncrement = 91;
        int orderSeed = 11;
        int orderMultiplier = 53;
        int orderIncrement = 19;

        Obfuscator.BytecodeChunks chunks = Obfuscator.splitBytecode(payload, chunkMultiplier, chunkIncrement,
                orderSeed, orderMultiplier, orderIncrement);
        String rebuilt = rebuildChunks(chunks, chunkMultiplier, chunkIncrement, orderSeed, orderMultiplier, orderIncrement);

        assertEquals(payload, rebuilt);
        assertFalse(chunks.assignments.contains("\""));
        assertFalse(chunks.order.equals("1"));
    }

    private static byte[] unwrap(byte[] raw, int key, int salt, int multiplier, int increment) {
        int state = Obfuscator.bytecodeState(key, salt, raw);
        int length = 0;
        int scale = 1;
        for (int i = 0; i < 4; i++) {
            state = Obfuscator.nextBytecodeState(state, multiplier, increment);
            length += ((raw[4 + i] & 0xFF) ^ Obfuscator.bytecodeMask(state, i, salt)) * scale;
            scale *= 256;
        }

        int expectedChecksum = 0;
        scale = 1;
        for (int i = 0; i < 4; i++) {
            state = Obfuscator.nextBytecodeState(state, multiplier, increment);
            expectedChecksum += ((raw[8 + i] & 0xFF) ^ Obfuscator.bytecodeMask(state, i + 4, salt)) * scale;
            scale *= 256;
        }

        byte[] decoded = new byte[length];
        int checksum = 0;
        for (int i = 0; i < length; i++) {
            state = Obfuscator.nextBytecodeState(state, multiplier, increment);
            int mask = Obfuscator.bytecodeMask(state, i + 8, salt);
            int encoded = raw[12 + i] & 0xFF;
            int mixed = (encoded - ((mask + i + salt) & 0xFF)) & 0xFF;
            int value = mixed ^ mask;
            checksum = (int) ((checksum + (long) value * (length - i)) % 2147483648L);
            decoded[length - i - 1] = (byte) value;
        }
        assertEquals(expectedChecksum, checksum);
        return decoded;
    }

    private static boolean startsWithLuaSignature(byte[] value) {
        return value.length >= 4
                && value[0] == 27
                && value[1] == 'L'
                && value[2] == 'u'
                && value[3] == 'a';
    }

    private static String rebuildChunks(Obfuscator.BytecodeChunks chunks, int chunkMultiplier, int chunkIncrement,
                                        int orderSeed, int orderMultiplier, int orderIncrement) {
        Map<Integer, int[]> entries = new HashMap<Integer, int[]>();
        Matcher entryMatcher = Pattern.compile("bytetbl\\[(\\d+)]\\=\\{([^}]*)}", Pattern.DOTALL).matcher(chunks.assignments);
        while (entryMatcher.find()) {
            entries.put(Integer.parseInt(entryMatcher.group(1)), parseInts(entryMatcher.group(2)));
        }

        int[] orderData = parseInts(chunks.order);
        int orderState = orderSeed;
        StringBuilder rebuilt = new StringBuilder();
        for (int i = 0; i < orderData.length; i++) {
            orderState = (orderState * orderMultiplier + orderIncrement) & 0xFF;
            int slot = orderData[i] - orderState - i - 1;
            rebuilt.append(decodeChunk(entries.get(slot), chunkMultiplier, chunkIncrement));
        }
        return rebuilt.toString();
    }

    private static String decodeChunk(int[] entry, int multiplier, int increment) {
        int state = entry[0];
        StringBuilder out = new StringBuilder(entry.length - 1);
        for (int i = 1; i < entry.length; i++) {
            state = (state * multiplier + increment) & 0xFF;
            out.append((char) ((entry[i] - state - i - 1) & 0xFF));
        }
        return out.toString();
    }

    private static int[] parseInts(String text) {
        Matcher matcher = Pattern.compile("\\d+").matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        int[] values = new int[count];
        matcher.reset();
        for (int i = 0; matcher.find(); i++) {
            values[i] = Integer.parseInt(matcher.group());
        }
        return values;
    }
}
