# Barangay Health Center — Java Swing Application

Simple Java Swing app for patient/staff/admin appointment management, patient records, billing, and notifications. Uses MySQL for persistence and a small DatabaseHelper utility for DB access.

---

## Features
- Patient, Staff and Admin dashboards (Swing)
- User authentication and role-based views
- Book appointments (calendar + time picker)
- Auto-approve / disapprove appointments with 1‑hour conflict buffer
- Appointment history and per-date calendar view for staff
- Basic billing and notification insertion
- CRUD for appointments (create, delete, approve/disapprove)

---

## Prerequisites
- Java 11+ (JDK)
- MySQL server
- MySQL Connector/J (JAR)
- IDE (recommended: VS Code or IntelliJ) or command-line tools

---

## Project structure
- src/
  - LoginScreen.java
  - PatientDashboard.java
  - StaffDashboard.java
  - AdminDashboard.java
  - DatabaseHelper.java
  - ... other UI/forms

---

## Setup

1. Clone or unzip project into:
   `C:\Users\DELL\Downloads\health center`

2. Install MySQL and create a database (example name used by project):
   - Database name: `barangay_health_center`

3. Add MySQL Connector/J to project:
   - Place the connector JAR in a `lib/` folder and add to classpath.
   - Example download: https://dev.mysql.com/downloads/connector/j/

4. Configure DB credentials:
   - Edit `DatabaseHelper.java` constants:
     - DB_URL, DB_USER, DB_PASSWORD
   - Do NOT commit plain passwords to shared repos.

5. Initialize tables
   - Either run the app (DatabaseHelper.initializeDatabase() is called somewhere or call it manually),
   - Or run the SQL DDL below in your MySQL client.

Example minimal schema (same structure created by DatabaseHelper.initializeDatabase()):

```sql
CREATE DATABASE IF NOT EXISTS barangay_health_center;
USE barangay_health_center;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  role VARCHAR(20) NOT NULL DEFAULT 'PATIENT',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS staff (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  designation VARCHAR(255),
  contact_details VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS appointments (
  id INT AUTO_INCREMENT PRIMARY KEY,
  patient_id INT NOT NULL,
  staff_id INT DEFAULT NULL,
  appointment_date DATETIME NOT NULL,
  reason VARCHAR(255),
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (patient_id) REFERENCES users(id),
  FOREIGN KEY (staff_id) REFERENCES staff(id)
);
```

---

## Build & Run (Windows CLI)

1. Compile:
   - Place mysql-connector JAR path accordingly (example `lib\mysql-connector-java-8.0.xx.jar`)
   - From project root:
     ```
     javac -cp ".;lib\mysql-connector-java-8.0.xx.jar" -d out src\*.java
     ```
2. Run (start with LoginScreen):
     ```
     java -cp "out;lib\mysql-connector-java-8.0.xx.jar" LoginScreen
     ```

Or open the project in VS Code / IntelliJ and add the connector JAR to project libraries, then run `LoginScreen` main class.

---

## Appointment 1‑hour conflict logic (how it works / how to test)

Goal: an approved appointment at T blocks the interval [T, T+1 hour). Any new appointment whose start time falls inside that interval should be disapproved.

Implementation (DatabaseHelper.autoApproveAppointment):
- When a new appointment is created, the app runs autoApproveAppointment(appointmentId).
- The SQL checks whether the NEW appointment time (call it newT) satisfies:
  - newT >= existing_appointment_time
  - AND newT < existing_appointment_time + INTERVAL 1 HOUR
  - AND existing appointment has status = 'APPROVED'
- If any such existing appointment exists → conflict → new appointment set to DISAPPROVED.

Testing steps:
1. Book appointment A at 2025-12-04 09:30 → should become APPROVED.
2. Book appointment B at 2025-12-04 10:00 → should be DISAPPROVED because 10:00 ∈ [09:30, 10:30).

Debugging:
- DatabaseHelper.autoApproveAppointment prints debug logs. Watch console for:
  - All appointments for the date
  - The conflict query and detected conflicts
- If 10:00 still becomes APPROVED:
  - Verify the 9:30 appointment is actually stored as `status = 'APPROVED'`
  - Use the debug method `debugPrintAllAppointmentsForDate('2025-12-04')` (called from autoApproveAppointment already)
  - Ensure datetime formats are consistent (`YYYY-MM-DD HH:MM:SS`) and stored in same timezone.

Common causes if conflicts not detected:
- The first appointment is not yet APPROVED (status != 'APPROVED')
- Different date / timezone mismatch / wrong stored minutes
- SQL comparisons use DATETIME strings; ensure stored values exactly match expectations

---

## Troubleshooting tips
- Ensure MySQL timezone consistent with JVM timezone or store/compare in UTC
- Add additional System.out debug lines in DatabaseHelper.autoApproveAppointment to print exact DB rows
- Confirm that `status` values are uppercase `'APPROVED'` when expecting approved entries
- If using prepared statements with STR_TO_DATE, verify inserted format matches SELECT comparisons

---

## Extending & Contributing
- Improve time-slot granularity (e.g., duration per appointment)
- Replace inline DB credentials with a config file or environment variables
- Use a connection pool (HikariCP) for production
- Replace plaintext password storage with BCrypt
- Create unit tests for DatabaseHelper (use an in-memory database for tests)

---

## License
MIT — see LICENSE file or add one.

---