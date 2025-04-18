// In Service/AuthService.java
package Service;

import DBConnection.DBConnection;
import Models.Account;
import java.sql.*;

public class AuthService {
    public Account authenticate(String email, String password) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM account WHERE email = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(rs.getInt("adminid"), rs.getString("email"));
            }
            return null;
        }
    }
}