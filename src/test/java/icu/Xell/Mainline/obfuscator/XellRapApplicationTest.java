package icu.Xell.Mainline.obfuscator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class XellRapApplicationTest {
    @Test
    public void rapApplicationUsesDedicatedEntryPoint() {
        assertEquals("/xell", XellRapApplication.ENTRY_POINT_PATH);
    }

    @Test
    public void rapEntryPointStartsWithDefaultSource() {
        assertEquals("x=1\nprint(\"test int:\", x)", XellRapEntryPoint.DEFAULT_SOURCE);
    }
}
