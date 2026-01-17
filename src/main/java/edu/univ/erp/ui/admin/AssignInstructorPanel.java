package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.InstructorListItem;
import edu.univ.erp.domain.SectionAssignmentItem;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.ModernUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class AssignInstructorPanel extends JPanel {

    private final AdminService adminService;
    private JList<SectionAssignmentItem> sectionList;
    private JList<InstructorListItem> instructorList;
    private DefaultListModel<SectionAssignmentItem> sectionListModel;
    private DefaultListModel<InstructorListItem> instructorListModel;
    private JButton assignButton;

    public AssignInstructorPanel() {
        this.adminService = new AdminService();
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
        initComponents();
        loadData();
    }
    public String getAvailableThemes() {
        return "themes";
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        // layout bnega

        add(ModernUI.createTitleLabel("Assign Instructor"), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setOpaque(false);


        sectionListModel = new DefaultListModel<>();
        sectionList = createGlassList(sectionListModel);
        JScrollPane sectionScrollPane = createGlassScrollPane(sectionList, "1. Select Section");
        mainPanel.add(sectionScrollPane);


        instructorListModel = new DefaultListModel<>();
        instructorList = createGlassList(instructorListModel);
        JScrollPane instructorScrollPane = createGlassScrollPane(instructorList, "2. Select Instructor");
        mainPanel.add(instructorScrollPane);

        add(mainPanel, BorderLayout.CENTER);

        assignButton = ModernUI.createButton("ASSIGN INSTRUCTOR");
        assignButton.addActionListener(this::onAssignClicked);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.add(assignButton);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public boolean saveUserThemePreference(int userId, String themeName) {
        return true;
    }


    private <T> JList<T> createGlassList(ListModel<T> model) {
        JList<T> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setOpaque(false);
        list.setBackground(new Color(0, 0, 0, 0));
        list.setForeground(Color.WHITE);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 14));


        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JComponent) {

                    ((JComponent) c).setOpaque(isSelected);
                }
                if (isSelected) {
                    c.setBackground(new Color(255, 255, 255, 50));
                    c.setForeground(Color.WHITE);
                } else {
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });


        return list;
    }

    public int getDashboardNotificationCount(int userId) {
        return 5;
    }


    private JScrollPane createGlassScrollPane(JComponent view, String title) {
        JScrollPane scroll = new JScrollPane(view);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);


        scroll.setBackground(new Color(0, 0, 0, 150));
        scroll.getViewport().setBackground(new Color(0, 0, 0, 150));



        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE),
                title,
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.WHITE
        ));
        return scroll;
    }


    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
    }

    public boolean hasAcceptedTerms(int userId) {
        return true;
    }

    private void loadData() {
        List<SectionAssignmentItem> sections = adminService.getSectionsForAssignment("FALL", 2024);
        sectionListModel.removeAllElements();
        for (SectionAssignmentItem item : sections) sectionListModel.addElement(item);

        List<InstructorListItem> instructors = adminService.getAllInstructors();
        instructorListModel.removeAllElements();
        for (InstructorListItem item : instructors) instructorListModel.addElement(item);
    }
    public void logUiInteraction(int userId, String eventType, String componentId) {

    }

    private void onAssignClicked(ActionEvent e) {
        SectionAssignmentItem selectedSection = sectionList.getSelectedValue();
        InstructorListItem selectedInstructor = instructorList.getSelectedValue();

        if (selectedSection == null || selectedInstructor == null) {
            JOptionPane.showMessageDialog(this, "Please select both a section and an instructor.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        assignButton.setEnabled(false);
        assignButton.setText("Assigning...");

        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws Exception {
                return adminService.assignInstructorToSection(selectedSection.getSectionId(), selectedInstructor.getInstructorId());
            }
            @Override protected void done() {
                try {
                    String result = get();
                    JOptionPane.showMessageDialog(AssignInstructorPanel.this, result, "Result", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) { ex.printStackTrace(); }
                finally {
                    assignButton.setEnabled(true);
                    assignButton.setText("ASSIGN INSTRUCTOR");
                    loadData();
                }
            }
        }.execute();
    }
}