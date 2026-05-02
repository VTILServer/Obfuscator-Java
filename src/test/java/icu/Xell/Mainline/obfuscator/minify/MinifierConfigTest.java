package icu.Xell.Mainline.obfuscator.minify;

import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class MinifierConfigTest {

    @After
    public void clearProperties() {
        System.clearProperty("xell.darklua.column_span");
        System.clearProperty("xell.darklua.source.column_span");
        System.clearProperty("xell.darklua.final.column_span");
    }

    @Test
    public void stageColumnSpanOverridesGlobalColumnSpan() throws Exception {
        System.setProperty("xell.darklua.column_span", "80");
        System.setProperty("xell.darklua.final.column_span", "120");

        assertEquals(120, darkluaColumnSpan("final"));
        assertEquals(80, darkluaColumnSpan("source"));
    }

    @Test
    public void invalidColumnSpanFallsBackToDefault() throws Exception {
        System.setProperty("xell.darklua.column_span", "not-a-number");

        assertEquals(2, darkluaColumnSpan("final"));
    }

    private static int darkluaColumnSpan(String stage) throws Exception {
        Method method = Minifier.class.getDeclaredMethod("darkluaColumnSpan", String.class);
        method.setAccessible(true);
        return (Integer) method.invoke(null, stage);
    }
}
