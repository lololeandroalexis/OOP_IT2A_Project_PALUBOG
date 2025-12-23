import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.border.Border;

/**
 * A Java Swing application that replicates the Patient Information Editor
 * from the provided screenshot, featuring a left status panel and a
 * tabbed content area for basic information.
 */
public class PatientProfileEditor extends JFrame {

    // Define standard colors and fonts
    private static final Color PRIMARY_BLUE = new Color(0, 123, 255);
    private static final Color DARK_BLUE_BACKGROUND = new Color(0, 60, 100);
    private static final Color BACKGROUND_GREY = new Color(245, 245, 245);
    private static final Color TEXT_GREY = new Color(100, 100, 100);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);

    private final ButtonGroup genderGroup = new ButtonGroup();

    // --- Components for specific logic ---
    private JTextField ageField;
    private JTextField dateField;

    // Cache for the country list to avoid recreating it
    private static String[] countryListCache = null;


    public PatientProfileEditor() {
        setTitle("Barangay Health Center - Patient Profile Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1400, 900)); // Large size appropriate for a form
        
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_GREY);

        // --- 1. Top Header (Simple Bar) ---
        JPanel topHeader = createTopHeader();
        add(topHeader, BorderLayout.NORTH);

        // --- 2. Main Content Area (Right Editor Panel) ---
        add(createRightEditorPanel(), BorderLayout.CENTER);

        // Final frame setup
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Creates a simple top bar matching the style of the previous dashboard.
     */
    private JPanel createTopHeader() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Left side: Logo Placeholder
        JLabel logoLabel = new JLabel("BARANGAY HEALTH CENTER");
        logoLabel.setFont(HEADER_FONT.deriveFont(Font.BOLD, 18f));
        logoLabel.setForeground(PRIMARY_BLUE);
        topBar.add(logoLabel, BorderLayout.WEST);

        return topBar;
    }
    
    // --- Right Editor Panel Components ---

    /**
     * Creates the main right-hand panel with tabs for editing profile info.
     */
    private JPanel createRightEditorPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Top Header (Title, Progress)
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);
        
        // Left part: Title with blue bar
        JPanel titlePanel = new JPanel(new BorderLayout(10, 0));
        titlePanel.setOpaque(false);
        JPanel blueBar = new JPanel();
        blueBar.setPreferredSize(new Dimension(3, 20));
        blueBar.setBackground(PRIMARY_BLUE);
        titlePanel.add(blueBar, BorderLayout.WEST);
        
        JLabel title = new JLabel("PATIENT INFORMATION");
        title.setFont(HEADER_FONT);
        title.setForeground(DARK_BLUE_BACKGROUND);
        titlePanel.add(title, BorderLayout.CENTER);
        header.add(titlePanel, BorderLayout.WEST);
        
        panel.add(header, BorderLayout.NORTH);
        
        // Tabbed Content
        JTabbedPane tabbedPane = createTabbedContent();
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        // --- Footer with Back Button ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setOpaque(false); // Match panel background

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

        backButton.addActionListener(e -> {
            new PatientDashboard().setVisible(true);
            SwingUtilities.getWindowAncestor(panel).dispose();
        });

        footer.add(backButton);
        panel.add(footer, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates the tabbed pane and populates the Basic Info tab.
     */
    private JTabbedPane createTabbedContent() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(LABEL_FONT.deriveFont(14f));
        
        // Use lazy loading for tabs. The content is created only when a tab is first selected.
        addLazyTab(tabbedPane, "BASIC INFO", this::createBasicInfoPanel);
        addLazyTab(tabbedPane, "ADDRESS", this::createAddressPanel);
        addLazyTab(tabbedPane, "DISEASE HISTORY", this::createDiseaseHistoryPanel);
        addLazyTab(tabbedPane, "MEDICAL HISTORY", this::createMedicalHistoryPanel);
        addLazyTab(tabbedPane, "LIFESTYLE", this::createLifestylePanel);
        addLazyTab(tabbedPane, "VACCINATION", this::createVaccinationPanel);
        addLazyTab(tabbedPane, "EMERGENCY CONTACT", this::createEmergencyContactPanel);

        // Set initial tab style
        tabbedPane.setForegroundAt(0, PRIMARY_BLUE);
        tabbedPane.setFont(tabbedPane.getFont().deriveFont(Font.BOLD));
        tabbedPane.setSelectedIndex(0);

        return tabbedPane;
    }

    /**
     * Adds a tab to the JTabbedPane that will load its content lazily.
     * @param tabbedPane The pane to add the tab to.
     * @param title The title of the tab.
     * @param contentSupplier A supplier that returns the JPanel content for the tab.
     */
    private void addLazyTab(JTabbedPane tabbedPane, String title, Supplier<JPanel> contentSupplier) {
        // Use a placeholder panel initially
        tabbedPane.addTab(title, new JPanel());
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex != -1 && tabbedPane.getTitleAt(selectedIndex).equals(title)) {
                // If the selected tab is our lazy tab and it's still a placeholder, load the real content
                Component comp = tabbedPane.getComponentAt(selectedIndex);
                if (comp instanceof JPanel && ((Container)comp).getComponentCount() == 0) {
                    tabbedPane.setComponentAt(selectedIndex, contentSupplier.get());
                }
            }
        });
    }
    
    /**
     * Creates the detailed form content for the "BASIC INFO" tab.
     */
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 10, 10, 10));
        
        // Use GridBagLayout for the main form structure
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5); // Spacing between components
        
        // --- BASIC INFORMATION (First Row) ---
        JLabel basicTitle = new JLabel("Basic Information");
        basicTitle.setFont(BOLD_FONT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        form.add(basicTitle, gbc);

        // --- Name Fields (Second Row) ---
        gbc.gridwidth = 1; gbc.gridy = 1;

        form.add(createLabeledComboBox("Title", new String[]{"Mr.", "Ms.", "Dr."}), gbc(0, 1, 0.2)); // Tracked
        form.add(createLabeledTextField("Last Name *", "", true), gbc(1, 1, 0.4)); // Tracked
        form.add(createLabeledTextField("First Name *", "", true), gbc(2, 1, 0.4)); // Tracked
        form.add(createLabeledTextField("Middle Name", "", true), gbc(3, 1, 0.4)); // Tracked

        // --- Details Fields (Third Row) ---
        gbc.gridy = 2;

        form.add(createLabeledTextField("Suffix", "", true), gbc(0, 2, 0.2)); // Tracked
        form.add(createLabeledDateField("Birthdate *"), gbc(1, 2, 0.4)); // Tracked
        form.add(createLabeledTextField("Age *", "", true), gbc(2, 2, 0.4)); // Tracked
        form.add(createLabeledTextField("Place of Birth", "", true), gbc(3, 2, 0.4)); // Tracked
        
        // --- Gender Buttons (Fourth Row) ---
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 5, 20, 5); // Add bottom margin

        // Empty spacer for alignment
        form.add(Box.createVerticalStrut(1), gbc(0, 3, 0.2)); 
        
        JPanel genderButtons = new JPanel(new GridLayout(1, 2, 0, 0));
        genderButtons.setOpaque(false);
        JToggleButton maleButton = new JToggleButton("Male");
        maleButton.setSelected(true);
        maleButton.setBackground(PRIMARY_BLUE);
        maleButton.setForeground(Color.WHITE);
        maleButton.setFocusPainted(false);

        JToggleButton femaleButton = new JToggleButton("Female");
        femaleButton.setSelected(false);
        femaleButton.setBackground(BACKGROUND_GREY);
        femaleButton.setForeground(Color.BLACK);
        femaleButton.setFocusPainted(false);

        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);

        genderButtons.add(maleButton);
        genderButtons.add(femaleButton);

        ActionListener genderListener = e -> {
            updateGenderButtons();
        };
        maleButton.addActionListener(genderListener);
        femaleButton.addActionListener(genderListener);
        
        form.add(genderButtons, gbc(1, 3, 0.4)); // Place the gender buttons here
        
        // Empty spacers for remaining columns
        form.add(Box.createVerticalStrut(1), gbc(2, 3, 0.4));
        form.add(Box.createVerticalStrut(1), gbc(3, 3, 0.4));


        // --- PATIENT ACCOUNT ---
        JLabel patientAccountTitle = new JLabel("Patient Account");
        patientAccountTitle.setFont(BOLD_FONT);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 5, 10, 5); // Top margin
        form.add(patientAccountTitle, gbc);

        // --- Contact Row ---
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 5, 10, 5);
        
        form.add(createLabeledTextField("Email *", "", true), gbc(0, 5, 0.5)); // Tracked
        JPanel contactPanel = createPrefixedLabeledTextField("Contact Number *", "+63", "", true);
        form.add(contactPanel, gbc(1, 5, 0.5));
        
        // --- Identification Row ---
        gbc.gridy = 6;
        form.add(createLabeledTextField("Identification Number *", "", true), gbc(0, 6, 0.5)); // Tracked
        form.add(createLabeledTextField("Philhealth ID", "", true), gbc(1, 6, 0.5)); // Tracked

        // --- Medical Record Number ---
        JLabel mrnTitle = new JLabel("Medical Record Number");
        mrnTitle.setFont(BOLD_FONT);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 4; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 5, 10, 5); // Top margin
        form.add(mrnTitle, gbc);
        
        gbc.gridy = 8;
        gbc.gridwidth = 2; // Span two columns
        gbc.insets = new Insets(10, 5, 10, 5);
        form.add(createLabeledTextField("MRN", "", true), gbc(0, 8, 0.5)); // Tracked
        
        // Vertical Glue to push content up
        gbc.gridy = 9; gbc.weighty = 1.0; gbc.gridwidth = 4;
        form.add(Box.createVerticalGlue(), gbc);
        
        panel.add(form, BorderLayout.CENTER);
        
        return panel;
    }
    
    /** Helper method for GridBagConstraints */
    private GridBagConstraints gbc(int x, int y, double weightX) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = weightX;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        return gbc;
    }

    /** Creates a labeled text field similar to the screenshot's style. */
    private JPanel createLabeledTextField(String labelText, String placeholder, boolean track) {
        JPanel panel = createLabeledTextField(labelText, placeholder);
        // The 'track' parameter is currently unused, but kept for potential future logic
        // where not all fields should be tracked.

        // Special handling for the Age field to make it non-editable
        if (labelText.startsWith("Age")) {
            // Find the text field inside the panel
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JTextField) {
                    ageField = (JTextField) comp;
                    ageField.setEditable(false);
                }
            }
        }
        return panel;
    }

    private JPanel createLabeledTextField(String labelText, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_GREY);
        panel.add(label, BorderLayout.NORTH);
        
        JTextField textField = new JTextField(placeholder);
        textField.setFont(LABEL_FONT.deriveFont(14f));
        textField.setForeground(Color.BLACK);
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 10, 5, 10)
        ));

        panel.add(textField, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates a labeled text field with a non-editable prefix inside the field.
     * @param labelText The text for the label.
     * @param prefixText The text for the prefix.
     * @param placeholder The placeholder text for the input area.
     * @param track Whether to track this field for progress.
     * @return A JPanel containing the styled component.
     */
    private JPanel createPrefixedLabeledTextField(String labelText, String prefixText, String placeholder, boolean track) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_GREY);
        panel.add(label, BorderLayout.NORTH);

        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 10, 5, 10)
        ));

        JLabel prefixLabel = new JLabel(prefixText);
        prefixLabel.setFont(LABEL_FONT.deriveFont(14f));
        prefixLabel.setForeground(TEXT_GREY);
        prefixLabel.setOpaque(true);
        prefixLabel.setBackground(BACKGROUND_GREY);
        prefixLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY), // Right separator
            new EmptyBorder(0, 8, 0, 8) // Padding
        ));

        JTextField textField = new JTextField(placeholder);
        textField.setFont(LABEL_FONT.deriveFont(14f));
        textField.setForeground(Color.BLACK);
        textField.setBackground(Color.WHITE);
        textField.setBorder(null); // Remove border since the panel has it

        fieldPanel.add(prefixLabel, BorderLayout.WEST);
        fieldPanel.add(textField, BorderLayout.CENTER);

        panel.add(fieldPanel, BorderLayout.CENTER);

        return panel;
    }

    /** Creates a labeled combo box. */
    private JPanel createLabeledComboBox(String labelText, String[] options) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_GREY);
        panel.add(label, BorderLayout.NORTH);
        
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setFont(LABEL_FONT.deriveFont(14f));
        comboBox.setForeground(Color.BLACK);
        comboBox.setBackground(Color.WHITE);
        comboBox.setSelectedItem("Mr."); // Set default value

        
        // Remove default border and add a custom one
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5) // Inner padding
        ));
        
        panel.add(comboBox, BorderLayout.CENTER);
        return panel;
    }

    /** Creates a labeled date field with a calendar icon. */
    private JPanel createLabeledDateField(String labelText) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_GREY);
        panel.add(label, BorderLayout.NORTH);
        
        JPanel dateFieldPanel = new JPanel(new BorderLayout());
        dateFieldPanel.setBackground(Color.WHITE);
        dateFieldPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 10, 5, 10)
        ));

        dateField = new JTextField("");
        dateField.setEditable(false);
        dateField.setBorder(null); // Remove JTextField border
        dateField.setOpaque(false);
        dateField.setFont(LABEL_FONT.deriveFont(14f));

        dateFieldPanel.add(dateField, BorderLayout.CENTER);
        
        // --- Calendar Icon ---
        // Load an image icon. Make sure you have an icon file (e.g., calendar_icon.png)
        // in a resource folder (e.g., src/main/resources/icons/calendar_icon.png).
        ImageIcon calendarImageIcon = null;
        try {
            java.net.URL iconUrl = getClass().getResource("/icons/calendar_icon.png");
            if (iconUrl != null) {
                calendarImageIcon = new ImageIcon(iconUrl);
            }
        } catch (Exception e) { /* Icon not found, will fall back to text */ }

        JLabel calendarIcon = (calendarImageIcon != null) ? new JLabel(calendarImageIcon) : new JLabel("📅");
        calendarIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dateFieldPanel.add(calendarIcon, BorderLayout.EAST);

        // Add click listener to the icon to open a date picker
        calendarIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Simple dialog-based date picker
                String dateStr = JOptionPane.showInputDialog(panel, "Enter date (YYYY-MM-DD):");
                try {
                    LocalDate selectedDate = LocalDate.parse(dateStr);
                    dateField.setText(selectedDate.toString());
                    updateAge(selectedDate);
                } catch (Exception ex) {
                    // Handle invalid date format
                }
            }
        });
        
        panel.add(dateFieldPanel, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Creates a generic labeled date field with a calendar icon that opens a date picker dialog.
     * @param labelText The text for the label above the field.
     * @return A JPanel containing the styled date field and calendar icon.
     */
    private JPanel createLabeledDateFieldWithPicker(String labelText) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_GREY);
        panel.add(label, BorderLayout.NORTH);

        JPanel dateFieldPanel = new JPanel(new BorderLayout());
        dateFieldPanel.setBackground(Color.WHITE);
        dateFieldPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 10, 5, 10)
        ));

        JTextField effectiveDateField = new JTextField("");
        effectiveDateField.setEditable(false);
        effectiveDateField.setBorder(null);
        effectiveDateField.setOpaque(false);
        effectiveDateField.setFont(LABEL_FONT.deriveFont(14f));
        dateFieldPanel.add(effectiveDateField, BorderLayout.CENTER);

        // --- Calendar Icon ---
        // Reuse the same logic to load the image icon.
        ImageIcon calendarImageIcon = null;
        try {
            java.net.URL iconUrl = getClass().getResource("/icons/calendar_icon.png");
            if (iconUrl != null) {
                calendarImageIcon = new ImageIcon(iconUrl);
            }
        } catch (Exception e) { /* Icon not found, will fall back to text */ }

        JLabel calendarIcon = (calendarImageIcon != null) ? new JLabel(calendarImageIcon) : new JLabel("📅");
        calendarIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dateFieldPanel.add(calendarIcon, BorderLayout.EAST);

        calendarIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String dateStr = JOptionPane.showInputDialog(panel, "Enter date (YYYY-MM-DD):");
                try {
                    effectiveDateField.setText(LocalDate.parse(dateStr).toString());
                } catch (Exception ex) { /* Ignore invalid date format */ }
            }
        });

        panel.add(dateFieldPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Updates the visual state of the gender toggle buttons.
     */
    private void updateGenderButtons() {
        java.util.Enumeration<AbstractButton> buttons = genderGroup.getElements();
        while (buttons.hasMoreElements()) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                button.setBackground(PRIMARY_BLUE);
                button.setForeground(Color.WHITE);
            } else {
                button.setBackground(BACKGROUND_GREY);
                button.setForeground(Color.BLACK);
            }
        }
    }

    /**
     * Creates the detailed form content for the "LIFESTYLE" tab.
     */
    private JPanel createLifestylePanel() {
        // Main container panel for the tab
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Form panel to hold the content, using GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        int gridY = 0;

        // --- Cigarettes Section ---
        formPanel.add(createLifestyleSection("Cigarettes", new String[]{"Effective Date", "Remarks"}), gbc(0, gridY++, 1.0));
        // --- Alcohol Section ---
        formPanel.add(createLifestyleSection("Alcohol", new String[]{"Effective Date", "Remarks"}), gbc(0, gridY++, 1.0));
        // --- Exercise habits Section ---
        formPanel.add(createLifestyleSection("Exercise habits", new String[]{"Effective Date", "Remarks"}), gbc(0, gridY++, 1.0));
        // --- Diet and Nutrition Section ---
        formPanel.add(createLifestyleSection("Diet and Nutrition", new String[]{"Effective Date", "Remarks"}), gbc(0, gridY++, 1.0));
        // --- Drugs Section ---
        formPanel.add(createLifestyleSection("Drugs", new String[]{"Effective Date", "Remarks"}), gbc(0, gridY++, 1.0));

        // --- Other Lifestyle Fields ---
        JPanel otherLifestylePanel = new JPanel(new GridBagLayout());
        otherLifestylePanel.setOpaque(false);
        otherLifestylePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            "Other Lifestyle Information",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            BOLD_FONT,
            DARK_BLUE_BACKGROUND
        ));
        otherLifestylePanel.add(createLabeledTextField("Suicidality", "", true), gbc(0, 0, 1.0));
        otherLifestylePanel.add(createLabeledTextField("Sexual history", "", true), gbc(0, 1, 1.0));
        otherLifestylePanel.add(createLabeledTextField("Social history", "", true), gbc(0, 2, 1.0));
        otherLifestylePanel.add(createLabeledTextField("Environmental Exposure", "", true), gbc(0, 3, 1.0));
        otherLifestylePanel.add(createLabeledTextField("Development", "", true), gbc(0, 4, 1.0));

        formPanel.add(otherLifestylePanel, gbc(0, gridY++, 1.0));

        // Pushes content to the top
        formPanel.add(Box.createVerticalGlue(), gbc(0, gridY++, 1.0, 1.0));

        // Wrap the form in a scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Helper method to create a titled section for lifestyle habits.
     * @param title The title of the section (e.g., "Cigarettes").
     * @param fields An array of field names to include in the section.
     * @return A JPanel representing the lifestyle section.
     */
    private JPanel createLifestyleSection(String title, String[] fields) {
        JPanel sectionPanel = new JPanel(new GridBagLayout());
        sectionPanel.setOpaque(false);
        sectionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            title,
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            BOLD_FONT,
            DARK_BLUE_BACKGROUND
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Add the main field for the habit itself
        sectionPanel.add(createLabeledTextField(title, "", true), gbc(0, 0, 1.0));

        // Add additional fields if provided
        for (int i = 0; i < fields.length; i++) {
            if ("Effective Date".equals(fields[i])) {
                sectionPanel.add(createLabeledDateFieldWithPicker(fields[i]), gbc(0, i + 1, 1.0));
            } else {
                sectionPanel.add(createLabeledTextField(fields[i], "", true), gbc(0, i + 1, 1.0));
            }
        }

        return sectionPanel;
    }

    /**
     * Creates the detailed form content for the "VACCINATION" tab.
     */
    private JPanel createVaccinationPanel() {
        // Main container panel for the tab
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Form panel to hold the content, using GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 10, 10, 10));

        // --- Vaccination Section ---
        JPanel vaccinationPanel = new JPanel(new GridBagLayout());
        vaccinationPanel.setOpaque(false);
        vaccinationPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            "Vaccination",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            BOLD_FONT,
            DARK_BLUE_BACKGROUND
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Add all the requested fields
        vaccinationPanel.add(createLabeledTextField("Vaccine", "", true), gbc(0, 0, 1.0));
        vaccinationPanel.add(createLabeledTextField("Vaccine type", "", true), gbc(0, 1, 1.0));
        vaccinationPanel.add(createLabeledTextField("Administered By", "", true), gbc(0, 2, 1.0));
        vaccinationPanel.add(createLabeledTextField("Dosage", "", true), gbc(0, 3, 1.0));
        vaccinationPanel.add(createLabeledDateFieldWithPicker("Date administered"), gbc(0, 4, 1.0));
        vaccinationPanel.add(createLabeledDateFieldWithPicker("Date Due"), gbc(0, 5, 1.0));
        vaccinationPanel.add(createLabeledTextField("Remarks", "", true), gbc(0, 6, 1.0));

        formPanel.add(vaccinationPanel, gbc(0, 0, 1.0));
        formPanel.add(Box.createVerticalGlue(), gbc(0, 1, 1.0, 1.0)); // Pushes content to the top

        // Wrap the form in a scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the detailed form content for the "EMERGENCY CONTACT" tab.
     */
    private JPanel createEmergencyContactPanel() {
        // Main container panel for the tab
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Form panel to hold the content, using GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 10, 10, 10));

        // --- Emergency Contact Section ---
        JPanel emergencyContactPanel = new JPanel(new GridBagLayout());
        emergencyContactPanel.setOpaque(false);
        emergencyContactPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            "Emergency Contact",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            BOLD_FONT,
            DARK_BLUE_BACKGROUND
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Add all the requested fields
        emergencyContactPanel.add(createLabeledTextField("First Name", "", true), gbc(0, 0, 1.0));
        emergencyContactPanel.add(createLabeledTextField("Middle Name", "", true), gbc(0, 1, 1.0));
        emergencyContactPanel.add(createLabeledTextField("Last Name", "", true), gbc(0, 2, 1.0));
        emergencyContactPanel.add(createLabeledTextField("Relation", "", true), gbc(0, 3, 1.0));

        emergencyContactPanel.add(createPrefixedLabeledTextField("Contact No.", "+63", "", true), gbc(0, 4, 1.0));

        emergencyContactPanel.add(createLabeledTextField("Address", "", true), gbc(0, 5, 1.0));

        formPanel.add(emergencyContactPanel, gbc(0, 0, 1.0));
        formPanel.add(Box.createVerticalGlue(), gbc(0, 1, 1.0, 1.0)); // Pushes content to the top

        // Wrap the form in a scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the detailed form content for the "MEDICAL HISTORY" tab.
     */
    private JPanel createMedicalHistoryPanel() {
        // Main container panel for the tab
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Form panel to hold the content, using GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 10, 10, 10));

        // --- Basic Information Section ---
        JPanel basicInfoPanel = new JPanel(new GridBagLayout());
        basicInfoPanel.setOpaque(false);
        basicInfoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            "Basic Information",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            BOLD_FONT,
            DARK_BLUE_BACKGROUND
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Add the requested fields
        basicInfoPanel.add(createLabeledTextField("Known allergies", "", true), gbc(0, 0, 1.0));
        basicInfoPanel.add(createLabeledTextField("Current illness & other medications", "", true), gbc(0, 1, 1.0));

        // --- Weight Section ---
        JPanel weightPanel = new JPanel(new GridBagLayout());
        weightPanel.setOpaque(false);
        weightPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            "Weight",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            BOLD_FONT,
            DARK_BLUE_BACKGROUND
        ));

        // Add the requested weight fields
        weightPanel.add(createLabeledTextField("Highest Weight", "", true), gbc(0, 0, 1.0));
        weightPanel.add(createLabeledTextField("Lowest Weight", "", true), gbc(0, 1, 1.0));
        weightPanel.add(createLabeledTextField("Weight Gained over the year", "", true), gbc(0, 2, 1.0));
        weightPanel.add(createLabeledTextField("Date", "", true), gbc(0, 3, 1.0));
        weightPanel.add(createLabeledTextField("Remarks", "", true), gbc(0, 4, 1.0));

        // Add the panels to the main form panel
        formPanel.add(basicInfoPanel, gbc(0, 0, 1.0));
        formPanel.add(weightPanel, gbc(0, 1, 1.0));
        formPanel.add(Box.createVerticalGlue(), gbc(0, 2, 1.0, 1.0)); // Pushes content to the top

        // Wrap the form in a scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the detailed form content for the "DISEASE HISTORY" tab.
     */
    private JPanel createDiseaseHistoryPanel() {
        // Main container panel for the tab
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Form panel to hold the content, using GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 10, 10, 10));

        // --- Liver Disease Section ---
        JPanel liverPanel = new JPanel(new GridBagLayout());
        liverPanel.setOpaque(false);
        liverPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            "Liver Disease - Related History",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            BOLD_FONT,
            DARK_BLUE_BACKGROUND
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Add all the requested fields to the liver panel
        liverPanel.add(createLabeledTextField("Previous liver resection", "None", true), gbc(0, 0, 1.0));
        liverPanel.add(createLabeledTextField("Remarks", "", true), gbc(0, 1, 1.0));
        liverPanel.add(createLabeledTextField("Tumor Type", "None", true), gbc(0, 2, 1.0));
        liverPanel.add(createLabeledTextField("Remarks", "", true), gbc(0, 3, 1.0));
        liverPanel.add(createLabeledTextField("Previous abdominal surgery", "None", true), gbc(0, 4, 1.0));
        liverPanel.add(createLabeledTextField("Remarks", "", true), gbc(0, 5, 1.0));
        liverPanel.add(createLabeledTextField("Family Hx of Liver Disease", "None", true), gbc(0, 6, 1.0));
        liverPanel.add(createLabeledTextField("Relation & age of diagnosis", "", true), gbc(0, 7, 1.0));
        liverPanel.add(createLabeledTextField("Family Hx of other Liver Disease", "None", true), gbc(0, 8, 1.0));
        liverPanel.add(createLabeledTextField("Relation & age of diagnosis", "", true), gbc(0, 9, 1.0));

        formPanel.add(liverPanel, gbc(0, 0, 1.0));

        // --- CKD Section ---
        JPanel ckdPanel = new JPanel(new GridBagLayout());
        ckdPanel.setOpaque(false);
        ckdPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            "Chronic Kidney Disease (CKD) - Related History",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            BOLD_FONT,
            DARK_BLUE_BACKGROUND
        ));

        // Add all the requested fields to the CKD panel
        ckdPanel.add(createLabeledTextField("Previous kidney surgery", "None", true), gbc(0, 0, 1.0));
        ckdPanel.add(createLabeledTextField("Remarks", "", true), gbc(0, 1, 1.0));
        ckdPanel.add(createLabeledTextField("Family Hx of CKD", "None", true), gbc(0, 2, 1.0));
        ckdPanel.add(createLabeledTextField("Relation & age of diagnosis", "", true), gbc(0, 3, 1.0));
        ckdPanel.add(createLabeledTextField("Family Hx of other CKD", "None", true), gbc(0, 4, 1.0));
        ckdPanel.add(createLabeledTextField("Relation & age of diagnosis", "", true), gbc(0, 5, 1.0));

        // Add the CKD panel to the form
        formPanel.add(ckdPanel, gbc(0, 1, 1.0));

        // Pushes content to the top
        formPanel.add(Box.createVerticalGlue(), gbc(0, 2, 1.0, 1.0)); 

        // Wrap the form in a scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel); // FIX: Wrap in scroll pane
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the detailed form content for the "ADDRESS" tab.
     */
    private JPanel createAddressPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 10, 10, 10));

        // Use GridBagLayout for the main form structure
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5); // Spacing between components

        // --- PRESENT ADDRESS (First Row) ---
        JLabel addressTitle = new JLabel("Present Address");
        addressTitle.setFont(BOLD_FONT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        form.add(addressTitle, gbc);

        // --- Address Fields ---
        gbc.gridwidth = 1; gbc.gridy = 1;
        form.add(createLabeledComboBox("Country", getCountryList()), gbc(0, 1, 0.5)); // Tracked
        form.add(createLabeledTextField("House No. & Street", "", true), gbc(1, 1, 0.5)); // Tracked

        gbc.gridy = 2;
        form.add(createLabeledTextField("Barangay", "", true), gbc(0, 2, 0.5)); // Tracked
        form.add(createLabeledTextField("Municipality/City", "", true), gbc(1, 2, 0.5)); // Tracked

        gbc.gridy = 3;
        form.add(createLabeledTextField("Region", "", true), gbc(0, 3, 0.5)); // Tracked
        form.add(createLabeledTextField("Postal Code", "", true), gbc(1, 3, 0.5)); // Tracked

        // --- Checkbox for Permanent Address ---
        gbc.gridy = 4; gbc.gridwidth = 2; gbc.insets = new Insets(20, 5, 10, 5);
        JCheckBox permanentCheckBox = new JCheckBox("Click here if your Present Address is not your Permanent Address");
        permanentCheckBox.setFont(LABEL_FONT);
        permanentCheckBox.setForeground(TEXT_GREY);
        permanentCheckBox.setOpaque(false);
        form.add(permanentCheckBox, gbc);

        // Vertical Glue to push content up
        gbc.gridy = 5; gbc.weighty = 1.0; gbc.gridwidth = 2;
        form.add(Box.createVerticalGlue(), gbc);

        // FIX: Wrap the form in a scroll pane
        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /** Overloaded helper for gbc to include weighty */
    private GridBagConstraints gbc(int x, int y, double weightX, double weighty) {
        GridBagConstraints gbc = gbc(x, y, weightX);
        gbc.weighty = weighty;
        return gbc;
    }

    /**
     * Calculates and updates the age field based on the provided birthdate.
     * @param birthDate The selected date of birth.
     */
    private void updateAge(LocalDate birthDate) {
        if (birthDate != null && ageField != null) {
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            ageField.setText(String.valueOf(age));
        }
    }

    /**
     * Returns a sorted array of country names.
     * @return A string array of countries sorted alphabetically.
     */
    private String[] getCountryList() {
        // Use a cached list to avoid creating this large array repeatedly.
        if (countryListCache == null) {
            countryListCache = new String[] {
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
        return countryListCache;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PatientProfileEditor();
            }
        });
    }
}
