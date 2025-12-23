import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Calendar;
import java.util.function.Consumer;

/**
 * Staff Dashboard - Manage appointments, patient records, billing history
 * Appointment History organized by date (APPROVED only)
 */
public class StaffDashboard extends JFrame {
    private static final Color PRIMARY_BLUE = new Color(0, 102, 204);
    private static final Color SIDEBAR_GRAY = new Color(240, 240, 240);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color DATE_HEADER_BLUE = new Color(230, 240, 250);
    private static final Font BUTTON_FONT = new Font("Inter", Font.BOLD, 14);
    private static final Font TITLE_FONT = new Font("Inter", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Inter", Font.BOLD, 12);
    private static final Font DATE_HEADER_FONT = new Font("Inter", Font.BOLD, 14);

    private JPanel mainContentPanel;
    private JLabel totalAppointmentsLabel;
    private JLabel totalPatientsLabel;
    private DefaultTableModel appointmentsModel;
    private DefaultTableModel patientsModel;
    private DefaultTableModel billingModel;

    public StaffDashboard() {
        setTitle("Barangay Health Center - Staff Dashboard");
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

        JLabel titleLabel = new JLabel("STAFF PANEL");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_BLUE);
        sidebar.add(titleLabel);
        sidebar.add(Box.createVerticalStrut(30));

        sidebar.add(createNavButton("📊 Dashboard", e -> showDashboard()));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createNavButton("📅 Manage Appointments", e -> showManageAppointments()));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createNavButton("👤 Patient Records", e -> showPatientRecords()));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createNavButton("📋 Appointment History", e -> showAppointmentHistory()));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createNavButton("💳 Billing History", e -> showBillingHistory()));
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
     * Creates main content area
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

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        statsPanel.setBackground(Color.WHITE);

        totalAppointmentsLabel = new JLabel("Appointments: 0");
        totalAppointmentsLabel.setFont(new Font("Inter", Font.BOLD, 16));
        totalAppointmentsLabel.setForeground(PRIMARY_BLUE);

        totalPatientsLabel = new JLabel("Patients: 0");
        totalPatientsLabel.setFont(new Font("Inter", Font.BOLD, 16));
        totalPatientsLabel.setForeground(PRIMARY_BLUE);

        statsPanel.add(totalAppointmentsLabel);
        statsPanel.add(totalPatientsLabel);
        content.add(statsPanel);
        content.add(Box.createVerticalStrut(30));

        loadCounts();

        mainContentPanel.add(new JScrollPane(content), BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    /**
     * Shows manage appointments tab
     */
    private void showManageAppointments() {
        mainContentPanel.removeAll();

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Manage Appointments");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        content.add(titleLabel, BorderLayout.NORTH);

        appointmentsModel = new DefaultTableModel(new Object[]{"ID","Patient","Reason","Date","Time","Status"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable appointmentsTable = new JTable(appointmentsModel);
        appointmentsTable.setRowHeight(25);
        JScrollPane apptScroll = new JScrollPane(appointmentsTable);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(apptScroll, BorderLayout.CENTER);

        JPanel apptActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton approveBtn = new JButton("Approve");
        JButton disapproveBtn = new JButton("Disapprove");
        apptActions.add(approveBtn);
        apptActions.add(disapproveBtn);
        centerPanel.add(apptActions, BorderLayout.SOUTH);

        approveBtn.addActionListener(e -> {
            int sel = appointmentsTable.getSelectedRow();
            if (sel == -1) { JOptionPane.showMessageDialog(this, "Select an appointment first."); return; }
            int modelRow = appointmentsTable.convertRowIndexToModel(sel);
            Object idObj = appointmentsModel.getValueAt(modelRow, 0);
            if (idObj == null) return;
            int apptId = Integer.parseInt(idObj.toString());
            boolean ok = DatabaseHelper.updateAppointmentStatus(apptId, "APPROVED");
            if (ok) { JOptionPane.showMessageDialog(this, "Appointment approved."); loadAppointments(); loadCounts(); }
            else JOptionPane.showMessageDialog(this, "Error updating appointment.", "Error", JOptionPane.ERROR_MESSAGE);
        });

        disapproveBtn.addActionListener(e -> {
            int sel = appointmentsTable.getSelectedRow();
            if (sel == -1) { JOptionPane.showMessageDialog(this, "Select an appointment first."); return; }
            int modelRow = appointmentsTable.convertRowIndexToModel(sel);
            Object idObj = appointmentsModel.getValueAt(modelRow, 0);
            if (idObj == null) return;
            int apptId = Integer.parseInt(idObj.toString());
            boolean ok = DatabaseHelper.updateAppointmentStatus(apptId, "DISAPPROVED");
            if (ok) { JOptionPane.showMessageDialog(this, "Appointment disapproved."); loadAppointments(); loadCounts(); }
            else JOptionPane.showMessageDialog(this, "Error updating appointment.", "Error", JOptionPane.ERROR_MESSAGE);
        });

        content.add(centerPanel, BorderLayout.CENTER);
        mainContentPanel.add(content);
        loadAppointments();
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    /**
     * Shows patient records tab
     */
    private void showPatientRecords() {
        mainContentPanel.removeAll();

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Patient Records");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        content.add(titleLabel, BorderLayout.NORTH);

        patientsModel = new DefaultTableModel(new Object[]{"ID","Name","Age","Contact","Last Visit"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable patientsTable = new JTable(patientsModel);
        patientsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(patientsTable);
        content.add(scrollPane, BorderLayout.CENTER);

        mainContentPanel.add(content);
        loadPatients();
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    /**
     * Shows appointment history - select date via calendar, display APPROVED appointments for that date
     */
    private void showAppointmentHistory() {
        mainContentPanel.removeAll();

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Approved Appointments by Date");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        content.add(titleLabel, BorderLayout.NORTH);

        // Top panel: calendar + buttons
        JPanel topPanel = new JPanel(new BorderLayout(15, 15));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Selected date label
        JLabel selectedDateLabel = new JLabel("Select a date from the calendar");
        selectedDateLabel.setFont(LABEL_FONT);
        selectedDateLabel.setForeground(PRIMARY_BLUE);

        // Appointments table for selected date
        DefaultTableModel appointmentsModel = new DefaultTableModel(
            new Object[]{"ID","Patient","Reason","Time","Staff","Status"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable appointmentsTable = new JTable(appointmentsModel);
        appointmentsTable.setRowHeight(24);
        JScrollPane tableScroll = new JScrollPane(appointmentsTable);
        tableScroll.setPreferredSize(new Dimension(700, 300));

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(selectedDateLabel, BorderLayout.NORTH);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        // Calendar panel
        JPanel calendarPanel = createCalendarPanel((selectedCal) -> {
            SimpleDateFormat fmt = new SimpleDateFormat("MMM dd, yyyy");
            selectedDateLabel.setText("Appointments for: " + fmt.format(selectedCal.getTime()));

            SimpleDateFormat dbFmt = new SimpleDateFormat("yyyy-MM-dd");
            String selectedDate = dbFmt.format(selectedCal.getTime());

            new Thread(() -> {
                Object[][] appts = DatabaseHelper.getApprovedAppointmentsByDate(selectedDate);
                SwingUtilities.invokeLater(() -> {
                    appointmentsModel.setRowCount(0);
                    if (appts != null && appts.length > 0) {
                        for (Object[] appt : appts) {
                            String time = "";
                            if (appt.length > 3 && appt[3] != null) {
                                String full = appt[3].toString();
                                int idx = full.indexOf(' ');
                                time = (idx >= 0 && idx + 1 < full.length()) ? full.substring(idx + 1) : full;
                            }
                            appointmentsModel.addRow(new Object[]{appt[0], appt[1], appt[2], time, appt[4], appt[5]});
                        }
                    }
                });
            }).start();
        });

        topPanel.add(calendarPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.CENTER);
        content.add(topPanel, BorderLayout.CENTER);

        mainContentPanel.add(content);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    /**
     * Creates a calendar panel for date selection with callback
     */
    private JPanel createCalendarPanel(DateSelectionCallback callback) {
        JPanel calendarPanel = new JPanel();
        calendarPanel.setLayout(new BoxLayout(calendarPanel, BoxLayout.Y_AXIS));
        calendarPanel.setBackground(Color.WHITE);
        calendarPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Calendar currentCal = Calendar.getInstance();

        // Month/Year label
        JLabel monthYearLabel = new JLabel();
        monthYearLabel.setFont(new Font("Inter", Font.BOLD, 14));
        monthYearLabel.setForeground(PRIMARY_BLUE);
        monthYearLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        calendarPanel.add(monthYearLabel);
        calendarPanel.add(Box.createVerticalStrut(10));

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
        calendarPanel.add(navPanel);
        calendarPanel.add(Box.createVerticalStrut(10));

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
        calendarPanel.add(dayHeaderPanel);
        calendarPanel.add(Box.createVerticalStrut(5));

        // Calendar grid
        JPanel gridPanel = new JPanel(new GridLayout(6, 7, 5, 5));
        gridPanel.setBackground(Color.WHITE);

        // Array to hold the Runnable (allows self-reference)
        final Runnable[] populateCalendarRef = new Runnable[1];

        // Populate calendar
        populateCalendarRef[0] = new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat fmt = new SimpleDateFormat("MMMM yyyy");
                monthYearLabel.setText(fmt.format(currentCal.getTime()));

                // Clear all buttons first
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

                        // Check if today
                        Calendar today = Calendar.getInstance();
                        if (currentCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                            currentCal.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                            day == today.get(Calendar.DAY_OF_MONTH)) {
                            dayBtn.setBackground(PRIMARY_BLUE);
                            dayBtn.setForeground(Color.WHITE);
                        }

                        // Add action listener with proper closure
                        final int selectedDay = day;
                        final Calendar selectedCal = (Calendar) currentCal.clone();
                        dayBtn.addActionListener(e -> {
                            selectedCal.set(Calendar.DAY_OF_MONTH, selectedDay);
                            callback.onDateSelected(selectedCal);

                            // Highlight selected date - refresh entire calendar
                            populateCalendarRef[0].run();

                            // Re-highlight the selected button
                            for (Component comp : gridPanel.getComponents()) {
                                if (comp instanceof JButton) {
                                    JButton b = (JButton) comp;
                                    if (b.getText().equals(String.valueOf(selectedDay))) {
                                        b.setBackground(new Color(100, 150, 255));
                                        b.setForeground(Color.WHITE);
                                    }
                                }
                            }
                        });
                    }

                    gridPanel.add(dayBtn);
                }

                gridPanel.revalidate();
                gridPanel.repaint();
            }
        };

        // Previous month
        prevBtn.addActionListener(e -> {
            currentCal.add(Calendar.MONTH, -1);
            populateCalendarRef[0].run();
        });

        // Next month
        nextBtn.addActionListener(e -> {
            currentCal.add(Calendar.MONTH, 1);
            populateCalendarRef[0].run();
        });

        calendarPanel.add(gridPanel);
        populateCalendarRef[0].run();
        return calendarPanel;
    }

    /**
     * Functional interface for date selection callback
     */
    @FunctionalInterface
    private interface DateSelectionCallback {
        void onDateSelected(Calendar selectedDate);
    }

    /**
     * Shows billing history tab
     */
    private void showBillingHistory() {
        mainContentPanel.removeAll();

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Billing History");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        content.add(titleLabel, BorderLayout.NORTH);

        billingModel = new DefaultTableModel(new Object[]{"PATIENT_ID","NAME","CONTACT","BILL","STATUS"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable billingTable = new JTable(billingModel);
        billingTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(billingTable);
        content.add(scrollPane, BorderLayout.CENTER);

        mainContentPanel.add(content);
        loadBillings();
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void loadCounts() {
        new Thread(() -> {
            int apptCount = DatabaseHelper.getTotalAppointmentsCount();
            int patientCount = DatabaseHelper.getPatientCount();
            SwingUtilities.invokeLater(() -> {
                totalAppointmentsLabel.setText("Appointments: " + apptCount);
                totalPatientsLabel.setText("Patients: " + patientCount);
            });
        }).start();
    }

    private void loadAppointments() {
        new Thread(() -> {
            Object[][] rows = DatabaseHelper.getAllAppointments();
            SwingUtilities.invokeLater(() -> {
                appointmentsModel.setRowCount(0);
                if (rows != null) {
                    for (Object[] r : rows) {
                        appointmentsModel.addRow(new Object[]{r[0], r[1], r[2], r[3], r[4], r[5]});
                    }
                }
            });
        }).start();
    }

    private void loadPatients() {
        new Thread(() -> {
            Object[][] rows = DatabaseHelper.getAllPatientsForStaffView();
            SwingUtilities.invokeLater(() -> {
                patientsModel.setRowCount(0);
                if (rows != null) {
                    for (Object[] r : rows) {
                        patientsModel.addRow(r);
                    }
                }
            });
        }).start();
    }

    private void loadBillings() {
        new Thread(() -> {
            Object[][] rows = DatabaseHelper.getBillingHistory();
            SwingUtilities.invokeLater(() -> {
                billingModel.setRowCount(0);
                if (rows != null) {
                    for (Object[] r : rows) {
                        billingModel.addRow(new Object[]{r[1], r[2], r[3], r[4], r[5]});
                    }
                }
            });
        }).start();
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
        SwingUtilities.invokeLater(StaffDashboard::new);
    }
}