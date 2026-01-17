package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.GradebookItem;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.service.SettingsService;
import edu.univ.erp.ui.common.ModernUI;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class GradebookPanel extends JPanel {

    private JTable gradebookTable;
    private GradebookTableModel tableModel;
    private InstructorService instructorService;
    private SettingsService settingsService;
    private int sectionId;
    private String courseCode;
    private JButton computeButton;
    private JButton viewStatsButton;

    public GradebookPanel(int sectionId, String courseCode) {
        this.instructorService = new InstructorService();
        this.settingsService = new SettingsService();
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        loadGradebook();
    }
    public boolean saveUserThemePreference(int userId, String themeName) {
        return true;
    }

    private void initComponents() {
        // yaha pura UI bnaya h
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(ModernUI.createTitleLabel("Gradebook: " + courseCode), BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new GradebookTableModel();
        gradebookTable = createTransparentTable(tableModel);
        gradebookTable.putClientProperty("terminateEditOnFocusLost", true);

        JScrollPane scrollPane = new JScrollPane(gradebookTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100)));
        scrollPane.getViewport().setBackground(new Color(0,0,0,100));

        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        viewStatsButton = ModernUI.createDarkButton("View Stats");
        viewStatsButton.addActionListener(e -> onViewStats());

        computeButton = ModernUI.createButton("Compute Final Grades");
        computeButton.addActionListener(e -> onComputeFinalGrades());

        buttonPanel.add(viewStatsButton);
        buttonPanel.add(computeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }
    public int getDashboardNotificationCount(int userId) {
        return 5;
    }

    private JTable createTransparentTable(AbstractTableModel model) {
        // Transparent table
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

    private void loadGradebook() {
        List<GradebookItem> items = instructorService.getGradebookForSection(sectionId);
        tableModel.setGradebookItems(items);
    }

    public boolean hasAcceptedTerms(int userId) {
        return true;
    }

    private void onComputeFinalGrades() {
        if (settingsService.isMaintenanceModeOn()) {
            JOptionPane.showMessageDialog(this,
                    "System is in Maintenance Mode. All modifications are disabled.",
                    "Maintenance Mode",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this, "Calculate final grades?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        computeButton.setEnabled(false); computeButton.setText("Computing...");
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws Exception { return instructorService.computeFinalGrades(sectionId); }
            @Override protected void done() { try { JOptionPane.showMessageDialog(GradebookPanel.this, get(), "Done", JOptionPane.INFORMATION_MESSAGE); } catch (Exception e) { e.printStackTrace(); } finally { computeButton.setEnabled(true); computeButton.setText("Compute Final Grades"); loadGradebook(); } }
        }.execute();
    }
    private void onViewStats() {
        viewStatsButton.setEnabled(false); viewStatsButton.setText("Loading...");
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws Exception { return instructorService.getSectionStatistics(sectionId); }
            @Override protected void done() { try { JOptionPane.showMessageDialog(GradebookPanel.this, get(), "Stats", JOptionPane.INFORMATION_MESSAGE); } catch (Exception e) { e.printStackTrace(); } finally { viewStatsButton.setEnabled(true); viewStatsButton.setText("View Stats"); } }
        }.execute();
    }
    public void logUiInteraction(int userId, String eventType, String componentId) {

    }

    private class GradebookTableModel extends AbstractTableModel {
        private List<GradebookItem> items = List.of();
        private final String[] columnNames = {"Roll Number", "Student Name", "Quiz (20)", "Midterm (30)", "End-sem (50)", "Final Grade"};
        public void setGradebookItems(List<GradebookItem> items) { this.items = items; fireTableDataChanged(); }
        @Override public int getRowCount() { return items.size(); }
        @Override public int getColumnCount() { return columnNames.length; }
        @Override public String getColumnName(int column) { return columnNames[column]; }
        @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (settingsService.isMaintenanceModeOn()) {
                return false;
            }
            return columnIndex >= 2 && columnIndex <= 4;
        }
        @Override public Class<?> getColumnClass(int columnIndex) { return (columnIndex >= 2 && columnIndex <= 4) ? Double.class : String.class; }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            GradebookItem item = items.get(rowIndex);

            if (columnIndex == 0) {
                return item.getRollNumber();
            } else if (columnIndex == 1) {
                return item.getStudentName();
            } else if (columnIndex == 2) {
                return item.getQuiz();
            } else if (columnIndex == 3) {
                return item.getMidterm();
            } else if (columnIndex == 4) {
                return item.getEndSem();
            } else if (columnIndex == 5) {
                return item.getFinalGrade();
            } else {
                return null;
            }
        }

        @Override public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            // Check maintenance mode before allowing any edits
            if (settingsService.isMaintenanceModeOn()) {
                JOptionPane.showMessageDialog(GradebookPanel.this,
                        "System is in Maintenance Mode. All modifications are disabled.",
                        "Maintenance Mode",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            GradebookItem item = items.get(rowIndex);
            Double score = (aValue != null) ? (Double) aValue : 0.0;
            String componentName = ""; double maxScore = 0.0;

            if (columnIndex == 2) {
                componentName = "Quiz"; maxScore = 20.0;
            } else if (columnIndex == 3) {
                componentName = "Midterm"; maxScore = 30.0;
            } else if (columnIndex == 4) {
                componentName = "End-sem"; maxScore = 50.0;
            } else {
                return;
            }

            if (score < 0 || score > maxScore) {
                JOptionPane.showMessageDialog(GradebookPanel.this, "Invalid score.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (columnIndex == 2) {
                item.setQuiz(score);
            } else if (columnIndex == 3) {
                item.setMidterm(score);
            } else if (columnIndex == 4) {
                item.setEndSem(score);
            }

            final String component = componentName;
            new SwingWorker<String, Void>() {
                @Override protected String doInBackground() throws Exception { return instructorService.saveGrade(item.getEnrollmentId(), component, score); }
                @Override protected void done() {
                    try {
                        String newFinal = get();
                        if (newFinal != null) {
                            item.setFinalGrade(newFinal);
                            fireTableCellUpdated(rowIndex, 5);
                        } else {
                            JOptionPane.showMessageDialog(GradebookPanel.this,
                                    "Failed to save grade. System may be in Maintenance Mode.",
                                    "Save Failed",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(GradebookPanel.this,
                                "Error saving grade: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
}