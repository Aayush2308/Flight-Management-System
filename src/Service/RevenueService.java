package Service;

import Models.Revenue;
import DBConnection.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RevenueService {
    public RevenueService() throws SQLException {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Failed to establish database connection");
        }
        DBConnection.closeConnection();
    }
    
    public List<Revenue> getRevenueData(String filterType, Object value1, Object value2) throws SQLException {
        List<Revenue> revenues = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT transactionId, amount, paymentType, ticketNumber FROM Revenue";
            
            if ("PaymentType".equals(filterType)) {
                sql += " WHERE paymentType = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, value1.toString());
            } else if ("Amount".equals(filterType)) {
                sql += " WHERE amount BETWEEN ? AND ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setDouble(1, (Double) value1);
                pstmt.setDouble(2, (Double) value2);
            } else {
                pstmt = conn.prepareStatement(sql);
            }
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                revenues.add(new Revenue(
                    rs.getInt("transactionId"),
                    rs.getDouble("amount"),
                    rs.getString("paymentType"),
                    rs.getString("ticketNumber")
                ));
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DBConnection.closeConnection();
        }
        
        return revenues;
    }
    
    public double calculateTotalRevenue(List<Revenue> revenues) {
        return revenues.stream().mapToDouble(Revenue::getAmount).sum();
    }
}