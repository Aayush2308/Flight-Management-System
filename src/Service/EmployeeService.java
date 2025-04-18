// package Service;

// import Models.Employee;
// import DBConnection.DBConnection;
// import java.sql.*;
// import java.util.ArrayList;
// import java.util.List;

// public class EmployeeService {
    
//     // Retrieve employees with sorting and optional filtering
//     public List<Employee> getEmployees(String sortColumn, boolean asc, Integer searchId) throws SQLException {
//         List<Employee> employees = new ArrayList<>();
//         String baseQuery = "SELECT employeeId, employeeName, role, recordId FROM Employee";
//         String whereClause = searchId != null ? " WHERE employeeId = ?" : "";
//         String orderClause = " ORDER BY " + sortColumn + (asc ? " ASC" : " DESC");
        
//         try (Connection conn = DBConnection.getConnection();
//              PreparedStatement stmt = conn.prepareStatement(baseQuery + whereClause + orderClause)) {
            
//             if (searchId != null) {
//                 stmt.setInt(1, searchId);
//             }

//             ResultSet rs = stmt.executeQuery();
//             while (rs.next()) {
//                 employees.add(new Employee(
//                     rs.getInt("employeeId"),
//                     rs.getString("employeeName"),
//                     rs.getString("role"),
//                     rs.getInt("recordId")
//                 ));
//             }
//         }
//         return employees;
//     }
    
//     // Add a new employee with transaction handling
//     public boolean addEmployee(String name, String role, int facilityId, Integer flightId) throws SQLException {
//         Connection conn = null;
//         try {
//             conn = DBConnection.getConnection();
//             conn.setAutoCommit(false);
            
//             // 1. Create record entry first
//             int recordId;
//             try (PreparedStatement psRec = conn.prepareStatement(
//                     "INSERT INTO Records () VALUES()", Statement.RETURN_GENERATED_KEYS)) {
//                 psRec.executeUpdate();
//                 try (ResultSet gen = psRec.getGeneratedKeys()) {
//                     gen.next(); 
//                     recordId = gen.getInt(1);
//                 }
//             }
            
//             // 2. Create employee entry
//             int empId;
//             try (PreparedStatement psEmp = conn.prepareStatement(
//                     "INSERT INTO Employee (employeeName, role, recordId) VALUES (?,?,?)", 
//                     Statement.RETURN_GENERATED_KEYS)) {
//                 psEmp.setString(1, name);
//                 psEmp.setString(2, role);
//                 psEmp.setInt(3, recordId);
//                 psEmp.executeUpdate();
//                 try (ResultSet gen = psEmp.getGeneratedKeys()) {
//                     gen.next(); 
//                     empId = gen.getInt(1);
//                 }
//             }
            
//             // 3. Create role-specific entry
//             String sql;
//             if (flightId != null) {
//                 sql = "INSERT INTO " + role + 
//                       " (employeeId, employeeName, facilityId, recordId, flightId) VALUES (?,?,?,?,?)";
//             } else {
//                 sql = "INSERT INTO " + role + 
//                       " (employeeId, employeeName, facilityId, recordId) VALUES (?,?,?,?)";
//             }
            
//             try (PreparedStatement psRole = conn.prepareStatement(sql)) {
//                 psRole.setInt(1, empId); 
//                 psRole.setString(2, name);
//                 psRole.setInt(3, facilityId); 
//                 psRole.setInt(4, recordId);
//                 if (flightId != null) psRole.setInt(5, flightId);
//                 psRole.executeUpdate();
//             }
            
//             conn.commit();
//             return true;
//         } catch (SQLException e) {
//             if (conn != null) conn.rollback();
//             throw e;
//         } finally {
//             if (conn != null) conn.setAutoCommit(true);
//         }
//     }
    
//     // Update existing employee
//     public boolean updateEmployee(int id, String newName, String newRole, int newFacilityId, Integer newFlightId) throws SQLException {
//         Connection conn = null;
//         try {
//             conn = DBConnection.getConnection();
//             conn.setAutoCommit(false);
            
//             // 1. Get current employee data
//             String currentRole;
//             int recordId;
//             try (PreparedStatement ps = conn.prepareStatement(
//                     "SELECT role, recordId FROM Employee WHERE employeeId = ?")) {
//                 ps.setInt(1, id);
//                 ResultSet rs = ps.executeQuery();
//                 if (!rs.next()) {
//                     throw new SQLException("Employee not found");
//                 }
//                 currentRole = rs.getString("role");
//                 recordId = rs.getInt("recordId");
//             }
            
//             // 2. Update employee table
//             try (PreparedStatement psEmp = conn.prepareStatement(
//                     "UPDATE Employee SET employeeName = ?, role = ? WHERE employeeId = ?")) {
//                 psEmp.setString(1, newName);
//                 psEmp.setString(2, newRole);
//                 psEmp.setInt(3, id);
//                 psEmp.executeUpdate();
//             }
            
//             // 3. Handle role change if needed
//             if (!newRole.equals(currentRole)) {
//                 // Delete from old role table
//                 try (PreparedStatement psDel = conn.prepareStatement(
//                         "DELETE FROM " + currentRole + " WHERE employeeId = ?")) {
//                     psDel.setInt(1, id);
//                     psDel.executeUpdate();
//                 }
                
//                 // Insert into new role table
//                 String sqlIns = newFlightId != null
//                     ? "INSERT INTO " + newRole + " (employeeId, employeeName, facilityId, recordId, flightId) VALUES (?,?,?,?,?)"
//                     : "INSERT INTO " + newRole + " (employeeId, employeeName, facilityId, recordId) VALUES (?,?,?,?)";
                
//                 try (PreparedStatement psIns = conn.prepareStatement(sqlIns)) {
//                     psIns.setInt(1, id);
//                     psIns.setString(2, newName);
//                     psIns.setInt(3, newFacilityId);
//                     psIns.setInt(4, recordId);
//                     if (newFlightId != null) psIns.setInt(5, newFlightId);
//                     psIns.executeUpdate();
//                 }
//             } else {
//                 // Update within same role table
//                 String sqlUpd = newFlightId != null
//                     ? "UPDATE " + currentRole + " SET employeeName = ?, facilityId = ?, flightId = ? WHERE employeeId = ?"
//                     : "UPDATE " + currentRole + " SET employeeName = ?, facilityId = ? WHERE employeeId = ?";
                
//                 try (PreparedStatement psUpd = conn.prepareStatement(sqlUpd)) {
//                     psUpd.setString(1, newName);
//                     psUpd.setInt(2, newFacilityId);
//                     if (newFlightId != null) {
//                         psUpd.setInt(3, newFlightId);
//                         psUpd.setInt(4, id);
//                     } else {
//                         psUpd.setInt(3, id);
//                     }
//                     psUpd.executeUpdate();
//                 }
//             }
            
//             conn.commit();
//             return true;
//         } catch (SQLException e) {
//             if (conn != null) conn.rollback();
//             throw e;
//         } finally {
//             if (conn != null) conn.setAutoCommit(true);
//         }
//     }
    
//     // Delete an employee
//     public boolean deleteEmployee(int id) throws SQLException {
//         Connection conn = null;
//         try {
//             conn = DBConnection.getConnection();
//             conn.setAutoCommit(false);
            
//             // 1. Get employee role and record ID
//             String role;
//             int recordId;
//             try (PreparedStatement ps = conn.prepareStatement(
//                     "SELECT role, recordId FROM Employee WHERE employeeId = ?")) {
//                 ps.setInt(1, id);
//                 ResultSet rs = ps.executeQuery();
//                 if (!rs.next()) {
//                     throw new SQLException("Employee not found");
//                 }
//                 role = rs.getString("role");
//                 recordId = rs.getInt("recordId");
//             }
            
//             // 2. Delete from role-specific table
//             try (PreparedStatement psRole = conn.prepareStatement(
//                     "DELETE FROM " + role + " WHERE employeeId = ?")) {
//                 psRole.setInt(1, id);
//                 psRole.executeUpdate();
//             }
            
//             // 3. Delete from Employee table
//             try (PreparedStatement psEmp = conn.prepareStatement(
//                     "DELETE FROM Employee WHERE employeeId = ?")) {
//                 psEmp.setInt(1, id);
//                 psEmp.executeUpdate();
//             }
            
//             // 4. Delete from Records table
//             try (PreparedStatement psRec = conn.prepareStatement(
//                     "DELETE FROM Records WHERE recordId = ?")) {
//                 psRec.setInt(1, recordId);
//                 psRec.executeUpdate();
//             }
            
//             conn.commit();
//             return true;
//         } catch (SQLException e) {
//             if (conn != null) conn.rollback();
//             throw e;
//         } finally {
//             if (conn != null) conn.setAutoCommit(true);
//         }
//     }
    
//     // Validate flight exists
//     public boolean validateFlightExists(int flightId) throws SQLException {
//         try (Connection conn = DBConnection.getConnection();
//              PreparedStatement ps = conn.prepareStatement(
//                     "SELECT COUNT(*) FROM Flight WHERE flightId = ?")) {
//             ps.setInt(1, flightId);
//             ResultSet rs = ps.executeQuery();
//             rs.next();
//             return rs.getInt(1) > 0;
//         }
//     }
    
//     // Validate facility exists
//     public boolean validateFacilityExists(int facilityId) throws SQLException {
//         try (Connection conn = DBConnection.getConnection();
//              PreparedStatement ps = conn.prepareStatement(
//                     "SELECT COUNT(*) FROM Facilities WHERE facilityId = ?")) {
//             ps.setInt(1, facilityId);
//             ResultSet rs = ps.executeQuery();
//             rs.next();
//             return rs.getInt(1) > 0;
//         }
//     }
// }