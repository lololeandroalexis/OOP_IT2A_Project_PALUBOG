import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

/**
 * Patient Dashboard - Display patient profile, appointments, and staff information
 */
public class PatientDashboard extends JFrame {
    private static final Color PRIMARY_BLUE = new Color(0, 102, 204);
    private static final Color SIDEBAR_GRAY = new Color(240, 240, 240);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Font BUTTON_FONT = new Font("Inter", Font.BOLD, 14);
    private static final Font TITLE_FONT = new Font("Inter", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Inter", Font.BOLD, 12);

    private int patientId;
    private String patientEmail;
    private JPanel mainContentPanel;
    private JLabel patientNameLabel;
    private JLabel birthdateLabel;

    private JLabel profilePicLabel; // new
    private String profilePicPath;  // new

    public PatientDashboard(int userId, String email) {
        this.patientId = userId;
        this.patientEmail = email;
        
        setTitle("Barangay Health Center - Patient Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1200, 700));
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createSidebar(), BorderLayout.WEST);
        mainPanel.add(createContent(), BorderLayout.CENTER);

        add(mainPanel);
        
        pack();  // Call pack FIRST
        setLocationRelativeTo(null);  // Then center
        setVisible(true);
    }

    /**
     * Creates left sidebar navigation
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_GRAY);
        sidebar.setPreferredSize(new Dimension(200, 750));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel titleLabel = new JLabel("PATIENT");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_BLUE);
        sidebar.add(titleLabel);
        sidebar.add(Box.createVerticalStrut(30));

        sidebar.add(createNavButton("👤 Profile", e -> showProfile()));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createNavButton("📅 Appointments", e -> showAppointments()));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createNavButton("👨‍⚕️ Staff Info", e -> showStaffInfo()));
        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createNavButton("🚪 Logout", e -> logout()));

        return sidebar;
    }

    /**
     * Creates navigation button
     */
    private JButton createNavButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(PRIMARY_BLUE);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 1));
        button.setMaximumSize(new Dimension(180, 40));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(listener);
        return button;
    }

    /**
     * Creates main content panel
     */
    private JPanel createContent() {
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(Color.WHITE);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        showProfile();
        return mainContentPanel;
    }

    /**
     * Shows patient profile
     */
    private void showProfile() {
        mainContentPanel.removeAll();

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);

        // Profile header with patient info
        JPanel profileHeader = createProfileHeader();
        content.add(profileHeader);
        content.add(Box.createVerticalStrut(20));

        // Patient details
        JPanel detailsPanel = createPatientDetailsPanel();
        content.add(detailsPanel);
        content.add(Box.createVerticalStrut(20));

        // Edit profile button
        JButton editBtn = new JButton("✏️ Edit Profile");
        editBtn.setFont(BUTTON_FONT);
        editBtn.setBackground(PRIMARY_BLUE);
        editBtn.setForeground(Color.WHITE);
        editBtn.setMaximumSize(new Dimension(150, 40));
        editBtn.addActionListener(e -> openEditProfileDialog());
        content.add(editBtn);

        mainContentPanel.add(new JScrollPane(content), BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    /**
     * Creates profile header with patient name and birthdate
     */
    private JPanel createProfileHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(LIGHT_GRAY);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Profile picture placeholder
        profilePicLabel = new JLabel(); // changed: use JLabel with Icon
        profilePicLabel.setPreferredSize(new Dimension(100, 100));
        profilePicLabel.setMaximumSize(new Dimension(100, 100));
        profilePicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePicLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profilePicLabel.setVerticalAlignment(SwingConstants.CENTER);
        profilePicLabel.setOpaque(true);
        profilePicLabel.setBackground(Color.WHITE);
        profilePicLabel.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 2));
        profilePicLabel.setText("👤");
        profilePicLabel.setFont(new Font("Arial", Font.PLAIN, 42));
        profilePicLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        profilePicLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openEditProfileDialog(); // open edit dialog on picture click
            }
        });

        header.add(profilePicLabel);
        header.add(Box.createVerticalStrut(10));

        // Patient name (clickable to edit)
        patientNameLabel = new JLabel("Loading...");
        patientNameLabel.setFont(TITLE_FONT);
        patientNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        patientNameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        patientNameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openEditProfileDialog();
            }
        });
        header.add(patientNameLabel);

        // Birthdate
        birthdateLabel = new JLabel("Birthdate: Loading...");
        birthdateLabel.setFont(LABEL_FONT);
        birthdateLabel.setForeground(new Color(100, 100, 100));
        birthdateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(birthdateLabel);

        // Load patient data (including profile picture path)
        loadPatientData();

        return header;
    }

    /**
     * Loads patient data from database
     */
    private void loadPatientData() {
        new Thread(() -> {
            try {
                System.out.println("Loading patient data for ID: " + patientId);
                // now returns first, last, dob, profile_pic_path
                String[] patientData = DatabaseHelper.getPatientData(patientId);
                if (patientData != null) {
                    String first = patientData[0];
                    String last = patientData[1];
                    String dob = patientData[2];
                    profilePicPath = patientData.length > 3 ? patientData[3] : null;

                    final String name = (first != null ? first : "") + (last != null ? " " + last : "");
                    SwingUtilities.invokeLater(() -> {
                        patientNameLabel.setText(name.trim().isEmpty() ? "Unnamed Patient" : name.trim());
                        birthdateLabel.setText("Birthdate: " + (dob != null ? dob : "Not set"));

                        // load picture if path exists
                        if (profilePicPath != null && !profilePicPath.isEmpty()) {
                            try {
                                ImageIcon ico = new ImageIcon(profilePicPath);
                                Image img = ico.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                                profilePicLabel.setIcon(new ImageIcon(img));
                                profilePicLabel.setText("");
                            } catch (Exception ex) {
                                profilePicLabel.setIcon(null);
                                profilePicLabel.setText("👤");
                            }
                        } else {
                            profilePicLabel.setIcon(null);
                            profilePicLabel.setText("👤");
                        }
                    });
                } else {
                    System.out.println("Patient data is null");
                }
            } catch (Exception e) {
                System.err.println("Error loading patient data: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Creates patient details panel
     * now includes appointments table identical to the Appointments view
     */
    private JPanel createPatientDetailsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Patient Information");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));

        String[] details = DatabaseHelper.getPatientDetails(patientId);
        if (details != null) {
            panel.add(createDetailRow("Email:", details[0]));
            panel.add(Box.createVerticalStrut(8));
            panel.add(createDetailRow("Phone:", details[1]));
            panel.add(Box.createVerticalStrut(8));
            panel.add(createDetailRow("Address:", details[2]));
            panel.add(Box.createVerticalStrut(8));
            panel.add(createDetailRow("Occupation:", details[3]));
            panel.add(Box.createVerticalStrut(8));
            panel.add(createDetailRow("Civil Status:", details[4]));
        }

        // Appointments section — show same table used in Appointments tab
        panel.add(Box.createVerticalStrut(15));
        panel.add(new JSeparator(SwingConstants.HORIZONTAL));
        panel.add(Box.createVerticalStrut(15));

        JLabel appointmentsTitle = new JLabel("Recent Appointments");
        appointmentsTitle.setFont(LABEL_FONT);
        appointmentsTitle.setForeground(PRIMARY_BLUE);
        panel.add(appointmentsTitle);
        panel.add(Box.createVerticalStrut(10));

        Object[][] appointmentData = DatabaseHelper.getPatientAppointments(patientId);
        if (appointmentData != null && appointmentData.length > 0) {
            String[] columns = {"ID", "Reason", "Date", "Time", "Status"};
            JTable appointmentsTable = new JTable(appointmentData, columns);
            appointmentsTable.setRowHeight(25);
            JScrollPane scrollPane = new JScrollPane(appointmentsTable);
            scrollPane.setPreferredSize(new Dimension(700, 150));
            panel.add(scrollPane);
        } else {
            JLabel noApptLabel = new JLabel("No appointments scheduled");
            noApptLabel.setFont(new Font("Inter", Font.ITALIC, 12));
            noApptLabel.setForeground(new Color(150, 150, 150));
            panel.add(noApptLabel);
        }

        return panel;
    }

    /**
     * Helper to create detail row
     */
    private JPanel createDetailRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setOpaque(false);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(LABEL_FONT);
        labelComp.setForeground(PRIMARY_BLUE);
        labelComp.setPreferredSize(new Dimension(120, 20));

        JLabel valueComp = new JLabel(value != null ? value : "N/A");
        valueComp.setFont(new Font("Inter", Font.PLAIN, 12));
        valueComp.setForeground(new Color(50, 50, 50));

        row.add(labelComp);
        row.add(valueComp);
        return row;
    }

    /**
     * Opens edit profile dialog — now includes first/last name and change picture capability
     */
    private void openEditProfileDialog() {
        JDialog editDialog = new JDialog(this, "Edit Profile", true);
        editDialog.setSize(520, 560);
        editDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Get current data
        String[] details = DatabaseHelper.getPatientDetails(patientId);
        String[] nameData = DatabaseHelper.getPatientNameComponents(patientId); // new helper: first,last

        JLabel firstLabel = new JLabel("First Name:");
        firstLabel.setFont(LABEL_FONT);
        JTextField firstField = new JTextField(nameData != null ? nameData[0] : "");
        panel.add(firstLabel);
        panel.add(firstField);
        panel.add(Box.createVerticalStrut(10));

        JLabel lastLabel = new JLabel("Last Name:");
        lastLabel.setFont(LABEL_FONT);
        JTextField lastField = new JTextField(nameData != null ? nameData[1] : "");
        panel.add(lastLabel);
        panel.add(lastField);
        panel.add(Box.createVerticalStrut(10));

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(LABEL_FONT);
        JTextField emailField = new JTextField(details != null ? details[0] : "");
        emailField.setEditable(false);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(10));

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(LABEL_FONT);
        JTextField phoneField = new JTextField(details != null ? details[1] : "");
        panel.add(phoneLabel);
        panel.add(phoneField);
        panel.add(Box.createVerticalStrut(10));

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(LABEL_FONT);
        JTextField addressField = new JTextField(details != null ? details[2] : "");
        panel.add(addressLabel);
        panel.add(addressField);
        panel.add(Box.createVerticalStrut(10));

        JLabel occupationLabel = new JLabel("Occupation:");
        occupationLabel.setFont(LABEL_FONT);
        JTextField occupationField = new JTextField(details != null ? details[3] : "");
        panel.add(occupationLabel);
        panel.add(occupationField);
        panel.add(Box.createVerticalStrut(10));

        JLabel civilLabel = new JLabel("Civil Status:");
        civilLabel.setFont(LABEL_FONT);
        JComboBox<String> civilCombo = new JComboBox<>(new String[]{"Single but not available", "Married", "Divorced", "Widowed", "Broken", "Nonchalant"});
        if (details != null && details[4] != null) {
            civilCombo.setSelectedItem(details[4]);
        }
        panel.add(civilLabel);
        panel.add(civilCombo);
        panel.add(Box.createVerticalStrut(10));

        // Profile picture chooser
        JLabel picLabel = new JLabel("Profile Picture:");
        picLabel.setFont(LABEL_FONT);
        panel.add(picLabel);

        JTextField picPathField = new JTextField(profilePicPath != null ? profilePicPath : "");
        picPathField.setEditable(false);
        panel.add(picPathField);

        JButton choosePicBtn = new JButton("Change Picture");
        choosePicBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
            int res = chooser.showOpenDialog(editDialog);
            if (res == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                picPathField.setText(f.getAbsolutePath());
            }
        });
        panel.add(choosePicBtn);
        panel.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(PRIMARY_BLUE);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            boolean ok = DatabaseHelper.updatePatientProfileExtended(
                patientId,
                firstField.getText().trim(),
                lastField.getText().trim(),
                phoneField.getText().trim(),
                addressField.getText().trim(),
                occupationField.getText().trim(),
                (String) civilCombo.getSelectedItem(),
                picPathField.getText().trim()
            );
            if (ok) {
                JOptionPane.showMessageDialog(editDialog, "Profile updated successfully!");
                editDialog.dispose();
                loadPatientData(); // reload name + picture
                showProfile();
            } else {
                JOptionPane.showMessageDialog(editDialog, "Error updating profile", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> editDialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel);

        editDialog.add(new JScrollPane(panel));
        editDialog.setVisible(true);
    }

    /**
     * Shows appointments tab
     */
    private void showAppointments() {
        mainContentPanel.removeAll();

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("My Appointments");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        content.add(titleLabel, BorderLayout.NORTH);

        // Appointments table
        DefaultTableModel appointmentsModel = new DefaultTableModel(
            new Object[]{"ID","Reason","Date","Time", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable appointmentsTable = new JTable(appointmentsModel);
        appointmentsTable.setRowHeight(25);
        JScrollPane apptScroll = new JScrollPane(appointmentsTable);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(apptScroll, BorderLayout.CENTER);

        // Action buttons
        JPanel apptActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        apptActions.setBackground(Color.WHITE);

        JButton bookBtn = new JButton("+ Book Appointment");
        bookBtn.setBackground(PRIMARY_BLUE);
        bookBtn.setForeground(Color.WHITE);
        bookBtn.addActionListener(e -> openBookAppointmentDialog());
        apptActions.add(bookBtn);

        JButton removeBtn = new JButton("🗑 Remove Appointment");
        removeBtn.setBackground(new Color(220, 53, 69));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.addActionListener(e -> {
            int sel = appointmentsTable.getSelectedRow();
            if (sel == -1) {
                JOptionPane.showMessageDialog(this, "Please select an appointment to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Object idObj = appointmentsModel.getValueAt(sel, 0);
            if (idObj == null) return;

            int apptId = Integer.parseInt(idObj.toString());
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this appointment?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = DatabaseHelper.deleteAppointment(apptId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Appointment cancelled successfully.");
                    showAppointments(); // Refresh
                } else {
                    JOptionPane.showMessageDialog(this, "Error cancelling appointment.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        apptActions.add(removeBtn);

        centerPanel.add(apptActions, BorderLayout.SOUTH);
        content.add(centerPanel, BorderLayout.CENTER);

        mainContentPanel.add(content);
        loadAppointments(appointmentsModel);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void loadAppointments(DefaultTableModel model) {
        new Thread(() -> {
            Object[][] rows = DatabaseHelper.getPatientAppointments(this.patientId);
            SwingUtilities.invokeLater(() -> {
                model.setRowCount(0);
                if (rows != null) {
                    for (Object[] r : rows) {
                        model.addRow(new Object[]{r[0], r[1], r[2], r[3], r[4]});
                    }
                }
            });
        }).start();
    }

    /**
     * Opens book appointment dialog with calendar + time picker
     */
    private void openBookAppointmentDialog() {
        JDialog bookDialog = new JDialog(this, "Book Appointment", true);
        bookDialog.setSize(700, 600);
        bookDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Select Appointment Date & Time");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Content panel: Calendar on left, time picker on right
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(Color.WHITE);

        Calendar selectedCal = Calendar.getInstance();
        selectedCal.add(Calendar.DAY_OF_MONTH, 1); // Default to TOMORROW (ensures future date)

        // Calendar panel
        JPanel calendarPanel = createBookingCalendarPanel(selectedCal);
        contentPanel.add(calendarPanel, BorderLayout.WEST);

        // Time picker panel
        JPanel timePanel = createTimePickerPanel(selectedCal);
        contentPanel.add(timePanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Reason and buttons at bottom
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 15));
        bottomPanel.setBackground(Color.WHITE);

        JLabel reasonLabel = new JLabel("Reason for Visit:");
        reasonLabel.setFont(LABEL_FONT);
        bottomPanel.add(reasonLabel, BorderLayout.NORTH);

        JTextArea reasonArea = new JTextArea(3, 30);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setFont(new Font("Inter", Font.PLAIN, 12));
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        bottomPanel.add(reasonScroll, BorderLayout.CENTER);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton bookBtn = new JButton("Book Appointment");
        bookBtn.setBackground(PRIMARY_BLUE);
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFont(BUTTON_FONT);
        bookBtn.addActionListener(e -> {
            String reason = reasonArea.getText().trim();
            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(bookDialog, "Please provide a reason for the appointment.", "Missing Reason", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get selected time from spinners
            int hour = (Integer) ((JSpinner) timePanel.getClientProperty("hourSpinner")).getValue();
            int minute = (Integer) ((JSpinner) timePanel.getClientProperty("minuteSpinner")).getValue();

            selectedCal.set(Calendar.HOUR_OF_DAY, hour);
            selectedCal.set(Calendar.MINUTE, minute);
            selectedCal.set(Calendar.SECOND, 0);

            System.out.println("DEBUG: Final selectedCal = " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(selectedCal.getTime()));
            System.out.println("DEBUG: Current time = " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            System.out.println("DEBUG: Is future? " + selectedCal.getTime().after(new Date()));

            if (!selectedCal.getTime().after(new Date())) {
                JOptionPane.showMessageDialog(bookDialog, "Please choose a future date/time.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                return;
            }

            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat timeOnlyFmt = new SimpleDateFormat("HH:mm");
            String appointmentDateTime = fmt.format(selectedCal.getTime());
            String appointmentDate = new SimpleDateFormat("yyyy-MM-dd").format(selectedCal.getTime());

            // Check for appointment conflicts
            String[] conflictInfo = checkAppointmentConflict(appointmentDate, hour, minute);
            if (conflictInfo != null) {
                // Conflict detected
                String conflictMessage = "Not allowed. " + conflictInfo[0] + "\nSuggested time: " + conflictInfo[1];
                JOptionPane.showMessageDialog(bookDialog, conflictMessage, "Time Conflict", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = DatabaseHelper.bookAppointmentWithoutStaff(this.patientId, appointmentDateTime, reason);
            if (success) {
                int apptId = DatabaseHelper.getLatestAppointmentIdForPatient(this.patientId);
                if (apptId > 0) {
                    DatabaseHelper.autoApproveAppointment(apptId);
                    // Add a small delay to ensure database update completes
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                JOptionPane.showMessageDialog(bookDialog, "Appointment requested successfully!\nStatus will be updated shortly.");
                bookDialog.dispose();
                showAppointments();
            } else {
                JOptionPane.showMessageDialog(bookDialog, "Error booking appointment. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> bookDialog.dispose());

        buttonPanel.add(bookBtn);
        buttonPanel.add(cancelBtn);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        bookDialog.add(mainPanel);
        bookDialog.setVisible(true);
    }

    /**
     * Creates a calendar panel for booking with date selection
     */
    private JPanel createBookingCalendarPanel(Calendar selectedCal) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Calendar currentCal = Calendar.getInstance();
        currentCal.add(Calendar.DAY_OF_MONTH, 1); // Start from tomorrow

        // Month/Year label
        JLabel monthYearLabel = new JLabel();
        monthYearLabel.setFont(new Font("Inter", Font.BOLD, 14));
        monthYearLabel.setForeground(PRIMARY_BLUE);
        monthYearLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(monthYearLabel);
        panel.add(Box.createVerticalStrut(10));

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        navPanel.setBackground(Color.WHITE);
        JButton prevBtn = new JButton("◀");
        JButton nextBtn = new JButton("▶");
        prevBtn.setFont(new Font("Arial", Font.BOLD, 14));
        nextBtn.setFont(new Font("Arial", Font.BOLD, 14));
        prevBtn.setFocusPainted(false);
        nextBtn.setFocusPainted(false);
        navPanel.add(prevBtn);
        navPanel.add(nextBtn);
        panel.add(navPanel);
        panel.add(Box.createVerticalStrut(10));

        // Day headers
        JPanel dayHeaderPanel = new JPanel(new GridLayout(1, 7, 5, 5));
        dayHeaderPanel.setBackground(Color.WHITE);
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : days) {
            JLabel dayLabel = new JLabel(day);
            dayLabel.setFont(new Font("Inter", Font.BOLD, 11));
            dayLabel.setForeground(PRIMARY_BLUE);
            dayLabel.setHorizontalAlignment(JLabel.CENTER);
            dayHeaderPanel.add(dayLabel);
        }
        panel.add(dayHeaderPanel);
        panel.add(Box.createVerticalStrut(5));

        // Calendar grid
        JPanel gridPanel = new JPanel(new GridLayout(6, 7, 5, 5));
        gridPanel.setBackground(Color.WHITE);

        final int[] lastSelectedDay = {-1}; // Track last selected day for highlighting

        final Runnable[] populateCalendarRef = new Runnable[1];

        populateCalendarRef[0] = new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat fmt = new SimpleDateFormat("MMMM yyyy");
                monthYearLabel.setText(fmt.format(currentCal.getTime()));

                gridPanel.removeAll();

                Calendar firstDay = (Calendar) currentCal.clone();
                firstDay.set(Calendar.DAY_OF_MONTH, 1);
                int startDay = firstDay.get(Calendar.DAY_OF_WEEK) - 1;
                int daysInMonth = currentCal.getActualMaximum(Calendar.DAY_OF_MONTH);

                for (int i = 0; i < 42; i++) {
                    JButton dayBtn = new JButton();
                    dayBtn.setFont(new Font("Inter", Font.PLAIN, 12));
                    dayBtn.setFocusPainted(false);
                    dayBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

                    if (i < startDay || i >= startDay + daysInMonth) {
                        dayBtn.setText("");
                        dayBtn.setEnabled(false);
                        dayBtn.setBackground(new Color(245, 245, 245));
                    } else {
                        int day = i - startDay + 1;
                        dayBtn.setText(String.valueOf(day));
                        dayBtn.setEnabled(true);
                        dayBtn.setBackground(Color.WHITE);

                        // Check if this is today
                        Calendar today = Calendar.getInstance();
                        if (currentCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                            currentCal.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                            day == today.get(Calendar.DAY_OF_MONTH)) {
                            dayBtn.setBackground(PRIMARY_BLUE);
                            dayBtn.setForeground(Color.WHITE);
                        } else if (day == lastSelectedDay[0]) {
                            // Highlight previously selected day
                            dayBtn.setBackground(new Color(100, 150, 255));
                            dayBtn.setForeground(Color.WHITE);
                        }

                        final int selectedDay = day;
                        dayBtn.addActionListener(e -> {
                            // Update selectedCal with year, month, and day
                            selectedCal.set(Calendar.YEAR, currentCal.get(Calendar.YEAR));
                            selectedCal.set(Calendar.MONTH, currentCal.get(Calendar.MONTH));
                            selectedCal.set(Calendar.DAY_OF_MONTH, selectedDay);
                            
                            lastSelectedDay[0] = selectedDay;
                            
                            System.out.println("DEBUG: Date selected = " + new SimpleDateFormat("yyyy-MM-dd").format(selectedCal.getTime()));

                            // Refresh calendar to show selection
                            populateCalendarRef[0].run();
                        });
                    }

                    gridPanel.add(dayBtn);
                }

                gridPanel.revalidate();
                gridPanel.repaint();
            }
        };

        prevBtn.addActionListener(e -> {
            currentCal.add(Calendar.MONTH, -1);
            populateCalendarRef[0].run();
        });

        nextBtn.addActionListener(e -> {
            currentCal.add(Calendar.MONTH, 1);
            populateCalendarRef[0].run();
        });

        panel.add(gridPanel);
        populateCalendarRef[0].run();
        return panel;
    }

    /**
     * Creates time picker panel with hour and minute spinners
     */
    private JPanel createTimePickerPanel(Calendar selectedCal) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel timeLabel = new JLabel("Select Time:");
        timeLabel.setFont(LABEL_FONT);
        timeLabel.setForeground(PRIMARY_BLUE);
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(timeLabel);
        panel.add(Box.createVerticalStrut(15));

        // Hour spinner
        JPanel hourPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        hourPanel.setBackground(Color.WHITE);
        hourPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hourLabel = new JLabel("Hour (0-23):");
        hourLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        hourPanel.add(hourLabel);

        SpinnerNumberModel hourModel = new SpinnerNumberModel(9, 0, 23, 1);
        JSpinner hourSpinner = new JSpinner(hourModel);
        hourSpinner.setPreferredSize(new Dimension(70, 30));
        hourPanel.add(hourSpinner);
        panel.add(hourPanel);
        panel.add(Box.createVerticalStrut(15));

        // Minute spinner
        JPanel minutePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        minutePanel.setBackground(Color.WHITE);
        minutePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel minuteLabel = new JLabel("Minute:");
        minuteLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        minutePanel.add(minuteLabel);

        SpinnerNumberModel minuteModel = new SpinnerNumberModel(0, 0, 59, 5);
        JSpinner minuteSpinner = new JSpinner(minuteModel);
        minuteSpinner.setPreferredSize(new Dimension(70, 30));
        minutePanel.add(minuteSpinner);
        panel.add(minutePanel);
        panel.add(Box.createVerticalStrut(30));

        // Selected date/time display
        JLabel selectedDateTimeLabel = new JLabel("Selected: Not set");
        selectedDateTimeLabel.setFont(new Font("Inter", Font.BOLD, 12));
        selectedDateTimeLabel.setForeground(PRIMARY_BLUE);
        selectedDateTimeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(selectedDateTimeLabel);

        // Update display when spinners change
        hourSpinner.addChangeListener(e -> updateDateTimeDisplay(selectedCal, hourSpinner, minuteSpinner, selectedDateTimeLabel));
        minuteSpinner.addChangeListener(e -> updateDateTimeDisplay(selectedCal, hourSpinner, minuteSpinner, selectedDateTimeLabel));

        panel.add(Box.createVerticalGlue());

        // Store spinners in panel properties for later access
        panel.putClientProperty("hourSpinner", hourSpinner);
        panel.putClientProperty("minuteSpinner", minuteSpinner);

        return panel;
    }

    /**
     * Updates the selected date/time display label
     */
    private void updateDateTimeDisplay(Calendar selectedCal, JSpinner hourSpinner, JSpinner minuteSpinner, JLabel displayLabel) {
        int hour = (Integer) hourSpinner.getValue();
        int minute = (Integer) minuteSpinner.getValue();

        SimpleDateFormat fmt = new SimpleDateFormat("MMM dd, yyyy");
        String dateStr = fmt.format(selectedCal.getTime());
        displayLabel.setText(String.format("Selected: %s at %02d:%02d", dateStr, hour, minute));
    }

    /**
     * Shows staff information
     */
    private void showStaffInfo() {
        mainContentPanel.removeAll();

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Health Center Staff");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(20));

        // Staff table
        Object[][] staffData = DatabaseHelper.getAllStaff();
        if (staffData != null && staffData.length > 0) {
            String[] columns = {"Id", "Name", "Role", "Area of Focus", "Contact"};
            JTable staffTable = new JTable(staffData, columns);
            staffTable.setRowHeight(25);
            JScrollPane scrollPane = new JScrollPane(staffTable);
            content.add(scrollPane);
        } else {
            JLabel noStaffLabel = new JLabel("No staff information available");
            noStaffLabel.setFont(new Font("Inter", Font.ITALIC, 12));
            noStaffLabel.setForeground(new Color(150, 150, 150));
            content.add(noStaffLabel);
        }

        mainContentPanel.add(new JScrollPane(content), BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    /**
     * Checks for appointment conflicts on the selected date and time
     * Checks against ALL APPROVED and PENDING appointments in the system (not just current patient's)
     * This prevents double-booking across all patients
     * Only blocks times AT and AFTER existing appointments (1-hour buffer after only)
     * @param appointmentDate the appointment date in format "yyyy-MM-dd"
     * @param hour the appointment hour (0-23)
     * @param minute the appointment minute (0-59)
     * @return Array with conflict info [conflict message, suggested time] or null if no conflict
     */
    private String[] checkAppointmentConflict(String appointmentDate, int hour, int minute) {
        // Get all APPROVED and PENDING appointments for this date (system-wide check)
        Object[][] appointmentData = DatabaseHelper.getApprovedAppointmentsByDateForConflictCheck(appointmentDate);
        
        if (appointmentData == null || appointmentData.length == 0) {
            return null; // No existing appointments, no conflict
        }

        // Convert selected time to minutes for easier calculation
        int selectedMinutes = hour * 60 + minute;
        
        for (Object[] appt : appointmentData) {
            if (appt.length >= 4) {
                String apptDate = appt[2].toString(); // Date column
                String apptTime = appt[3].toString(); // Time column
                
                // Only check conflicts on the same date
                if (apptDate.equals(appointmentDate)) {
                    // Parse existing appointment time
                    String[] timeParts = apptTime.split(":");
                    if (timeParts.length >= 2) {
                        int existingHour = Integer.parseInt(timeParts[0]);
                        int existingMinute = Integer.parseInt(timeParts[1]);
                        int existingMinutes = existingHour * 60 + existingMinute;
                        
                        // Check if selected time is AT or AFTER existing appointment
                        // and within 1 hour after (60 minutes)
                        if (selectedMinutes >= existingMinutes && selectedMinutes < existingMinutes + 60) {
                            // Conflict found! Find suggested times
                            // Convert array to list for the findSuggestedTime method
                            java.util.List<Object[]> appointmentList = new java.util.ArrayList<>();
                            for (Object[] a : appointmentData) {
                                appointmentList.add(a);
                            }
                            String suggestedTime = findSuggestedTime(appointmentDate, appointmentList);
                            return new String[]{"Appointment conflicts with existing appointment (1-hour buffer).", suggestedTime};
                        }
                    }
                }
            }
        }
        
        return null; // No conflict
    }

    /**
     * Finds suggested available times on the given date
     * Respects 1-hour buffer AFTER existing appointments only
     * @param appointmentDate the date to find available times for
     * @param allAppointments list of existing appointments data
     * @return suggested time in format "HH:mm"
     */
    private String findSuggestedTime(String appointmentDate, java.util.List<Object[]> allAppointments) {
        // Collect all booked times with 1-hour buffer AFTER on this date
        java.util.Set<Integer> blockedMinutes = new java.util.HashSet<>();
        
        for (Object[] appt : allAppointments) {
            if (appt.length >= 4) {
                String apptDate = appt[2].toString();
                String apptTime = appt[3].toString();
                
                if (apptDate.equals(appointmentDate)) {
                    // Parse appointment time
                    String[] timeParts = apptTime.split(":");
                    if (timeParts.length >= 2) {
                        int existingHour = Integer.parseInt(timeParts[0]);
                        int existingMinute = Integer.parseInt(timeParts[1]);
                        int existingMinutes = existingHour * 60 + existingMinute;
                        
                        // Block from appointment time to 1 hour after (but NOT before)
                        for (int i = existingMinutes; i < existingMinutes + 60; i++) {
                            blockedMinutes.add(i);
                        }
                    }
                }
            }
        }
        
        // Find next available slot (30-minute intervals, starting from 9:00 AM)
        for (int hour = 9; hour <= 17; hour++) { // Business hours 9 AM to 5 PM
            for (int minute = 0; minute < 60; minute += 30) {
                int totalMinutes = hour * 60 + minute;
                
                // Skip if this time is blocked
                if (blockedMinutes.contains(totalMinutes)) {
                    continue;
                }
                
                // Return first available time
                return String.format("%02d:%02d", hour, minute);
            }
        }
        
        // If no slot found in business hours, try early morning next available
        for (int hour = 9; hour <= 17; hour++) {
            for (int minute = 0; minute < 60; minute += 15) { // Try 15-minute intervals as fallback
                int totalMinutes = hour * 60 + minute;
                if (!blockedMinutes.contains(totalMinutes)) {
                    return String.format("%02d:%02d", hour, minute);
                }
            }
        }
        
        return "09:00";
    }

    /**
     * Logout
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new LoginScreen().setVisible(true);
            this.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatientDashboard(1, "patient@example.com"));
    }
}