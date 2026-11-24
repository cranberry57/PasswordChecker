import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Color BG = new Color(18, 24, 38);
    private static final Color PANEL = new Color(29, 38, 56);
    private static final Color ACCENT = new Color(92, 124, 250);
    private static final Color TEXT = new Color(238, 242, 255);
    private static final Color MUTED = new Color(165, 176, 199);

    private JFrame frame;
    private JPasswordField passwordField;
    private JCheckBox showPassword;
    private JLabel strengthLabel;
    private JProgressBar strengthBar;
    private JTextArea checklist;
    private JTextArea suggestions;
    private JSlider lengthSlider;
    private JSpinner countSpinner;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().createAndShow());
    }

    private void createAndShow() {
        frame = new JFrame("Password Checker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(820, 860);
        frame.setMinimumSize(new Dimension(700, 650));
        frame.setLocationRelativeTo(null);
        frame.setContentPane(buildRoot());
        frame.setVisible(true);
    }

    JPanel buildRoot() {

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("Password Checker");
        title.setForeground(TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        JLabel subtitle = new JLabel("Check password strength and create a stronger one.");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);
        root.add(heading, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel checkPanel = panel("CHECK YOUR PASSWORD");
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        passwordField.setBackground(new Color(15, 20, 32));
        passwordField.setForeground(TEXT);
        passwordField.setCaretColor(TEXT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(67, 80, 108)),
            new EmptyBorder(12, 12, 12, 12)));

        showPassword = new JCheckBox("Show password");
        showPassword.setOpaque(false);
        showPassword.setForeground(MUTED);
        showPassword.addActionListener(e -> passwordField.setEchoChar(showPassword.isSelected() ? (char) 0 : '•'));

        JButton checkButton = button("Check Password");
        checkButton.addActionListener(e -> checkCurrentPassword());
        passwordField.addActionListener(e -> checkCurrentPassword());

        strengthLabel = new JLabel("Strength: Not checked");
        strengthLabel.setForeground(TEXT);
        strengthLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        strengthBar = new JProgressBar(0, 5);
        strengthBar.setValue(0);
        strengthBar.setStringPainted(true);
        strengthBar.setString("0 / 5");
        strengthBar.setForeground(ACCENT);
        strengthBar.setBackground(new Color(15, 20, 32));
        strengthBar.setPreferredSize(new Dimension(100, 22));

        checklist = textArea();
        suggestions = textArea();

        checkPanel.add(passwordField);
        checkPanel.add(showPassword);
        checkPanel.add(checkButton);
        checkPanel.add(Box.createVerticalStrut(8));
        checkPanel.add(strengthLabel);
        checkPanel.add(strengthBar);
        checkPanel.add(sectionLabel("Requirements"));
        checkPanel.add(checklist);
        checkPanel.add(sectionLabel("Suggestions"));
        checkPanel.add(suggestions);

        JPanel generatorPanel = panel("PASSWORD GENERATOR");
        JLabel lengthLabel = new JLabel("Password length: 16");
        lengthLabel.setForeground(TEXT);
        lengthSlider = new JSlider(8, 32, 16);
        lengthSlider.setBackground(PANEL);
        lengthSlider.setForeground(TEXT);
        lengthSlider.setMajorTickSpacing(8);
        lengthSlider.setMinorTickSpacing(1);
        lengthSlider.setPaintTicks(true);
        lengthSlider.setPaintLabels(true);
        lengthSlider.addChangeListener(e -> lengthLabel.setText("Password length: " + lengthSlider.getValue()));

        JTextField generatedField = new JTextField();
        generatedField.setEditable(false);
        generatedField.setFont(new Font("Monospaced", Font.PLAIN, 15));
        generatedField.setBackground(new Color(15, 20, 32));
        generatedField.setForeground(TEXT);
        generatedField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(67, 80, 108)),
            new EmptyBorder(10, 10, 10, 10)));

        JButton generateButton = button("Generate Password");
        JButton copyButton = button("Copy");
        generateButton.addActionListener(e -> generatedField.setText(PasswordGenerator.generate(lengthSlider.getValue())));
        copyButton.addActionListener(e -> {
            if (!generatedField.getText().isEmpty()) {
                Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(generatedField.getText()), null);
                JOptionPane.showMessageDialog(frame, "Generated password copied.");
            }
        });

        JPanel generatorButtons = new JPanel(new GridLayout(1, 2, 10, 0));
        generatorButtons.setOpaque(false);
        generatorButtons.add(generateButton);
        generatorButtons.add(copyButton);

        lengthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        lengthSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        generatedField.setAlignmentX(Component.LEFT_ALIGNMENT);
        generatorButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
        generatedField.setMaximumSize(new Dimension(Integer.MAX_VALUE, generatedField.getPreferredSize().height));
        generatorButtons.setMaximumSize(new Dimension(Integer.MAX_VALUE, generatorButtons.getPreferredSize().height));

        generatorPanel.add(lengthLabel);
        generatorPanel.add(lengthSlider);
        generatorPanel.add(generatedField);
        generatorPanel.add(Box.createVerticalStrut(8));
        generatorPanel.add(generatorButtons);

        // Generate many passwords and send them straight into the batch checker.
        generatorPanel.add(Box.createVerticalStrut(16));
        generatorPanel.add(sectionLabel("Generate for batch check"));
        JPanel countRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        countRow.setOpaque(false);
        countRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        countSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100000, 1));
        countRow.add(mutedLabel("How many passwords?"));
        countRow.add(countSpinner);
        countRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, countRow.getPreferredSize().height));
        generatorPanel.add(countRow);
        generatorPanel.add(Box.createVerticalStrut(8));
        JButton toBatchButton = button("Generate Into Batch Check");
        toBatchButton.addActionListener(e -> generateIntoBatch());
        generatorPanel.add(toBatchButton);

        content.add(checkPanel);
        content.add(Box.createVerticalStrut(16));
        content.add(generatorPanel);
        content.add(Box.createVerticalStrut(16));
        content.add(buildBatchPanel());

        // Line everything up on the left edge so the panels look even.
        for (Component panelComp : content.getComponents()) {
            if (panelComp instanceof JPanel) {
                for (Component child : ((JPanel) panelComp).getComponents()) {
                    if (child instanceof JComponent) ((JComponent) child).setAlignmentX(Component.LEFT_ALIGNMENT);
                }
            }
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        root.add(scroll, BorderLayout.CENTER);

        JLabel footer = new JLabel("Your password is checked locally in this application. Do not reuse passwords.");
        footer.setForeground(MUTED);
        footer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        root.add(footer, BorderLayout.SOUTH);

        return root;
    }

    private JPanel panel(String heading) {
        JPanel p = new JPanel();
        p.setBackground(PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(49, 61, 85)),
            new EmptyBorder(16, 16, 16, 16)));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel h = new JLabel(heading);
        h.setForeground(new Color(140, 164, 255));
        h.setFont(new Font("SansSerif", Font.BOLD, 12));
        h.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(h);
        p.add(Box.createVerticalStrut(12));
        return p;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setBorder(new EmptyBorder(10, 0, 5, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextArea textArea() {
        JTextArea area = new JTextArea(5, 30);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        area.setBackground(PANEL);
        area.setForeground(TEXT);
        area.setBorder(new EmptyBorder(4, 0, 4, 0));
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        return area;
    }

    private JButton button(String label) {
        JButton b = new JButton(label);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(ACCENT);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 14, 10, 14));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        return b;
    }

    // ---------- Parallel batch checker ----------

    private JTextArea batchInput;
    private JSpinner threadSpinner;
    private JSpinner delaySpinner;
    private JLabel batchStatus;
    private JButton batchButton;
    private DefaultTableModel batchModel;

    private JPanel buildBatchPanel() {
        JPanel p = panel("PARALLEL BATCH CHECK");

        JLabel hint = new JLabel("Enter sample passwords, one per line. They are checked at the same time.");
        hint.setForeground(MUTED);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(hint);
        p.add(Box.createVerticalStrut(8));

        batchInput = new JTextArea(6, 30);
        batchInput.setFont(new Font("Monospaced", Font.PLAIN, 13));
        batchInput.setBackground(new Color(15, 20, 32));
        batchInput.setForeground(TEXT);
        batchInput.setCaretColor(TEXT);
        batchInput.setBorder(new EmptyBorder(8, 8, 8, 8));
        batchInput.setText("password\nHello123\nMyDog#2024\nCorrect-Horse-Battery-9\nabc\n12345678");
        JScrollPane inputScroll = new JScrollPane(batchInput);
        inputScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputScroll.setBorder(BorderFactory.createLineBorder(new Color(67, 80, 108)));
        p.add(inputScroll);
        p.add(Box.createVerticalStrut(10));

        JPanel options = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        options.setOpaque(false);
        options.setAlignmentX(Component.LEFT_ALIGNMENT);
        int cores = Runtime.getRuntime().availableProcessors();
        threadSpinner = new JSpinner(new SpinnerNumberModel(Math.max(2, cores), 1, 16, 1));
        delaySpinner = new JSpinner(new SpinnerNumberModel(300, 0, 2000, 50));
        options.add(mutedLabel("Threads:"));
        options.add(threadSpinner);
        options.add(mutedLabel("Simulated work per password (ms):"));
        options.add(delaySpinner);
        p.add(options);
        p.add(Box.createVerticalStrut(10));

        batchButton = button("Check All in Parallel");
        batchButton.addActionListener(e -> runBatch());
        p.add(batchButton);
        p.add(Box.createVerticalStrut(10));

        batchStatus = new JLabel("Not run yet.");
        batchStatus.setForeground(TEXT);
        batchStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(batchStatus);
        p.add(Box.createVerticalStrut(8));

        batchModel = new DefaultTableModel(
                new Object[]{"#", "Password", "Score", "Strength", "Thread", "Time (ms)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(batchModel);
        table.setBackground(new Color(15, 20, 32));
        table.setForeground(TEXT);
        table.setGridColor(new Color(49, 61, 85));
        table.setRowHeight(24);
        table.getTableHeader().setBackground(PANEL);
        table.getTableHeader().setForeground(TEXT);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(600, 190));
        tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableScroll.getViewport().setBackground(new Color(15, 20, 32));
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(67, 80, 108)));
        p.add(tableScroll);
        return p;
    }

    private JLabel mutedLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(MUTED);
        return l;
    }

    private void generateIntoBatch() {
        int count = (Integer) countSpinner.getValue();
        int length = lengthSlider.getValue();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append('\n');
            sb.append(PasswordGenerator.generate(length));
        }
        batchInput.setText(sb.toString());
        batchInput.setCaretPosition(0);
        runBatch();
    }

    private void runBatch() {
        List<String> passwords = new ArrayList<>();
        for (String line : batchInput.getText().split("\\R")) {
            if (!line.isEmpty()) passwords.add(line);
        }
        if (passwords.isEmpty()) {
            batchStatus.setText("Type at least one password first.");
            return;
        }
        final int threads = (Integer) threadSpinner.getValue();
        final int delay = (Integer) delaySpinner.getValue();

        // Warn before a run that would take a long time with these settings.
        long estimatedMs = (long) Math.ceil(passwords.size() / (double) threads) * delay;
        if (estimatedMs > 20000) {
            int choice = JOptionPane.showConfirmDialog(frame,
                "With " + passwords.size() + " passwords, " + threads + " threads and " + delay
                    + " ms of simulated work, this will take about " + (estimatedMs / 1000) + " seconds.\n"
                    + "Tip: lower the simulated work to make it faster.\n\nRun it anyway?",
                "This may take a while", JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) return;
        }

        batchButton.setEnabled(false);
        batchStatus.setText("Checking " + passwords.size() + " passwords...");
        batchModel.setRowCount(0);

        // SwingWorker keeps the window responsive while the threads work.
        new SwingWorker<Object[], Void>() {
            @Override
            protected Object[] doInBackground() throws Exception {
                long t1 = System.nanoTime();
                List<BatchChecker.Entry> par = BatchChecker.checkParallel(passwords, threads, delay);
                long parMs = (System.nanoTime() - t1) / 1_000_000;

                // Running everything one at a time can take very long for big batches,
                // so for those we time a small sample and scale it up (an estimate).
                final int sampleSize = 20;
                boolean estimated = (long) passwords.size() * delay > 15000 && passwords.size() > sampleSize;
                long t2 = System.nanoTime();
                if (estimated) {
                    BatchChecker.checkSequential(passwords.subList(0, sampleSize), delay);
                } else {
                    BatchChecker.checkSequential(passwords, delay);
                }
                long seqMs = (System.nanoTime() - t2) / 1_000_000;
                if (estimated) seqMs = seqMs * passwords.size() / sampleSize;

                return new Object[]{par, parMs, seqMs, estimated};
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void done() {
                try {
                    Object[] out = get();
                    List<BatchChecker.Entry> entries = (List<BatchChecker.Entry>) out[0];
                    long parMs = (Long) out[1];
                    long seqMs = (Long) out[2];
                    boolean estimated = (Boolean) out[3];
                    // Add all rows at once so big batches don't freeze the table.
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    java.util.Vector<java.util.Vector> data = batchModel.getDataVector();
                    for (BatchChecker.Entry en : entries) {
                        java.util.Vector<Object> row = new java.util.Vector<>();
                        row.add(en.index + 1);
                        row.add(en.password);
                        row.add(en.result.score + " / 5");
                        row.add(en.result.strength);
                        row.add(en.threadName);
                        row.add(en.millis);
                        data.add(row);
                    }
                    batchModel.fireTableDataChanged();
                    batchStatus.setText(entries.size() + " passwords   |   Parallel: " + parMs
                        + " ms   |   One at a time: " + seqMs + " ms" + (estimated ? " (estimated)" : "")
                        + "   |   " + threads + " threads");
                } catch (Exception ex) {
                    batchStatus.setText("Something went wrong: " + ex.getMessage());
                } finally {
                    batchButton.setEnabled(true);
                }
            }
        }.execute();
    }

    private void checkCurrentPassword() {
        String password = new String(passwordField.getPassword());
        PasswordChecker.Result result = PasswordChecker.checkPassword(password);
        strengthLabel.setText("Strength: " + result.strength);
        strengthBar.setValue(result.score);
        strengthBar.setString(result.score + " / 5");
        strengthBar.setForeground(result.score <= 2 ? new Color(239, 83, 80)
            : result.score <= 3 ? new Color(255, 183, 77) : new Color(102, 187, 106));
        checklist.setText(String.join("\n", result.checks));
        suggestions.setText(String.join("\n", result.suggestions));
    }
}
