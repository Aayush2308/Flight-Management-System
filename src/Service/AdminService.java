package Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import DBConnection.DBConnection;
import Models.Admin;

// Service/AdminService.java
public class AdminService {
    public Admin getAdminById(int adminId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT * FROM admin WHERE adminId = ?")) {
            pst.setInt(1, adminId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Admin(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("contactNumber"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // This method checks if the provided old password (hashed) matches the stored password for the admin
    public boolean isPasswordCorrect(int adminId, String hashedOldPassword) {
        Admin admin = getAdminById(adminId);
        if (admin != null) {
            return admin.getPassword().equals(hashedOldPassword);
        }
        return false; // Return false if admin is not found
    }

    public void updatePassword(int adminId, String hashPassword) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("UPDATE ACCOUNT SET password = ? where adminId = ?")) {
                pst.setString(1, hashPassword);
                pst.setInt(2, adminId);
            int res = pst.executeUpdate();
            if(res>0){
                JOptionPane.showMessageDialog(null, "Password Updated Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

