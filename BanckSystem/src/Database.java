import java.sql.*;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/banking_system";
    private static final String USER = "root";  // Replace with your MySQL username
    private static final String PASSWORD = "Anu@123";  // Replace with your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}