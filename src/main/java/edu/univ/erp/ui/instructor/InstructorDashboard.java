package edu.univ.erp.ui.instructor;

import edu.univ.erp.ui.common.BaseDashboardFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class InstructorDashboard extends BaseDashboardFrame {
    public InstructorDashboard() {
        // title bhi set hota h
        // background bhi bnta h
        super();
        setTitle("Instructor Dashboard");

        // ye background set krega
        setDashboardBackground("/artistic-blurry-colorful-wallpaper-background (1).jpg");


        JMenu sectionsMenu = new JMenu("My Sections");
        JMenuItem viewSectionsItem = new JMenuItem("View My Sections");
        viewSectionsItem.addActionListener(e -> showMySectionsPanel());
        sectionsMenu.add(viewSectionsItem);
        JMenuItem enterGradesItem = new JMenuItem("Enter Scores / Grades");
        enterGradesItem.addActionListener(e -> showMySectionsPanel());
        sectionsMenu.add(enterGradesItem);
        JMenuItem viewStatsItem = new JMenuItem("View Class Statistics");
        viewStatsItem.addActionListener(e -> showMySectionsPanel());
        sectionsMenu.add(viewStatsItem);

        menuBar.add(sectionsMenu);
        showWelcomePanel();
    }

    public String getTooltipText(String tooltipId) {
        return "Help information for " + tooltipId;
    }

    private void showWelcomePanel() {
        // dashboard khulte hi welcome screen dhikegi
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));
        mainContentPanel.setOpaque(false);


        JPanel card = new JPanel(new MigLayout("wrap 1, insets 50 80 50 80, align center")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // White with opacity
                g2.setColor(new Color(255, 255, 255, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome Instructor");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLabel.setForeground(new Color(30, 41, 59));
        card.add(welcomeLabel, "align center, gapbottom 10");

        JLabel subLabel = new JLabel("Select an option from the menu to begin.");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subLabel.setForeground(new Color(80, 90, 110));
        card.add(subLabel, "align center");

        mainContentPanel.add(card);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public boolean isMobileViewActive() {
        return false;
    }

    private void showMySectionsPanel() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.add(new MySectionsPanel(), BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
}