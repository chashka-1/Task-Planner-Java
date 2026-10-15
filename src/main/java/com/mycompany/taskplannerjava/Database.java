/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.taskplannerjava;

import java.sql.*;

/**
 *
 * @author goatm
 */
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
