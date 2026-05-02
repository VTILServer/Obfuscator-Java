package icu.Xell.Mainline.obfuscator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class XellJavaInstallerAppTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void installerHasExpectedBranding() {
        assertEquals("Xell Obfuscator Installer", XellJavaInstallerApp.TITLE);
        assertEquals("XellObfuscator", XellJavaInstallerApp.APP_NAME);
        assertEquals(1000, XellJavaInstallerApp.SPLASH_DELAY_MS);
    }

    @Test
    public void copyDirectoryCopiesPayloadFiles() throws Exception {
        Path source = temporaryFolder.newFolder("payload").toPath();
        Path nested = Files.createDirectories(source.resolve("app"));
        Files.write(nested.resolve("payload.txt"), "xell".getBytes(StandardCharsets.UTF_8));

        Path target = temporaryFolder.newFolder("install").toPath();
        XellJavaInstallerApp.copyDirectory(source, target);

        assertEquals("xell", new String(Files.readAllBytes(target.resolve("app").resolve("payload.txt")), StandardCharsets.UTF_8));
    }

    @Test
    public void writeLaunchersCreatesInstallHelpers() throws Exception {
        Path target = temporaryFolder.newFolder("installed").toPath();
        XellJavaInstallerApp.writeLaunchers(target);

        assertTrue(Files.exists(target.resolve("Launch Xell Obfuscator.cmd")));
        assertTrue(Files.exists(target.resolve("Uninstall Xell Obfuscator.cmd")));
    }

    @Test(expected = java.io.IOException.class)
    public void validateInstallRejectsMissingPayload() throws Exception {
        Path source = temporaryFolder.newFolder("missing-payload").toPath();
        Path target = temporaryFolder.newFolder("target").toPath();

        XellJavaInstallerApp.validateInstall(source, target);
    }
}
