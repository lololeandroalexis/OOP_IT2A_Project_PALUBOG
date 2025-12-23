import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * A new window for displaying the Barangay Health Center Health Personnel Directory and Schedule.
 * This "floats" on top and is accessed from the main dashboard.
 */
public class HealthPersonnelDirectory extends JFrame {

    // Define standard colors and fonts for consistency
    private static final Color PRIMARY_BLUE = new Color(0, 123, 255);
    private static final Color DARK_BLUE_BACKGROUND = new Color(0, 60, 100);
    private static final Color BACKGROUND_GREY = new Color(245, 245, 245);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font SECTION_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);

    public HealthPersonnelDirectory() {
        setTitle("BHC Health Personnel Directory and Schedule");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(950, 850));
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_GREY);

        // Add the main form panel with a scroll pane
        JScrollPane scrollPane = new JScrollPane(createMainFormPanel());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        add(scrollPane, BorderLayout.CENTER);

        setLocationRelativeTo(null); // Center the window
    }

    /**
     * Creates the main panel containing the entire form.
     */
    private JPanel createMainFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // --- Main Content ---
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        contentPanel.add(createFormHeader());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createStaffListSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createScheduleSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createBhwAssignmentSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createAcknowledgmentSection());

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the top header of the form.
     */
    private JPanel createFormHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = createCenteredLabel("BARANGAY HEALTH CENTER HEALTH PERSONNEL DIRECTORY AND SCHEDULE", HEADER_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(15));
        headerPanel.add(createCenteredLabel("Republic of the Philippines", LABEL_FONT));
        headerPanel.add(createCenteredLabel("Province of Davao Oriental", LABEL_FONT));
        headerPanel.add(createCenteredLabel("City of Mati", LABEL_FONT));
        headerPanel.add(createCenteredLabel("BARANGAY SAINZ HEALTH CENTER", BOLD_FONT));

        return headerPanel;
    }

    /**
     * Creates the "I. ACTIVE BHC STAFF LIST AND SPECIALIZATION" section.
     */
    private JPanel createStaffListSection() {
        JPanel sectionPanel = createTitledSectionPanel("I. ACTIVE BHC STAFF LIST AND SPECIALIZATION");
        sectionPanel.setLayout(new BorderLayout());

        String[] columnNames = {"Personnel Name", "Designation/Role", "Primary Area of Focus", "Contact (BHC Phone/Extension)"};
        Object[][] data = {
            {"", "Physician / Nurse", "", ""},
            {"", "Midwife / BHW", "", ""},
            {"", "Physician / Nurse", "", ""},
            {"", "Midwife / BHW", "", ""},
            {"", "BHW / Admin Staff", "", ""}
        };
        JTable staffTable = new JTable(data, columnNames);
        staffTable.setFont(LABEL_FONT);
        staffTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        sectionPanel.add(scrollPane, BorderLayout.CENTER);
        return sectionPanel;
    }

    /**
     * Creates the "II. WEEKLY AVAILABILITY SCHEDULE" section.
     */
    private JPanel createScheduleSection() {
        JPanel sectionPanel = createTitledSectionPanel("II. WEEKLY AVAILABILITY SCHEDULE");
        sectionPanel.setLayout(new BorderLayout());

        String[] columnNames = {"Day", "Physician (M.D.)", "Nurse (R.N.)", "Midwife"};
        Object[][] data = {
            {"Monday", "", "", ""},
            {"Tuesday", "", "", ""},
            {"Wednesday", "", "", ""},
            {"Thursday", "", "", ""},
            {"Friday", "", "", ""}
        };
        JTable scheduleTable = new JTable(new DefaultTableModel(data, columnNames));
        scheduleTable.setFont(LABEL_FONT);
        scheduleTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        sectionPanel.add(scrollPane, BorderLayout.CENTER);
        return sectionPanel;
    }

    /**
     * Creates the "III. BHW ASSIGNMENT BY PUROK/ZONE" section.
     */
    private JPanel createBhwAssignmentSection() {
        JPanel sectionPanel = createTitledSectionPanel("III. BHW ASSIGNMENT BY PUROK/ZONE");
        sectionPanel.setLayout(new BorderLayout());

        String[] columnNames = {"Barangay Health Worker (BHW) Name", "Assigned Purok/Zone"};
        Object[][] data = {{"", ""}, {"", ""}, {"", ""}};
        JTable bhwTable = new JTable(data, columnNames);
        bhwTable.setFont(LABEL_FONT);
        bhwTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(bhwTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        sectionPanel.add(scrollPane, BorderLayout.CENTER);
        return sectionPanel;
    }

    /**
     * Creates the "IV. BHC ADMINISTRATOR ACKNOWLEDGMENT" section.
     */
    private JPanel createAcknowledgmentSection() {
        JPanel sectionPanel = createTitledSectionPanel("IV. BHC ADMINISTRATOR ACKNOWLEDGMENT");
        sectionPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        sectionPanel.add(createEditableField("Prepared by (Staff Name)", ""), gbc);
        gbc.gridy = 1;
        sectionPanel.add(createEditableField("Position", ""), gbc);
        gbc.gridy = 2;
        sectionPanel.add(createEditableField("Date Reviewed/Updated", ""), gbc);

        // Note at the bottom
        JLabel noteLabel = new JLabel("Note: Schedule is subject to change without prior notice. Please check with the front desk for immediate availability.");
        noteLabel.setFont(LABEL_FONT);
        gbc.gridy = 3; gbc.insets = new Insets(15, 5, 5, 5);
        sectionPanel.add(noteLabel, gbc);

        return sectionPanel;
    }

    // --- HELPER METHODS ---

    private JLabel createCenteredLabel(String text, Font font) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JPanel createEditableField(String labelText, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        panel.add(label, BorderLayout.NORTH);

        JTextField textField = new JTextField(placeholder);
        textField.setFont(LABEL_FONT.deriveFont(14f));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 10, 5, 10)
        ));
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTitledSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        Border lineBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        panel.setBorder(BorderFactory.createTitledBorder(
            lineBorder, title, TitledBorder.LEFT, TitledBorder.TOP,
            SECTION_TITLE_FONT, DARK_BLUE_BACKGROUND
        ));
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new HealthPersonnelDirectory().setVisible(true);
        });
    }
}