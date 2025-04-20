package Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}

