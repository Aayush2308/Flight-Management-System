package DBConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String url = "jdbc:mysql://localhost:3306/java";
    private static final String username = "root";
    private static final String password = "okabe#24";  //Carnage@2065 -- need to change this before pushing to git

    private static Connection connection = null;

    private DBConnection() {

    }

    public static Connection getConnection() throws SQLException{
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Database connected successfully.");
            } catch (SQLException e) {
                System.err.println("Failed to connect to the database.");
                throw e;
            }
        }
        return connection;
    }

    public static void closeConnection(){
        try{
            if(connection!=null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database Connection Closed");
            }
        } catch (SQLException e) {
            System.err.println("Failed to close database connection: "+e.getMessage());
        }
    }
}
