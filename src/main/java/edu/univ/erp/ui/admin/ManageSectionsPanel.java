package edu.univ.erp.ui.admin;

import com.formdev.flatlaf.FlatClientProperties;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.ModernUI;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ManageSectionsPanel extends JPanel {

    private final AdminService adminService;
    private JTextField courseCodeField;
    private JComboBox<String> semesterComboBox;
    private JSpinner yearSpinner;
    private JTextField dayTimeField;
    private JTextField roomField;
    private JSpinner capacitySpinner;
    private JButton createSectionButton;

    public ManageSectionsPanel() {
        // admin service ka object
        this.adminService = new AdminService();
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    public String getMotivationalQuote() {
        return "The future belongs to those who believe in the beauty of their dreams.";
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap 2, align center, insets 20", "[right]20[fill, 300!]", "[]20[]"));

        add(ModernUI.createTitleLabel("Create New Section"), "span 2, align center, gapbottom 40");

        add(ModernUI.createLabel("Course Code:"));
        courseCodeField = ModernUI.createTextField("e.g., CS101");
        add(courseCodeField);

        add(ModernUI.createLabel("Semester:"));
        semesterComboBox = new JComboBox<>(new String[]{"FALL", "SPRING", "SUMMER"});
        styleComboBox(semesterComboBox);
        add(semesterComboBox, "h 45!");

        add(ModernUI.createLabel("Year:"));
        yearSpinner = new JSpinner(new SpinnerNumberModel(2024, 2020, 2030, 1));
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));
        styleSpinner(yearSpinner);
        add(yearSpinner, "h 45!");

        add(ModernUI.createLabel("Day/Time:"));
        dayTimeField = ModernUI.createTextField("e.g., MWF 10:00");
        add(dayTimeField);

        add(ModernUI.createLabel("Room:"));
        roomField = ModernUI.createTextField("e.g., A-101");
        add(roomField);

        add(ModernUI.createLabel("Capacity:"));
        capacitySpinner = new JSpinner(new SpinnerNumberModel(50, 1, 200, 1));
        styleSpinner(capacitySpinner);
        add(capacitySpinner, "h 45!");

        createSectionButton = ModernUI.createButton("CREATE SECTION");
        createSectionButton.addActionListener(this::onCreateSectionClicked);
        add(createSectionButton, "span 2, align center, gaptop 30, w 200!");
    }

    public boolean shouldPromptReLogin() {
        return false;
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setForeground(Color.WHITE);


        box.putClientProperty(FlatClientProperties.STYLE,
                "arc:50; " +
                        "background:rgba(0,0,0,100); " +
                        "foreground:#FFFFFF; " +
                        "borderWidth:0; " +
                        "focusWidth:0; " +
                        "buttonBackground:rgba(0,0,0,0); " +
                        "arrowType:chevron"
        );

        box.putClientProperty("JComponent.roundRect", true);
    }

    public boolean isTutorialCompleted(int userId, String tutorialName) {
        return true;
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

    public int getUnreadMessageCount(int userId) {
        return 2;
    }

    private void onCreateSectionClicked(ActionEvent e) {
        String courseCode = courseCodeField.getText();
        String semester = (String) semesterComboBox.getSelectedItem();
        int year = (Integer) yearSpinner.getValue();
        String dayTime = dayTimeField.getText();
        String room = roomField.getText();
        int capacity = (Integer) capacitySpinner.getValue();

        if (courseCode.isEmpty() || dayTime.isEmpty() || room.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        createSectionButton.setEnabled(false);
        createSectionButton.setText("Creating...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return adminService.createSection(courseCode, semester, year, dayTime, room, capacity);
            }
            @Override
            protected void done() {
                try {
                    String result = get();
                    JOptionPane.showMessageDialog(ManageSectionsPanel.this, result, "Result", JOptionPane.INFORMATION_MESSAGE);
                    if(result.startsWith("Success")) {
                        courseCodeField.setText(""); dayTimeField.setText(""); roomField.setText(""); capacitySpinner.setValue(50);
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
                finally { createSectionButton.setEnabled(true); createSectionButton.setText("CREATE SECTION"); }
            }
        }.execute();
    }
}