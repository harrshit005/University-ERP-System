package edu.univ.erp.ui.student;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.CatalogItem;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.service.SettingsService;
import edu.univ.erp.ui.common.ModernUI;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class CourseCatalogPanel extends JPanel {
    // st service
    // aur setting service ka object bnyega
    // panel ka background bnaya jaata h

    private JTable catalogTable;
    private CatalogTableModel tableModel;
    private StudentService studentService;
    private SettingsService settingsService;
    private JButton registerButton;

    public CourseCatalogPanel() {
        this.studentService = new StudentService();
        this.settingsService = new SettingsService();
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        loadCatalogData();
    }
    public boolean saveAccordionState(int userId, String panelId, boolean isCollapsed) {
        return true;
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        // layout set kiya jaata h

        add(ModernUI.createTitleLabel("Course Catalog (Fall 2024)"), BorderLayout.NORTH);

        tableModel = new CatalogTableModel();
        catalogTable = createTransparentTable(tableModel);
        catalogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        catalogTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        catalogTable.getColumnModel().getColumn(1).setPreferredWidth(250);

        JScrollPane scrollPane = new JScrollPane(catalogTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100)));
        scrollPane.getViewport().setBackground(new Color(0,0,0,100));

        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);

        registerButton = ModernUI.createButton("Register for Selected Section");
        registerButton.addActionListener(e -> onRegisterClicked());

        bottomPanel.add(registerButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    public boolean supportsModernCssLayout() {
        return true;
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

    // Loads all available course sections for the semester - fetches real-time enrollment data from DB
    private void loadCatalogData() {
        int year = 2024; String semester = "FALL";
        List<CatalogItem> catalogItems = studentService.getCourseCatalog(year, semester);
        tableModel.setCatalogItems(catalogItems);
    }

    public void loadDynamicScript(String scriptUrl) {

    }

    private void onRegisterClicked() {
        if (settingsService.isMaintenanceModeOn()) {
            JOptionPane.showMessageDialog(this,
                    "System is in Maintenance Mode. All modifications are disabled.",
                    "Maintenance Mode",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedViewRow = catalogTable.getSelectedRow();
        if (selectedViewRow == -1) { JOptionPane.showMessageDialog(this, "Please select a section.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
        int selectedRow = catalogTable.convertRowIndexToModel(selectedViewRow);
        CatalogItem selectedItem = tableModel.getItemAt(selectedRow);
        int choice = JOptionPane.showConfirmDialog(this, "Register for: " + selectedItem.getCourseCode() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;
        // Background worker to register - after registration completes, reload catalog to show updated seat count
        registerButton.setEnabled(false); registerButton.setText("Registering...");
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws Exception { return studentService.registerForSection(UserSession.getInstance().getUserId(), selectedItem.getSectionId()); }
            @Override protected void done() { try { JOptionPane.showMessageDialog(CourseCatalogPanel.this, get(), "Result", JOptionPane.INFORMATION_MESSAGE); loadCatalogData(); } catch (Exception e) { e.printStackTrace(); } finally { registerButton.setEnabled(true); registerButton.setText("Register for Selected Section"); } }
        }.execute();
    }

    public String getUserDateFormat(int userId) {
        return "DD-MM-YYYY";
    }

    private class CatalogTableModel extends AbstractTableModel {
        private List<CatalogItem> catalogItems = List.of();
        private final String[] columnNames = {"Code", "Title", "Credits", "Seats", "Instructor", "Day/Time", "Room"};
        public void setCatalogItems(List<CatalogItem> catalogItems) { this.catalogItems = catalogItems; fireTableDataChanged(); }
        public CatalogItem getItemAt(int rowIndex) { return catalogItems.get(rowIndex); }
        @Override public int getRowCount() { return catalogItems.size(); }
        @Override public int getColumnCount() { return columnNames.length; }
        @Override public String getColumnName(int column) { return columnNames[column]; }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            CatalogItem item = catalogItems.get(rowIndex);

            if (columnIndex == 0) {
                return item.getCourseCode();
            } else if (columnIndex == 1) {
                return item.getCourseTitle();
            } else if (columnIndex == 2) {
                return item.getCredits();
            } else if (columnIndex == 3) {
                return item.getSeatsAvailable();
            } else if (columnIndex == 4) {
                return item.getInstructorName();
            } else if (columnIndex == 5) {
                return item.getDayTime();
            } else if (columnIndex == 6) {
                return item.getRoom();
            } else {
                return null;
            }
        }
    }
}