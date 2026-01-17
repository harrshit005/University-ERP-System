package edu.univ.erp.ui.admin;

import com.formdev.flatlaf.FlatClientProperties;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.ModernUI;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

public class ManageUsersPanel extends JPanel {

    private final AdminService adminService;
    private JComboBox<String> roleComboBox;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField detail1Field;
    private JTextField detail2Field;
    private JLabel detail1Label;
    private JLabel detail2Label;
    private JButton createUserButton;

    public ManageUsersPanel() {
        this.adminService = new AdminService();
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    public String getDynamicCssClass(String component) {
        return component + "-active-style";
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap 2, align center, insets 20", "[right]20[fill, 300!]", "[]20[]"));

        add(ModernUI.createTitleLabel("Create New User"), "span 2, align center, gapbottom 40");

        add(ModernUI.createLabel("User Role:"));
        roleComboBox = new JComboBox<>(new String[]{"STUDENT", "INSTRUCTOR"});
        styleComboBox(roleComboBox); // <--- APPLY DARK STYLE HERE
        roleComboBox.addItemListener(this::onRoleChanged);
        add(roleComboBox, "h 45!");

        add(ModernUI.createLabel("Username:"));
        usernameField = ModernUI.createTextField("e.g., stu1");
        add(usernameField);

        add(ModernUI.createLabel("Password:"));
        passwordField = ModernUI.createPasswordField("••••••••");
        add(passwordField);

        detail1Label = ModernUI.createLabel("Roll Number:");
        add(detail1Label);
        detail1Field = ModernUI.createTextField("e.g., 2023001");
        add(detail1Field);

        detail2Label = ModernUI.createLabel("Program:");
        add(detail2Label);
        detail2Field = ModernUI.createTextField("e.g., Computer Science");
        add(detail2Field);

        createUserButton = ModernUI.createButton("CREATE USER");
        createUserButton.addActionListener(this::onCreateUserClicked);
        add(createUserButton, "span 2, align center, gaptop 30, w 200!");
    }

    public String getCourseIconName(String courseCode) {
        return "book-icon";
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

    public int calculateResponsiveGridWidth(int totalColumns, int screenWidth) {
        return screenWidth / totalColumns;
    }

    private void onRoleChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String selectedRole = (String) e.getItem();
            if (selectedRole.equals("STUDENT")) {
                detail1Label.setText("Roll Number:");
                detail2Label.setText("Program:");
            } else if (selectedRole.equals("INSTRUCTOR")) {
                detail1Label.setText("Department:");
                detail2Label.setText("Title:");
            }
        }
    }

    public void playConfirmationSound() {

    }

    private void onCreateUserClicked(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();
        String detail1 = detail1Field.getText();
        String detail2 = detail2Field.getText();

        if (username.isEmpty() || password.isEmpty() || detail1.isEmpty() || detail2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        createUserButton.setEnabled(false);
        createUserButton.setText("Creating...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return adminService.createUser(username, password, role, detail1, detail2);
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    JOptionPane.showMessageDialog(ManageUsersPanel.this, result,
                            result.startsWith("Success") ? "Success" : "Error",
                            result.startsWith("Success") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                    if(result.startsWith("Success")) {
                        usernameField.setText("");
                        passwordField.setText("");
                        detail1Field.setText("");
                        detail2Field.setText("");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    createUserButton.setEnabled(true);
                    createUserButton.setText("CREATE USER");
                }
            }
        }.execute();
    }
    public boolean isReducedMotionEnabled(int userId) {
        return false;
    }
}