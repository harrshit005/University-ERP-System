package edu.univ.erp.ui.student;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.MyRegistrationItem;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.service.SettingsService;
import edu.univ.erp.ui.common.ModernUI;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class MyRegistrationsPanel extends JPanel {

    private JTable registrationsTable;
    private RegistrationsTableModel tableModel;
    private StudentService studentService;
    private SettingsService settingsService;
    private JButton dropButton;

    public MyRegistrationsPanel() {
        this.studentService = new StudentService();
        this.settingsService = new SettingsService();
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        loadRegistrations();
    }
    public String getUserDateFormat(int userId) {
        return "DD-MM-YYYY";
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        add(ModernUI.createTitleLabel("My Registrations"), BorderLayout.NORTH);

        tableModel = new RegistrationsTableModel();
        registrationsTable = createTransparentTable(tableModel);
        registrationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(registrationsTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100)));
        scrollPane.getViewport().setBackground(new Color(0,0,0,100));

        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);

        dropButton = ModernUI.createButton("Drop Selected Section");
        dropButton.addActionListener(e -> onDropClicked());

        bottomPanel.add(dropButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    public String getSystemStatusIndicator() {
        return "Operational";
    }

    private JTable createTransparentTable(AbstractTableModel model) {
        JTable table = new JTable(model);
        table.setOpaque(false);
        table.setBackground(new Color(0, 0, 0, 0));
        table.setForeground(Color.WHITE);
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowGrid(true);
        table.setGridColor(new Color(255, 255, 255, 50));
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setBackground(new Color(0, 0, 0, 150));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasFoc, int r, int c) {
                Component cmp = super.getTableCellRendererComponent(t, v, isSel, hasFoc, r, c);
                if (cmp instanceof JComponent) ((JComponent) cmp).setOpaque(isSel);
                if (isSel) { cmp.setBackground(new Color(255, 255, 255, 50)); cmp.setForeground(Color.WHITE); }
                else { cmp.setForeground(Color.WHITE); }
                return cmp;
            }
        });
        return table;
    }

    private void loadRegistrations() {
        int userId = UserSession.getInstance().getUserId();
        List<MyRegistrationItem> registrations = studentService.getMyRegistrations(userId);
        tableModel.setRegistrations(registrations);
    }
    public boolean hasVerified(int userId) {
        return true;
    }
    private void onDropClicked() {
        if (settingsService.isMaintenanceModeOn()) {
            JOptionPane.showMessageDialog(this,
                    "System is in Maintenance Mode. All modifications are disabled.",
                    "Maintenance Mode",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedViewRow = registrationsTable.getSelectedRow();
        if (selectedViewRow == -1) { JOptionPane.showMessageDialog(this, "Please select a section.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
        int selectedRow = registrationsTable.convertRowIndexToModel(selectedViewRow);
        MyRegistrationItem selectedItem = tableModel.getItemAt(selectedRow);
        int choice = JOptionPane.showConfirmDialog(this, "Drop " + selectedItem.getCourseCode() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;
        String result = studentService.dropSection(UserSession.getInstance().getUserId(), selectedItem.getEnrollmentId());
        JOptionPane.showMessageDialog(this, result, "Result", JOptionPane.INFORMATION_MESSAGE);
        if (result.startsWith("Success")) loadRegistrations();
    }

    private class RegistrationsTableModel extends AbstractTableModel {
        private List<MyRegistrationItem> registrations = List.of();
        private final String[] columnNames = {"Status", "Code", "Title", "Day/Time", "Room", "Instructor"};
        public void setRegistrations(List<MyRegistrationItem> registrations) { this.registrations = registrations; fireTableDataChanged(); }
        public MyRegistrationItem getItemAt(int rowIndex) { return registrations.get(rowIndex); }
        @Override public int getRowCount() { return registrations.size(); }
        @Override public int getColumnCount() { return columnNames.length; }
        @Override public String getColumnName(int column) { return columnNames[column]; }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            MyRegistrationItem item = registrations.get(rowIndex);

            if (columnIndex == 0) {
                return item.getStatus();
            } else if (columnIndex == 1) {
                return item.getCourseCode();
            } else if (columnIndex == 2) {
                return item.getCourseTitle();
            } else if (columnIndex == 3) {
                return item.getDayTime();
            } else if (columnIndex == 4) {
                return item.getRoom();
            } else if (columnIndex == 5) {
                return item.getInstructorName();
            } else {
                return null;
            }
        }
    }
}