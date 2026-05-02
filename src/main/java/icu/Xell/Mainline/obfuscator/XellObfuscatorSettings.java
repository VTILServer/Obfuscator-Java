package icu.Xell.Mainline.obfuscator;

import java.util.concurrent.Callable;

final class XellObfuscatorSettings {
    static final int DEFAULT_COLUMN_SPAN = 2;

    private static final Object PROPERTY_LOCK = new Object();

    final int sourceColumnSpan;
    final int finalColumnSpan;
    final String darkluaPath;

    XellObfuscatorSettings(int sourceColumnSpan, int finalColumnSpan, String darkluaPath) {
        this.sourceColumnSpan = sourceColumnSpan;
        this.finalColumnSpan = finalColumnSpan;
        this.darkluaPath = normalize(darkluaPath);
    }

    static XellObfuscatorSettings defaults() {
        return new XellObfuscatorSettings(DEFAULT_COLUMN_SPAN, DEFAULT_COLUMN_SPAN, "");
    }

    static XellObfuscatorSettings fromText(String sourceColumnSpan, String finalColumnSpan, String darkluaPath) {
        return new XellObfuscatorSettings(
                parsePositive(sourceColumnSpan, "Source column span"),
                parsePositive(finalColumnSpan, "Final column span"),
                darkluaPath
        );
    }

    <T> T runWithProperties(Callable<T> callable) throws Exception {
        synchronized (PROPERTY_LOCK) {
            String previousSourceSpan = System.getProperty("xell.darklua.source.column_span");
            String previousFinalSpan = System.getProperty("xell.darklua.final.column_span");
            String previousDarkluaPath = System.getProperty("xell.darklua.path");
            try {
                System.setProperty("xell.darklua.source.column_span", Integer.toString(sourceColumnSpan));
                System.setProperty("xell.darklua.final.column_span", Integer.toString(finalColumnSpan));
                setOrClear("xell.darklua.path", darkluaPath);
                return callable.call();
            } finally {
                restore("xell.darklua.source.column_span", previousSourceSpan);
                restore("xell.darklua.final.column_span", previousFinalSpan);
                restore("xell.darklua.path", previousDarkluaPath);
            }
        }
    }

    private static int parsePositive(String value, String label) {
        String normalized = normalize(value);
        if (normalized.isEmpty()) {
            return DEFAULT_COLUMN_SPAN;
        }
        try {
            int parsed = Integer.parseInt(normalized);
            if (parsed > 0) {
                return parsed;
            }
        } catch (NumberFormatException ignored) {
        }
        throw new IllegalArgumentException(label + " must be a positive integer.");
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private static void setOrClear(String key, String value) {
        if (value == null || value.isEmpty()) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
        }
    }

    private static void restore(String key, String value) {
        if (value == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
        }
    }
}
