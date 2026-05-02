package icu.Xell.Mainline.obfuscator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutionException;

public final class XellJavaInstallerApp {
    static final String TITLE = "Xell Obfuscator Installer";
    static final String APP_NAME = "XellObfuscator";
    static final String APP_EXE = "XellObfuscator.exe";
    static final int SPLASH_DELAY_MS = 1000;

    private static final Color HEADER = XellDesktopApp.HEADER;
    private static final Color PANEL = Color.WHITE;
    private static final Color CANVAS = XellDesktopApp.CANVAS;
    private static final Color BORDER = XellDesktopApp.BORDER;

    private JTextField installPath;
    private JCheckBox desktopShortcut;
    private JLabel status;
    private JProgressBar progress;
    private JButton install;
    private JButton launch;
    private Path installedExe;

    private XellJavaInstallerApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            XellJavaInstallerApp app = new XellJavaInstallerApp();
            if (hasFlag(args, "--no-splash")) {
                app.show();
            } else {
                app.showSplashThenMain();
            }
        });
    }

    private static boolean hasFlag(String[] args, String flag) {
        if (args == null) {
            return false;
        }
        for (String arg : args) {
            if (flag.equals(arg)) {
                return true;
            }
        }
        return false;
    }

    private void showSplashThenMain() {
        final JWindow splash = new JWindow();
        splash.setContentPane(splashContent());
        splash.pack();
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);

        Timer timer = new Timer(SPLASH_DELAY_MS, event -> {
            splash.setVisible(false);
            splash.dispose();
            show();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private JPanel splashContent() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setPreferredSize(new Dimension(440, 190));
        root.setBackground(PANEL);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HEADER, 2),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel title = new JLabel(TITLE);
        title.setForeground(HEADER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        root.add(title, BorderLayout.NORTH);

        JLabel detail = new JLabel("Preparing the Java installer");
        detail.setForeground(new Color(70, 76, 84));
        detail.setFont(detail.getFont().deriveFont(Font.PLAIN, 14f));
        root.add(detail, BorderLayout.CENTER);

        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        bar.setBorderPainted(false);
        bar.setForeground(HEADER);
        root.add(bar, BorderLayout.SOUTH);
        return root;
    }

    private void show() {
        JFrame frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(640, 360));
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(CANVAS);
        frame.add(header(), BorderLayout.NORTH);
        frame.add(content(), BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel header() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER);
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel title = new JLabel(TITLE);
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(title, BorderLayout.WEST);

        JLabel mode = new JLabel("Java installer");
        mode.setForeground(Color.WHITE);
        panel.add(mode, BorderLayout.EAST);
        return panel;
    }

    private JPanel content() {
        JPanel root = new JPanel(new BorderLayout(8, 12));
        root.setBackground(CANVAS);
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel title = new JLabel("Install Xell Obfuscator");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        card.add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(0, 1, 0, 8));
        fields.setBackground(PANEL);
        fields.add(installPathRow());
        desktopShortcut = new JCheckBox("Create desktop shortcut", true);
        desktopShortcut.setBackground(PANEL);
        fields.add(desktopShortcut);
        card.add(fields, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setBackground(PANEL);
        status = new JLabel("Ready");
        progress = new JProgressBar();
        progress.setStringPainted(false);
        progress.setVisible(false);
        bottom.add(status, BorderLayout.NORTH);
        bottom.add(progress, BorderLayout.CENTER);
        bottom.add(buttons(), BorderLayout.SOUTH);
        card.add(bottom, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
        return root;
    }

    private JPanel installPathRow() {
        JPanel row = new JPanel(new BorderLayout(8, 4));
        row.setBackground(PANEL);
        row.add(new JLabel("Install folder"), BorderLayout.NORTH);
        installPath = new JTextField(defaultInstallPath().toString());
        row.add(installPath, BorderLayout.CENTER);
        JButton browse = new JButton("Browse");
        browse.addActionListener(event -> chooseInstallPath());
        row.add(browse, BorderLayout.EAST);
        return row;
    }

    private JPanel buttons() {
        JPanel row = new JPanel(new GridLayout(1, 0, 8, 0));
        row.setBackground(PANEL);
        install = new JButton("Install");
        install.addActionListener(event -> install());
        launch = new JButton("Launch");
        launch.setEnabled(false);
        launch.addActionListener(event -> launchInstalledApp());
        row.add(install);
        row.add(launch);
        return row;
    }

    private void chooseInstallPath() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setSelectedFile(installPath().toFile());
        if (chooser.showOpenDialog(installPath) == JFileChooser.APPROVE_OPTION) {
            installPath.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void install() {
        final Path source;
        final Path target = installPath();
        try {
            source = payloadPath();
            validateInstall(source, target);
        } catch (Exception e) {
            showError("Installer is not ready", e);
            return;
        }

        install.setEnabled(false);
        launch.setEnabled(false);
        progress.setIndeterminate(true);
        progress.setVisible(true);
        status.setText("Installing");

        SwingWorker<Path, Void> worker = new SwingWorker<Path, Void>() {
            @Override
            protected Path doInBackground() throws Exception {
                copyDirectory(source, target);
                writeLaunchers(target);
                if (desktopShortcut.isSelected()) {
                    createDesktopShortcut(target.resolve(APP_EXE));
                }
                return target.resolve(APP_EXE);
            }

            @Override
            protected void done() {
                progress.setVisible(false);
                install.setEnabled(true);
                try {
                    installedExe = get();
                    status.setText("Installed to " + installedExe.getParent());
                    launch.setEnabled(true);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    status.setText("Interrupted");
                } catch (ExecutionException e) {
                    showError("Install failed", e);
                    status.setText("Install failed");
                }
            }
        };
        worker.execute();
    }

    private void launchInstalledApp() {
        if (installedExe == null) {
            return;
        }
        try {
            new ProcessBuilder(installedExe.toString()).directory(installedExe.getParent().toFile()).start();
        } catch (IOException e) {
            showError("Unable to launch", e);
        }
    }

    static Path defaultInstallPath() {
        String localAppData = System.getenv("LOCALAPPDATA");
        Path base = localAppData == null || localAppData.trim().isEmpty()
                ? Paths.get(System.getProperty("user.home"), "AppData", "Local")
                : Paths.get(localAppData);
        return base.resolve(APP_NAME);
    }

    private Path installPath() {
        return Paths.get(installPath.getText().trim()).toAbsolutePath().normalize();
    }

    static Path payloadPath() throws URISyntaxException {
        Path code = Paths.get(XellJavaInstallerApp.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path appDir = Files.isRegularFile(code) ? code.getParent() : code;
        Path imageRoot = appDir != null && "app".equalsIgnoreCase(appDir.getFileName().toString()) ? appDir.getParent() : appDir;
        Path packagedPayload = appDir.resolve("payload").resolve(APP_NAME);
        if (Files.exists(packagedPayload)) {
            return packagedPayload.toAbsolutePath().normalize();
        }
        Path imagePayload = imageRoot.resolve("app").resolve("payload").resolve(APP_NAME);
        if (Files.exists(imagePayload)) {
            return imagePayload.toAbsolutePath().normalize();
        }
        return Paths.get("target", "java-installer-payload", APP_NAME).toAbsolutePath().normalize();
    }

    static void validateInstall(Path source, Path target) throws IOException {
        if (!Files.isDirectory(source)) {
            throw new IOException("Missing installer payload: " + source);
        }
        if (!Files.exists(source.resolve(APP_EXE))) {
            throw new IOException("Installer payload is missing " + APP_EXE);
        }
        if (target.equals(source) || source.startsWith(target)) {
            throw new IOException("Choose an install folder outside the installer payload.");
        }
    }

    static void copyDirectory(final Path source, final Path target) throws IOException {
        Files.createDirectories(target);
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(target.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    static void writeLaunchers(Path target) throws IOException {
        Path launcher = target.resolve("Launch Xell Obfuscator.cmd");
        try (BufferedWriter writer = Files.newBufferedWriter(launcher, StandardCharsets.UTF_8)) {
            writer.write("@echo off\r\n");
            writer.write("start \"\" \"%~dp0" + APP_EXE + "\"\r\n");
        }

        Path uninstall = target.resolve("Uninstall Xell Obfuscator.cmd");
        try (BufferedWriter writer = Files.newBufferedWriter(uninstall, StandardCharsets.UTF_8)) {
            writer.write("@echo off\r\n");
            writer.write("echo Close Xell Obfuscator before uninstalling.\r\n");
            writer.write("pause\r\n");
            writer.write("cd /d \"%TEMP%\"\r\n");
            writer.write("rmdir /s /q \"" + target.toAbsolutePath() + "\"\r\n");
        }
    }

    private static void createDesktopShortcut(Path exe) throws IOException {
        Path desktop = Paths.get(System.getProperty("user.home"), "Desktop");
        Files.createDirectories(desktop);
        Path shortcut = desktop.resolve("Xell Obfuscator.lnk");
        Path script = Files.createTempFile("xell-shortcut", ".vbs");
        try (BufferedWriter writer = Files.newBufferedWriter(script, StandardCharsets.UTF_8)) {
            writer.write("Set shell = CreateObject(\"WScript.Shell\")\r\n");
            writer.write("Set shortcut = shell.CreateShortcut(\"" + escapeVbs(shortcut.toString()) + "\")\r\n");
            writer.write("shortcut.TargetPath = \"" + escapeVbs(exe.toString()) + "\"\r\n");
            writer.write("shortcut.WorkingDirectory = \"" + escapeVbs(exe.getParent().toString()) + "\"\r\n");
            writer.write("shortcut.Save\r\n");
        }
        try {
            Process process = new ProcessBuilder("wscript.exe", script.toString()).start();
            int exit = process.waitFor();
            if (exit != 0) {
                writeShortcutFallback(desktop, exe);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            writeShortcutFallback(desktop, exe);
        } catch (IOException e) {
            writeShortcutFallback(desktop, exe);
        } finally {
            Files.deleteIfExists(script);
        }
    }

    private static void writeShortcutFallback(Path desktop, Path exe) throws IOException {
        Path fallback = desktop.resolve("Xell Obfuscator.cmd");
        try (BufferedWriter writer = Files.newBufferedWriter(fallback, StandardCharsets.UTF_8)) {
            writer.write("@echo off\r\n");
            writer.write("start \"\" \"" + exe.toString() + "\"\r\n");
        }
    }

    private static String escapeVbs(String value) {
        return value.replace("\"", "\"\"");
    }

    private void showError(String title, Exception e) {
        Throwable cause = e.getCause() == null ? e : e.getCause();
        JOptionPane.showMessageDialog(installPath, cause.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }
}
