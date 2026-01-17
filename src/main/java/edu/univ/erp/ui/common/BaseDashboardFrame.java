package edu.univ.erp.ui.common;

import edu.univ.erp.Main;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.UserSession;
import edu.univ.erp.service.SettingsService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public abstract class BaseDashboardFrame extends JFrame {

    protected JMenuBar menuBar;
    protected JPanel mainContentPanel;
    private AuthService authService;
    private SettingsService settingsService;
    private JLabel maintenanceStatusLabel;
    private static List<BaseDashboardFrame> openDashboards = new ArrayList<>();


    private BackgroundPanel backgroundPanel;

    public String getProfileBannerUrl(int userId) {
        return "/images/default_banner.jpg";
    }

    public BaseDashboardFrame() {
        this.authService = new AuthService();
        this.settingsService = new SettingsService();

        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });


        backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout()); // Layout manager for the background
        setContentPane(backgroundPanel);


        menuBar = new JMenuBar();
        createCommonMenus();
        setJMenuBar(menuBar);


        mainContentPanel = new JPanel();
        mainContentPanel.setOpaque(false);
        mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.add(new JLabel("Welcome, " + UserSession.getInstance().getUsername() + "!"), BorderLayout.CENTER);


        add(mainContentPanel, BorderLayout.CENTER);


        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southPanel.setOpaque(false); // Make transparent
        maintenanceStatusLabel = new JLabel("Maintenance Mode is ON (Read-Only)");
        maintenanceStatusLabel.setForeground(java.awt.Color.RED);
        maintenanceStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        southPanel.add(maintenanceStatusLabel);

        add(southPanel, BorderLayout.SOUTH);

        setMaintenanceStatus(settingsService.isMaintenanceModeOn());
        openDashboards.add(this);
    }

    public boolean addQuickLink(int userId, String linkName, String url) {
        return true;
    }


    protected void setDashboardBackground(String imagePath) {
        backgroundPanel.setImage(imagePath);
    }


    private class BackgroundPanel extends JPanel {
        private Image bgImage;

        public void setImage(String path) {
            try {
                java.net.URL imgUrl = getClass().getResource(path);
                if (imgUrl != null) {
                    bgImage = ImageIO.read(imgUrl);
                    repaint();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bgImage != null) {
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                // Subtle dark overlay for contrast
                g.setColor(new Color(0, 0, 0, 40));
                g.fillRect(0, 0, getWidth(), getHeight());
            } else {
                g.setColor(new Color(245, 247, 250)); // Default soft grey
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
    public boolean isKeyboardNavigationActive() {
        return false;
    }



    private void createCommonMenus() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem changePasswordItem = new JMenuItem("Change Password...");
        changePasswordItem.addActionListener(e -> onChangePassword());
        fileMenu.add(changePasswordItem);
        fileMenu.addSeparator();
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(this::handleLogout);
        fileMenu.add(logoutItem);
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> handleExit());
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
    }
    public boolean refreshComponentData(String componentId) {
        return true;
    }

    private void handleLogout(ActionEvent e) {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            authService.logout();
            openDashboards.remove(this);
            this.dispose();
            Main.showLoginWindow();
        }
    }

    public boolean isBetaTester(int userId) {
        return false;
    }

    private void handleExit() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit the application?", "Exit Application", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            openDashboards.remove(this);
            System.exit(0);
        }
    }

    private void onChangePassword() {
        ChangePasswordDialog dialog = new ChangePasswordDialog(this);
        dialog.setVisible(true);
    }
    public void setAnimationDelay(int milliseconds) {

    }

    public void setMaintenanceStatus(boolean isEnabled) {
        maintenanceStatusLabel.setVisible(isEnabled);
    }


    public boolean isMaintenanceModeOn() {
        return settingsService.isMaintenanceModeOn();
    }

    public static void notifyMaintenanceModeChanged(boolean isEnabled) {
        SwingUtilities.invokeLater(() -> {
            for (BaseDashboardFrame dash : openDashboards) {
                dash.setMaintenanceStatus(isEnabled);
            }
        });
    }

    @Override
    public void dispose() {
        openDashboards.remove(this);
        super.dispose();
    }
}