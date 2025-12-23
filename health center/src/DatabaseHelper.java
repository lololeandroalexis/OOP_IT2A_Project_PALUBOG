import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper class for managing MySQL database connections and user authentication.
 * Requires MySQL Connector/J library in the classpath.
 */
public class DatabaseHelper {

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/barangay_health_center";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "LeandrO#_9"; // Change this to your MySQL password
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Please add mysql-connector-java to your classpath.");
            e.printStackTrace();
        }
    }

    /**
     * Establishes a connection to the MySQL database.
     * @return Connection object if successful, null otherwise.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Authenticates a user by verifying email and password against the database.
     * @param email User's email address.
     * @param password User's password.
     * @return User's role (PATIENT, ADMIN, STAFF) if authentication is successful, null otherwise.
     */
    public static String authenticateUser(String email, String password) {
        String query = "SELECT password, role FROM users WHERE email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String role = rs.getString("role");
                
                // Compare passwords (use bcrypt in production!)
                if (storedPassword.equals(password)) {
                    return role;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error during authentication: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Authenticates user and returns ID and role
     */
    public static String[] authenticateUserWithId(String email, String password) {
        String query = "SELECT id, role FROM users WHERE email = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new String[]{String.valueOf(rs.getInt("id")), rs.getString("role")};
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Registers a new user in the database.
     * @param email User's email address.
     * @param password User's password.
     * @param firstName User's first name.
     * @param lastName User's last name.
     * @return true if registration is successful, false otherwise.
     */
    public static boolean registerUser(String email, String password, String firstName, String lastName) {
        String query = "INSERT INTO users (email, password, first_name, last_name, created_at) VALUES (?, ?, ?, ?, NOW())";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Checks if an email already exists in the database.
     * @param email Email address to check.
     * @return true if email exists, false otherwise.
     */
    public static boolean emailExists(String email) {
        String query = "SELECT id FROM users WHERE email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next();
            
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Creates the required database and tables if they don't exist.
     * Run this once during application initialization.
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            
            // Create users table with role column
            String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "email VARCHAR(255) UNIQUE NOT NULL," +
                    "password VARCHAR(255) NOT NULL," +
                    "first_name VARCHAR(255)," +
                    "last_name VARCHAR(255)," +
                    "role VARCHAR(20) NOT NULL DEFAULT 'PATIENT'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(usersTable);
            
            // Create staff table
            String staffTable = "CREATE TABLE IF NOT EXISTS staff (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "designation VARCHAR(255) NOT NULL," +
                    "area_of_focus VARCHAR(255)," +
                    "contact_details VARCHAR(255)," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(staffTable);
            
            // Create appointments table
            String appointmentsTable = "CREATE TABLE IF NOT EXISTS appointments (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "patient_id INT NOT NULL," +
                    "staff_id INT," +
                    "appointment_date DATETIME NOT NULL," +
                    "reason VARCHAR(255)," +
                    "status VARCHAR(20) NOT NULL DEFAULT 'PENDING'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (patient_id) REFERENCES users(id)," +
                    "FOREIGN KEY (staff_id) REFERENCES staff(id)" +
                    ")";
            stmt.executeUpdate(appointmentsTable);
            
            System.out.println("Database tables initialized successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error initializing tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns all users as a 2D object array for JTable display
     */
    public static Object[][] getUsersForTable() {
        String query = "SELECT id, email, first_name, last_name, role, created_at FROM users";
        java.util.List<Object[]> data = new java.util.ArrayList<>();
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                data.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("role"),
                    rs.getTimestamp("created_at").toString()
                });
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return data.toArray(new Object[0][]);
    }

    /**
     * Returns first_name, last_name, date_of_birth, profile_picture_path
     */
    public static String[] getPatientData(int patientId) {
        String q = "SELECT first_name, last_name, date_of_birth, COALESCE(profile_picture,'') as profile_picture FROM users WHERE id = ? AND role = 'PATIENT'";
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement(q)) {
            p.setInt(1, patientId);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return new String[]{
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("date_of_birth"),
                    rs.getString("profile_picture")
                };
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Returns first and last name only (convenience)
     */
    public static String[] getPatientNameComponents(int patientId) {
        String q = "SELECT first_name, last_name FROM users WHERE id = ? AND role = 'PATIENT'";
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement(q)) {
            p.setInt(1, patientId);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return new String[]{ rs.getString("first_name"), rs.getString("last_name") };
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return new String[]{"",""};
    }

    /**
     * Extended update: update name, phone, address, occupation, civil_status, profile picture path
     */
    public static boolean updatePatientProfileExtended(int patientId, String firstName, String lastName,
                                                       String phone, String address, String occupation,
                                                       String civilStatus, String profilePicPath) {
        String q = "UPDATE users SET first_name = ?, last_name = ?, phone_number = ?, address = ?, occupation = ?, civil_status = ?, profile_picture = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement(q)) {
            p.setString(1, firstName);
            p.setString(2, lastName);
            p.setString(3, phone);
            p.setString(4, address);
            p.setString(5, occupation);
            p.setString(6, civilStatus);
            p.setString(7, profilePicPath);
            p.setInt(8, patientId);
            return p.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Gets patient details
     */
    public static String[] getPatientDetails(int patientId) {
        String query = "SELECT email, phone_number, address, occupation, civil_status FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new String[]{
                    rs.getString("email"),
                    rs.getString("phone_number"),
                    rs.getString("address"),
                    rs.getString("occupation"),
                    rs.getString("civil_status")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates patient profile
     */
    public static boolean updatePatientProfile(int patientId, String phone, String address, String occupation, String civilStatus) {
        String query = "UPDATE users SET phone_number = ?, address = ?, occupation = ?, civil_status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, phone);
            pstmt.setString(2, address);
            pstmt.setString(3, occupation);
            pstmt.setString(4, civilStatus);
            pstmt.setInt(5, patientId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Gets patient appointments
     */
    public static Object[][] getPatientAppointments(int patientId) {
        String query = """
          SELECT 
              a.id,
              a.reason,
            DATE(a.appointment_date) AS appointment_date,
            TIME_FORMAT(a.appointment_date, '%H:%i') AS appointment_time,
            UPPER(a.status) AS status
        FROM appointments a
        WHERE a.patient_id = ?
        ORDER BY a.appointment_date DESC
    """;

    List<Object[]> rows = new ArrayList<>();

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

        pstmt.setInt(1, patientId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            rows.add(new Object[]{
                rs.getInt("id"),                  // 0: ID
                rs.getString("reason"),           // 1: Reason
                rs.getString("appointment_date"), // 2: Date (e.g., 2025-12-21)
                rs.getString("appointment_time"), // 3: Time (e.g., 10:00)
                rs.getString("status")            // 4: Status
            });
        }

        System.out.println("Loaded " + rows.size() + " appointments for patient ID: " + patientId);

    } catch (SQLException e) {
        System.err.println("Error loading appointments for patient " + patientId + ": " + e.getMessage());
        e.printStackTrace();
    }

    return rows.toArray(new Object[0][]);
}

    /**
     * Gets all staff
     */
    public static Object[][] getAllStaff() {
        String query = "SELECT id, name, designation, area_of_focus, contact_details FROM staff";
        List<Object[]> data = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                data.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("designation"),
                    rs.getString("area_of_focus"),
                    rs.getString("contact_details")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data.toArray(new Object[0][]);
    }

    /**
     * Books an appointment
     */
    public static boolean bookAppointment(int patientId, int staffId, String appointmentDateTime, String reason) {
        String query = "INSERT INTO appointments (patient_id, staff_id, appointment_date, reason, status) VALUES (?, ?, STR_TO_DATE(?, '%Y-%m-%d %H:%i'), ?, 'PENDING')";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, patientId);
            pstmt.setInt(2, staffId);
            pstmt.setString(3, appointmentDateTime);
            pstmt.setString(4, reason);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Books an appointment without assigning a staff member (staff_id = NULL).
     * status defaults to 'PENDING'.
     */
    public static boolean bookAppointmentWithoutStaff(int patientId, String appointmentDateTime, String reason) {
        String q = "INSERT INTO appointments (patient_id, appointment_date, reason, status, created_at) VALUES (?, ?, ?, 'PENDING', ?)";
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement(q)) {
            p.setInt(1, patientId);
            p.setString(2, appointmentDateTime); // expects 'yyyy-MM-dd HH:mm:ss'
            p.setString(3, reason);
            p.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            return p.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ERROR bookAppointmentWithoutStaff: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Gets patient count for admin dashboard
     */
    public static int getPatientCount() {
        String query = "SELECT COUNT(*) as count FROM users WHERE role = 'PATIENT'";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Gets staff count for admin dashboard
     */
    public static int getStaffCount() {
        String query = "SELECT COUNT(*) as count FROM staff";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Gets pending appointments count
     */
    public static int getPendingAppointmentsCount() {
        String query = "SELECT COUNT(*) as count FROM appointments WHERE status = 'PENDING'";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Total appointments count
     */
    public static int getTotalAppointmentsCount() {
        String q = "SELECT COUNT(*) as c FROM appointments";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(q)) {
            if (rs.next()) return rs.getInt("c");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Return all appointments for staff view: id, patient name, reason, date, time, status
     */
    public static Object[][] getAllAppointments() {
        String q = "SELECT a.id, CONCAT(u.first_name,' ',u.last_name) AS patient, a.reason, DATE_FORMAT(a.appointment_date,'%Y-%m-%d') as appt_date, TIME_FORMAT(a.appointment_date,'%H:%i') as appt_time, a.status FROM appointments a LEFT JOIN users u ON a.patient_id = u.id ORDER BY a.appointment_date DESC";
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(q)) {
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("patient"),
                    rs.getString("reason"),
                    rs.getString("appt_date"),
                    rs.getString("appt_time"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows.toArray(new Object[0][]);
    }

    /**
     * Update appointment status (APPROVED / DISAPPROVED / PENDING)
     */
    public static boolean updateAppointmentStatus(int appointmentId, String status) {
        String q = "UPDATE appointments SET status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement(q)) {
            p.setString(1, status);
            p.setInt(2, appointmentId);
            return p.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Returns patients rows for staff view:
     * id, name, age, contact, last_visit (datetime), purpose_of_visit (last reason)
     */
    public static Object[][] getAllPatientsForStaffView() {
        String q =
            "SELECT u.id, CONCAT(u.first_name,' ',u.last_name) AS name, " +
            "IFNULL(TIMESTAMPDIFF(YEAR, u.date_of_birth, CURDATE()), '') AS age, " +
            "IFNULL(u.phone_number,'') AS contact, " +
            "(SELECT DATE_FORMAT(a.appointment_date,'%Y-%m-%d %H:%i') FROM appointments a WHERE a.patient_id = u.id ORDER BY a.appointment_date DESC LIMIT 1) AS last_visit, " +
            "(SELECT a.reason FROM appointments a WHERE a.patient_id = u.id ORDER BY a.appointment_date DESC LIMIT 1) AS purpose " +
            "FROM users u WHERE u.role = 'PATIENT' ORDER BY u.last_name, u.first_name";
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(q)) {
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("age"),
                    rs.getString("contact"),
                    rs.getString("last_visit"),
                    rs.getString("purpose")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows.toArray(new Object[0][]);
    }

    /**
     * Returns billing history rows:
     * billing_id, patient_id, patient_name, contact, amount (as String), status (PAID/UNPAID)
     */
    public static Object[][] getBillingHistory() {
        String q = "SELECT b.id AS billing_id, b.patient_id, CONCAT(u.first_name,' ',u.last_name) AS name, " +
                   "COALESCE(u.phone_number,'') AS contact, CONCAT('$', FORMAT(b.amount,2)) AS amount, " +
                   "UPPER(b.status) AS status " +
                   "FROM billing b " +
                   "JOIN users u ON b.patient_id = u.id " +
                   "ORDER BY b.created_at DESC";
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(q)) {
            System.out.println("DEBUG: Executing getBillingHistory query...");
            int count = 0;
            while (rs.next()) {
                count++;
                Object[] row = new Object[]{
                    rs.getInt("billing_id"),
                    rs.getInt("patient_id"),
                    rs.getString("name"),
                    rs.getString("contact"),
                    rs.getString("amount"),
                    rs.getString("status")
                };
                System.out.println("DEBUG: Row " + count + " - " + row[2] + " (" + row[5] + ")");
                rows.add(row);
            }
            System.out.println("DEBUG: getBillingHistory returned " + count + " rows");
        } catch (SQLException e) {
            System.err.println("ERROR in getBillingHistory: " + e.getMessage());
            e.printStackTrace();
        }
        return rows.toArray(new Object[0][]);
    }

    /**
     * Update billing status PAID/UNPAID
     */
    public static boolean updateBillingStatus(int billingId, String status) {
        String q = "UPDATE billing SET status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement(q)) {
            p.setString(1, status);
            p.setInt(2, billingId);
            int updated = p.executeUpdate();
            System.out.println("DEBUG: updateBillingStatus - Updated " + updated + " rows for billing_id=" + billingId);
            return updated > 0;
        } catch (SQLException e) {
            System.err.println("ERROR in updateBillingStatus: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Adds a new staff member to the staff table
     */
    public static boolean addStaff(String name, String designation, String areaOfFocus, String contactDetails) {
        String q = "INSERT INTO staff (name, designation, area_of_focus, contact_details) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement(q)) {
            p.setString(1, name);
            p.setString(2, designation);
            p.setString(3, areaOfFocus);
            p.setString(4, contactDetails);
            return p.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Gets all staff for admin view with their user account info
     */
    public static Object[][] getAllStaffForAdmin() {
        String q = "SELECT s.id, s.name, s.designation, u.email, s.contact_details FROM staff s " +
                   "LEFT JOIN users u ON u.id = s.id WHERE u.role = 'STAFF' OR u.role IS NULL ORDER BY s.name";
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(q)) {
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("designation"),
                    rs.getString("email"),
                    rs.getString("contact_details")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows.toArray(new Object[0][]);
    }

    /**
     * Gets all users (patients and staff)
     */
    public static Object[][] getAllUsers() {
        String q = "SELECT id, CONCAT(first_name, ' ', last_name) AS name, email, role FROM users ORDER BY role, last_name";
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(q)) {
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows.toArray(new Object[0][]);
    }

    /**
     * Registers a new user (staff or patient)
     */
    public static boolean registerUser(String firstName, String lastName, String email, String password, String role) {
        String q = "INSERT INTO users (first_name, last_name, email, password, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement(q)) {
            p.setString(1, firstName);
            p.setString(2, lastName);
            p.setString(3, email);
            p.setString(4, password);
            p.setString(5, role);
            return p.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns appointment history for all dates (ordered by appointment_date ASC):
     * id, patient_name, reason, appointment_date (YYYY-MM-DD HH:mm), staff_name, status
     */
    public static Object[][] getAppointmentHistory() {
        String q = "SELECT a.id, CONCAT(u.first_name,' ',u.last_name) AS patient, a.reason, " +
                   "DATE_FORMAT(a.appointment_date, '%Y-%m-%d %H:%i') AS scheduled, " +
                   "COALESCE(s.name,'Unassigned') AS staff, UPPER(a.status) AS status " +
                   "FROM appointments a " +
                   "LEFT JOIN users u ON a.patient_id = u.id " +
                   "LEFT JOIN staff s ON a.staff_id = s.id " +
                   "ORDER BY a.appointment_date ASC";
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(q)) {
            int count = 0;
            while (rs.next()) {
                count++;
                rows.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("patient"),
                    rs.getString("reason"),
                    rs.getString("scheduled"),
                    rs.getString("staff"),
                    rs.getString("status")
                });
            }
            System.out.println("DEBUG: DatabaseHelper.getAppointmentHistory -> SQL returned " + count + " rows");
        } catch (SQLException e) {
            System.err.println("ERROR getAppointmentHistory: " + e.getMessage());
            e.printStackTrace();
        }
        return rows.toArray(new Object[0][]);
    }

    /**
     * Returns appointment history for a specific date (yyyy-MM-dd)
     */
    public static Object[][] getAppointmentHistoryByDate(String yyyyMMdd) {
        String q = "SELECT a.id, CONCAT(u.first_name,' ',u.last_name) AS patient, a.reason, " +
                   "DATE_FORMAT(a.appointment_date, '%Y-%m-%d %H:%i') AS scheduled, " +
                   "COALESCE(s.name,'Unassigned') AS staff, UPPER(a.status) AS status " +
                   "FROM appointments a " +
                   "LEFT JOIN users u ON a.patient_id = u.id " +
                   "LEFT JOIN staff s ON a.staff_id = s.id " +
                   "WHERE DATE(a.appointment_date) = ? " +
                   "ORDER BY a.appointment_date ASC";
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement(q)) {
            p.setString(1, yyyyMMdd);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("patient"),
                    rs.getString("reason"),
                    rs.getString("scheduled"),
                    rs.getString("staff"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows.toArray(new Object[0][]);
    }

    /**
     * Auto-approves or disapproves appointment based on scheduling conflicts.
     * 1-hour buffer: appointment at 9:30 blocks 9:30-10:30
     * So an appointment at 10:00 should be DISAPPROVED because 10:00 falls within 9:30-10:30
     */
    public static void autoApproveAppointment(int appointmentId) {
        try (Connection conn = getConnection()) {
            // Get appointment details
            String getApptQ = "SELECT a.id, a.patient_id, a.appointment_date, a.reason FROM appointments a WHERE a.id = ?";
            PreparedStatement p1 = conn.prepareStatement(getApptQ);
            p1.setInt(1, appointmentId);
            ResultSet rs = p1.executeQuery();
            
            if (!rs.next()) {
                System.out.println("DEBUG: Appointment " + appointmentId + " not found");
                return;
            }
            
            int patientId = rs.getInt("patient_id");
            String appointmentDateTime = rs.getString("appointment_date");
            String reason = rs.getString("reason");

            System.out.println("\n" + "=".repeat(100));
            System.out.println("DEBUG: ==== AUTO-APPROVE CONFLICT CHECK START ====");
            System.out.println("DEBUG: New Appointment ID: " + appointmentId);
            System.out.println("DEBUG: Patient ID: " + patientId);
            System.out.println("DEBUG: New Appointment Date/Time: " + appointmentDateTime);
            
            // Extract date for debugging
            String dateOnly = appointmentDateTime.substring(0, 10);
            System.out.println("DEBUG: Date only: " + dateOnly);
            
            // Print ALL appointments for this date BEFORE checking conflicts
            System.out.println("\nDEBUG: All appointments in database for date: " + dateOnly);
            String checkAllQ = "SELECT id, patient_id, appointment_date, status FROM appointments WHERE DATE(appointment_date) = ? ORDER BY appointment_date ASC";
            try (PreparedStatement pCheck = conn.prepareStatement(checkAllQ)) {
                pCheck.setString(1, dateOnly);
                try (ResultSet rsCheck = pCheck.executeQuery()) {
                    int appointmentCount = 0;
                    while (rsCheck.next()) {
                        appointmentCount++;
                        System.out.println("  [" + appointmentCount + "] ID: " + rsCheck.getInt("id") + 
                                         " | Patient: " + rsCheck.getInt("patient_id") + 
                                         " | Time: " + rsCheck.getString("appointment_date") + 
                                         " | Status: " + rsCheck.getString("status"));
                    }
                    if (appointmentCount == 0) {
                        System.out.println("  (No appointments found for this date)");
                    }
                }
            }

            // CORRECTED LOGIC:
            // Check if the NEW appointment time falls WITHIN an existing APPROVED appointment's 1-hour block
            // If existing appointment is at 9:30, it blocks 9:30 to 10:30
            // So new appointment at 10:00 should conflict because 10:00 is >= 9:30 AND 10:00 < 10:30
            
            String conflictQ = "SELECT a.id, a.patient_id, a.appointment_date, a.status " +
                              "FROM appointments a " +
                              "WHERE a.status = 'APPROVED' " +
                              "AND a.id != ? " +
                              "AND DATE(a.appointment_date) = DATE(?) " +
                              "AND ? >= a.appointment_date " +
                              "AND ? < DATE_ADD(a.appointment_date, INTERVAL 1 HOUR)";
            
            PreparedStatement p2 = conn.prepareStatement(conflictQ);
            p2.setInt(1, appointmentId);
            p2.setString(2, appointmentDateTime);
            p2.setString(3, appointmentDateTime);  // NEW appointment time >= EXISTING appointment time
            p2.setString(4, appointmentDateTime);  // NEW appointment time < EXISTING appointment time + 1 hour
            
            System.out.println("\nDEBUG: Checking if NEW appointment at " + appointmentDateTime);
            System.out.println("  falls within ANY APPROVED appointment's 1-hour block");
            System.out.println("  Query: WHERE status='APPROVED' AND date matches");
            System.out.println("         AND ? >= appointment_date (check if new time >= existing time)");
            System.out.println("         AND ? < appointment_date + 1 hour (check if new time < existing time + 1hr)");
            
            ResultSet rsConflict = p2.executeQuery();
            
            boolean hasConflict = false;
            int conflictCount = 0;
            while (rsConflict.next()) {
                conflictCount++;
                System.out.println("\n⚠️  CONFLICT #" + conflictCount + " DETECTED:");
                System.out.println("  Existing Appointment ID: " + rsConflict.getInt("id"));
                System.out.println("  Existing Time: " + rsConflict.getString("appointment_date"));
                System.out.println("  Existing blocks until: " + rsConflict.getString("appointment_date") + " + 1 hour");
                System.out.println("  New appointment at " + appointmentDateTime + " falls within this block!");
                hasConflict = true;
            }
            System.out.println("\nDEBUG: Total conflicts found: " + conflictCount);

            String newStatus = hasConflict ? "DISAPPROVED" : "APPROVED";
            
            // Update appointment status
            String updateQ = "UPDATE appointments SET status = ? WHERE id = ?";
            PreparedStatement p3 = conn.prepareStatement(updateQ);
            p3.setString(1, newStatus);
            p3.setInt(2, appointmentId);
            p3.executeUpdate();
            
            System.out.println("\n✓ Appointment " + appointmentId + " status set to: " + newStatus);
            System.out.println("DEBUG: ==== AUTO-APPROVE CONFLICT CHECK END ====");
            System.out.println("=".repeat(100) + "\n");

            // If disapproved, notify patient
            if (hasConflict) {
                Object[][] availableSlots = getAvailableSlots(appointmentDateTime);
                String suggestedTimes = "";
                if (availableSlots != null && availableSlots.length > 0) {
                    for (int i = 0; i < Math.min(3, availableSlots.length); i++) {
                        suggestedTimes += "\n  • " + availableSlots[i][0];
                    }
                }
                
                String message = "Your appointment scheduled for " + appointmentDateTime + " could not be approved.\n" +
                                "Reason: Time slot is occupied (1-hour appointment duration).\n" +
                                "Suggested available times:" + suggestedTimes;
                
                insertPatientNotification(patientId, "Appointment Status", message);
                System.out.println("DEBUG: Patient " + patientId + " notified of disapproval");
            } else {
                String message = "Your appointment for " + appointmentDateTime + " has been APPROVED.";
                insertPatientNotification(patientId, "Appointment Approved", message);
                System.out.println("DEBUG: Patient " + patientId + " notified of approval");
            }

        } catch (SQLException e) {
            System.err.println("ERROR autoApproveAppointment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets available appointment slots (next 7 days, hourly, no conflicts, 1-hour buffer)
     */
    public static Object[][] getAvailableSlots(String referenceDateTime) {
        List<Object[]> slots = new ArrayList<>();
        try (Connection conn = getConnection()) {
            // Get available slots: check if 1-hour window is free
            String q = "WITH RECURSIVE hours AS (" +
                       "SELECT DATE_ADD(DATE(?), INTERVAL 0 HOUR) as slot_time " +
                       "UNION ALL " +
                       "SELECT DATE_ADD(slot_time, INTERVAL 1 HOUR) FROM hours " +
                       "WHERE DATE(slot_time) <= DATE_ADD(DATE(?), INTERVAL 6 DAY) " +
                       "AND HOUR(slot_time) < 23" +
                       ") " +
                       "SELECT DATE_FORMAT(h.slot_time, '%Y-%m-%d %H:00') as slot " +
                       "FROM hours h " +
                       "WHERE h.slot_time > NOW() " +
                       "AND NOT EXISTS (" +
                       "  SELECT 1 FROM appointments a " +
                       "  WHERE a.status = 'APPROVED' " +
                       "  AND DATE(a.appointment_date) = DATE(h.slot_time) " +
                       "  AND TIME(a.appointment_date) >= TIME(h.slot_time) " +
                       "  AND TIME(a.appointment_date) < TIME(DATE_ADD(h.slot_time, INTERVAL 1 HOUR))" +
                       ") " +
                       "LIMIT 10";
            PreparedStatement p = conn.prepareStatement(q);
            p.setString(1, referenceDateTime);
            p.setString(2, referenceDateTime);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                slots.add(new Object[]{ rs.getString("slot") });
            }
            System.out.println("DEBUG: Found " + slots.size() + " available slots");
        } catch (SQLException e) {
            System.err.println("ERROR getAvailableSlots: " + e.getMessage());
            e.printStackTrace();
        }
        return slots.toArray(new Object[0][]);
    }

    /**
     * Inserts a notification for a patient
     */
    public static void insertPatientNotification(int patientId, String title, String message) {
        String q = "INSERT INTO notifications (patient_id, title, message, created_at) VALUES (?, ?, ?, NOW())";
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement(q)) {
            p.setInt(1, patientId);
            p.setString(2, title);
            p.setString(3, message);
            p.executeUpdate();
            System.out.println("DEBUG: Notification sent to patient " + patientId);
        } catch (SQLException e) {
            System.err.println("ERROR insertPatientNotification: " + e.getMessage());
        }
    }

    /**
     * Returns APPROVED appointments organized by date
     * Result: id, patient_name, reason, appointment_date (YYYY-MM-DD HH:mm), staff_name, status
     */
    public static Object[][] getApprovedAppointmentsByDate(String dateYYYYMMDD) {
        String q = "SELECT a.id, CONCAT(u.first_name,' ',u.last_name) AS patient, a.reason, " +
                   "DATE_FORMAT(a.appointment_date, '%Y-%m-%d %H:%i') AS scheduled, " +
                   "COALESCE(s.name,'Unassigned') AS staff, UPPER(a.status) AS status " +
                   "FROM appointments a " +
                   "LEFT JOIN users u ON a.patient_id = u.id " +
                   "LEFT JOIN staff s ON a.staff_id = s.id " +
                   "WHERE DATE(a.appointment_date) = ? AND a.status = 'APPROVED' " +
                   "ORDER BY a.appointment_date ASC";
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement(q)) {
            p.setString(1, dateYYYYMMDD);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("patient"),
                    rs.getString("reason"),
                    rs.getString("scheduled"),
                    rs.getString("staff"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            System.err.println("ERROR getApprovedAppointmentsByDate: " + e.getMessage());
            e.printStackTrace();
        }
        return rows.toArray(new Object[0][]);
    }

    /**
     * Returns all APPROVED appointments
     */
    public static Object[][] getAllApprovedAppointments() {
        String q = "SELECT a.id, CONCAT(u.first_name,' ',u.last_name) AS patient, a.reason, " +
                   "DATE_FORMAT(a.appointment_date, '%Y-%m-%d %H:%i') AS scheduled, " +
                   "COALESCE(s.name,'Unassigned') AS staff, UPPER(a.status) AS status " +
                   "FROM appointments a " +
                   "LEFT JOIN users u ON a.patient_id = u.id " +
                   "LEFT JOIN staff s ON a.staff_id = s.id " +
                   "WHERE a.status = 'APPROVED' " +
                   "ORDER BY a.appointment_date ASC";
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(q)) {
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("patient"),
                    rs.getString("reason"),
                    rs.getString("scheduled"),
                    rs.getString("staff"),
                    rs.getString("status")
                });
            }
            System.out.println("DEBUG: getAllApprovedAppointments returned " + rows.size() + " rows");
        } catch (SQLException e) {
            System.err.println("ERROR getAllApprovedAppointments: " + e.getMessage());
            e.printStackTrace();
        }
        return rows.toArray(new Object[0][]);
    }

    /**
     * Gets the latest appointment ID for a patient
     */
    public static int getLatestAppointmentIdForPatient(int patientId) {
        String q = "SELECT id FROM appointments WHERE patient_id = ? ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement(q)) {
            p.setInt(1, patientId);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Deletes an appointment from the database
     */
    public static boolean deleteAppointment(int appointmentId) {
        String q = "DELETE FROM appointments WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement(q)) {
            p.setInt(1, appointmentId);
            int rowsAffected = p.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("DEBUG: Appointment " + appointmentId + " deleted successfully");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("ERROR deleteAppointment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Debug: Print all APPROVED appointments for a specific date
     */
    public static void debugPrintAppointmentsForDate(String dateYYYYMMDD) {
        String q = "SELECT id, appointment_date, status FROM appointments WHERE DATE(appointment_date) = ? ORDER BY appointment_date ASC";
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement(q)) {
            p.setString(1, dateYYYYMMDD);
            ResultSet rs = p.executeQuery();
            System.out.println("\n==== APPOINTMENTS FOR DATE: " + dateYYYYMMDD + " ====");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + " | Time: " + rs.getString("appointment_date") + " | Status: " + rs.getString("status"));
            }
            System.out.println("====================================\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Debug: Print ALL appointments for a specific date (regardless of status)
     */
    public static void debugPrintAllAppointmentsForDate(String dateYYYYMMDD) {
        String q = "SELECT id, patient_id, appointment_date, status FROM appointments WHERE DATE(appointment_date) = ? ORDER BY appointment_date ASC";
        try (Connection conn = getConnection();
             PreparedStatement p = conn.prepareStatement(q)) {
            p.setString(1, dateYYYYMMDD);
            ResultSet rs = p.executeQuery();
            System.out.println("\n" + "=".repeat(80));
            System.out.println("DEBUG: ALL APPOINTMENTS FOR DATE: " + dateYYYYMMDD);
            System.out.println("=".repeat(80));
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("  ID: " + rs.getInt("id") + 
                                 " | Patient: " + rs.getInt("patient_id") + 
                                 " | Time: " + rs.getString("appointment_date") + 
                                 " | Status: " + rs.getString("status"));
            }
            if (count == 0) {
                System.out.println("  (No appointments found for this date)");
            }
            System.out.println("=".repeat(80) + "\n");
        } catch (SQLException e) {
            System.err.println("ERROR debugPrintAllAppointmentsForDate: " + e.getMessage());
            e.printStackTrace();
        }
    }
}