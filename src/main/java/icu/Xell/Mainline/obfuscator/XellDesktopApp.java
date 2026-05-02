package icu.Xell.Mainline.obfuscator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class XellDesktopApp {
    static final String TITLE = "Xell Obfuscator";
    static final String DEFAULT_SOURCE = "x=1\nprint(\"test int:\", x)";
    static final int SPLASH_DELAY_MS = 1100;
    static final Color HEADER = new Color(74, 117, 165);
    static final Color CANVAS = new Color(246, 247, 248);
    static final Color PANEL = Color.WHITE;
    static final Color BORDER = new Color(190, 194, 199);

    private final AtomicBoolean obfuscating = new AtomicBoolean(false);
    private JTextArea source;
    private JTextArea output;
    private JLabel status;
    private JLabel bytes;
    private JButton obfuscate;
    private JTextField sourceColumnSpan;
    private JTextField finalColumnSpan;
    private JTextField darkluaPath;

    private XellDesktopApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {
                }
                if (hasFlag(args, "--no-splash")) {
                    new XellDesktopApp().show();
                } else {
                    new XellDesktopApp().showSplashThenMain();
                }
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
        root.setPreferredSize(new Dimension(420, 190));
        root.setBackground(PANEL);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HEADER, 2),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JPanel heading = new JPanel(new BorderLayout(0, 4));
        heading.setOpaque(false);

        JLabel title = new JLabel(TITLE);
        title.setForeground(HEADER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        heading.add(title, BorderLayout.NORTH);

        JLabel subtitle = new JLabel("Loading desktop workspace");
        subtitle.setForeground(new Color(70, 76, 84));
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 14f));
        heading.add(subtitle, BorderLayout.CENTER);
        root.add(heading, BorderLayout.CENTER);

        JProgressBar progress = new JProgressBar();
        progress.setIndeterminate(true);
        progress.setBorderPainted(false);
        progress.setForeground(HEADER);
        root.add(progress, BorderLayout.SOUTH);
        return root;
    }

    private void show() {
        JFrame frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1120, 680));
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(CANVAS);
        frame.add(header(), BorderLayout.NORTH);
        frame.add(workspace(), BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel header() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER);
        panel.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel title = new JLabel(TITLE);
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(title, BorderLayout.WEST);

        JLabel mode = new JLabel("Desktop Java");
        mode.setForeground(Color.WHITE);
        panel.add(mode, BorderLayout.EAST);
        return panel;
    }

    private JPanel workspace() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(CANVAS);
        root.setBorder(new EmptyBorder(8, 10, 10, 10));

        source = editor(DEFAULT_SOURCE, true);
        output = editor("", false);
        JSplitPane editors = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                pane("Source", source),
                pane("Output", output));
        editors.setResizeWeight(0.5);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editors, controls());
        split.setResizeWeight(0.78);
        split.setBorder(null);
        root.add(split, BorderLayout.CENTER);
        return root;
    }

    private JPanel pane(String title, JTextArea editor) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        JLabel label = new JLabel(title);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 16f));
        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(editor), BorderLayout.CENTER);
        return panel;
    }

    private JTextArea editor(String text, boolean editable) {
        JTextArea editor = new JTextArea(text);
        editor.setEditable(editable);
        editor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        editor.setTabSize(4);
        editor.setBackground(Color.WHITE);
        editor.setBorder(new EmptyBorder(6, 6, 6, 6));
        return editor;
    }

    private JPanel controls() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(6, 6, 6, 6)
        ));
        panel.setPreferredSize(new Dimension(300, 400));

        JPanel info = new JPanel(new GridLayout(0, 1, 0, 4));
        info.setBackground(PANEL);
        JLabel controls = new JLabel("Controls");
        controls.setFont(controls.getFont().deriveFont(Font.PLAIN, 16f));
        status = new JLabel("Ready");
        bytes = new JLabel("Output bytes: 0");
        info.add(controls);
        info.add(status);
        info.add(bytes);
        panel.add(info, BorderLayout.NORTH);

        JPanel stack = new JPanel(new BorderLayout(0, 10));
        stack.setBackground(PANEL);
        stack.add(settingsPanel(), BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(0, 1, 0, 6));
        buttons.setBackground(PANEL);
        buttons.add(button("Open source", new Runnable() {
            @Override
            public void run() {
                openSource();
            }
        }));
        buttons.add(button("Sample", new Runnable() {
            @Override
            public void run() {
                source.setText("local total=0\nfor i=1,5 do\n\ttotal=total+i\nend\nprint(\"sum\", total)");
                setStatus("Sample loaded");
            }
        }));
        obfuscate = button("Obfuscate", new Runnable() {
            @Override
            public void run() {
                obfuscateSource();
            }
        });
        buttons.add(obfuscate);
        buttons.add(button("Save output", new Runnable() {
            @Override
            public void run() {
                saveOutput();
            }
        }));
        buttons.add(button("Copy output", new Runnable() {
            @Override
            public void run() {
                copyOutput();
            }
        }));
        buttons.add(button("Clear output", new Runnable() {
            @Override
            public void run() {
                output.setText("");
                updateBytes();
                setStatus("Ready");
            }
        }));
        stack.add(buttons, BorderLayout.CENTER);
        panel.add(stack, BorderLayout.CENTER);
        return panel;
    }

    private JPanel settingsPanel() {
        JPanel settings = new JPanel(new GridLayout(0, 1, 0, 5));
        settings.setBackground(PANEL);
        settings.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Settings"),
                new EmptyBorder(0, 4, 4, 4)
        ));

        sourceColumnSpan = textField(Integer.toString(XellObfuscatorSettings.DEFAULT_COLUMN_SPAN));
        finalColumnSpan = textField(Integer.toString(XellObfuscatorSettings.DEFAULT_COLUMN_SPAN));
        darkluaPath = textField("");

        settings.add(labeledField("Source column span", sourceColumnSpan));
        settings.add(labeledField("Final column span", finalColumnSpan));
        settings.add(darkluaRow());
        return settings;
    }

    private JPanel labeledField(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        panel.setBackground(PANEL);
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(field, BorderLayout.EAST);
        return panel;
    }

    private JPanel darkluaRow() {
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        panel.setBackground(PANEL);
        panel.add(new JLabel("Darklua path"), BorderLayout.NORTH);
        panel.add(darkluaPath, BorderLayout.CENTER);
        JButton browse = new JButton("Browse");
        browse.addActionListener(event -> chooseDarklua());
        panel.add(browse, BorderLayout.EAST);
        return panel;
    }

    private JTextField textField(String value) {
        JTextField field = new JTextField(value);
        field.setPreferredSize(new Dimension(86, 26));
        return field;
    }

    private JButton button(String text, final Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(event -> action.run());
        return button;
    }

    private void openSource() {
        JFileChooser chooser = luaFileChooser();
        if (chooser.showOpenDialog(source) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            source.setText(new String(Files.readAllBytes(chooser.getSelectedFile().toPath()), StandardCharsets.UTF_8));
            setStatus("Source loaded");
        } catch (IOException e) {
            showError("Unable to open source", e);
        }
    }

    private void saveOutput() {
        JFileChooser chooser = luaFileChooser();
        chooser.setSelectedFile(new File("Xell.out.lua"));
        if (chooser.showSaveDialog(output) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            Files.write(chooser.getSelectedFile().toPath(), output.getText().getBytes(StandardCharsets.UTF_8));
            setStatus("Output saved");
        } catch (IOException e) {
            showError("Unable to save output", e);
        }
    }

    private JFileChooser luaFileChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Lua files", "lua", "luau", "txt"));
        return chooser;
    }

    private void chooseDarklua() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Darklua executable", "exe"));
        if (chooser.showOpenDialog(source) == JFileChooser.APPROVE_OPTION) {
            darkluaPath.setText(chooser.getSelectedFile().getAbsolutePath());
            setStatus("Darklua path set");
        }
    }

    private void copyOutput() {
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(output.getText()), null);
        setStatus("Output copied");
    }

    private void obfuscateSource() {
        setStatus("Obfuscating");
        output.setText("");
        updateBytes();
        obfuscate.setEnabled(false);
        final XellObfuscatorSettings settings;
        try {
            settings = currentSettings();
        } catch (IllegalArgumentException e) {
            obfuscate.setEnabled(true);
            setStatus(e.getMessage());
            return;
        }

        SwingWorker<Obfuscator.ObfuscationResult, Void> worker = new SwingWorker<Obfuscator.ObfuscationResult, Void>() {
            @Override
            protected Obfuscator.ObfuscationResult doInBackground() {
                return Obfuscator.runObfuscation(source.getText(), obfuscating, settings);
            }

            @Override
            protected void done() {
                obfuscate.setEnabled(true);
                try {
                    Obfuscator.ObfuscationResult result = get();
                    if (result.ok()) {
                        output.setText(result.body);
                        updateBytes();
                        setStatus("Complete");
                    } else {
                        setStatus(result.body);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    setStatus("Interrupted");
                } catch (ExecutionException e) {
                    showError("Obfuscation failed", e);
                }
            }
        };
        worker.execute();
    }

    private XellObfuscatorSettings currentSettings() {
        return XellObfuscatorSettings.fromText(
                sourceColumnSpan.getText(),
                finalColumnSpan.getText(),
                darkluaPath.getText()
        );
    }

    private void updateBytes() {
        bytes.setText("Output bytes: " + output.getText().getBytes(StandardCharsets.UTF_8).length);
    }

    private void setStatus(String value) {
        status.setText(value == null || value.isEmpty() ? "Ready" : value);
    }

    private void showError(String title, Exception e) {
        String message = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
        JOptionPane.showMessageDialog(output, message, title, JOptionPane.ERROR_MESSAGE);
        setStatus(title);
    }
}
