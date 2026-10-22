package com.mycompany.taskplannerjava;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Database {
    // relative path connection string, foreign_keys enforced
    static String url = "jdbc:sqlite:PlannerInfo.db?foreign_keys=on";
    private Connection conn;
    
    public void connect() {
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connected to PlannerInfo.db.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }   
    }
    
    public void disconnect() {
        try {   
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Disconnected from PlannerInfo.db joyfully!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public Connection getConn() {
        return this.conn;
    }
}
