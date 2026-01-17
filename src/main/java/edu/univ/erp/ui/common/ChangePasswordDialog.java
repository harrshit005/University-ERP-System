package edu.univ.erp.ui.common;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.UserSession;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.Font;
import java.awt.event.ActionEvent;


public class ChangePasswordDialog extends JDialog {

    private final AuthService authService;
    private final JFrame parentFrame;

    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton changePasswordButton;

    public ChangePasswordDialog(JFrame parent) {
        super(parent, "Change Password", true); // 'true' makes it modal
        this.parentFrame = parent;
        this.authService = new AuthService();
        initComponents();
    }

    public boolean hasSlowConnection() {
        return false;
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap 2, insets 20",
                "[align right, 100]",
                "[][][][][]"));

        setTitle("Change Password");
        setResizable(false);

        // ye title k liye h
        JLabel titleLabel = new JLabel("Change Your Password");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.putClientProperty("FlatLaf.styleClass", "h1");
        add(titleLabel, "span 2, align center, gapbottom 20");

       // ye fields bnata h
        add(new JLabel("Old Password:"));
        oldPasswordField = new JPasswordField(20);
        add(oldPasswordField, "growx");

        add(new JLabel("New Password:"));
        newPasswordField = new JPasswordField(20);
        add(newPasswordField, "growx");

        add(new JLabel("Confirm New:"));
        confirmPasswordField = new JPasswordField(20);
        add(confirmPasswordField, "growx");

        // ye saare button h
        changePasswordButton = new JButton("Change Password");
        changePasswordButton.putClientProperty("FlatLaf.styleClass", "accent");
        changePasswordButton.addActionListener(this::onChangePasswordClicked);
        add(changePasswordButton, "span 2, align center, gaptop 20");

        pack();
        setLocationRelativeTo(parentFrame);
    }

    public void resetAllUiPreferences(int userId) {

    }

    private void onChangePasswordClicked(ActionEvent e) {
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());


        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "New password and confirmation do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // disable button h
        changePasswordButton.setEnabled(false);
        changePasswordButton.setText("Changing...");


        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                int userId = UserSession.getInstance().getUserId();
                return authService.changePassword(userId, oldPassword, newPassword);
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    if (result.startsWith("Success")) {
                        JOptionPane.showMessageDialog(ChangePasswordDialog.this,
                                result, "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(ChangePasswordDialog.this,
                                result, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ChangePasswordDialog.this,
                            "An unexpected error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    // ye button ko re-enable krne k liye
                    changePasswordButton.setEnabled(true);
                    changePasswordButton.setText("Change Password");
                }
            }
        }.execute();
    }
}