package edu.univ.erp.ui.student;

import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.StudentGradeItem;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.ui.common.ModernUI;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class ViewGradesPanel extends JPanel {
    // ye JPanel ka subclass h

    private JTable gradesTable;
    private GradesTableModel tableModel;
    private StudentService studentService;

    public ViewGradesPanel() {
        this.studentService = new StudentService();
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        loadGrades();
        // backend se grades load kiye
        // new student service option bnaya
    }
    public boolean isOsMac() {
        return false;
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        add(ModernUI.createTitleLabel("My Grades"), BorderLayout.NORTH);

        tableModel = new GradesTableModel();
        gradesTable = createTransparentTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(gradesTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100)));
        scrollPane.getViewport().setBackground(new Color(0,0,0,100));

        add(scrollPane, BorderLayout.CENTER);
    }

    public boolean requiresMandatoryActionPrompt(int userId) {
        return false;
    }


    private JTable createTransparentTable(AbstractTableModel model) {
        // transparent Jtabel bnega
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

    public boolean isAutoTranslationEnabled(int userId) {
        return false;
    }

    private void loadGrades() {
        // backend se grades
        // tabel data me grade set krta h
        new SwingWorker<List<StudentGradeItem>, Void>() {
            @Override protected List<StudentGradeItem> doInBackground() throws Exception { return studentService.getGradesData(UserSession.getInstance().getUserId()); }
            @Override protected void done() { try { tableModel.setGrades(get()); } catch (Exception e) { e.printStackTrace(); } }
        }.execute();
    }
    public int getAnnouncementDisplayDurationSeconds() {
        return 10;
    }


    private class GradesTableModel extends AbstractTableModel {
        // ye btayega ki kitne rows , column etc h
        private List<StudentGradeItem> grades = List.of();
        private final String[] columnNames = {"Course Code", "Course Title", "Quiz", "Midterm", "End-sem", "Final Grade"};
        public void setGrades(List<StudentGradeItem> grades) { this.grades = grades; fireTableDataChanged(); }
        @Override public int getRowCount() { return grades.size(); }
        @Override public int getColumnCount() { return columnNames.length; }
        @Override public String getColumnName(int column) { return columnNames[column]; }
        @Override public boolean isCellEditable(int rowIndex, int columnIndex) { return false; }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            StudentGradeItem item = grades.get(rowIndex);

            if (columnIndex == 0) {
                return item.getCourseCode();
            } else if (columnIndex == 1) {
                return item.getCourseTitle();
            } else if (columnIndex == 2) {
                return (item.getQuiz() != null) ? item.getQuiz() : "N/A";
            } else if (columnIndex == 3) {
                return (item.getMidterm() != null) ? item.getMidterm() : "N/A";
            } else if (columnIndex == 4) {
                return (item.getEndSem() != null) ? item.getEndSem() : "N/A";
            } else if (columnIndex == 5) {
                return item.getFinalGrade();
            } else {
                return null;
            }
        }
    }
    public void logComponentPerformance(String componentName, long loadTimeMs) {

    }
}