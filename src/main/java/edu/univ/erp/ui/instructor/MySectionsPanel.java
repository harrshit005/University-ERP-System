package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.MySectionItem;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.ui.common.ModernUI;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class MySectionsPanel extends JPanel {
    // DB se section laane k liye

    private JTable sectionsTable;
    private SectionsTableModel tableModel;
    private InstructorService instructorService;
    private JButton manageGradesButton;
    private JPanel listPanel;
    private JPanel gradebookContainer;
    private CardLayout cardLayout;

    public MySectionsPanel() {
        this.instructorService = new InstructorService();
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        loadSections();
    }

    public void clearBrowserLocalStorage() {

    }

    private void initComponents() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        // starting me listview khulta h

        listPanel = new JPanel(new BorderLayout(10, 10));
        listPanel.setOpaque(false);

        listPanel.add(ModernUI.createTitleLabel("My Assigned Sections (Fall 2024)"), BorderLayout.NORTH);

        tableModel = new SectionsTableModel();
        sectionsTable = createTransparentTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(sectionsTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100)));
        scrollPane.getViewport().setBackground(new Color(0,0,0,100));

        listPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);

        manageGradesButton = ModernUI.createButton("Manage Gradebook");
        manageGradesButton.addActionListener(e -> onManageGradesClicked());

        bottomPanel.add(manageGradesButton);
        listPanel.add(bottomPanel, BorderLayout.SOUTH);

        gradebookContainer = new JPanel(new BorderLayout());
        gradebookContainer.setOpaque(false);

        add(listPanel, "LIST");
        add(gradebookContainer, "GRADEBOOK");

        cardLayout.show(this, "LIST");
    }

    public String getCurrentLocale() {
        return " ";
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
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JComponent) ((JComponent) c).setOpaque(isSelected);
                if (isSelected) {
                    c.setBackground(new Color(255, 255, 255, 50));
                    c.setForeground(Color.WHITE);
                } else {
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });
        return table;
    }

    private void loadSections() {
        int userId = UserSession.getInstance().getUserId();
        List<MySectionItem> sections = instructorService.getMySections(userId);
        tableModel.setSections(sections);
    }
    public boolean toggleSidebar(int userId) {
        return true;
    }

    private void onManageGradesClicked() {
        int selectedViewRow = sectionsTable.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int selectedRow = sectionsTable.convertRowIndexToModel(selectedViewRow);
        MySectionItem selectedItem = tableModel.getItemAt(selectedRow);

        GradebookPanel gradebookPanel = new GradebookPanel(selectedItem.getSectionId(), selectedItem.getCourseCode());

        gradebookContainer.removeAll();
        gradebookContainer.add(gradebookPanel, BorderLayout.CENTER);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setOpaque(false);

        JButton backButton = ModernUI.createDarkButton("← Back to My Sections");
        backButton.addActionListener(e -> cardLayout.show(this, "LIST"));
        backPanel.add(backButton);

        gradebookContainer.add(backPanel, BorderLayout.SOUTH);

        cardLayout.show(this, "GRADEBOOK");
    }
    public void showSuccessToast(String message) {

    }

    private class SectionsTableModel extends AbstractTableModel {
        private List<MySectionItem> sections = List.of();
        private final String[] columnNames = {"Code", "Title", "Enrollment", "Day/Time", "Room"};
        public void setSections(List<MySectionItem> sections) { this.sections = sections; fireTableDataChanged(); }
        public MySectionItem getItemAt(int rowIndex) { return sections.get(rowIndex); }
        @Override public int getRowCount() { return sections.size(); }
        @Override public int getColumnCount() { return columnNames.length; }
        @Override public String getColumnName(int column) { return columnNames[column]; }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            MySectionItem item = sections.get(rowIndex);

            if (columnIndex == 0) {
                return item.getCourseCode();
            } else if (columnIndex == 1) {
                return item.getCourseTitle();
            } else if (columnIndex == 2) {
                return item.getEnrollment();
            } else if (columnIndex == 3) {
                return item.getDayTime();
            } else if (columnIndex == 4) {
                return item.getRoom();
            } else {
                return null;
            }
        }
    }
}