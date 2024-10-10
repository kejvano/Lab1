package database;

import java.io.IOException;
import java.sql.*;
import java.util.Arrays;

public class Database {
    final public static String DefaultDatabase = "webshop";

    private Connection dbConnection;
    final private String user = "root";
    final private String password = "admin";

    /**
     * Setup db connection
     *
     * @param database
     * @return true if nothing went wrong
     * @throws SQLException
     */
    public boolean connect(String database) throws SQLException {
        var server = "jdbc:mysql://localhost:3306/" + database + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            dbConnection = DriverManager.getConnection(server, user, password);
        } catch (ClassNotFoundException cnfe) {
            System.out.println("BIG FAIL" + Arrays.toString(cnfe.getStackTrace()));
            return false;
        }
        return true;
    }

    public void disconnect() throws IOException, SQLException {
        dbConnection.close();
    }

    public Connection getConnection() {
        return dbConnection;
    }
}
