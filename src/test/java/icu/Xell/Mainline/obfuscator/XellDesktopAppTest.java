package icu.Xell.Mainline.obfuscator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class XellDesktopAppTest {
    @Test
    public void desktopAppHasExpectedTitleAndSample() {
        assertEquals("Xell Obfuscator", XellDesktopApp.TITLE);
        assertEquals("x=1\nprint(\"test int:\", x)", XellDesktopApp.DEFAULT_SOURCE);
        assertEquals(1100, XellDesktopApp.SPLASH_DELAY_MS);
    }
}
