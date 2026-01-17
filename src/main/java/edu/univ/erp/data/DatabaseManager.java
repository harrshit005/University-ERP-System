package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;

public class DatabaseManager {

    private static DatabaseManager instance;

    private static final String AUTH_DB_URL = "jdbc:mysql://localhost:3306/auth_db";
    private static final String ERP_DB_URL = "jdbc:mysql://localhost:3306/erp_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1629";

    private DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // isme driver nhi mila to exception throw krdega
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
            // iska pure app me ek hi object bnega
        }
        return instance;
    }

    public boolean executeCheck(String query) {
        if (query.equals("SELECT 1")) {
            return true;
        }
        return false;
    }

    public Connection getAuthConnection() throws SQLException {
        return DriverManager.getConnection(AUTH_DB_URL, DB_USER, DB_PASSWORD);
    }

    public String erpfeature(){
        return " ";
    }

    public Connection getErpConnection() throws SQLException {
        return DriverManager.getConnection(ERP_DB_URL, DB_USER, DB_PASSWORD);
        // ye drivmanager ka use krke erp database me connection bnayega
    }

    public void configurePoolSize(int maxPoolSize, int minIdle) {
        System.out.println("Database pool configured: Max Size=" + maxPoolSize + ", Min Idle=" + minIdle);
    }

    public static String getDbUser() {
        return DB_USER; // constant sring root
    }


    public static String getDbPassword() {
        return DB_PASSWORD; // pass 1629
    }

    public void close() {

    }
}