/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
*/
package com.mycompany.taskplannerjava;
 
import java.sql.DriverManager;
import java.sql.SQLException;
 
/**
*
* @author Andrey
* @author Sofi
*/
public class TaskPlannerJava {
 
    public static void connect() {
        // relative path connection string, foreign_keys enforced
        var url = "jdbc:sqlite:PlannerInfo.db?foreign_keys=on";
 
        try (var conn = DriverManager.getConnection(url)) {
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }   
    }
 
    public static void main(String[] args) {
        connect();
    }
}