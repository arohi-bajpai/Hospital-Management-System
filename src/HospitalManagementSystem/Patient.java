package HospitalManagementSystem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Patient {
    private final Connection connection;
    private final Scanner sc;
    private static final Logger lg = Logger.getLogger(Patient.class.getName());


    public Patient(Connection connection, Scanner sc){
        this.connection=connection;
        this.sc=sc;
    }

    public void addPatient() {
        System.out.println("Patient's Data- ");
        System.out.println("Name: ");
        String P_NAME= sc.next();
        System.out.println("Age: ");
        int P_AGE= sc.nextInt();
        System.out.println("Gender: ");
        String P_GENDER= sc.next();

        try{
            String query="INSERT INTO PATIENTS(P_NAME,P_AGE,P_GENDER) VALUES(?,?,?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1,P_NAME);
            ps.setInt(2,P_AGE);
            ps.setString(3,P_GENDER);
            int aR= ps.executeUpdate(); // Find rows being affected.
            if(aR>0){
                System.out.println("Patient Added");
            }else{
                System.out.println("Failed to add Patient");
            }
        }catch(SQLException e){
            lg.log(Level.SEVERE, "Database operation failed: {0}", e.getMessage());
            lg.log(Level.SEVERE, "SQL State: {0}, Error Code: {1}", new Object[]{e.getSQLState(), e.getErrorCode()});
            lg.log(Level.SEVERE, "Stack Trace:", e);
        }
    }

    public void viewPatients() throws SQLException {
        String query = "SELECT * FROM PATIENTS";
        try{
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rS= ps.executeQuery(); // Holds the data coming from the database. Sets a pointer using Next Method. And prints / assigns to local variables.
            System.out.println("+------------+--------------------+----------+------------+");
            System.out.println("| Patient Id | Name               | Age      | Gender     |");
            System.out.println("+------------+--------------------+----------+------------+");
            while(rS.next()){
                int P_ID = rS.getInt("P_ID");
                String P_NAME = rS.getString("P_NAME");
                int P_AGE = rS.getInt("P_AGE");
                String P_GENDER = rS.getString("P_GENDER");
                System.out.printf("| %-10s | %-18s | %-8s | %-10s |\n", P_ID, P_NAME, P_AGE, P_GENDER); // printf- helps format output
                System.out.println("+------------+--------------------+----------+------------+");
            }
        } catch(SQLException e){
                lg.log(Level.SEVERE, "SQL State: {0}, Error Code: {1}", new Object[]{e.getSQLState(), e.getErrorCode()});
                lg.log(Level.SEVERE, "SQL State: {0}, Error Code: {1}", new Object[]{e.getSQLState(), e.getErrorCode()});
                lg.log(Level.SEVERE, "Stack Trace:", e);
        }
    }

    public boolean getPatientById(int P_ID){
        String query = "SELECT * FROM PATIENTS WHERE P_ID = ?";
        try{
            PreparedStatement pS = connection.prepareStatement(query);
            pS.setInt(1, P_ID);
            ResultSet rS = pS.executeQuery();
            return rS.next();
        }catch (SQLException e){
                lg.log(Level.SEVERE, "Stack Trace:", e);
            lg.log(Level.SEVERE, "SQL State: {0}, Error Code: {1}", new Object[]{e.getSQLState(), e.getErrorCode()});
            lg.log(Level.SEVERE, "Stack Trace:", e);
        }
        return false;
    }
}
