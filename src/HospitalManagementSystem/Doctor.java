package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Doctor {
    private final Connection connection;
    private static final Logger lg = Logger.getLogger(Doctor.class.getName());


    public Doctor(Connection connection){
        this.connection = connection;
    }

    public void viewDoctors(){
        String query = "SELECT * FROM DOCTORS";
        try{
            PreparedStatement pS = connection.prepareStatement(query);
            ResultSet rS = pS.executeQuery();
            System.out.println("DOCTORS");
            System.out.println("+------------+--------------------+------------------+");
            System.out.println("| Doctor Id  | Name               | Department       |");
            System.out.println("+------------+--------------------+------------------+");
            while(rS.next()){
                int D_ID = rS.getInt("D_ID");
                String D_NAME = rS.getString("D_NAME");
                String DEPARTMENT = rS.getString("DEPARTMENT");
                System.out.printf("| %-10s | %-18s | %-16s |\n", D_ID, D_NAME, DEPARTMENT);
                System.out.println("+------------+--------------------+------------------+");
            }

        }catch (SQLException e){
            lg.log(Level.SEVERE, "Database operation failed: {0}", e.getMessage());
            lg.log(Level.SEVERE, "SQL State: {0}, Error Code: {1}", new Object[]{e.getSQLState(), e.getErrorCode()});
            lg.log(Level.SEVERE, "Stack Trace:", e);
        }
    }

    public boolean getDoctorById(int D_ID){
        String query = "SELECT * FROM DOCTORS WHERE D_ID = ?";
        try{
            PreparedStatement pS = connection.prepareStatement(query);
            pS.setInt(1, D_ID);
            ResultSet rS = pS.executeQuery();
            return rS.next();
        }catch (SQLException e){
            lg.log(Level.SEVERE, "Database operation failed: {0}", e.getMessage());
            lg.log(Level.SEVERE, "SQL State: {0}, Error Code: {1}", new Object[]{e.getSQLState(), e.getErrorCode()});
            lg.log(Level.SEVERE, "Stack Trace:", e);
        }
        return false;
    }
}