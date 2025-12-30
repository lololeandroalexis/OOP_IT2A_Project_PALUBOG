import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * A Java Swing application that replicates Facebook-style sign up form
 * with fields: First Name, Last Name, Mobile/Email, Password, and Birthdate (MM:DD:YYYY)
 */
public class SignupForm extends JFrame {

    // Define colors for Facebook-like design
    private static final Color PRIMARY_BLUE = new Color(59, 89, 152);
    private static final Color SECONDARY_GREY = new Color(245, 245, 245);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);
    
    // Form fields
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField mobileEmailField;
    private JPasswordField passwordField;
    private JComboBox<String> monthCombo;
    private JComboBox<String> dayCombo;
    private JComboBox<String> yearCombo;

    // Map to hold references to text components for easy validation
    private final Map<String, JComponent> formFields = new HashMap<>();

    public SignupForm() {
        // --- 1. Frame Setup ---
        setTitle("Sign Up - Health Center");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(new Dimension(500, 750));
        setResizable(false);

        // Main container with background color
        JPanel mainContainer = new JPanel();
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setLayout(new BorderLayout());

        // Content panel with white background
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));

        // --- 2. Header Section ---
        JLabel titleLabel = new JLabel("SIGN UP");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(25));

        // --- 3. Name Fields Row (First Name and Last Name) ---
        JLabel firstNameLabel = new JLabel("FIRST NAME");
        firstNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        firstNameLabel.setForeground(TEXT_COLOR);
        firstNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(firstNameLabel);
        contentPanel.add(Box.createVerticalStrut(3));

        JPanel nameRow = new JPanel(new GridLayout(1, 2, 10, 0));
        nameRow.setOpaque(false);
        nameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        nameRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        firstNameField = new JTextField();
        firstNameField.setFont(INPUT_FONT);
        firstNameField.setBorder(createInputFieldBorder());
        if (firstNameField instanceof PlaceholderTextField) {
            ((PlaceholderTextField) firstNameField).setPlaceholder("First name");
        }
        formFields.put("First Name", firstNameField);
        nameRow.add(firstNameField);

        lastNameField = new JTextField();
        lastNameField.setFont(INPUT_FONT);
        lastNameField.setBorder(createInputFieldBorder());
        if (lastNameField instanceof PlaceholderTextField) {
            ((PlaceholderTextField) lastNameField).setPlaceholder("Last name");
        }
        formFields.put("Last Name", lastNameField);
        nameRow.add(lastNameField);

        contentPanel.add(nameRow);
        contentPanel.add(Box.createVerticalStrut(10));

        // Add label for Last Name
        JLabel lastNameLabel = new JLabel("LAST NAME");
        lastNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lastNameLabel.setForeground(TEXT_COLOR);
        lastNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Note: Last Name is already in the row with First Name, so we position it differently

        // --- 4. Mobile Number or Email Field ---
        JLabel mobileEmailLabel = new JLabel("MOBILE NUMBER OR EMAIL");
        mobileEmailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        mobileEmailLabel.setForeground(TEXT_COLOR);
        mobileEmailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(mobileEmailLabel);
        contentPanel.add(Box.createVerticalStrut(3));

        mobileEmailField = new JTextField();
        mobileEmailField.setFont(INPUT_FONT);
        mobileEmailField.setBorder(createInputFieldBorder());
        if (mobileEmailField instanceof PlaceholderTextField) {
            ((PlaceholderTextField) mobileEmailField).setPlaceholder("Mobile number or email");
        }
        mobileEmailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        mobileEmailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formFields.put("Mobile/Email", mobileEmailField);
        contentPanel.add(mobileEmailField);
        contentPanel.add(Box.createVerticalStrut(10));

        // --- 5. Password Field ---
        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(TEXT_COLOR);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(passwordLabel);
        contentPanel.add(Box.createVerticalStrut(3));

        passwordField = new JPasswordField();
        passwordField.setFont(INPUT_FONT);
        passwordField.setBorder(createInputFieldBorder());
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formFields.put("Password", passwordField);
        contentPanel.add(passwordField);
        contentPanel.add(Box.createVerticalStrut(10));

        // --- 6. Birthdate Section (MM:DD:YYYY) ---
        JLabel birthdateLabel = new JLabel("MM:DD:YYYY");
        birthdateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        birthdateLabel.setForeground(TEXT_COLOR);
        birthdateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(birthdateLabel);
        contentPanel.add(Box.createVerticalStrut(3));

        // Birthdate combo boxes
        JPanel birthdateRow = new JPanel(new GridLayout(1, 3, 8, 0));
        birthdateRow.setOpaque(false);
        birthdateRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        birthdateRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Month combo
        String[] months = {"MM", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        monthCombo = new JComboBox<>(months);
        monthCombo.setFont(INPUT_FONT);
        monthCombo.setBorder(createInputFieldBorder());
        monthCombo.setBackground(Color.WHITE);
        formFields.put("Month", monthCombo);
        birthdateRow.add(monthCombo);

        // Day combo
        String[] days = new String[32];
        days[0] = "DD";
        for (int i = 1; i <= 31; i++) {
            days[i] = String.valueOf(i);
        }
        dayCombo = new JComboBox<>(days);
        dayCombo.setFont(INPUT_FONT);
        dayCombo.setBorder(createInputFieldBorder());
        dayCombo.setBackground(Color.WHITE);
        formFields.put("Day", dayCombo);
        birthdateRow.add(dayCombo);

        // Year combo
        String[] years = new String[100];
        years[0] = "YYYY";
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        for (int i = 1; i < 100; i++) {
            years[i] = String.valueOf(currentYear - i + 1);
        }
        yearCombo = new JComboBox<>(years);
        yearCombo.setFont(INPUT_FONT);
        yearCombo.setBorder(createInputFieldBorder());
        yearCombo.setBackground(Color.WHITE);
        formFields.put("Year", yearCombo);
        birthdateRow.add(yearCombo);

        contentPanel.add(birthdateRow);
        contentPanel.add(Box.createVerticalStrut(20));

        // --- 7. Sign Up Button ---
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(BUTTON_FONT);
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setBackground(PRIMARY_BLUE);
        signUpButton.setFocusPainted(false);
        signUpButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        signUpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signUpButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        signUpButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        signUpButton.addActionListener(e -> handleSignUp());
        contentPanel.add(signUpButton);

        // --- 8. Back to Login Link ---
        contentPanel.add(Box.createVerticalStrut(15));
        JPanel loginLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginLinkPanel.setOpaque(false);
        loginLinkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel prompt = new JLabel("Already have an account?");
        prompt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        prompt.setForeground(TEXT_COLOR);

        JLabel loginLink = new JLabel("<html><u>Log in</u></html>");
        loginLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginLink.setForeground(PRIMARY_BLUE);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new LoginScreen().setVisible(true);
                dispose();
            }
        });

        loginLinkPanel.add(prompt);
        loginLinkPanel.add(loginLink);
        contentPanel.add(loginLinkPanel);

        // Add content panel to main container
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainContainer.add(scrollPane, BorderLayout.CENTER);

        add(mainContainer);
        setLocationRelativeTo(null);
    }

    /**
     * Helper method to create a rounded border for input fields
     */
    private Border createInputFieldBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        );
    }

    /**
     * Checks all required form fields for emptiness or invalid states.
     * @return true if the form is valid, false otherwise.
     */
    private boolean validateForm() {
        // Validation 1: First Name
        String firstName = firstNameField.getText().trim();
        if (firstName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter your first name.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validation 2: Last Name
        String lastName = lastNameField.getText().trim();
        if (lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter your last name.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validation 3: Mobile/Email
        String mobileEmail = mobileEmailField.getText().trim();
        if (mobileEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter your mobile number or email.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validation 4: Password
        String password = new String(passwordField.getPassword()).trim();
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a password.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validation 5: Birthdate (check that all fields are selected)
        if (monthCombo.getSelectedIndex() == 0 || dayCombo.getSelectedIndex() == 0 || yearCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, 
                "Please enter your complete birthdate (MM:DD:YYYY).", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Handles the sign-up button action with database registration.
     */
    private void handleSignUp() {
        if (validateForm()) {
            String email = mobileEmailField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            String month = (String) monthCombo.getSelectedItem();
            String day = (String) dayCombo.getSelectedItem();
            String year = (String) yearCombo.getSelectedItem();
            String birthdate = month + ":" + day + ":" + year;

            // Check if email already exists
            if (DatabaseHelper.emailExists(email)) {
                JOptionPane.showMessageDialog(this, 
                    "This email or mobile number is already registered. Please use a different one or log in.", 
                    "Registration Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Register user
            if (DatabaseHelper.registerUser(email, password, firstName, lastName)) {
                JOptionPane.showMessageDialog(this, 
                    "Sign Up Successful! Please log in.", 
                    "Success", 
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
    
    public static void main(String[] args) {
        // Use the event dispatch thread to build and display the GUI
        SwingUtilities.invokeLater(() -> {
            SignupForm form = new SignupForm();
            form.setVisible(true);
        });
    }
}

/**
 * Custom JTextField class to support placeholder text
 */
class PlaceholderTextField extends JTextField {
    private String placeholder;
    private boolean placeholderShowing = false;

    public PlaceholderTextField(int columns) {
        super(columns);
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        if (getText().isEmpty()) {
            showPlaceholder();
        }
    }

    private void showPlaceholder() {
        if (placeholder != null) {
            setText(placeholder);
            setForeground(Color.GRAY);
            placeholderShowing = true;
        }
    }

    private void hidePlaceholder() {
        if (placeholderShowing) {
            setText("");
            setForeground(Color.BLACK);
            placeholderShowing = false;
        }
    }

    @Override
    protected void processFocusEvent(java.awt.event.FocusEvent e) {
        if (e.getID() == java.awt.event.FocusEvent.FOCUS_GAINED) {
            if (placeholderShowing) {
                hidePlaceholder();
            }
        } else if (e.getID() == java.awt.event.FocusEvent.FOCUS_LOST) {
            if (getText().isEmpty() && placeholder != null) {
                showPlaceholder();
            }
        }
        super.processFocusEvent(e);
    }
}
