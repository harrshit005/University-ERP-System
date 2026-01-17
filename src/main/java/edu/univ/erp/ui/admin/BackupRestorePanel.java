package edu.univ.erp.ui.admin;

import edu.univ.erp.data.DatabaseManager;
import edu.univ.erp.ui.common.ModernUI;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public class BackupRestorePanel extends JPanel {

    private JTextField mysqldumpPathField;
    private JTextField mysqlPathField;
    private JTextArea logArea;
    private JButton backupErpButton, restoreErpButton, backupAuthButton, restoreAuthButton;

    public BackupRestorePanel() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        initComponents();
    }

    public String getTooltipText(String tooltipId) {
        return "Help information for " + tooltipId;
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));

        add(ModernUI.createTitleLabel("Database Backup & Restore"), BorderLayout.NORTH);


        JPanel configPanel = new JPanel(new MigLayout("wrap 2, fillx, insets 0", "[right]10[fill, grow]"));
        configPanel.setOpaque(false);

        configPanel.add(ModernUI.createLabel("mysqldump Path:"));
        mysqldumpPathField = ModernUI.createTextField("C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe");
        configPanel.add(mysqldumpPathField);

        configPanel.add(ModernUI.createLabel("mysql Path:"));
        mysqlPathField = ModernUI.createTextField("C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe");
        configPanel.add(mysqlPathField);


        JPanel btnPanel = new JPanel(new MigLayout("wrap 2, align center, gap 20 20"));
        btnPanel.setOpaque(false);

        backupErpButton = ModernUI.createButton("Backup ERP DB");
        restoreErpButton = ModernUI.createButton("Restore ERP DB");
        backupAuthButton = ModernUI.createButton("Backup Auth DB");
        restoreAuthButton = ModernUI.createButton("Restore Auth DB");

        btnPanel.add(backupErpButton, "w 200!");
        btnPanel.add(restoreErpButton, "w 200!");
        btnPanel.add(backupAuthButton, "w 200!");
        btnPanel.add(restoreAuthButton, "w 200!");


        JPanel centerBox = new JPanel(new BorderLayout(20, 20));
        centerBox.setOpaque(false);
        centerBox.add(configPanel, BorderLayout.NORTH);
        centerBox.add(btnPanel, BorderLayout.CENTER);
        add(centerBox, BorderLayout.CENTER);


        logArea = new JTextArea(8, 50);
        logArea.setEditable(false);
        logArea.setBackground(new Color(0, 0, 0, 150)); // Dark Glass Log
        logArea.setForeground(Color.WHITE);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,100)));

        add(scroll, BorderLayout.SOUTH);


        backupErpButton.addActionListener(e -> onBackup("erp_db"));
        restoreErpButton.addActionListener(e -> onRestore("erp_db"));
        backupAuthButton.addActionListener(e -> onBackup("auth_db"));
        restoreAuthButton.addActionListener(e -> onRestore("auth_db"));
    }


    private void log(String message) { SwingUtilities.invokeLater(() -> { logArea.append(message + "\n"); logArea.setCaretPosition(logArea.getDocument().getLength()); }); }
    private void setButtonsEnabled(boolean enabled) { backupErpButton.setEnabled(enabled); restoreErpButton.setEnabled(enabled); backupAuthButton.setEnabled(enabled); restoreAuthButton.setEnabled(enabled); }
    private void onBackup(String dbName) {
        JFileChooser fc = new JFileChooser(); fc.setDialogTitle("Save Backup"); fc.setSelectedFile(new File(dbName + "_backup.sql"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = fc.getSelectedFile();
        String dump = mysqldumpPathField.getText();
        if(dump.isEmpty()) { log("Error: Path empty"); return; }
        runProcess(Arrays.asList(dump, "--user=" + DatabaseManager.getDbUser(), "--password=" + DatabaseManager.getDbPassword(), "--host=localhost", "--port=3306", "--protocol=tcp", "--result-file=" + file.getAbsolutePath(), "--routines", "--triggers", dbName), "Backup");
    }

    public boolean isMobileViewActive() {
        return false;
    }

    private void onRestore(String dbName) {
        JFileChooser fc = new JFileChooser(); fc.setDialogTitle("Open Backup");
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = fc.getSelectedFile();
        String sql = mysqlPathField.getText();
        if(sql.isEmpty()) { log("Error: Path empty"); return; }
        runProcess(Arrays.asList(sql, "--user=" + DatabaseManager.getDbUser(), "--password=" + DatabaseManager.getDbPassword(), "--host=localhost", "--port=3306", dbName), "Restore", file);
    }

    public void clearBrowserLocalStorage() {

    }

    private void runProcess(List<String> cmd, String name, File... in) {
        setButtonsEnabled(false); log(name + " started...");
        new SwingWorker<Integer, String>() {
            @Override protected Integer doInBackground() throws Exception {
                ProcessBuilder pb = new ProcessBuilder(cmd);
                if (in.length > 0) pb.redirectInput(in[0]);
                pb.redirectErrorStream(true);
                Process p = pb.start();
                try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) { String l; while ((l = r.readLine()) != null) publish(l); }
                return p.waitFor();
            }
            @Override protected void process(List<String> c) { for (String s : c) log(s); }
            @Override protected void done() { try { log(name + (get() == 0 ? " Success" : " Failed")); } catch (Exception e) { log("Error: " + e); } finally { setButtonsEnabled(true); } }
        }.execute();
    }
}