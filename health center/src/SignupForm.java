import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * A Java Swing application that replicates the "SIGN UP" form layout
 * shown in the provided screenshot, using GridBagLayout for flexible positioning.
 */
public class SignupForm extends JFrame {

    // Define colors for a cleaner, modern look
    private static final Color PRIMARY_BLUE = new Color(0, 123, 255);
    private static final Color SECONDARY_GREY = new Color(235, 235, 235);
    private static final Color BUTTON_HOVER_GREY = new Color(220, 220, 220);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    
    // Fields to be accessed by the validation method
    private JComboBox<String> titleCombo;
    private final JTextField lastNameField;
    private JTextField middleNameField;
    private final JTextField firstNameField;
    private JTextField birthdateField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox termsCheck;

    // Map to hold references to text components for easy validation
    private final Map<String, JComponent> formFields = new HashMap<>();

    public SignupForm() {
        // --- 1. Frame Setup ---
        setTitle("Barangay Health Center - Sign Up");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Use DISPOSE_ON_CLOSE
        setMinimumSize(new Dimension(600, 800)); // Adjusted for a narrower, taller layout

        // Use a main panel with GridBagLayout for flexible, complex layout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        mainPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5); // Padding around components

        // --- 2. Title Section (SIGN UP) ---

        // Custom panel for the blue bar and text
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);

        // Blue vertical bar (mimicking the design element)
        JPanel blueBar = new JPanel();
        blueBar.setPreferredSize(new Dimension(5, 30));
        blueBar.setBackground(PRIMARY_BLUE);
        headerPanel.add(blueBar, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("SIGN UP");
        titleLabel.setFont(TITLE_FONT);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span 2 columns
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(headerPanel, gbc);

        // Reset width for subsequent rows
        gbc.gridwidth = 1;

        // --- 3. Name Fields (Title, Last Name, Middle Name, First Name, Suffix) ---
        // Row 1: Title and First Name
        String[] titles = {"Title", "Mr.", "Ms.", "Dr."};
        titleCombo = new JComboBox<>(titles);
        formFields.put("Title", titleCombo); // Store for validation
        JPanel titlePanel = createInputPanel(titleCombo, "Title *");
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.5;
        mainPanel.add(titlePanel, gbc);

        firstNameField = new JTextField();
        formFields.put("First Name", firstNameField);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.5;
        mainPanel.add(createInputPanel(firstNameField, "First Name *"), gbc);

        // Row 2: Middle Name and Last Name
        middleNameField = new JTextField();
        formFields.put("Middle Name", middleNameField);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.5;
        mainPanel.add(createInputPanel(middleNameField, "Middle Name *"), gbc);

        lastNameField = new JTextField();
        formFields.put("Last Name", lastNameField);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.5;
        mainPanel.add(createInputPanel(lastNameField, "Last Name *"), gbc);

        // Add note for Middle Name
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.5;
        gbc.insets = new Insets(-5, 5, 0, 5); // Move up closer to the field
        JLabel middleNameNote = new JLabel("* If no Middle Name, write dash (-)");
        middleNameNote.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        middleNameNote.setForeground(Color.GRAY);
        mainPanel.add(middleNameNote, gbc);

        // Row 3: Suffix
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.5;
        gbc.insets = new Insets(10, 5, 10, 5); // Reset padding
        mainPanel.add(createInputField("Suffix"), gbc);


        // --- 4. Date/Age/Gender Fields ---

        // Row 4: Birthdate
        birthdateField = new JTextField();
        formFields.put("Birthdate", birthdateField);
        JButton calendarButton = new JButton("📅"); // Placeholder for calendar icon
        calendarButton.setFocusPainted(false);
        calendarButton.setBackground(Color.WHITE);
        calendarButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // Add action listener for calendar button if needed

        JPanel birthdateInput = new JPanel(new BorderLayout());
        birthdateInput.add(birthdateField, BorderLayout.CENTER);
        birthdateInput.add(calendarButton, BorderLayout.EAST);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1; gbc.weightx = 0.5;
        mainPanel.add(createInputPanel(birthdateInput, "Birthdate *"), gbc);

        // Row 4: Age (Not required, no need to store)
        gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 1; gbc.weightx = 0.5;
        mainPanel.add(createInputField("Age"), gbc);

        // Row 5: Gender
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        genderPanel.setOpaque(false);

        JRadioButton maleRadio = new JRadioButton("Male");
        JRadioButton femaleRadio = new JRadioButton("Female");
        maleRadio.setOpaque(false);
        femaleRadio.setOpaque(false);

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);

        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);

        // Create a styled border area for the Gender selection to mimic the input style
        JPanel genderContainer = new JPanel(new BorderLayout());
        genderContainer.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            "Gender", // Placeholder Title
            0,
            0,
            LABEL_FONT.deriveFont(Font.PLAIN),
            Color.DARK_GRAY
        ));
        genderContainer.add(genderPanel, BorderLayout.CENTER);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.weightx = 1.0;
        mainPanel.add(genderContainer, gbc);

        // --- 5. Patient Account Section ---

        // Label "Patient Account"
        JLabel patientAccountLabel = new JLabel("Patient Account");
        patientAccountLabel.setFont(LABEL_FONT.deriveFont(Font.BOLD, 16f));
        patientAccountLabel.setForeground(PRIMARY_BLUE.darker());
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 5; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 5, 10, 5); // Extra space above
        mainPanel.add(patientAccountLabel, gbc);
        gbc.insets = new Insets(10, 5, 10, 5); // Reset

        // Row 3: Email
        emailField = new JTextField();
        formFields.put("Email", emailField); // Store for validation
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2; gbc.weightx = 1.0;
        mainPanel.add(createInputPanel(emailField, "Email *"), gbc); // Use helper

        // Row 3: Password
        passwordField = new JPasswordField(); // Keep as JPasswordField
        formFields.put("Password", passwordField);
        passwordField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        JButton eyeButton = new JButton("👁"); // Placeholder for eye icon
        eyeButton.setFocusPainted(false);
        eyeButton.setBackground(Color.WHITE);
        eyeButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel passwordInput = new JPanel(new BorderLayout());
        passwordInput.add(passwordField, BorderLayout.CENTER);
        passwordInput.add(eyeButton, BorderLayout.EAST);

        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2; gbc.weightx = 1.0;
        mainPanel.add(createInputPanel(passwordInput, "Password *"), gbc);

        // Row 3: Contact Number (Required, but stored in a nested structure, so we access the inner field)
        JTextField contactField = new JTextField(); // This is the actual field for input
        formFields.put("Contact Number", contactField); // Store inner field for validation
        JLabel prefixLabel = new JLabel("+63"); // Prefix as per the screenshot
        prefixLabel.setFont(contactField.getFont());
        prefixLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPanel contactInput = new JPanel(new BorderLayout());
        contactInput.add(prefixLabel, BorderLayout.WEST);
        contactInput.add(contactField, BorderLayout.CENTER);

        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2; gbc.weightx = 1.0;
        // Add a small label for contact number title
        JLabel contactTitle = new JLabel("Contact Number *");
        contactTitle.setFont(LABEL_FONT.deriveFont(Font.PLAIN));
        contactTitle.setForeground(Color.DARK_GRAY);
        contactTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPanel contactContainer = new JPanel(new BorderLayout());
        contactContainer.setOpaque(false);
        contactContainer.add(contactTitle, BorderLayout.NORTH);
        contactContainer.add(createInputPanel(contactInput, null), BorderLayout.CENTER);
        
        mainPanel.add(contactContainer, gbc);

        // --- 6. Terms and Conditions Checkbox ---

        termsCheck = new JCheckBox("I agree to the ");
        formFields.put("Terms Agreement", termsCheck); // Store for validation
        termsCheck.setOpaque(false);
        termsCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Create a fake link for Terms of Service and Privacy Policy
        JLabel termsLink = new JLabel("<html><u>Terms of Service and Privacy Policy</u></html>");
        termsLink.setForeground(PRIMARY_BLUE);
        termsLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel termsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        termsPanel.setOpaque(false);
        termsPanel.add(termsCheck);
        termsPanel.add(termsLink);

        gbc.gridx = 0; gbc.gridy = 11; gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.insets = new Insets(15, 5, 30, 5); // Padding around checkbox
        mainPanel.add(termsPanel, gbc);

        // --- 7. Sign Up Button ---

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(LABEL_FONT.deriveFont(Font.BOLD, 16f));
        signUpButton.setForeground(Color.BLACK);
        signUpButton.setBackground(SECONDARY_GREY);
        signUpButton.setFocusPainted(false);
        signUpButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        signUpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Update action listener to perform validation
        signUpButton.addActionListener(e -> handleSignUp());
        
        // Center the button in the remaining space
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 5, 10, 5);
        mainPanel.add(signUpButton, gbc);

        // --- 8. Back to Login Link ---
        JPanel loginLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginLinkPanel.setOpaque(false);

        JLabel prompt = new JLabel("Already have an account?");
        prompt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        prompt.setForeground(Color.GRAY);

        JLabel loginLink = new JLabel("Log in");
        loginLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginLink.setForeground(PRIMARY_BLUE);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new LoginScreen().setVisible(true);
                dispose(); // Close the current signup form
            }
        });

        loginLinkPanel.add(prompt);
        loginLinkPanel.add(loginLink);

        gbc.gridy = 13; // Place it below the sign up button
        mainPanel.add(loginLinkPanel, gbc);

        // Add main panel to frame
        add(mainPanel);
        pack(); // Pack components to their preferred size
        setLocationRelativeTo(null); // Center the window
    }

    /**
     * Checks all required form fields for emptiness or invalid states.
     * Displays a JOptionPane warning for the first missing field found.
     * @return true if the form is valid, false otherwise.
     */
    private boolean validateForm() {
        // Validation 1: Title selection (must not be the default "Title")
        if (titleCombo.getSelectedIndex() == 0) {
            // Updated message to match the requested generic format
            JOptionPane.showMessageDialog(this, 
                "The 'Title' field is required and cannot be empty.", 
                "Required Field Missing", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validation 2: Text fields
        String[] requiredFields = {"Last Name", "First Name", "Birthdate", "Email", "Password", "Contact Number"};
        
        for (String fieldName : requiredFields) {
            JComponent component = formFields.get(fieldName);
            String value = "";
            
            if (component instanceof JTextField) {
                value = ((JTextField) component).getText().trim();
            } else if (component instanceof JPasswordField) {
                value = new String(((JPasswordField) component).getPassword()).trim();
            }

            if (value.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "The '" + fieldName + "' field is required and cannot be empty.", 
                    "Required Field Missing", 
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        
        // Validation 3: Middle Name specific check
        // Check if Middle Name is empty and user did not input the required dash
        String middleNameValue = middleNameField.getText().trim();
        if (middleNameValue.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "The 'Middle Name' field is required. If you don't have one, please write a dash (-).",
                "Required Field Missing", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Validation 4: Terms and Conditions
        if (!termsCheck.isSelected()) {
            JOptionPane.showMessageDialog(this, 
                "You must agree to the Terms of Service and Privacy Policy.", 
                "Required Agreement Missing", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true; // All checks passed
    }

    /**
     * Handles the sign-up button action with database registration.
     */
    private void handleSignUp() {
        if (validateForm()) {
            String email = emailField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String password = new String(passwordField.getPassword());

            // Check if email already exists
            if (DatabaseHelper.emailExists(email)) {
                JOptionPane.showMessageDialog(this, 
                    "This email is already registered. Please use a different email or log in.", 
                    "Email Already Exists", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Register user
            if (DatabaseHelper.registerUser(email, password, firstName, lastName)) {
                JOptionPane.showMessageDialog(this, 
                    "Sign Up Successful! Please log in.", 
                    "Registration Complete", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                new LoginScreen().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Registration failed. Please try again.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Helper function to create a text field wrapped in a panel with a titled border,
     * mimicking a modern floating label input.
     * @param labelText The text for the titled border (e.g., "Last Name *").
     * @return A JPanel containing the JTextField.
     */
    private JPanel createInputField(String labelText) {
        JTextField field = new JTextField();
        // Since we are now manually tracking required fields, we don't rely on this generic method for tracking.
        return createInputPanel(field, labelText);
    }

    /**
     * Helper function to create a component wrapped in a panel with a titled border.
     * @param component The JComponent to wrap (JTextField, JComboBox, etc.).
     * @param labelText The text for the titled border (e.g., "Last Name *").
     * @return A JPanel containing the component.
     */
    private JPanel createInputPanel(JComponent component, String labelText) {
        JPanel panel = new JPanel(new BorderLayout());

        // Remove default border from component for a cleaner look
        if (component instanceof JTextField || component instanceof JPasswordField || component instanceof JComboBox) {
             component.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        }
        
        // Apply a titled border to act as the label, unless labelText is null (used for Contact Number structure)
        if (labelText != null) {
            panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true), // Rounded grey border
                labelText,
                0, // Title justification (Left)
                0, // Title position (Top)
                LABEL_FONT.deriveFont(Font.PLAIN),
                Color.DARK_GRAY // Label color
            ));
        }

        panel.add(component, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(150, 55)); // Set preferred height
        return panel;
    }

    public static void main(String[] args) {
        // Use the event dispatch thread to build and display the GUI
        SwingUtilities.invokeLater(() -> {
            SignupForm form = new SignupForm();
            form.setVisible(true); // The creator of the form should make it visible
        });
    }
}
