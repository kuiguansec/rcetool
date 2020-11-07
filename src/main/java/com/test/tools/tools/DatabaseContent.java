package com.test.tools.tools;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;


public class DatabaseContent {
    public static Connection getConnect() {
        String db = System.getProperty("user.dir")+"\\tools\\cms_finger.db";
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + db);
        } catch (ClassNotFoundException | java.sql.SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
