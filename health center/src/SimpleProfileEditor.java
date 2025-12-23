import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * A simplified profile editor screen containing only essential fields.
 * This screen is opened from the "Edit Profile" button on the PatientDashboard.
 */
public class SimpleProfileEditor extends JFrame {

    // Define standard colors and fonts for consistency
    private static final Color PRIMARY_BLUE = new Color(0, 123, 255);
    private static final Color DARK_BLUE_BACKGROUND = new Color(0, 60, 100);
    private static final Color BACKGROUND_GREY = new Color(245, 245, 245);
    private static final Color TEXT_GREY = new Color(100, 100, 100);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);

    // Field to store the selected profile image
    private Image selectedProfileImage;

    public SimpleProfileEditor() {
        setTitle("Barangay Health Center - Edit Profile");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_GREY);

        // Add the main editor panel
        add(createEditorPanel(), BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Creates the main panel containing the header, form, and footer.
     */
    private JPanel createEditorPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);

        JPanel titlePanel = new JPanel(new BorderLayout(10, 0));
        titlePanel.setOpaque(false);
        JPanel blueBar = new JPanel();
        blueBar.setPreferredSize(new Dimension(3, 20));
        blueBar.setBackground(PRIMARY_BLUE);
        titlePanel.add(blueBar, BorderLayout.WEST);

        JLabel title = new JLabel("EDIT YOUR PROFILE");
        title.setFont(HEADER_FONT);
        title.setForeground(DARK_BLUE_BACKGROUND);
        titlePanel.add(title, BorderLayout.CENTER);
        header.add(titlePanel, BorderLayout.WEST);

        panel.add(header, BorderLayout.NORTH);

        // --- Form Content ---
        panel.add(createSimpleForm(), BorderLayout.CENTER);

        // --- Footer with Back Button ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setOpaque(false);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(BOLD_FONT);
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(PRIMARY_BLUE);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_BLUE),
            new EmptyBorder(8, 15, 8, 15)
        ));
        backButton.setFocusPainted(false);

        backButton.addActionListener(e -> {
            // Create the dashboard, passing the selected image to it
            PatientDashboard dashboard = new PatientDashboard(selectedProfileImage);
            dashboard.setVisible(true);

            SwingUtilities.getWindowAncestor(panel).dispose();
        });

        footer.add(backButton);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the form with the requested fields.
     */
    private JPanel createSimpleForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(20, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // --- Add the requested fields ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        form.add(createLabeledTextField("First Name", ""), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        form.add(createLabeledTextField("Surname", ""), gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.5;
        form.add(createLabeledTextField("Middle Name", ""), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.5;
        form.add(createLabeledTextField("Email Address", ""), gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.5;
        form.add(createLabeledTextField("City/Town", ""), gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.5;
        // Use a sorted list of countries
        form.add(createLabeledComboBox("Select a Country", getCountryList()), gbc);

        // --- Add the file chooser field ---
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(createFileChooserField("Upload Profile Picture"), gbc);

        // Vertical Glue to push content to the top
        gbc.gridy = 4; gbc.weighty = 1.0; gbc.gridwidth = 2;
        form.add(Box.createVerticalGlue(), gbc);

        return form;
    }

    /** Creates a labeled text field with consistent styling. */
    private JPanel createLabeledTextField(String labelText, String placeholder) {
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

        return panel;
    }

    /** Creates a labeled combo box with consistent styling. */
    private JPanel createLabeledComboBox(String labelText, String[] options) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_GREY);
        panel.add(label, BorderLayout.NORTH);

        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setFont(LABEL_FONT.deriveFont(14f));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(4, 5, 4, 5)
        ));
        panel.add(comboBox, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates a labeled file chooser component with a text field and a button.
     * @param labelText The text for the label.
     * @return A JPanel containing the file chooser components.
     */
    private JPanel createFileChooserField(String labelText) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_GREY);
        panel.add(label, BorderLayout.NORTH);

        // Panel to hold the text field and button
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0));
        fieldPanel.setOpaque(false);

        JTextField filePathField = new JTextField("No file selected");
        filePathField.setEditable(false);
        filePathField.setFont(LABEL_FONT.deriveFont(14f));
        filePathField.setBackground(BACKGROUND_GREY);
        filePathField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 10, 5, 10)
        ));
        fieldPanel.add(filePathField, BorderLayout.CENTER);

        JButton chooseFileButton = new JButton("Choose File");
        chooseFileButton.setFont(BOLD_FONT.deriveFont(12f));
        chooseFileButton.setFocusPainted(false);
        chooseFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            // Filter for image files
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
            int result = fileChooser.showOpenDialog(panel);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
                // Load the image and store it in our member variable
                this.selectedProfileImage = new ImageIcon(selectedFile.getAbsolutePath()).getImage();
            }
        });
        fieldPanel.add(chooseFileButton, BorderLayout.EAST);

        panel.add(fieldPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Returns a sorted array of country names.
     * @return A string array of countries sorted alphabetically.
     */
    private String[] getCountryList() {
        return new String[] {
            "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua and Barbuda", "Argentina", "Armenia", "Australia", "Austria", "Azerbaijan",
            "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei", "Bulgaria", "Burkina Faso", "Burundi",
            "Cabo Verde", "Cambodia", "Cameroon", "Canada", "Central African Republic", "Chad", "Chile", "China", "Colombia", "Comoros", "Congo, Democratic Republic of the", "Congo, Republic of the", "Costa Rica", "Cote d'Ivoire", "Croatia", "Cuba", "Cyprus", "Czech Republic",
            "Denmark", "Djibouti", "Dominica", "Dominican Republic",
            "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Eswatini", "Ethiopia",
            "Fiji", "Finland", "France",
            "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Greece", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana",
            "Haiti", "Honduras", "Hungary",
            "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy",
            "Jamaica", "Japan", "Jordan",
            "Kazakhstan", "Kenya", "Kiribati", "Kosovo", "Kuwait", "Kyrgyzstan",
            "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg",
            "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro", "Morocco", "Mozambique", "Myanmar",
            "Namibia", "Nauru", "Nepal", "Netherlands", "New Zealand", "Nicaragua", "Niger", "Nigeria", "North Korea", "North Macedonia", "Norway",
            "Oman",
            "Pakistan", "Palau", "Palestine State", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal",
            "Qatar",
            "Romania", "Russia", "Rwanda",
            "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Korea", "South Sudan", "Spain", "Sri Lanka", "Sudan", "Suriname", "Sweden", "Switzerland", "Syria",
            "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor-Leste", "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Tuvalu",
            "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States of America", "Uruguay", "Uzbekistan",
            "Vanuatu", "Vatican City", "Venezuela", "Vietnam",
            "Yemen",
            "Zambia", "Zimbabwe"
        };
    }
}