import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * A new window for displaying the Barangay Health Center Contact Information and Operating Hours.
 * This "floats" on top and is accessed from the main dashboard.
 */
public class ContactInformation extends JFrame {

    // Define standard colors and fonts for consistency
    private static final Color PRIMARY_BLUE = new Color(0, 123, 255);
    private static final Color DARK_BLUE_BACKGROUND = new Color(0, 60, 100);
    private static final Color BACKGROUND_GREY = new Color(245, 245, 245);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font SECTION_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);

    public ContactInformation() {
        setTitle("BHC Contact Information and Operating Hours");
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

    private JPanel createMainFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        contentPanel.add(createFormHeader());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createLocationSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createContactChannelsSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createOperatingHoursSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createEmergencyInfoSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createFooterSection());

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFormHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = createCenteredLabel("BARANGAY HEALTH CENTER CONTACT INFORMATION AND OPERATING HOURS", HEADER_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(15));
        headerPanel.add(createCenteredLabel("Republic of the Philippines", LABEL_FONT));
        headerPanel.add(createCenteredLabel("Province of Davao Oriental", LABEL_FONT));
        headerPanel.add(createCenteredLabel("City of Mati", LABEL_FONT));
        headerPanel.add(createCenteredLabel("BARANGAY SAINZ HEALTH CENTER", BOLD_FONT));

        return headerPanel;
    }

    private JPanel createLocationSection() {
        JPanel sectionPanel = createTitledSectionPanel("I. LOCATION AND ADDRESS");
        sectionPanel.setLayout(new GridLayout(0, 1, 0, 10));
        sectionPanel.add(createEditableField("Physical Address", "Mapantad St., Barangay Sainz, City of Mati, Davao Oriental"));
        sectionPanel.add(createEditableField("Landmark/Directions", "Cultural Center, Near Sainz Mati National Comprehensive High School"));
        return sectionPanel;
    }

    private JPanel createContactChannelsSection() {
        JPanel sectionPanel = createTitledSectionPanel("II. CONTACT NUMBERS AND COMMUNICATION CHANNELS");
        sectionPanel.setLayout(new BorderLayout());
        String[] columnNames = {"Channel", "Contact Details", "Person/Office in Charge"};
        Object[][] data = {
            {"BHC Main Landline", "888-3333-2222", "Front Desk / Admin"},
            {"Official Mobile (SMS/Call)", "09453212456", "Dodong Mapantad"},
            {"BHC Email Address", "barangaysainz@gmail.com", "Admin Staff"},
            {"Official Social Media Page (Optional)", "https://www.facebook.com/Sainz-Health-Center", "Information Desk"}
        };
        JTable table = new JTable(data, columnNames);
        table.setFont(LABEL_FONT);
        table.setRowHeight(25);
        sectionPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        return sectionPanel;
    }

    private JPanel createOperatingHoursSection() {
        JPanel sectionPanel = createTitledSectionPanel("III. OPERATING HOURS AND SCHEDULE");
        sectionPanel.setLayout(new BorderLayout(0, 15));

        String[] columnNames = {"Activity", "Day/s", "Time"};
        Object[][] data = {
            {"General Consultation", "Monday - Friday", "[8:00 AM - 12:00 PM] and [1:00 PM - 5:00 PM]"},
            {"Immunization / Vaccination", "Monday - Sunday", "7:00AM - 5:00PM"},
            {"Maternal Care Services", "Monday - Friday", "[8:00 AM - 12:00 PM] and [1:00 PM - 5:00 PM]"},
            {"Child Wellness Services", "Monday - Friday", "[8:00 AM - 12:00 PM] and [1:00 PM - 5:00 PM]"},
            {"Family Planning Services", "Monday - Friday", "[8:00 AM - 12:00 PM] and [1:00 PM - 5:00 PM]"},
            {"Health Education Sessions","Monday - Friday", "[8:00 AM - 12:00 PM] and [1:00 PM - 5:00 PM]"},
            {"Prenatal Check-up", "Monday - Friday", "[8:00 AM - 12:00 PM] and [1:00 PM - 5:00 PM]"}
        };
        JTable table = new JTable(data, columnNames);
        table.setFont(LABEL_FONT);
        table.setRowHeight(25);
        sectionPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.add(createEditableField("Office/Administrative Hours", "Monday - Friday"));

        JPanel holidayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        holidayPanel.setOpaque(false);
        JCheckBox closedCheck = new JCheckBox("Closed");
        JCheckBox onCallCheck = new JCheckBox("On-Call (see Emergency contact below)");
        holidayPanel.add(closedCheck);
        holidayPanel.add(onCallCheck);
        bottomPanel.add(createReadOnlyField("Weekend/Holiday Status", holidayPanel));

        sectionPanel.add(bottomPanel, BorderLayout.SOUTH);
        return sectionPanel;
    }

    private JPanel createEmergencyInfoSection() {
        JPanel sectionPanel = createTitledSectionPanel("IV. EMERGENCY INFORMATION");
        sectionPanel.setLayout(new GridLayout(0, 1, 0, 10));
        sectionPanel.add(createEditableField("Barangay Emergency Hotline", "For immediate transport/assistance."));
        sectionPanel.add(createEditableField("Nearest Hospital", ""));
        return sectionPanel;
    }

    private JPanel createFooterSection() {
        JPanel footerPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        footerPanel.add(createEditableField("Prepared By (Printed Name and Signature of BHC Staff)", ""));
        footerPanel.add(createEditableField("Date Posted", ""));
        return footerPanel;
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
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), new EmptyBorder(5, 10, 5, 10)));
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReadOnlyField(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        panel.add(label, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTitledSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        Border lineBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        panel.setBorder(BorderFactory.createTitledBorder(
            lineBorder, title, TitledBorder.LEFT, TitledBorder.TOP,
            SECTION_TITLE_FONT, DARK_BLUE_BACKGROUND));
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ContactInformation().setVisible(true);
        });
    }
}