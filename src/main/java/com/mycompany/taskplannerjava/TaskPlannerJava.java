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
        Database db = new Database();
        db.connect();
        
        Planner planner = new Planner(db.getConn());
        
        planner.setVisible(true);
        
        LocalDate testDate = LocalDate.now();
        
//        planner.addTask("Do this roday!", testDate);
        
    }
}