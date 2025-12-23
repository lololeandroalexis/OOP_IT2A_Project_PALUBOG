import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * A new window for displaying the Barangay Health Center Service and Fee Collection Record.
 * This "floats" on top and is accessed from the main dashboard.
 */
public class BillingRecordForm extends JFrame {

    // Define standard colors and fonts for consistency
    private static final Color PRIMARY_BLUE = new Color(0, 123, 255);
    private static final Color DARK_BLUE_BACKGROUND = new Color(0, 60, 100);
    private static final Color BACKGROUND_GREY = new Color(245, 245, 245);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font SECTION_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);

    public BillingRecordForm() {
        setTitle("BHC Service and Fee Collection Record");
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
        contentPanel.add(createServicesSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createPaymentDetailsSection());

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

        JLabel titleLabel = createCenteredLabel("BARANGAY HEALTH CENTER SERVICE AND FEE COLLECTION RECORD", HEADER_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(15));
        JLabel republicLabel = createCenteredLabel("Republic of the Philippines", LABEL_FONT);
        republicLabel.setForeground(PRIMARY_BLUE);
        headerPanel.add(republicLabel);
        JLabel provinceLabel = createCenteredLabel("Province of [Province]", LABEL_FONT);
        provinceLabel.setForeground(PRIMARY_BLUE);
        headerPanel.add(provinceLabel);
        JLabel cityLabel = createCenteredLabel("City/Municipality of [City/Municipality]", LABEL_FONT);
        cityLabel.setForeground(PRIMARY_BLUE);
        headerPanel.add(cityLabel);
        headerPanel.add(createCenteredLabel("BARANGAY [Barangay Name] HEALTH CENTER", BOLD_FONT));

        return headerPanel;
    }

    /**
     * Creates the "I. PATIENT AND TRANSACTION INFORMATION" section.
     */
    private JPanel createPatientInfoSection() {
        JPanel sectionPanel = createTitledSectionPanel("I. PATIENT AND TRANSACTION INFORMATION");
        sectionPanel.setLayout(new GridLayout(2, 2, 15, 10)); // 2x2 grid

        sectionPanel.add(createEditableField("Patient Full Name", ""));
        sectionPanel.add(createEditableField("Date of Service", ""));
        sectionPanel.add(createEditableField("BHC Transaction/Receipt No.", "BHC-TRN-"));
        sectionPanel.add(createEditableField("Health Personnel Attending", ""));

        return sectionPanel;
    }

    /**
     * Creates the "II. ITEMIZED SERVICES RENDERED AND CHARGES" section.
     */
    private JPanel createServicesSection() {
        JPanel sectionPanel = createTitledSectionPanel("II. ITEMIZED SERVICES RENDERED AND CHARGES");
        sectionPanel.setLayout(new BorderLayout());

        // Table for services
        String[] columnNames = {"Item No.", "Description of Service / Supply", "Unit Fee (₱)", "Quantity", "Total Amount (₱)"};
        Object[][] data = {
            {"1.", "", "", "", ""},
            {"2.", "", "", "", ""},
            {"3.", "", "", "", ""},
            {"4.", "", "", "", ""},
            {"5.", "", "", "", ""},
            {"6.", "", "", "", ""},
            {"7.", "", "", "", ""},
            {"8.", "", "", "", ""},
            {"9.", "", "", "", ""},
            {"10.", "", "", "", ""}
        };
        JTable servicesTable = new JTable(data, columnNames);
        servicesTable.setFont(LABEL_FONT);
        servicesTable.setRowHeight(25);
        // servicesTable.setEnabled(false); // Make it editable
        JScrollPane scrollPane = new JScrollPane(servicesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        sectionPanel.add(scrollPane, BorderLayout.CENTER);

        // Summary fields below the table
        JPanel summaryPanel = new JPanel(new GridLayout(3, 2, 15, 5));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        summaryPanel.add(new JLabel("")); // Spacer
        summaryPanel.add(createEditableField("Subtotal of Charges", ""));
        summaryPanel.add(new JLabel("")); // Spacer
        summaryPanel.add(createEditableField("Discount/Subsidy (e.g., Senior/PWD)", ""));
        summaryPanel.add(new JLabel("")); // Spacer
        summaryPanel.add(createEditableField("TOTAL AMOUNT DUE", ""));

        sectionPanel.add(summaryPanel, BorderLayout.SOUTH);

        return sectionPanel;
    }

    /**
     * Creates the "III. PAYMENT DETAILS" section.
     */
    private JPanel createPaymentDetailsSection() {
        JPanel sectionPanel = createTitledSectionPanel("III. PAYMENT DETAILS");
        sectionPanel.setLayout(new GridLayout(0, 2, 15, 10)); // Flexible rows, 2 columns

        sectionPanel.add(createEditableField("Amount Paid (In Words)", ""));
        sectionPanel.add(createEditableField("Amount Paid (In Figures)", "₱ "));

        // Payment Method Checkboxes
        JPanel paymentMethodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        paymentMethodPanel.setOpaque(false);
        JCheckBox cashCheck = new JCheckBox("Cash");
        JCheckBox philhealthCheck = new JCheckBox("PhilHealth/Program Covered");
        JCheckBox donationCheck = new JCheckBox("Donation/In-Kind");
        paymentMethodPanel.add(cashCheck);
        paymentMethodPanel.add(philhealthCheck);
        paymentMethodPanel.add(donationCheck);
        sectionPanel.add(createReadOnlyField("Payment Method", paymentMethodPanel));

        sectionPanel.add(createEditableField("Change Given", "₱ "));
        sectionPanel.add(createEditableField("Payer Signature", ""));

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
     * Helper to create a read-only field with a label above it.
     */
    private JPanel createReadOnlyField(String labelText, String valueText) {
        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        panel.add(label, BorderLayout.NORTH);

        JLabel value = new JLabel(valueText);
        value.setFont(LABEL_FONT.deriveFont(14f));
        panel.add(value, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Helper to create a read-only field with a label and a custom component.
     */
    private JPanel createReadOnlyField(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        panel.add(label, BorderLayout.NORTH);

        panel.add(component, BorderLayout.CENTER);

        return panel;
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
            new BillingRecordForm().setVisible(true);
        });
    }
}
