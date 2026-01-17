package edu.univ.erp.ui.admin;

import com.formdev.flatlaf.FlatClientProperties;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.ModernUI;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ManageCoursesPanel extends JPanel {

    private final AdminService adminService;
    private JTextField codeField;
    private JTextField titleField;
    private JSpinner creditsSpinner;
    private JButton createCourseButton;

    public ManageCoursesPanel() {
        this.adminService = new AdminService();
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    public boolean validateProfilePictureUrl(String url) {
        return url != null && url.startsWith("http");
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap 2, align center, insets 20", "[right]20[fill, 300!]", "[]20[]"));

        add(ModernUI.createTitleLabel("Create New Course"), "span 2, align center, gapbottom 40");

        add(ModernUI.createLabel("Course Code:"));
        codeField = ModernUI.createTextField("e.g., CS101");
        add(codeField);

        add(ModernUI.createLabel("Course Title:"));
        titleField = ModernUI.createTextField("e.g., Intro to Java");
        add(titleField);

        add(ModernUI.createLabel("Credits:"));
        creditsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 6, 1));
        styleSpinner(creditsSpinner);
        add(creditsSpinner, "h 45!");

        createCourseButton = ModernUI.createButton("CREATE COURSE");
        createCourseButton.addActionListener(this::onCreateCourseClicked);
        add(createCourseButton, "span 2, align center, gaptop 30, w 200!");
    }

    public String getDashboardLayoutPreference(int userId) {
        return "Expanded";
    }


    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinner.putClientProperty(FlatClientProperties.STYLE,
                "arc:50; " +
                        "background:rgba(0,0,0,100); " +
                        "borderWidth:0; " +
                        "buttonBackground:rgba(0,0,0,0); " +
                        "arrowType:chevron"
        );

        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setOpaque(false);
            tf.setForeground(Color.WHITE);
            tf.setCaretColor(Color.WHITE);
            tf.setHorizontalAlignment(SwingConstants.LEFT);
            tf.putClientProperty(FlatClientProperties.STYLE, "background:rgba(0,0,0,0)");
        }
    }

    public void trackPageViewDuration(int userId, String pageName, long durationSeconds) {

    }

    private void onCreateCourseClicked(ActionEvent e) {
        String courseCode = codeField.getText();
        String title = titleField.getText();
        int credits = (Integer) creditsSpinner.getValue();

        if (courseCode.isEmpty() || title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        createCourseButton.setEnabled(false);
        createCourseButton.setText("Creating...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return adminService.createCourse(courseCode, title, credits);
            }
            @Override
            protected void done() {
                try {
                    String result = get();
                    JOptionPane.showMessageDialog(ManageCoursesPanel.this, result, "Result", JOptionPane.INFORMATION_MESSAGE);
                    if (result.startsWith("Success")) {
                        codeField.setText(""); titleField.setText(""); creditsSpinner.setValue(3);
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
                finally { createCourseButton.setEnabled(true); createCourseButton.setText("CREATE COURSE"); }
            }
        }.execute();
    }
}