import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Java Swing application for the "WELCOME BACK" Login Screen, replicating
 * the look and feel of the provided screenshot with functional MySQL database authentication.
 *
 * Features implemented:
 * - Centered card layout.
 * - Custom colors and fonts (Primary Blue).
 * - Styled text fields with borders.
 * - Styled Sign In button with database validation.
 * - Hyperlinks for "Sign up" (functional).
 * - Contact information section.
 * - MySQL database integration for user authentication.
 */
public class LoginScreen extends JFrame {

    // Define standard colors and fonts
    private static final Color PRIMARY_BLUE = new Color(0, 102, 204);
    private static final Color BACKGROUND_GRAY = new Color(248, 248, 248);
    private static final Color TEXT_GRAY = new Color(100, 100, 100);
    private static final Color ERROR_RED = new Color(220, 53, 69);
    private static final Font INTER_FONT = new Font("Inter", Font.PLAIN, 14);
    private static final Font WELCOME_FONT = new Font("Inter", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Inter", Font.BOLD, 12);

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton signInButton;
    private JLabel statusLabel;

    public LoginScreen() {
        setTitle("Barangay Health Center - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Set a preferred size for the window
        setPreferredSize(new Dimension(500, 750));
        
        // Use a background panel for centering the login card
        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        backgroundPanel.setBackground(Color.WHITE); 
        
        // Create the main login card
        JPanel loginCard = createLoginCard();
        
        // Add the login card to the center of the background panel
        backgroundPanel.add(loginCard);

        add(backgroundPanel);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Creates the main container panel for the login form.
     */
    private JPanel createLoginCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(30, 40, 30, 40)); 
        
        card.add(createHeader());
        card.add(Box.createVerticalStrut(30));
        
        card.add(createEmailField());
        card.add(Box.createVerticalStrut(15));

        card.add(createPasswordField()); 
        card.add(Box.createVerticalStrut(20));

        // Status label for error/success messages
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        statusLabel.setForeground(ERROR_RED);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(15));

        card.add(createSignInButton());
        card.add(Box.createVerticalStrut(15));
        
        card.add(createSignUpLink());
        card.add(Box.createVerticalStrut(30));
        
        card.add(new JSeparator(SwingConstants.HORIZONTAL));
        card.add(Box.createVerticalStrut(30));

        card.add(createContactInfo());
        
        return card;
    }

    /**
     * Creates the "WELCOME BACK" header with the blue vertical bar.
     */
    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setOpaque(false);
        
        JPanel blueBar = new JPanel();
        blueBar.setPreferredSize(new Dimension(3, 20));
        blueBar.setBackground(PRIMARY_BLUE);
        headerPanel.add(blueBar);
        headerPanel.add(Box.createHorizontalStrut(8));
        
        JLabel welcomeLabel = new JLabel("WELCOME");
        welcomeLabel.setFont(WELCOME_FONT);
        welcomeLabel.setForeground(PRIMARY_BLUE);
        headerPanel.add(welcomeLabel);

        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return headerPanel;
    }

    /**
     * Creates the styled Email Address text field with its label.
     */
    private JPanel createEmailField() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel("Email Address");
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_GRAY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(label);
        container.add(Box.createVerticalStrut(5));

        emailField = new JTextField("");
        emailField.setFont(INTER_FONT);
        emailField.setForeground(Color.BLACK); 
        emailField.setBackground(BACKGROUND_GRAY); 

        Border line = new LineBorder(Color.LIGHT_GRAY, 1, true);
        Border padding = new EmptyBorder(10, 10, 10, 10);
        emailField.setBorder(new CompoundBorder(line, padding));
        
        emailField.setMaximumSize(new Dimension(Short.MAX_VALUE, emailField.getPreferredSize().height));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        container.add(emailField);
        return container;
    }

    /**
     * Creates the password field component with its label.
     */
    private JPanel createPasswordField() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel("Password");
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_GRAY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(label);
        container.add(Box.createVerticalStrut(5));

        passwordField = new JPasswordField("");
        passwordField.setEchoChar('•'); 
        passwordField.setFont(INTER_FONT);
        passwordField.setForeground(Color.BLACK); 
        passwordField.setBackground(BACKGROUND_GRAY); 

        Border line = new LineBorder(PRIMARY_BLUE, 2, true);
        Border padding = new EmptyBorder(10, 10, 10, 10);
        passwordField.setBorder(new CompoundBorder(line, padding));
        
        passwordField.setMaximumSize(new Dimension(Short.MAX_VALUE, passwordField.getPreferredSize().height));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        container.add(passwordField);
        return container;
    }

    /**
     * Creates the styled Sign In button with database authentication.
     */
    private JButton createSignInButton() {
        signInButton = new JButton("Sign In");
        signInButton.setFont(INTER_FONT.deriveFont(Font.BOLD, 16f));
        signInButton.setBackground(PRIMARY_BLUE);
        signInButton.setForeground(Color.WHITE);
        signInButton.setFocusPainted(false);
        signInButton.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        
        signInButton.setMaximumSize(new Dimension(Short.MAX_VALUE, signInButton.getPreferredSize().height));
        signInButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        signInButton.addActionListener(e -> authenticateUser());
        
        return signInButton;
    }

    /**
     * Authenticates user against MySQL database.
     */
    private void authenticateUser() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both email and password.");
            statusLabel.setForeground(ERROR_RED);
            return;
        }

        signInButton.setEnabled(false);
        signInButton.setText("Signing In...");

        new Thread(() -> {
            try {
                String[] authResult = DatabaseHelper.authenticateUserWithId(email, password);

                if (authResult != null) {
                    int userId = Integer.parseInt(authResult[0]);
                    String role = authResult[1];
                    boolean isSuspended = false;

                    // Check if staff account is suspended
                    if (role.equalsIgnoreCase("STAFF")) {
                        isSuspended = DatabaseHelper.isStaffSuspended(userId);
                    }

                    final boolean suspended = isSuspended;
                    final int userIdFinal = userId;
                    final String roleFinal = role;

                    SwingUtilities.invokeLater(() -> {
                        if (suspended) {
                            statusLabel.setText("SUSPENDED");
                            statusLabel.setForeground(ERROR_RED);
                            JOptionPane.showMessageDialog(this, 
                                "Account suspended. You no longer have access to the system.", 
                                "Access Denied", 
                                JOptionPane.WARNING_MESSAGE);
                            signInButton.setEnabled(true);
                            signInButton.setText("Sign In");
                        } else {
                            // Account is active, proceed with login
                            if (role.equalsIgnoreCase("STAFF")) {
                                statusLabel.setText("ACTIVE");
                                statusLabel.setForeground(new Color(40, 167, 69));
                            }
                            proceedWithLogin(userIdFinal, roleFinal, email);
                        }
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Invalid email or password.");
                        statusLabel.setForeground(ERROR_RED);
                        signInButton.setEnabled(true);
                        signInButton.setText("Sign In");
                        passwordField.setText("");
                    });
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Database error: " + ex.getMessage());
                    statusLabel.setForeground(ERROR_RED);
                    signInButton.setEnabled(true);
                    signInButton.setText("Sign In");
                });
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * Proceeds with opening the appropriate dashboard for the authenticated user.
     */
    private void proceedWithLogin(int userId, String role, String email) {
        try {
            System.out.println("User ID: " + userId + ", Role: " + role);
            JFrame dashboard = null;
            switch (role.toUpperCase()) {
                case "PATIENT":
                    dashboard = new PatientDashboard(userId, email);
                    break;
                case "STAFF":
                    dashboard = new StaffDashboard();
                    break;
                case "ADMIN":
                    dashboard = new AdminDashboard();
                    break;
                default:
                    System.err.println("Unknown role: " + role);
                    JOptionPane.showMessageDialog(null, "Unknown role: " + role);
                    signInButton.setEnabled(true);
                    signInButton.setText("Sign In");
                    return;
            }

            if (dashboard != null) {
                statusLabel.setText("Login successful!");
                statusLabel.setForeground(new Color(40, 167, 69));
                dashboard.setVisible(true);
                LoginScreen.this.dispose();
            } else {
                signInButton.setEnabled(true);
                signInButton.setText("Sign In");
            }
        } catch (Throwable dashboardError) {
            // Capture full stack trace and show to user
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            dashboardError.printStackTrace(pw);
            pw.flush();
            String trace = sw.toString();
            System.err.println("ERROR opening dashboard:");
            System.err.println(trace);
            JTextArea ta = new JTextArea(trace);
            ta.setEditable(false);
            ta.setCaretPosition(0);
            JScrollPane sp = new JScrollPane(ta);
            sp.setPreferredSize(new Dimension(800, 400));
            JOptionPane.showMessageDialog(null, sp, "Error opening dashboard", JOptionPane.ERROR_MESSAGE);
            signInButton.setEnabled(true);
            signInButton.setText("Sign In");
            statusLabel.setText("Login failed due to internal error.");
            statusLabel.setForeground(ERROR_RED);
        }
    }
    private JPanel createSignUpLink() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setOpaque(false);
        
        JLabel prompt = new JLabel("Don't have an account yet?");
        prompt.setFont(INTER_FONT.deriveFont(12f));
        prompt.setForeground(TEXT_GRAY);
        
        JLabel signUpLink = new JLabel("Sign up");
        signUpLink.setFont(INTER_FONT.deriveFont(Font.BOLD, 12f));
        signUpLink.setForeground(PRIMARY_BLUE);
        signUpLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        signUpLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new SignupForm().setVisible(true);
                LoginScreen.this.dispose();
            }
        });
        
        panel.add(prompt);
        panel.add(signUpLink);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    /**
     * Creates the contact information section at the bottom.
     */
    private JPanel createContactInfo() {
        JPanel contactPanel = new JPanel();
        contactPanel.setOpaque(false);
        contactPanel.setLayout(new BoxLayout(contactPanel, BoxLayout.Y_AXIS));
        
        contactPanel.add(createContactRow("📧", "mail@barangayhealthcenter.com"));
        contactPanel.add(Box.createVerticalStrut(10));
        contactPanel.add(createContactRow("📞", "(632) 8988-1000 / (632) 8988-7000"));
        
        contactPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return contactPanel;
    }

    /**
     * Helper to create a row with an icon and contact text.
     */
    private JPanel createContactRow(String icon, String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        iconLabel.setForeground(PRIMARY_BLUE);
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(INTER_FONT);
        textLabel.setForeground(TEXT_GRAY.darker());
        
        row.add(iconLabel);
        row.add(textLabel);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        return row;
    }

    public static void main(String[] args) {
        // Initialize database tables on first run
        DatabaseHelper.initializeDatabase();
        
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}