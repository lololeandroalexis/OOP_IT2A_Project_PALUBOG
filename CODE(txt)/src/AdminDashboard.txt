import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Admin Dashboard - Manage staff, view billing records, system settings
 */
public class AdminDashboard extends JFrame {
    private static final Color PRIMARY_BLUE = new Color(0, 102, 204);
    private static final Color SIDEBAR_GRAY = new Color(240, 240, 240);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Font BUTTON_FONT = new Font("Inter", Font.BOLD, 14);
    private static final Font TITLE_FONT = new Font("Inter", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Inter", Font.BOLD, 12);

    private JPanel mainContentPanel;
    private JLabel totalPatientsLabel;
    private JLabel totalStaffLabel;
    private JLabel pendingAppointmentsLabel;
    private DefaultTableModel staffTableModel;
    private DefaultTableModel billingTableModel;
    private DefaultTableModel usersTableModel;

    public AdminDashboard() {
        setTitle("Barangay Health Center - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1200, 750));

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

        JLabel titleLabel = new JLabel("ADMIN PANEL");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_BLUE);
        sidebar.add(titleLabel);
        sidebar.add(Box.createVerticalStrut(30));

        sidebar.add(createNavButton("📊 Dashboard", e -> showDashboard()));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createNavButton("👥 Manage Staff", e -> showStaffManagement()));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createNavButton("💰 Billing Records", e -> showBillingRecords()));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createNavButton("👤 User Management", e -> showUserManagement()));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createNavButton("⚙️ Settings", e -> showSettings()));
        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createNavButton("🚪 Logout", e -> logout()));

        return sidebar;
    }

    /**
     * Creates navigation button
     */
    private JButton createNavButton(String text, ActionListener listener) {
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
        showDashboard();
        return mainContentPanel;
    }

    /**
     * Shows dashboard with counts
     */
    private void showDashboard() {
        mainContentPanel.removeAll();

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(30));

        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setMaximumSize(new Dimension(800, 120));

        // Total Patients
        totalPatientsLabel = new JLabel("0");
        totalPatientsLabel.setFont(new Font("Inter", Font.BOLD, 32));
        totalPatientsLabel.setForeground(PRIMARY_BLUE);
        JPanel patientBox = createStatBox("Total Patients", totalPatientsLabel);
        statsPanel.add(patientBox);

        // Total Staff
        totalStaffLabel = new JLabel("0");
        totalStaffLabel.setFont(new Font("Inter", Font.BOLD, 32));
        totalStaffLabel.setForeground(PRIMARY_BLUE);
        JPanel staffBox = createStatBox("Total Staff", totalStaffLabel);
        statsPanel.add(staffBox);

        // Pending Appointments
        pendingAppointmentsLabel = new JLabel("0");
        pendingAppointmentsLabel.setFont(new Font("Inter", Font.BOLD, 32));
        pendingAppointmentsLabel.setForeground(PRIMARY_BLUE);
        JPanel apptBox = createStatBox("Pending Appointments", pendingAppointmentsLabel);
        statsPanel.add(apptBox);

        content.add(statsPanel);
        content.add(Box.createVerticalStrut(30));

        // Load stats
        loadDashboardStats();

        mainContentPanel.add(new JScrollPane(content), BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    /**
     * Creates a stat box
     */
    private JPanel createStatBox(String label, JLabel valueLabel) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(LIGHT_GRAY);
        box.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 2));
        box.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(LABEL_FONT);
        labelComp.setForeground(PRIMARY_BLUE);
        box.add(labelComp);
        box.add(Box.createVerticalStrut(10));

        box.add(valueLabel);

        return box;
    }

    /**
     * Loads dashboard statistics
     */
    private void loadDashboardStats() {
        new Thread(() -> {
            int patients = DatabaseHelper.getPatientCount();
            int staff = DatabaseHelper.getStaffCount();
            int pending = DatabaseHelper.getPendingAppointmentsCount();

            SwingUtilities.invokeLater(() -> {
                totalPatientsLabel.setText(String.valueOf(patients));
                totalStaffLabel.setText(String.valueOf(staff));
                pendingAppointmentsLabel.setText(String.valueOf(pending));
            });
        }).start();
    }

    /**
     * Shows staff management with add new staff button
     */
    private void showStaffManagement() {
        mainContentPanel.removeAll();

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Manage Staff");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        content.add(titleLabel, BorderLayout.NORTH);

        // Add Staff button
        JButton addStaffBtn = new JButton("+ Add New Staff");
        addStaffBtn.setFont(BUTTON_FONT);
        addStaffBtn.setBackground(PRIMARY_BLUE);
        addStaffBtn.setForeground(Color.WHITE);
        addStaffBtn.setMaximumSize(new Dimension(150, 40));
        addStaffBtn.addActionListener(e -> openAddStaffDialog());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addStaffBtn);
        content.add(buttonPanel, BorderLayout.NORTH);

        // Staff table
        staffTableModel = new DefaultTableModel(new Object[]{"ID", "NAME", "POSITION", "EMAIL", "CONTACT"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable staffTable = new JTable(staffTableModel);
        staffTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(staffTable);
        content.add(scrollPane, BorderLayout.CENTER);

        mainContentPanel.add(content);
        loadStaffTable();
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    /**
     * Opens add staff dialog
     */
    private void openAddStaffDialog() {
        JDialog dialog = new JDialog(this, "Add New Staff", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(LABEL_FONT);
        JTextField nameField = new JTextField();
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(10));

        JLabel positionLabel = new JLabel("Position:");
        positionLabel.setFont(LABEL_FONT);
        JTextField positionField = new JTextField();
        panel.add(positionLabel);
        panel.add(positionField);
        panel.add(Box.createVerticalStrut(10));

        JLabel areaLabel = new JLabel("Area of Focus:");
        areaLabel.setFont(LABEL_FONT);
        JTextField areaField = new JTextField();
        panel.add(areaLabel);
        panel.add(areaField);
        panel.add(Box.createVerticalStrut(10));

        JLabel contactLabel = new JLabel("Contact:");
        contactLabel.setFont(LABEL_FONT);
        JTextField contactField = new JTextField();
        panel.add(contactLabel);
        panel.add(contactField);
        panel.add(Box.createVerticalStrut(10));

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(LABEL_FONT);
        JTextField emailField = new JTextField();
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(10));

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(LABEL_FONT);
        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveBtn = new JButton("Add Staff");
        saveBtn.setBackground(PRIMARY_BLUE);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String position = positionField.getText().trim();
            String area = areaField.getText().trim();
            String contact = contactField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || position.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all required fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean staffOk = DatabaseHelper.addStaff(name, position, area, contact);
            boolean userOk = DatabaseHelper.registerUser(name, name, email, password, "STAFF");

            if (staffOk && userOk) {
                JOptionPane.showMessageDialog(dialog, "Staff added successfully!");
                dialog.dispose();
                showStaffManagement();
            } else {
                JOptionPane.showMessageDialog(dialog, "Error adding staff", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel);

        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    /**
     * Loads staff table
     */
    private void loadStaffTable() {
        new Thread(() -> {
            Object[][] staffData = DatabaseHelper.getAllStaffForAdmin();
            SwingUtilities.invokeLater(() -> {
                staffTableModel.setRowCount(0);
                if (staffData != null) {
                    for (Object[] row : staffData) {
                        staffTableModel.addRow(row);
                    }
                }
            });
        }).start();
    }

    /**
     * Shows billing records
     */
    private void showBillingRecords() {
        mainContentPanel.removeAll();

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Billing Records");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        content.add(titleLabel, BorderLayout.NORTH);

        // Billing table
        billingTableModel = new DefaultTableModel(new Object[]{"PATIENT_ID", "NAME", "CONTACT", "BILL", "STATUS", "ACTION"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col == 5; }
        };
        JTable billingTable = new JTable(billingTableModel);
        billingTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(billingTable);
        content.add(scrollPane, BorderLayout.CENTER);

        mainContentPanel.add(content);
        loadBillingTable();
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    /**
     * Loads billing table
     */
    private void loadBillingTable() {
        new Thread(() -> {
            Object[][] billingData = DatabaseHelper.getBillingHistory();
            SwingUtilities.invokeLater(() -> {
                billingTableModel.setRowCount(0);
                if (billingData != null) {
                    for (Object[] row : billingData) {
                        billingTableModel.addRow(new Object[]{row[1], row[2], row[3], row[4], row[5], "Toggle"});
                    }
                }
            });
        }).start();
    }

    /**
     * Shows user management
     */
    private void showUserManagement() {
        mainContentPanel.removeAll();

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        content.add(titleLabel, BorderLayout.NORTH);

        // Users table
        usersTableModel = new DefaultTableModel(new Object[]{"ID", "NAME", "EMAIL", "ROLE"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable usersTable = new JTable(usersTableModel);
        usersTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(usersTable);
        content.add(scrollPane, BorderLayout.CENTER);

        mainContentPanel.add(content);
        loadUsersTable();
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    /**
     * Loads users table
     */
    private void loadUsersTable() {
        new Thread(() -> {
            Object[][] userData = DatabaseHelper.getAllUsers();
            SwingUtilities.invokeLater(() -> {
                usersTableModel.setRowCount(0);
                if (userData != null) {
                    for (Object[] row : userData) {
                        usersTableModel.addRow(row);
                    }
                }
            });
        }).start();
    }

    /**
     * Shows settings
     */
    private void showSettings() {
        mainContentPanel.removeAll();

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("System Settings");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(20));

        JLabel settingLabel = new JLabel("Settings are currently under maintenance");
        settingLabel.setFont(new Font("Inter", Font.ITALIC, 14));
        settingLabel.setForeground(new Color(150, 150, 150));
        content.add(settingLabel);

        mainContentPanel.add(new JScrollPane(content), BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
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
        SwingUtilities.invokeLater(AdminDashboard::new);
    }
}