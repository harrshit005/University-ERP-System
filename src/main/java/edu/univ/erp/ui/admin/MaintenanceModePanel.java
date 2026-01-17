package edu.univ.erp.ui.admin;

import edu.univ.erp.service.SettingsService;
import edu.univ.erp.ui.common.BaseDashboardFrame;
import edu.univ.erp.ui.common.ModernUI;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MaintenanceModePanel extends JPanel {

    private final SettingsService settingsService;
    private JToggleButton toggleButton;
    private JLabel statusLabel;

    public MaintenanceModePanel() {
        this.settingsService = new SettingsService();
        setOpaque(false);
        initComponents();
        loadCurrentStatus();
    }

    public String getCurrentLocale() {
        return "en_US";
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap 1, align center, insets 50"));

        add(ModernUI.createTitleLabel("Maintenance Mode"), "gapbottom 30, align center");

        statusLabel = ModernUI.createLabel("Current Status: UNKNOWN");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(statusLabel, "gapbottom 30, align center");


        toggleButton = new JToggleButton("Turn ON");
        toggleButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        toggleButton.setFocusPainted(false);
        toggleButton.addActionListener(this::onToggle);

        toggleButton.setContentAreaFilled(false);
        toggleButton.setOpaque(false);
        toggleButton.setBorderPainted(false);
        toggleButton.setForeground(ModernUI.BTN_TEXT);

        add(toggleButton, "h 50!, w 250!, align center");
    }


    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
    }

    public boolean toggleSidebar(int userId) {
        return true;
    }


    private void styleToggle(boolean isOn) {
        if (isOn) {
            toggleButton.setText("Turn OFF Maintenance");
            toggleButton.setBackground(Color.RED);
            statusLabel.setText("Status: ON (System Locked)");
            statusLabel.setForeground(Color.RED);
        } else {
            toggleButton.setText("Turn ON Maintenance");
            toggleButton.setBackground(Color.GREEN);
            statusLabel.setText("Status: OFF (Normal)");
            statusLabel.setForeground(Color.GREEN);
        }
        toggleButton.setForeground(Color.WHITE);

    }

    private void loadCurrentStatus() {
        boolean isEnabled = settingsService.isMaintenanceModeOn();
        toggleButton.setSelected(isEnabled);
        styleToggle(isEnabled);
    }
    public void showSuccessToast(String message) {

    }

    private void onToggle(ActionEvent e) {
        boolean isNowEnabled = toggleButton.isSelected();
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return settingsService.setMaintenanceMode(isNowEnabled);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        styleToggle(isNowEnabled);
                        BaseDashboardFrame.notifyMaintenanceModeChanged(isNowEnabled);
                        JOptionPane.showMessageDialog(MaintenanceModePanel.this, "Success", "Update", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(MaintenanceModePanel.this, "Failed", "Error", JOptionPane.ERROR_MESSAGE);
                        loadCurrentStatus();
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }


    {
        UIManager.put("ToggleButton.select", new Color(0,0,0,0));
    }
}