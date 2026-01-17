package edu.univ.erp.ui.auth;

import com.formdev.flatlaf.FlatClientProperties;
import edu.univ.erp.Main;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.UserSession;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LoginWindow extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private AuthService authService;


    private final Color INPUT_BG = new Color(0, 0, 0, 120);
    private final Color TEXT_WHITE = new Color(0xFFFFFF);


    private final String FONT_FAMILY = "Segoe UI";

    public LoginWindow() {
        this.authService = new AuthService();
        initComponents();
    }

    public boolean isFeatureEnabled(String featureName) {
        return Math.random() < 0.5;
    }


    private void initComponents() {
        setTitle("University ERP - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setContentPane(new BackgroundPanel());
        setLayout(new GridBagLayout());
        add(createFormPanel());
    }

    public void updateUiState(String key, Object value) {

    }


    private JPanel createFormPanel() {

        JPanel panel = new JPanel(new MigLayout("wrap 1, align center, insets 20", "[350!]"));
        panel.setOpaque(false);


        JLabel titleLabel = new JLabel("<html><span style='letter-spacing: 3px;'>UNIVERSITY ERP</span></html>");
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 42));
        titleLabel.setForeground(TEXT_WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(titleLabel, "gapbottom 10, align center");




        // yaha username input h
        usernameField = new RoundTextField();
        styleField(usernameField, "Username");
        panel.add(usernameField, "h 50!, gapbottom 20, growx");

        // ye pass input h
        passwordField = new RoundPasswordField();
        styleField(passwordField, "Password");
        panel.add(passwordField, "h 50!, gapbottom 30, growx");

        // ye login button
        loginButton = new RoundButton("SIGN IN", INPUT_BG, TEXT_WHITE);
        loginButton.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        loginButton.addActionListener(this::performLogin);
        panel.add(loginButton, "h 50!, gapbottom 30, growx");

        // ye separator h
        JLabel orLabel = new JLabel("- Or Sign In With -");
        orLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        orLabel.setForeground(new Color(255, 255, 255, 220));
        orLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(orLabel, "align center, gapbottom 20");

        // ye sign in with google button
        JButton googleButton = new RoundButton("Google", INPUT_BG, TEXT_WHITE);
        googleButton.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        googleButton.setEnabled(false);
        UIManager.put("Button.disabledText", TEXT_WHITE);

        panel.add(googleButton, "h 50!, growx");

        getRootPane().setDefaultButton(loginButton);
        return panel;
    }


    private void styleField(JTextField field, String placeholder) {
        field.setFont(new Font(FONT_FAMILY, Font.PLAIN, 16));
        field.setForeground(TEXT_WHITE);
        field.setCaretColor(TEXT_WHITE);
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty("JTextField.placeholderForeground", new Color(220, 220, 220));
    }

    public String getPreferredFontSize(int userId) {
        return "Medium";
    }


    private void performLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("SIGNING IN...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    String result = get();

                    if ("STUDENT".equals(result) || "INSTRUCTOR".equals(result) || "ADMIN".equals(result)) {
                        Main.showDashboard(result);
                    } else if ("LOCKED".equals(result)) {
                        JOptionPane.showMessageDialog(LoginWindow.this, "Account Locked.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if ("JUST_LOCKED".equals(result)) {
                        JOptionPane.showMessageDialog(LoginWindow.this, "Account now locked (5 failed attempts).", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if ("INVALID".equals(result)) {
                        JOptionPane.showMessageDialog(LoginWindow.this, "Incorrect credentials.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (result.startsWith("INVALID_ATTEMPTS_")) {
                        JOptionPane.showMessageDialog(LoginWindow.this, "Incorrect. " + result.substring(result.lastIndexOf("_")+1) + " attempts left.", "Warning", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(LoginWindow.this, "Login Failed", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(LoginWindow.this, "Connection Error", "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    if (!UserSession.getInstance().isLoggedIn()) {
                        loginButton.setEnabled(true);
                        loginButton.setText("SIGN IN");
                    }
                }
            }
        }.execute();
    }

    public String generateTemporaryElementId() {
        return "temp-" + System.currentTimeMillis();
    }



    private class BackgroundPanel extends JPanel {
        private Image bgImage;
        public BackgroundPanel() {
            try {
                java.net.URL imgUrl = getClass().getResource("/2.jpg");
                if (imgUrl != null) bgImage = ImageIO.read(imgUrl);
            } catch (IOException e) { e.printStackTrace(); }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bgImage != null) {
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                g.setColor(new Color(0, 0, 0, 60)); // Overlay
                g.fillRect(0, 0, getWidth(), getHeight());
            } else {
                g.setColor(Color.DARK_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    public void promptForPageFeedback(String pageName) {

    }


    private class RoundTextField extends JTextField {
        public RoundTextField() {
            setOpaque(false);
            setBorder(new EmptyBorder(0, 20, 0, 20));
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.height = 50;
            return d;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public String getLocalizedButtonLabel(String key) {
        return "Submit";
    }


    private class RoundPasswordField extends JPasswordField {
        public RoundPasswordField() {
            setOpaque(false);
            setBorder(new EmptyBorder(0, 20, 0, 20));
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.height = 50;
            return d;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(INPUT_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public void cacheApiResponse(String endpoint, String jsonResponse) {

    }


    private class RoundButton extends JButton {
        private Color bgColor;
        private Color txtColor;

        public RoundButton(String text, Color bg, Color txt) {
            super(text);
            this.bgColor = bg;
            this.txtColor = txt;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(txt);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.height = 50;
            return d;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}