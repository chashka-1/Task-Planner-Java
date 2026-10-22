package com.mycompany.taskplannerjava;

public class TaskPlannerJava {
 
    public static void main(String[] args) {
        Database db = new Database();
        db.connect();
        
        Planner planner = new Planner(db.getConn());
        planner.setLocationRelativeTo(null);
        planner.setVisible(true);
        
    }
}