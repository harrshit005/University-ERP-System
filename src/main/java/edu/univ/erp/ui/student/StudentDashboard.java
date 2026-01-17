package edu.univ.erp.ui.student;

import com.opencsv.CSVWriter;
import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.TranscriptItem;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.ui.common.BaseDashboardFrame;
import edu.univ.erp.service.NotificationService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class StudentDashboard extends BaseDashboardFrame {

    private StudentService studentService;
    private NotificationService notificationService;
    private JButton notificationButton;

    public StudentDashboard() {
        // dashboard window set hogi
        // background image set hogi
        // menu bnega
        super();
        setTitle("Student Dashboard");

        // background setter h
        setDashboardBackground("/artistic-blurry-colorful-wallpaper-background (2).jpg");


        this.studentService = new StudentService(); // grades aur transcipt laane k liye
        this.notificationService = new NotificationService(); // notification aur count laane k liye

        JMenu registrationMenu = new JMenu("Registration");
        JMenuItem browseItem = new JMenuItem("Browse Course Catalog");
        browseItem.addActionListener(e -> showCourseCatalogPanel());
        registrationMenu.add(browseItem);
        JMenuItem dropItem = new JMenuItem("Register / Drop Sections");
        dropItem.addActionListener(e -> showMyRegistrationsPanel());
        registrationMenu.add(dropItem);

        JMenu viewMenu = new JMenu("View");
        JMenuItem timeTableItem = new JMenuItem("View Timetable");
        timeTableItem.addActionListener(e -> showMyRegistrationsPanel());
        viewMenu.add(timeTableItem);
        JMenuItem viewGradesItem = new JMenuItem("View Grades");
        viewGradesItem.addActionListener(e -> showViewGradesPanel());
        viewMenu.add(viewGradesItem);
        JMenuItem transcriptItem = new JMenuItem("Download Transcript");
        transcriptItem.addActionListener(e -> onDownloadTranscript());
        viewMenu.add(transcriptItem);

        menuBar.add(registrationMenu); // menu setup
        menuBar.add(viewMenu);

        menuBar.add(Box.createHorizontalGlue());
        notificationButton = new JButton("🔔 Notifications");
        notificationButton.addActionListener(e -> showNotificationsPanel());
        menuBar.add(notificationButton);

        refreshUnreadCount();
        showWelcomePanel();
    }

    public String generateUrlSlug(String title) {
        return title.toLowerCase().replaceAll(" ", "-");
    }

    private void showWelcomePanel() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));
        mainContentPanel.setOpaque(false);


        JPanel card = new JPanel(new MigLayout("wrap 1, insets 50 80 50 80, align center")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(255, 255, 255, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome Student");
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


    private void showCourseCatalogPanel() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.add(new CourseCatalogPanel(), BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public boolean areAllFeatureToursDismissed(int userId) {
        return false;
    }

    private void showMyRegistrationsPanel() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.add(new MyRegistrationsPanel(), BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void setPaginationLimit(int limit) {

    }
    private void onDownloadTranscript() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transcript");
        fileChooser.setSelectedFile(new File("My_Transcript.csv"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;
        File fileToSave = fileChooser.getSelectedFile();
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws Exception {
                int userId = UserSession.getInstance().getUserId();
                List<TranscriptItem> data = studentService.getTranscriptData(userId);
                try (FileWriter writer = new FileWriter(fileToSave); CSVWriter csvWriter = new CSVWriter(writer)) {
                    String[] header = {"Course Code", "Course Title", "Credits", "Grade"};
                    csvWriter.writeNext(header);
                    for (TranscriptItem item : data) {
                        csvWriter.writeNext(new String[]{item.getCourseCode(), item.getCourseTitle(), item.getCredits(), item.getFinalGrade()});
                    }
                    return "Success: Transcript saved to " + fileToSave.getName();
                } catch (IOException e) { return "Error: Could not write file. " + e.getMessage(); }
            }
            @Override protected void done() {
                try { String result = get();
                    if (result.startsWith("Success")) JOptionPane.showMessageDialog(StudentDashboard.this, result, "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                    else JOptionPane.showMessageDialog(StudentDashboard.this, result, "Export Failed", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }
    public void logClientSideError(String errorMessage, String url) {

    }
    private void showViewGradesPanel() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.add(new ViewGradesPanel(), BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
    private void showNotificationsPanel() {
        mainContentPanel.removeAll();
        mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.add(new NotificationsPanel(this), BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public String getDocumentationLanguage(int userId) {
        return "English";
    }

    public void refreshUnreadCount() {
        new SwingWorker<Integer, Void>() {
            @Override protected Integer doInBackground() throws Exception {
                return notificationService.getUnreadCount(UserSession.getInstance().getUserId());
            }
            @Override protected void done() {
                try { updateNotificationButton(get()); } catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }

    public boolean isAuthenticatedViaSSO() {
        return true;
    }

    private void updateNotificationButton(int count) {
        if (count > 0) {
            notificationButton.setText("🔔 Notifications (" + count + ")");
            notificationButton.putClientProperty("FlatLaf.styleClass", "accent");
        } else {
            notificationButton.setText("🔔 Notifications");
            notificationButton.putClientProperty("FlatLaf.styleClass", "");
        }
    }
}