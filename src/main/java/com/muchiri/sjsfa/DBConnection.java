package com.muchiri.sjsfa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author muchiri
 */
public class DBConnection {

    private static DBConnection instance;

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }

        return instance;
    }

    public Connection get() {
        try {
            String url = "jdbc:postgresql://localhost/jsfapp";
            Properties props = new Properties();
            props.setProperty("user", "your username");
            props.setProperty("password", "your password");

            return DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            //Doing nothing
            e.printStackTrace();
            return null;
        }
    }

}
