import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * A new window for displaying the Barangay Health Center Diagnostic Result Slip.
 * This "floats" on top and is accessed from the main dashboard.
 */
public class DiagnosticResultSlip extends JFrame {

    // Define standard colors and fonts for consistency
    private static final Color PRIMARY_BLUE = new Color(0, 123, 255);
    private static final Color DARK_BLUE_BACKGROUND = new Color(0, 60, 100);
    private static final Color BACKGROUND_GREY = new Color(245, 245, 245);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font SECTION_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);

    public DiagnosticResultSlip() {
        setTitle("BHC Diagnostic Result Slip");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(900, 800));
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
        contentPanel.add(createPatientInfoSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createResultsSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createImpressionSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createCertificationSection());

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

        JLabel titleLabel = createCenteredLabel("BARANGAY HEALTH CENTER DIAGNOSTIC RESULT SLIP", HEADER_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(15));
        JLabel republicLabel = createCenteredLabel("Republic of the Philippines", LABEL_FONT);
        headerPanel.add(republicLabel);
        JLabel provinceLabel = createCenteredLabel("Province of [Province]", LABEL_FONT);
        headerPanel.add(provinceLabel);
        JLabel cityLabel = createCenteredLabel("City/Municipality of [City/Municipality]", LABEL_FONT);
        headerPanel.add(cityLabel);
        headerPanel.add(createCenteredLabel("BARANGAY [Barangay Name] HEALTH CENTER", BOLD_FONT));

        return headerPanel;
    }

    /**
     * Creates the "I. PATIENT AND REQUEST INFORMATION" section.
     */
    private JPanel createPatientInfoSection() {
        JPanel sectionPanel = createTitledSectionPanel("I. PATIENT AND REQUEST INFORMATION");
        sectionPanel.setLayout(new GridLayout(3, 2, 15, 10)); // 3x2 grid

        sectionPanel.add(createEditableField("Patient Full Name", ""));
        sectionPanel.add(createEditableField("Date of Birth", ""));
        sectionPanel.add(createEditableField("Address/Contact No.", ""));
        sectionPanel.add(createEditableField("Requesting Personnel", ""));
        sectionPanel.add(createEditableField("Date of Specimen Collection", ""));
        sectionPanel.add(createEditableField("Date Reported", ""));

        return sectionPanel;
    }

    /**
     * Creates the "II. DIAGNOSTIC RESULTS" section.
     */
    private JPanel createResultsSection() {
        JPanel sectionPanel = createTitledSectionPanel("II. DIAGNOSTIC RESULTS");
        sectionPanel.setLayout(new BorderLayout());

        // Table for results
        String[] columnNames = {"Test/Parameter", "Result", "Unit of Measurement", "Reference Range/Normal Value", "Remarks"};
        Object[][] data = {
            {"1.", "", "", "", ""},
            {"2.", "", "", "", ""},
            {"3.", "", "", "", ""},
            {"4.", "", "", "", ""},
            {"5.", "", "", "", ""}
        };
        JTable resultsTable = new JTable(data, columnNames);
        resultsTable.setFont(LABEL_FONT);
        resultsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        sectionPanel.add(scrollPane, BorderLayout.CENTER);
        return sectionPanel;
    }

    /**
     * Creates the "III. IMPRESSION AND INTERPRETATION" section.
     */
    private JPanel createImpressionSection() {
        JPanel sectionPanel = createTitledSectionPanel("III. IMPRESSION AND INTERPRETATION");
        sectionPanel.setLayout(new BorderLayout());

        JTextArea impressionArea = new JTextArea();
        impressionArea.setRows(4);
        impressionArea.setFont(LABEL_FONT.deriveFont(14f));
        impressionArea.setLineWrap(true);
        impressionArea.setWrapStyleWord(true);
        impressionArea.setBorder(new EmptyBorder(5, 10, 5, 10));
        JScrollPane scrollPane = new JScrollPane(impressionArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        sectionPanel.add(scrollPane, BorderLayout.CENTER);
        return sectionPanel;
    }

    /**
     * Creates the "IV. CERTIFICATION AND RELEASE" section.
     */
    private JPanel createCertificationSection() {
        JPanel sectionPanel = createTitledSectionPanel("IV. CERTIFICATION AND RELEASE");
        sectionPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for more control
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 0.5;

        // Row 0
        gbc.gridx = 0; gbc.gridy = 0;
        sectionPanel.add(createEditableField("Prepared by (BHC Staff/Technician)", ""), gbc);
        gbc.gridx = 1;
        sectionPanel.add(createEditableField("Signature", ""), gbc);

        // Row 1
        gbc.gridx = 0; gbc.gridy = 1;
        sectionPanel.add(createEditableField("Position", ""), gbc);
        gbc.gridx = 1;
        sectionPanel.add(createEditableField("Date Released to Patient", ""), gbc);

        // Note to patient
        JLabel noteLabel = new JLabel("<html><b>Note to Patient:</b> These results should be reviewed by a qualified healthcare provider (e.g., your BHC physician, nurse, or midwife) for proper interpretation and guidance.</html>");
        noteLabel.setFont(LABEL_FONT);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5); // Add top margin
        sectionPanel.add(noteLabel, gbc);

        return sectionPanel;
    }

    // --- HELPER METHODS ---

    /**
     * Helper to create a centered JLabel.
     */
    private JLabel createCenteredLabel(String text, Font font) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Helper to create an editable text field with a label above it.
     */
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

    /**
     * Helper to create a JPanel with a styled titled border.
     */
    private JPanel createTitledSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        Border lineBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        panel.setBorder(BorderFactory.createTitledBorder(
            lineBorder,
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            SECTION_TITLE_FONT,
            DARK_BLUE_BACKGROUND
        ));
        return panel;
    }

    /**
     * Main method for testing this frame independently.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new DiagnosticResultSlip().setVisible(true);
        });
    }
}