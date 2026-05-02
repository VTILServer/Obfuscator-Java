package icu.Xell.Mainline.obfuscator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class XellObfuscatorSettingsTest {
    @Test
    public void blankSettingsUseDefaults() {
        XellObfuscatorSettings settings = XellObfuscatorSettings.fromText(" ", "", "  ");

        assertEquals(XellObfuscatorSettings.DEFAULT_COLUMN_SPAN, settings.sourceColumnSpan);
        assertEquals(XellObfuscatorSettings.DEFAULT_COLUMN_SPAN, settings.finalColumnSpan);
        assertEquals("", settings.darkluaPath);
    }

    @Test
    public void explicitSettingsAreAppliedDuringRunAndRestoredAfterward() throws Exception {
        System.setProperty("xell.darklua.source.column_span", "44");
        System.clearProperty("xell.darklua.final.column_span");
        System.setProperty("xell.darklua.path", "before-darklua");

        XellObfuscatorSettings settings = XellObfuscatorSettings.fromText("5", "9", " C:\\Tools\\darklua.exe ");
        String observed = settings.runWithProperties(() ->
                System.getProperty("xell.darklua.source.column_span")
                        + ":"
                        + System.getProperty("xell.darklua.final.column_span")
                        + ":"
                        + System.getProperty("xell.darklua.path")
        );

        assertEquals("5:9:C:\\Tools\\darklua.exe", observed);
        assertEquals("44", System.getProperty("xell.darklua.source.column_span"));
        assertNull(System.getProperty("xell.darklua.final.column_span"));
        assertEquals("before-darklua", System.getProperty("xell.darklua.path"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void sourceColumnSpanMustBePositive() {
        XellObfuscatorSettings.fromText("0", "2", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void finalColumnSpanMustBeANumber() {
        XellObfuscatorSettings.fromText("2", "wide", "");
    }
}
