package com.mycompany.taskplannerjava;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class Database {
    private Connection conn;
    
    public void connect() {
        String workingDir = System.getProperty("user.dir");
        String dbPath = workingDir + java.io.File.separator + "PlannerInfo.db";
        String url = "jdbc:sqlite:" + dbPath;
        
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connected to PlannerInfo.db.");
            initializeDatabase(conn);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }   
    }
    
    public void disconnect() {
        try {   
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Disconnected from PlannerInfo.db.");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    private static void initializeDatabase(Connection conn) {
        // Use "CREATE TABLE IF NOT EXISTS" with your exact schema
        String tableCreationString = 
                "CREATE TABLE IF NOT EXISTS task ("
                + "task_id integer PRIMARY KEY, "
                + "name varchar(255), "
                + "date_created text, "
                + "date_completed text "
                + "default 'null', "
                + "status varchar(255) default 'active', "
                + "date text);";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(tableCreationString);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    
    public Connection getConn() {
        return this.conn;
    }
}
