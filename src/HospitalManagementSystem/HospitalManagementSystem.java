package HospitalManagementSystem;

import java.sql.*;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HospitalManagementSystem {

    private static final Logger lg = Logger.getLogger(HospitalManagementSystem.class.getName());
    private static final Properties PROPERTIES = loadProperties();
    private static final String url = PROPERTIES.getProperty("db.url");
    private static final String username = PROPERTIES.getProperty("db.username");
    private static final String password = PROPERTIES.getProperty("db.password");


    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = ClassLoader.getSystemResourceAsStream("config.properties")) {
            if (input == null) {
                assert lg != null;
                lg.log(Level.SEVERE, "Sorry, unable to find config.properties");
            }
            properties.load(input);
        }
        catch (Exception e) {
            assert lg != null;
            lg.log(Level.SEVERE, "Error loading configuration file: {0}", e.getMessage());
        }
        return properties;
    }

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            lg.log(Level.SEVERE, "JDBC Driver not found: {0}", e.getMessage());
            lg.log(Level.SEVERE, "Stack Trace:", e);
        }
        Scanner sc = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, sc);
            Doctor doctor = new Doctor(connection);
            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your choice: ");

                    int choice = sc.nextInt();
                    switch (choice) {
                        case 1:
                            // Add Patient
                            patient.addPatient();
                            System.out.println();
                            break;
                        case 2:
                            // View Patient
                            patient.viewPatients();
                            System.out.println();
                            break;
                        case 3:
                            // View Doctors
                            doctor.viewDoctors();
                            System.out.println();
                            break;
                        case 4:
                            // Book Appointment
                            bookAppointment(patient, doctor, connection, sc);
                            System.out.println();
                            break;
                        case 5:
                            System.out.println("THANK YOU! STAY HEALTHY!!");
                            return;
                        default:
                            System.out.println("Enter a valid choice!!!");
                    }
            }

        }catch (SQLException e){
            lg.log(Level.SEVERE, "Database operation failed: {0}", e.getMessage());
            lg.log(Level.SEVERE, "SQL State: {0}, Error Code: {1}", new Object[]{e.getSQLState(), e.getErrorCode()});
            lg.log(Level.SEVERE, "Stack Trace:", e);
        }
    }


    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner sc){
        System.out.print("Enter Patient Id: ");
        int PATIENT_ID = sc.nextInt();
        System.out.print("Enter Doctor Id: ");
        int DOCTOR_ID = sc.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String A_DATE = sc.next(); //Appointment Date
        if(patient.getPatientById(PATIENT_ID) && doctor.getDoctorById(DOCTOR_ID)){
            if(checkDoctorAvailability(DOCTOR_ID, A_DATE, connection)){
                String query = "INSERT INTO APPOINTMENTS(PATIENT_ID, DOCTOR_ID, A_DATE) VALUES(?, ?, ?)";
                try {
                    PreparedStatement pS = connection.prepareStatement(query);
                    pS.setInt(1, PATIENT_ID);
                    pS.setInt(2, DOCTOR_ID);
                    pS.setString(3, A_DATE);
                    int rA = pS.executeUpdate();//rows affected
                    if(rA>0){
                        System.out.println("Appointment Booked!");
                    }else{
                        System.out.println("Failed to Book Appointment!");
                    }
                }catch (SQLException e){
                    lg.log(Level.SEVERE, "Database operation failed: {0}", e.getMessage());
                    lg.log(Level.SEVERE, "SQL State: {0}, Error Code: {1}", new Object[]{e.getSQLState(), e.getErrorCode()});
                    lg.log(Level.SEVERE, "Stack Trace:", e);
                }
            }else{
                System.out.println("Doctor not available on this date!!");
            }
        }else{
            System.out.println("Either doctor or patient doesn't exist!!!");
        }
    }

    public static boolean checkDoctorAvailability(int DOCTOR_ID, String A_DATE, Connection connection){
        String query = "SELECT COUNT(*) FROM APPOINTMENTS WHERE DOCTOR_ID = ? AND A_DATE = ?";
        try{
            PreparedStatement pS = connection.prepareStatement(query);
            pS.setInt(1, DOCTOR_ID);
            pS.setString(2, A_DATE);
            ResultSet rS = pS.executeQuery();
            if(rS.next()){
                int count = rS.getInt(1);
                return count == 0;
            }
        } catch (SQLException e){
            lg.log(Level.SEVERE, "Database operation failed: {0}", e.getMessage());
            lg.log(Level.SEVERE, "SQL State: {0}, Error Code: {1}", new Object[]{e.getSQLState(), e.getErrorCode()});
            lg.log(Level.SEVERE, "Stack Trace:", e);
        }
        return false;
    }
}