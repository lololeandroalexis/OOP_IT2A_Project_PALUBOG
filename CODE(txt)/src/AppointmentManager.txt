import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.time.LocalDate;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * A new window for managing patient appointments.
 * This "floats" on top and is accessed from the main dashboard.
 */
public class AppointmentManager extends JFrame {

    // Define standard colors and fonts for consistency
    private static final Color PRIMARY_BLUE = new Color(0, 123, 255);
    private static final Color DARK_BLUE_BACKGROUND = new Color(0, 60, 100);
    private static final Color BACKGROUND_GREY = new Color(245, 245, 245);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Color TEXT_GREY = new Color(100, 100, 100);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    // Components for the calendar
    private JComboBox<String> monthComboBox;
    private JComboBox<Integer> yearComboBox;
    private JPanel daysPanel;
    private LocalDate selectedDate;

    // --- Form Fields for Validation ---
    private JTextField lastnameField;
    private JTextField firstnameField;
    private JTextField middlenameField;
    private JTextField contactNumberField;
    private JTextField emailField;
    private JTextField idNumberField;
    private JTextArea addressTextArea;
    private ButtonGroup serviceGroup;
    private JCheckBox consentCheck;


    public AppointmentManager() {
        setTitle("Barangay Health Center - Appointment Manager");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close to not exit the whole app
        setMinimumSize(new Dimension(900, 700));
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_GREY);

        // Initialize with the current date
        selectedDate = LocalDate.now();

        // Add the main editor panel with scroll pane
        JScrollPane scrollPane = new JScrollPane(createAppointmentPanel());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        setLocationRelativeTo(null); // Center the window
    }

    /**
     * Creates the main panel containing the header, form, and footer.
     */
    private JPanel createAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // --- Header ---
        panel.add(createHeader("MANAGE APPOINTMENTS"), BorderLayout.NORTH);

        // --- Form Content (Placeholder) ---
        panel.add(createFormContent(), BorderLayout.CENTER);

        // --- Footer with Back Button ---
        panel.add(createFooter(), BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates a reusable header with a title and a blue bar.
     * @param titleText The text to display in the header.
     * @return A JPanel for the header.
     */
    private JPanel createHeader(String titleText) {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);

        JPanel titlePanel = new JPanel(new BorderLayout(10, 0));
        titlePanel.setOpaque(false);

        JPanel blueBar = new JPanel();
        blueBar.setPreferredSize(new Dimension(3, 20));
        blueBar.setBackground(PRIMARY_BLUE);
        titlePanel.add(blueBar, BorderLayout.WEST);

        JLabel title = new JLabel(titleText);
        title.setFont(HEADER_FONT);
        title.setForeground(DARK_BLUE_BACKGROUND);
        titlePanel.add(title, BorderLayout.CENTER);

        header.add(titlePanel, BorderLayout.WEST);
        return header;
    }

    /**
     * Creates the main content area for the appointment manager.
     * Currently, it just shows a placeholder message.
     */
    private JComponent createFormContent() {
        JPanel mainContentPanel = new JPanel(new BorderLayout(20, 0));
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(new EmptyBorder(20, 10, 10, 10));

        // --- Center Panel (Calendar and Booking Details) ---
        JPanel centerWrapper = new JPanel(new BorderLayout(0, 20));
        centerWrapper.setOpaque(false);

        // Calendar Date Picker
        centerWrapper.add(createCalendarPanel(), BorderLayout.CENTER);

        // Booking Details Fields
        centerWrapper.add(createBookingDetailsPanel(), BorderLayout.SOUTH);

        mainContentPanel.add(centerWrapper, BorderLayout.CENTER);

        return mainContentPanel;
    }

    /**
     * Creates the panel for booking details like name.
     */
    private JPanel createBookingDetailsPanel() {
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Booking Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 15); // bottom padding, right padding
        gbc.weightx = 1.0;

        // --- Row 1: Name Fields ---
        gbc.gridy = 0;
        gbc.gridx = 0;
        detailsPanel.add(createLabeledTextField("Lastname", "", (field) -> lastnameField = field), gbc);
        gbc.gridx = 1;
        detailsPanel.add(createLabeledTextField("Firstname", "", (field) -> firstnameField = field), gbc);
        gbc.gridx = 2;
        gbc.insets.right = 0; // No right padding for the last item in a row
        detailsPanel.add(createLabeledTextField("Middlename", "", (field) -> middlenameField = field), gbc);

        // --- Row 2: Contact Number ---
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Span two columns to give it more space
        gbc.insets.right = 15; // Restore right inset
        detailsPanel.add(createLabeledTextField("Contact Number", "", (field) -> contactNumberField = field), gbc);

        // --- Row 3: Email ---
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Span two columns
        detailsPanel.add(createLabeledTextField("Email", "", (field) -> emailField = field), gbc);

        // --- Row 4: ID Number ---
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Span two columns
        gbc.insets.top = 10; // Add space above
        detailsPanel.add(createLabeledTextField("ID Number", "", (field) -> idNumberField = field), gbc);

        // --- Row 5: Complete Address ---
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 3; // Span all columns
        gbc.insets.top = 10; // Add space above
        detailsPanel.add(createLabeledTextArea("Complete Address", "", (area) -> addressTextArea = area), gbc);

        // --- Row 6: Online Consultation Checkbox ---
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 3; // Span all columns
        gbc.insets.top = 10; // Add a little space above the checkbox
        gbc.insets.bottom = 0; // Reset bottom inset
        gbc.insets.right = 0;
        detailsPanel.add(createOnlineConsultationCheckbox(), gbc);

        // --- Row 7: Type of Services ---
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 3; // Span all columns
        gbc.insets.top = 10; // Add a little space above the services panel
        gbc.insets.bottom = 10; // Add space below
        detailsPanel.add(createServicesSelectionPanel(), gbc);

        // --- Row 8: Patient Consent Checkbox ---
        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 3; // Span all columns
        gbc.insets.top = 5; // Add a little space above the checkbox
        gbc.insets.bottom = 0; // Reset bottom inset
        detailsPanel.add(createConsentCheckboxPanel(), gbc);

        return detailsPanel;
    }

    /**
     * Creates a panel with a checkbox for online consultation.
     * @return A JPanel containing the checkbox and its label.
     */
    private JPanel createOnlineConsultationCheckbox() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        JCheckBox onlineConsultationCheck = new JCheckBox();
        onlineConsultationCheck.setOpaque(false);
        panel.add(onlineConsultationCheck);
        panel.add(Box.createHorizontalStrut(5)); // Small space after checkbox
        panel.add(new JLabel("I am booking for an Online Consultation"));
        return panel;
    }

    /**
     * Creates a panel with radio buttons for selecting the type of service.
     * @return A JPanel containing the radio buttons for service selection.
     */
    private JPanel createServicesSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5)); // 2 columns, 10px horizontal gap, 5px vertical gap
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Type of Services"));

        serviceGroup = new ButtonGroup();

        String[] services = {
            "Out-Patient Medical Consultation",
            "Issuance of Certificates",
            "Minor Procedures",
            "Animal Bite Treatment",
            "Prenatal Care",
            "Postnatal Care",
            "Child Health Services",
            "Immunization (EPI)",
            "Health Education and Counseling",
            "Family Planning"
        };

        for (String service : services) {
            JRadioButton radioButton = new JRadioButton(service);
            radioButton.setOpaque(false);
            radioButton.setFont(LABEL_FONT);
            serviceGroup.add(radioButton);
            panel.add(radioButton);
        }

        return panel;
    }

    /**
     * Creates a panel with a checkbox and label for the patient consent form.
     * @return A JPanel containing the checkbox and its label.
     */
    private JPanel createConsentCheckboxPanel() {
        // Use a FlowLayout with 0 horizontal gap to keep the labels together
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        consentCheck = new JCheckBox();
        consentCheck.setOpaque(false);
        panel.add(consentCheck);
        panel.add(Box.createHorizontalStrut(5)); // Small space after checkbox

        panel.add(new JLabel("By booking, I have read and accepted the "));
        JLabel consentLink = new JLabel("Patient Consent Form");
        consentLink.setForeground(PRIMARY_BLUE);
        consentLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        consentLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showConsentFormDialog();
            }
        });
        panel.add(consentLink);
        return panel;
    }

    /**
     * Displays a modal dialog with the full patient consent form text.
     */
    private void showConsentFormDialog() {
        JDialog consentDialog = new JDialog(this, "Patient Consent Form", true);
        consentDialog.setSize(700, 600);
        consentDialog.setLocationRelativeTo(this);
        consentDialog.setLayout(new BorderLayout(10, 10));

        JTextArea consentTextArea = new JTextArea();
        consentTextArea.setEditable(false);
        consentTextArea.setLineWrap(true);
        consentTextArea.setWrapStyleWord(true);
        consentTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        consentTextArea.setMargin(new Insets(15, 15, 15, 15));

        String consentContent = getConsentText();
        consentTextArea.setText(consentContent);
        consentTextArea.setCaretPosition(0); // Scroll to the top

        JScrollPane scrollPane = new JScrollPane(consentTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JButton acceptButton = new JButton("Accept Terms");
        acceptButton.addActionListener(e -> consentDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        buttonPanel.add(acceptButton);

        consentDialog.add(scrollPane, BorderLayout.CENTER);
        consentDialog.add(buttonPanel, BorderLayout.SOUTH);

        consentDialog.setVisible(true);
    }

    /**
     * Functional interface for a callback that receives a JTextField.
     */
    @FunctionalInterface
    private interface TextFieldConsumer {
        void accept(JTextField field);
    }

    /**
     * Helper method to create a consistently styled labeled text field.
     * @param labelText The text for the label above the field.
     * @param placeholder The placeholder text inside the field.
     * @param consumer A callback to get the created JTextField instance.
     * @return A JPanel containing the styled component.
     */
    private JPanel createLabeledTextField(String labelText, String placeholder, TextFieldConsumer consumer) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_GREY);
        panel.add(label, BorderLayout.NORTH);

        JTextField textField = new JTextField(placeholder);
        textField.setFont(LABEL_FONT.deriveFont(14f));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 10, 5, 10)
        ));
        panel.add(textField, BorderLayout.CENTER);
        
        // Pass the created text field back to the caller
        if (consumer != null) {
            consumer.accept(textField);
        }
        
        return panel;
    }

    /**
     * Functional interface for a callback that receives a JTextArea.
     */
    @FunctionalInterface
    private interface TextAreaConsumer {
        void accept(JTextArea area);
    }
    /**
     * Helper method to create a consistently styled labeled text area.
     * @param labelText The text for the label above the area.
     * @param placeholder The placeholder text inside the area.
     * @return A JPanel containing the styled component.
     */
    private JPanel createLabeledTextArea(String labelText, String placeholder, TextAreaConsumer consumer) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_GREY);
        panel.add(label, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea(placeholder);
        textArea.setFont(LABEL_FONT.deriveFont(14f));
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 10, 5, 10)
        ));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setRows(3); // Set to 3 rows for address
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Pass the created text area back to the caller
        if (consumer != null) {
            consumer.accept(textArea);
        }

        return panel;
    }

    /**
     * Creates the calendar panel for date selection.
     */
    private JPanel createCalendarPanel() {
        JPanel calendarPanel = new JPanel(new BorderLayout(0, 10));
        calendarPanel.setOpaque(false);
        calendarPanel.setBorder(BorderFactory.createTitledBorder("Choose Your Schedule"));

        // --- Top Controls (Month and Year Dropdowns) ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        controlPanel.setOpaque(false);

        // Month ComboBox
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = java.time.Month.of(i + 1).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        }
        monthComboBox = new JComboBox<>(months);
        monthComboBox.setSelectedIndex(selectedDate.getMonthValue() - 1);
        monthComboBox.addActionListener(e -> updateCalendar());

        // Year ComboBox
        yearComboBox = new JComboBox<>();
        int currentYear = selectedDate.getYear();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            yearComboBox.addItem(i);
        }
        yearComboBox.setSelectedItem(currentYear);
        yearComboBox.addActionListener(e -> updateCalendar());

        controlPanel.add(new JLabel("Month:"));
        controlPanel.add(monthComboBox);
        controlPanel.add(new JLabel("Year:"));
        controlPanel.add(yearComboBox);

        calendarPanel.add(controlPanel, BorderLayout.NORTH);

        // --- Days Grid ---
        daysPanel = new JPanel(new GridLayout(0, 7, 5, 5)); // 7 columns, auto rows
        daysPanel.setOpaque(false);
        daysPanel.setBorder(new EmptyBorder(10, 5, 5, 5));

        // Add day of the week headers
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, SwingConstants.CENTER);
            dayLabel.setFont(BOLD_FONT);
            daysPanel.add(dayLabel);
        }

        calendarPanel.add(daysPanel, BorderLayout.CENTER);

        // Initial population of the calendar
        updateCalendar();

        return calendarPanel;
    }

    /**
     * Repopulates the calendar grid based on the selected month and year.
     */
    private void updateCalendar() {
        // Remove old day buttons (but not the day name headers)
        while (daysPanel.getComponentCount() > 7) {
            daysPanel.remove(daysPanel.getComponentCount() - 1);
        }

        int year = (Integer) yearComboBox.getSelectedItem();
        int month = monthComboBox.getSelectedIndex() + 1;

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstOfMonth = yearMonth.atDay(1);
        int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0, Monday = 1, etc.

        // Add empty placeholders for days before the 1st of the month
        for (int i = 0; i < firstDayOfWeek; i++) {
            daysPanel.add(new JLabel(""));
        }

        // Add buttons for each day of the month
        int daysInMonth = yearMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setFont(LABEL_FONT.deriveFont(14f));
            dayButton.setFocusPainted(false);
            dayButton.setMargin(new Insets(10, 10, 10, 10));
            dayButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            LocalDate thisDate = yearMonth.atDay(day);

            // Disable past dates
            if (thisDate.isBefore(LocalDate.now())) {
                dayButton.setEnabled(false);
                dayButton.setBackground(Color.LIGHT_GRAY);
                dayButton.setForeground(Color.GRAY);
            } else {
                // Style the button
                if (thisDate.equals(selectedDate)) {
                    dayButton.setBackground(PRIMARY_BLUE);
                    dayButton.setForeground(Color.WHITE);
                } else if (thisDate.equals(LocalDate.now())) {
                    dayButton.setBackground(Color.WHITE);
                    dayButton.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 1)); // Highlight today's date
                } else {
                    dayButton.setBackground(Color.WHITE);
                    dayButton.setForeground(Color.BLACK);
                }

                final int dayOfMonth = day;
                dayButton.addActionListener(e -> {
                    selectedDate = YearMonth.of(year, month).atDay(dayOfMonth);
                    // Redraw the calendar to show the new selection
                    updateCalendar();
                });
            }

            daysPanel.add(dayButton);
        }

        // Refresh the panel
        daysPanel.revalidate();
        daysPanel.repaint();
    }

    /**
     * Creates the footer panel with a "Back to Dashboard" button.
     * @return A JPanel for the footer.
     */
    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout()); // Use BorderLayout for left/right alignment
        footer.setOpaque(false);

        JButton backButton = new JButton("Back to Home");
        backButton.setFont(BOLD_FONT);
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(PRIMARY_BLUE);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_BLUE),
            new EmptyBorder(8, 15, 8, 15)
        ));
        backButton.setFocusPainted(false);

        // Action to go back to the dashboard
        backButton.addActionListener(e -> {
            new PatientDashboard().setVisible(true);
            // Close this appointment manager window
            SwingUtilities.getWindowAncestor((Component) e.getSource()).dispose();
        });

        footer.add(backButton, BorderLayout.WEST);

        // --- Book Now Button (on the right) ---
        JButton bookNowButton = new JButton("Book Now");
        bookNowButton.setFont(BOLD_FONT);
        bookNowButton.setBackground(PRIMARY_BLUE);
        bookNowButton.setForeground(Color.WHITE);
        bookNowButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookNowButton.setBorder(new EmptyBorder(8, 25, 8, 25));
        bookNowButton.setFocusPainted(false);

        // Action for booking the appointment
        bookNowButton.addActionListener(e -> {
            if (validateForm()) {
                // If validation passes, show success message
                JOptionPane.showMessageDialog(this,
                    "Appointment booked successfully!",
                    "Booking Confirmation",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        footer.add(bookNowButton, BorderLayout.EAST);
        return footer;
    }

    /**
     * Validates all required fields in the booking form.
     * @return true if the form is valid, false otherwise.
     */
    private boolean validateForm() {
        // Check if a text field is empty
        if (isFieldEmpty(lastnameField, "Lastname") ||
            isFieldEmpty(firstnameField, "Firstname") ||
            isFieldEmpty(middlenameField, "Middlename") ||
            isFieldEmpty(contactNumberField, "Contact Number") ||
            isFieldEmpty(emailField, "Email") ||
            isFieldEmpty(idNumberField, "ID Number") ||
            isFieldEmpty(addressTextArea, "Complete Address")) {
            return false;
        }

        // Check if a service type is selected
        if (serviceGroup.getSelection() == null) {
            JOptionPane.showMessageDialog(this, "Please select a 'Type of Service'.", "Required Field Missing", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Check if consent is given
        if (!consentCheck.isSelected()) {
            JOptionPane.showMessageDialog(this, "You must accept the 'Patient Consent Form' to proceed.", "Consent Required", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true; // All validations passed
    }

    private boolean isFieldEmpty(JTextComponent field, String fieldName) {
        if (field.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "The '" + fieldName + "' field cannot be empty.", "Required Field Missing", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }

    /**
     * Main method for testing this frame independently.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AppointmentManager().setVisible(true);
        });
    }

    /**
     * Helper method to return the full text of the patient consent form.
     * @return A string containing the consent form content.
     */
    private String getConsentText() {
        return "GENERAL CONSENT FOR TREATMENT\n\n" +
            "I, the undersigned patient, or the legally authorized representative of the patient named above, hereby acknowledge and agree to the following:\n\n" +
            "1. Authorization of Care: I voluntarily consent to and authorize the Barangay Health Center (BHC) personnel, including the Barangay Health Workers, nurses, and attending physician (if available), to provide basic health care services, routine diagnostic procedures, and treatments as deemed necessary for my or the patient's condition.\n\n" +
            "2. Scope of Service: The services covered by this consent include, but are not limited to:\n" +
            "   - Taking of vital signs (temperature, blood pressure, pulse rate, respiratory rate).\n" +
            "   - Basic physical assessment and consultation.\n" +
            "   - Distribution of prescribed or free BHC medication, vitamins, and supplements.\n" +
            "   - Minor procedures such as wound cleaning, dressing, and basic first aid.\n" +
            "   - Participation in BHC-initiated programs (e.g., vaccination, immunization, maternal and child health services).\n\n" +
            "3. Understanding of Procedures: I understand that all procedures and treatments provided at the BHC are aimed at promoting health and wellness, and that BHC staff will explain any significant procedures (such as vaccination or minor surgery) to me beforehand.\n\n" +
            "4. Refusal of Care: I understand that I have the right to refuse any procedure or treatment at any time, and that I should inform the BHC staff immediately if I wish to withdraw my consent.\n\n" +
            "5. Privacy and Confidentiality: I acknowledge that my health information will be kept confidential and will only be shared with authorized health personnel for the purpose of my treatment or as required by Philippine law.\n\n" +
            "6. Online Consultation: If this appointment is for an online consultation, I understand that this service is for non-urgent medical concerns and is not a substitute for an in-person emergency visit. I am aware of the potential risks to data privacy inherent in digital communications and will not hold the institution accountable for breaches outside its direct control.\n\n" +
            "By booking this appointment, I confirm that I have read, understood, and accepted the terms outlined in this Patient Consent Form.";
    }
}