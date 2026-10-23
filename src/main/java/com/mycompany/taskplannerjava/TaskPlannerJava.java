package com.mycompany.taskplannerjava;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

public class TaskPlannerJava {
    public static void main(String[] args) {
        Database db = new Database();
        db.connect();
        
        Planner planner = new Planner(db.getConn());
        
        planner.setLocationRelativeTo(null);
        planner.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        planner.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                db.disconnect();
                planner.dispose();
                System.exit(0);
            }
        });
        
        planner.setVisible(true);
    }
}