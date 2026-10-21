/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
*/
package com.mycompany.taskplannerjava;
import java.time.*;
/**
*
* @author Andrey
* @author Sofi
*/
public class TaskPlannerJava {
 
    public static void main(String[] args) {
        System.out.println("I'm pretty sure we're not using this file anymore i moved the db stuff to database.java");
        Database db = new Database();
        db.connect();
        
        Planner planner = new Planner(db.getConn());
        planner.setLocationRelativeTo(null);
        planner.setVisible(true);
        
    }
}