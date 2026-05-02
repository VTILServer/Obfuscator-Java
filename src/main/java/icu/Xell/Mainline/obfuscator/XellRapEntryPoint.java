package icu.Xell.Mainline.obfuscator;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.util.concurrent.atomic.AtomicBoolean;

public final class XellRapEntryPoint implements EntryPoint {
    static final String DEFAULT_SOURCE = "x=1\nprint(\"test int:\", x)";

    private final AtomicBoolean obfuscating = new AtomicBoolean(false);
    private Text sourceColumnSpan;
    private Text finalColumnSpan;
    private Text darkluaPath;

    @Override
    public int createUI() {
        Display display = Display.getDefault();
        Shell shell = new Shell(display);
        shell.setText("Xell Obfuscator");
        shell.setMaximized(true);
        shell.setLayout(new GridLayout(1, false));

        createHeader(shell);
        createToolSurface(shell);

        shell.open();
        return 0;
    }

    private void createHeader(Composite parent) {
        Composite header = new Composite(parent, SWT.NONE);
        header.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        header.setLayout(new GridLayout(2, false));

        Label title = new Label(header, SWT.NONE);
        title.setText("Xell Obfuscator");
        title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label mode = new Label(header, SWT.RIGHT);
        mode.setText("Eclipse RAP");
        mode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    }

    private void createToolSurface(Composite parent) {
        Composite root = new Composite(parent, SWT.NONE);
        root.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        root.setLayout(new GridLayout(3, false));

        Text source = createEditor(root, "Source", DEFAULT_SOURCE);
        Text output = createEditor(root, "Output", "");
        output.setEditable(false);

        Composite side = createSidePanel(root);
        Label status = new Label(side, SWT.WRAP);
        status.setText("Ready");
        status.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        Label bytes = new Label(side, SWT.NONE);
        bytes.setText("Output bytes: 0");
        bytes.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        createSettings(side);
        createActions(side, source, output, status, bytes);
    }

    private Text createEditor(Composite parent, String titleText, String value) {
        Composite pane = new Composite(parent, SWT.BORDER);
        pane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        pane.setLayout(new GridLayout(1, false));

        Label title = new Label(pane, SWT.NONE);
        title.setText(titleText);
        title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Text editor = new Text(pane, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        editor.setText(value);
        editor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        return editor;
    }

    private Composite createSidePanel(Composite parent) {
        Composite side = new Composite(parent, SWT.BORDER);
        GridData data = new GridData(SWT.FILL, SWT.FILL, false, true);
        data.widthHint = 280;
        side.setLayoutData(data);
        side.setLayout(new GridLayout(1, false));

        Label title = new Label(side, SWT.NONE);
        title.setText("Controls");
        title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        return side;
    }

    private void createSettings(Composite side) {
        Composite settings = new Composite(side, SWT.NONE);
        settings.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        settings.setLayout(new GridLayout(2, false));

        Label title = new Label(settings, SWT.NONE);
        title.setText("Settings");
        title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        sourceColumnSpan = settingText(settings, "Source column span", Integer.toString(XellObfuscatorSettings.DEFAULT_COLUMN_SPAN));
        finalColumnSpan = settingText(settings, "Final column span", Integer.toString(XellObfuscatorSettings.DEFAULT_COLUMN_SPAN));

        Label darklua = new Label(settings, SWT.NONE);
        darklua.setText("Darklua path");
        darklua.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        darkluaPath = new Text(settings, SWT.BORDER);
        darkluaPath.setMessage("PATH/default");
        darkluaPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    }

    private Text settingText(Composite parent, String label, String value) {
        Label settingLabel = new Label(parent, SWT.NONE);
        settingLabel.setText(label);
        settingLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Text setting = new Text(parent, SWT.BORDER);
        setting.setText(value);
        GridData data = new GridData(SWT.FILL, SWT.CENTER, false, false);
        data.widthHint = 76;
        setting.setLayoutData(data);
        return setting;
    }

    private void createActions(Composite side, Text source, Text output, Label status, Label bytes) {
        Composite actions = new Composite(side, SWT.NONE);
        actions.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        actions.setLayout(new FillLayout(SWT.VERTICAL));

        Button sample = new Button(actions, SWT.PUSH);
        sample.setText("Sample");
        sample.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                source.setText("local total=0\nfor i=1,5 do\n\ttotal=total+i\nend\nprint(\"sum\", total)");
                status.setText("Sample loaded");
            }
        });

        Button run = new Button(actions, SWT.PUSH);
        run.setText("Obfuscate");
        run.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                status.setText("Obfuscating");
                output.setText("");
                bytes.setText("Output bytes: 0");
                XellObfuscatorSettings settings;
                try {
                    settings = currentSettings();
                } catch (IllegalArgumentException e) {
                    status.setText(e.getMessage());
                    return;
                }
                Obfuscator.ObfuscationResult result = Obfuscator.runObfuscation(source.getText(), obfuscating, settings);
                if (result.ok()) {
                    output.setText(result.body);
                    bytes.setText("Output bytes: " + result.body.length());
                    status.setText("Complete");
                } else {
                    status.setText(result.body);
                }
            }
        });

        Button clear = new Button(actions, SWT.PUSH);
        clear.setText("Clear output");
        clear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                output.setText("");
                bytes.setText("Output bytes: 0");
                status.setText("Ready");
            }
        });

        Button select = new Button(actions, SWT.PUSH);
        select.setText("Select output");
        select.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                output.setFocus();
                output.selectAll();
                status.setText("Output selected");
            }
        });
    }

    private XellObfuscatorSettings currentSettings() {
        return XellObfuscatorSettings.fromText(
                sourceColumnSpan.getText(),
                finalColumnSpan.getText(),
                darkluaPath.getText()
        );
    }
}
