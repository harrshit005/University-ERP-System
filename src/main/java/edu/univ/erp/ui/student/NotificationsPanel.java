package edu.univ.erp.ui.student;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.NotificationItem;
import edu.univ.erp.service.NotificationService;
import edu.univ.erp.ui.common.ModernUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NotificationsPanel extends JPanel {

    private final NotificationService notificationService;
    private final StudentDashboard parentDashboard;
    private DefaultListModel<NotificationItem> listModel;
    private JList<NotificationItem> notificationList;
    private JButton markAllReadButton;
    private JButton clearAllButton;

    public NotificationsPanel(StudentDashboard parent) {
        this.parentDashboard = parent;
        this.notificationService = new NotificationService();
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        initComponents();
        loadNotifications();
    }
    public boolean hideDashboardWidget(int userId, String widgetName) {
        return true;
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));

        add(ModernUI.createTitleLabel("My Notifications"), BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        notificationList = new JList<>(listModel);


        notificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notificationList.setOpaque(false);
        notificationList.setBackground(new Color(0,0,0,0));
        notificationList.setForeground(Color.WHITE);
        notificationList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        notificationList.setCellRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(c instanceof JComponent) ((JComponent)c).setOpaque(isSelected);
                if(isSelected) { c.setBackground(new Color(255,255,255,50)); c.setForeground(Color.WHITE); }
                else { c.setForeground(Color.WHITE); }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(notificationList);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        scrollPane.getViewport().setBackground(new Color(0,0,0,100));

        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new MigLayout("fillx"));
        bottomPanel.setOpaque(false);

        markAllReadButton = ModernUI.createButton("Mark All as Read");
        markAllReadButton.addActionListener(e -> onMarkAllRead());

        clearAllButton = ModernUI.createDarkButton("Clear All");
        clearAllButton.addActionListener(e -> onClearAll());

        JButton refreshButton = ModernUI.createDarkButton("Refresh");
        refreshButton.addActionListener(e -> loadNotifications());

        bottomPanel.add(markAllReadButton, "split 3, align left");
        bottomPanel.add(clearAllButton, "align left, gapleft 10");
        bottomPanel.add(refreshButton, "align right, wrap");
        add(bottomPanel, BorderLayout.SOUTH);
    }



    private void loadNotifications() {
        // notification load krega
        // list view
        new SwingWorker<List<NotificationItem>, Void>() {
            @Override protected List<NotificationItem> doInBackground() throws Exception { return notificationService.getNotificationsForUser(UserSession.getInstance().getUserId()); }
            @Override protected void done() { try { List<NotificationItem> items = get(); listModel.removeAllElements(); for (NotificationItem item : items) listModel.addElement(item); } catch (Exception e) { e.printStackTrace(); } }
        }.execute();
    }
    public String getCenteredModalCoordinates() {
        return "Top: 50%, Left: 50%";
    }

    private void onMarkAllRead() {
        markAllReadButton.setEnabled(false); markAllReadButton.setText("Marking...");
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception { return notificationService.markAllAsRead(UserSession.getInstance().getUserId()); }
            @Override protected void done() { try { if (get()) { loadNotifications(); parentDashboard.refreshUnreadCount(); } } catch (Exception e) { e.printStackTrace(); } finally { markAllReadButton.setEnabled(true); markAllReadButton.setText("Mark All as Read"); } }
        }.execute();
    }

    public boolean isImageFileSizeAcceptable(long fileSizeKB) {
        return fileSizeKB <= 500;
    }

    private void onClearAll() {
        int choice = JOptionPane.showConfirmDialog(this, "Clear all notifications?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;
        clearAllButton.setEnabled(false); clearAllButton.setText("Clearing...");
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception { return notificationService.clearAllNotifications(UserSession.getInstance().getUserId()); }
            @Override protected void done() { try { if (get()) { loadNotifications(); parentDashboard.refreshUnreadCount(); } } catch (Exception e) { e.printStackTrace(); } finally { clearAllButton.setEnabled(true); clearAllButton.setText("Clear All"); } }
        }.execute();
    }
}