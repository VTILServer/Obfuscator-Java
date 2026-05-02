package icu.Xell.Mainline.obfuscator;

import org.junit.Test;

import java.util.LinkedHashMap;

import static org.junit.Assert.*;

public class ObfuscatorLiteralTest {

    @Test
    public void readsLongBracketStringsWithEqualsAndLeadingNewlineRule() {
        Obfuscator.LuaStringLiteral literal = Obfuscator.readLuaLongBracket("[=[\nhello]=] tail", 0);

        assertNotNull(literal);
        assertEquals("hello", literal.value);
        assertEquals("[=[\nhello]=]".length(), literal.endIndex);
    }

    @Test
    public void skipsLongBracketCommentsWithEquals() {
        String source = "--[=[ comment\nstill comment ]=]\nprint('live')";

        int end = Obfuscator.skipLuaComment(source, 0);

        assertEquals(source.indexOf("\nprint"), end);
    }

    @Test
    public void literalVmInjectionEncodesQuotedAndLongBracketStringsButLeavesComments() {
        String source = "-- keep \"comment\" 123\nlocal a = \"hello\"\nlocal b = [=[world]=]\nlocal n = 42";

        Obfuscator.SourceTransformResult result = Obfuscator.transformSourceLiterals(source, "__svm", "__nvm");

        assertTrue(result.changed);
        assertTrue(result.source.contains("-- keep \"comment\" 123"));
        assertFalse(result.source.contains("\"hello\""));
        assertFalse(result.source.contains("[=[world]=]"));
        assertTrue(result.source.contains("__svm({"));
        assertTrue(result.source.contains("__nvm({"));
    }

    @Test
    public void globalLocalizationIgnoresStringsAndMemberAccesses() {
        String source = "local text = [[print(game)]]\n"
                + "local svc = game:GetService('Players')\n"
                + "local b = string.byte('x')\n"
                + "local c = obj.string\n";

        String localized = Obfuscator.replaceGlobalReads(source, Obfuscator.SOURCE_GLOBALS, new LinkedHashMap<String, String>());

        assertTrue(localized.contains("[[print(game)]]"));
        assertTrue(localized.contains(":GetService"));
        assertTrue(localized.contains(".string"));
        assertTrue(localized.contains("__xell_global_"));
    }
}
