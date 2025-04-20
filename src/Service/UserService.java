package Service;

import DBConnection.DBConnection;
import java.sql.*;
import Exception.*;
import Utilities.HashPassword;
import Utilities.InputValidator;

public class UserService {
    public boolean registerUser(String name, String contact, String email, String password) throws InvalidEmailException, 
    InvalidPasswordException, WeakPasswordException, Exception {

        InputValidator.validateEmail(email);
        InputValidator.validatePassword(password);
        InputValidator.validatePasswordStrength(password);

        String hashedPassword = HashPassword.hashPassword(password);

        try (Connection conn = DBConnection.getConnection()) {
            if (userExists(conn, email)) {
                throw new Exception("User already exists");
            }

            String query = "INSERT INTO admin (name, password, email, contactNumber) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insert = conn.prepareStatement(query)) {
                insert.setString(1, name);
                insert.setString(2, hashedPassword); // Note: Should be hashed in production
                insert.setString(3, email);
                insert.setString(4, contact);
                return insert.executeUpdate() > 0;
            }
        }
    }

    private boolean userExists(Connection conn, String email) throws SQLException {
        String checkQuery = "SELECT 1 FROM admin WHERE email = ?";
        try (PreparedStatement check = conn.prepareStatement(checkQuery)) {
            check.setString(1, email);
            try (ResultSet rs = check.executeQuery()) {
                return rs.next();
            }
        }
    }
}