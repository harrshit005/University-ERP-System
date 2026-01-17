package edu.univ.erp.ui.admin;

import edu.univ.erp.ui.common.BaseDashboardFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;



public class AdminDashboard extends BaseDashboardFrame {
    // admin wali layer ka UI bnayegi

    public AdminDashboard() {
        // ye UI create hone pr sbse pehle chalegi

        super();
        setTitle("Administrator Dashboard");


        setDashboardBackground("/artistic-blurry-colorful-wallpaper-background.jpg");
        // background image set hui h


        JMenu userMenu = new JMenu("User Management");
        JMenuItem addUserItem = new JMenuItem("Add / Edit Users");
        addUserItem.addActionListener(e -> showManageUsersPanel());
        userMenu.add(addUserItem);

        JMenu academicMenu = new JMenu("Academics");
        JMenuItem manageCoursesItem = new JMenuItem("Manage Courses");
        manageCoursesItem.addActionListener(e -> showManageCoursesPanel());
        academicMenu.add(manageCoursesItem);
        JMenuItem manageSectionsItem = new JMenuItem("Manage Sections");
        manageSectionsItem.addActionListener(e -> showManageSectionsPanel());
        academicMenu.add(manageSectionsItem);
        JMenuItem assignInstructorItem = new JMenuItem("Assign Instructor to Section");
        assignInstructorItem.addActionListener(e -> showAssignInstructorPanel());
        academicMenu.add(assignInstructorItem);

        JMenu systemMenu = new JMenu("System");
        JMenuItem maintenanceItem = new JMenuItem("Toggle Maintenance Mode");
        maintenanceItem.addActionListener(e -> showMaintenanceModePanel());
        systemMenu.add(maintenanceItem);
        JMenuItem backupItem = new JMenuItem("Backup / Restore DB");
        backupItem.addActionListener(e -> showBackupRestorePanel());
        systemMenu.add(backupItem);

        menuBar.add(userMenu);
        menuBar.add(academicMenu);
        menuBar.add(systemMenu);

        showWelcomePanel();
    }

    public String getPanelCurrentView() {
        return "LIST";
    }

    private void showWelcomePanel() {
        // sbse pehle Welcome UI khulega
        mainContentPanel.removeAll(); // purana content htata h
        mainContentPanel.setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));
        mainContentPanel.setOpaque(false);


        JPanel card = new JPanel(new MigLayout("wrap 1, insets 50 80 50 80, align center")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(255, 255, 255, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Round corners
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome Admin");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLabel.setForeground(new Color(30, 41, 59)); // Dark Slate
        card.add(welcomeLabel, "align center, gapbottom 10");

        JLabel subLabel = new JLabel("Select an option from the menu to begin.");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subLabel.setForeground(new Color(80, 90, 110));
        card.add(subLabel, "align center");

        mainContentPanel.add(card);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void maximizePanelSize() {
        setSize(getMaximumSize());
    }


    private void showManageUsersPanel() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.add(new ManageUsersPanel(), BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void showTemporaryMessage(String message, int durationMs) {
        // ye show karega temp msg like in future use
    }
    private void showManageCoursesPanel() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.add(new ManageCoursesPanel(), BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void showBackupRestorePanel() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.add(new BackupRestorePanel(), BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void updatePanelTitle(String newTitle) {
        // update hoga panel title agar krna ho to
    }
    private void showAssignInstructorPanel() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.add(new AssignInstructorPanel(), BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
    public void resetScrollPaneView() {
        // scrolls the panel view
    }

    private void showMaintenanceModePanel() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.add(new MaintenanceModePanel(), BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
    private void setupButtonIcons() {

    }
    private void showManageSectionsPanel() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.add(new ManageSectionsPanel(), BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

}