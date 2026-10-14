/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.taskplannerjava;

import java.time.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 *
 * @author Sofi
 */
public class Planner extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Planner.class.getName());
    // used to execute SQL statements
    private Connection conn;
    ArrayList<Task> monday;
    HashMap<Integer, ArrayList<Task>> week = new HashMap<>();
    javax.swing.JList[] weekDays;
    
    /**
     * Creates new form Planner
     */
    public String[] fillModel(ArrayList<Task> day) {
        String[] strings = new String[day.size()];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = day.get(i).getName();
        }
        return strings;
    }
    
    
    public void fillWeek(HashMap<Integer, ArrayList<Task>> week) {
        for (int i = 0; i < 7; i++) {
            week.put(i, new ArrayList<Task>());
//            week.get(i).add(new Task("task1", java.time.LocalDate.now()));
            readDB(weekStart.plusDays(i), week.get(i));
        }
        System.out.println("fillWeek");
        System.out.println(week);

    }
    
    public void fillDay(ArrayList<Task> day) {
        LocalDate filter = weekStart;
        for (int i = 0; i < week.size(); i++) {
            week.put(i, new ArrayList<Task>());
        }
    }
    
    public void showTasks(HashMap<Integer, ArrayList<Task>> week) {
        for (int i = 0; i < week.size(); i++) {
            DefaultListModel<String> model = new DefaultListModel<>();
            
            // reset the day's model so that tasks dont duplicate
            weekDays[i].setModel(model); 
            
            if (week.get(i).isEmpty()) {
                continue;
            }
            
            String[] strings = fillModel(week.get(i));

            for (String task : strings) {
                model.addElement(task);
            }

            weekDays[i].setModel(model);
        }      
    }
    
    public void readDB (LocalDate date, ArrayList<Task> day) {
        
        if (conn == null) {
            System.out.println("ERROR: Connection to db not yet established");
            return;
        }
        
        ArrayList<Task> dbTasks = new ArrayList<>();
        
        String queryStatement = 
            "SELECT task_id, name, date_created, date_completed, status, date FROM task WHERE date = ? AND user_id = ?";
        
        try (var pstmt = conn.prepareStatement(queryStatement)) {
            pstmt.setString(1, date.toString());
            pstmt.setInt(2, 1); 
            
            var rs = pstmt.executeQuery();
            
            while (rs.next()) {
                dbTasks.add(new Task(
                        rs.getInt("task_id"),
                        rs.getString("name"),
                        LocalDate.parse(rs.getString("date")),
                        LocalDate.parse(rs.getString("date_created")),
                        rs.getString("status")
                ));
            }
            
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
            

        //select all tasks (and create them - task(data1, data2, ...)) from DB with a date in their due date to an arraylist dbTasks
        //for now we check with if but if is not needed in case of db bcs select can filter
        // myOwnDB();
        for (int i = 0; i < dbTasks.size(); i++) {
            if (dbTasks.get(i).getDueDate().equals(date)) {
                day.add(dbTasks.get(i));
            }
        }
        System.out.println("readDB");
        System.out.println(date);
        System.out.println(day.toString());
        //with file: find the file with date in name
        //with file: make an arraylist with tasks (data1, data2, ...)
        
    }
    
    public void addTask (String name, LocalDate dueDate) {
        if (conn == null) {
            System.out.println("ERROR: Connection to db not yet established");
            return;
        }
        String addStatement = 
                "INSERT INTO task(name, date_created, date, user_id) VALUES(?,?,?,?)";
        
        try (var pstmt = conn.prepareStatement(addStatement)) {
            pstmt.setString(1, name);
            pstmt.setString(2, LocalDate.now().toString());
            pstmt.setString(3, dueDate.toString());
            pstmt.setInt(4, 1);
            pstmt.executeUpdate();
            System.out.println("This should have wo rked.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        //add task to database
        //for now:
        // dbTasks.add(new Task(name, dueDate));

        fillWeek(week);
        showTasks(week);
        
    }
    
    public Planner(Connection conn) {
        
//        this.monday = new ArrayList<>();
//        monday.add(new Task("task1", java.time.LocalDate.now()));
//        monday.add(new Task("task2", java.time.LocalDate.now()));
//        monday.add(new Task("dddd", java.time.LocalDate.now()));
        
        initComponents();
        updateWeekTitles();
        this.weekDays = new javax.swing.JList[]{jList1, jList2, jList3, jList4, jList5, jList6, jList7};
        this.conn = conn;
        fillWeek(week);
        showTasks(week);
        
//        jList1.setModel(new javax.swing.AbstractListModel<String>() {
//            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
//            public int getSize() { return strings.length; }
//            public String getElementAt(int i) { return strings[i]; }
//        });
//        jList1.setModel(new javax.swing.AbstractListModel<String>() {
//            
//            String[] strings = fillModel(monday);
////            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
//            public int getSize() { return strings.length; }
//            public String getElementAt(int i) { return strings[i]; }
//        });
        
        
//            DefaultListModel<String> model = new DefaultListModel<>();
//            String[] strings = fillModel(monday);
//
//            for (String task : strings) {
//                model.addElement(task);
//            }
//
//            jList1.setModel(model);

        
//        jList1.setModel(new javax.swing.DefaultListModel<String>());
//        String[] strings = fillModel(monday);
//        for (String task : strings) {
//            jList1.getModel().addElement(task);
//        }

//        jList1.setModel(new Task ("task1", java.time.LocalDate.now()) {
//            String[] strings = { new Task, "Item 2", "Item 3", "Item 4", "Item 5" };
//            public int getSize() { return strings.length; }
//            public String getElementAt(int i) { return strings[i]; }
//        });
//        jList1.setModel(new javax.swing.AbstractListModel<Task>() {
//            Task[] tasks = { new Task(monday.get(0), java.time.LocalDate.now()), new Task(monday.get(1), java.time.LocalDate.now()) };
//            public int getSize() { return tasks.length; }
//            public Task getElementAt(int i) { return tasks[i].getName(); }
//        });
//        ArrayList<Task>[7,100]
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList8 = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList<>();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList<>();
        jScrollPane6 = new javax.swing.JScrollPane();
        jList5 = new javax.swing.JList<>();
        jScrollPane7 = new javax.swing.JScrollPane();
        jList6 = new javax.swing.JList<>();
        jScrollPane8 = new javax.swing.JScrollPane();
        jList7 = new javax.swing.JList<>();
        jPanel3 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        jLabel2.setText("jLabel2");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addComponent(jLabel2)
                .addContainerGap(94, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(52, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(48, 48, 48))
        );

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Tasks"));

        jList8.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList8);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Week 58"));
        jPanel2.setToolTipText("");
        jPanel2.setLayout(new java.awt.GridLayout(1, 7, 2, 0));

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Monday"));

        jList1.setToolTipText("");
        jScrollPane2.setViewportView(jList1);

        jPanel2.add(jScrollPane2);

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Tuesday"));

        jScrollPane3.setViewportView(jList2);

        jPanel2.add(jScrollPane3);

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder("Wednesday"));

        jScrollPane4.setViewportView(jList3);

        jPanel2.add(jScrollPane4);

        jScrollPane5.setBorder(javax.swing.BorderFactory.createTitledBorder("Thursday"));

        jScrollPane5.setViewportView(jList4);

        jPanel2.add(jScrollPane5);

        jScrollPane6.setBorder(javax.swing.BorderFactory.createTitledBorder("Friday"));

        jScrollPane6.setViewportView(jList5);

        jPanel2.add(jScrollPane6);

        jScrollPane7.setBorder(javax.swing.BorderFactory.createTitledBorder("Saturday"));

        jScrollPane7.setViewportView(jList6);

        jPanel2.add(jScrollPane7);

        jScrollPane8.setBorder(javax.swing.BorderFactory.createTitledBorder("Sunday"));

        jScrollPane8.setViewportView(jList7);

        jPanel2.add(jScrollPane8);

        jPanel3.setLayout(new java.awt.GridLayout(1, 0, 6, 0));

        jButton2.setText("Add");
        jButton2.addActionListener(this::jButton2ActionPerformed);
        jPanel3.add(jButton2);

        jButton1.setText("Remove");
        jButton1.addActionListener(this::jButton1ActionPerformed);
        jPanel3.add(jButton1);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("Task Planner");
        jLabel1.setToolTipText("");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 6, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jButton3.setText(">");
        jButton3.addActionListener(this::jButton3ActionPerformed);

        jButton4.setText("<");
        jButton4.addActionListener(this::jButton4ActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 781, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton4)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 666, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
        jPanel1.getAccessibleContext().setAccessibleName("");
        jPanel1.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        weekStart = weekStart.minusWeeks(1);
        updateWeekTitles();
        fillWeek(week);
        showTasks(week);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        weekStart = weekStart.plusWeeks(1);
        updateWeekTitles();
        fillWeek(week);
        showTasks(week);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        AddTaskForm popup = new AddTaskForm(this, true);
//        popup.getCo
//        AddTaskForm popup = new AddTaskForm(this, true, "aaa");
//        javax.swing.JDialog popup1 = jDialog1;
        popup.setLocationRelativeTo(this);
        popup.setVisible(true);
//        popup1.setLocationRelativeTo(this);
//        popup1.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed
    
    
    private java.time.LocalDate weekStart = java.time.LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
    
    HashMap<Integer, Task> tasks = new HashMap<>();
    
    private void updateWeekTitles() {
        javax.swing.JScrollPane[] panes = {jScrollPane2, jScrollPane3, jScrollPane4, jScrollPane5,
            jScrollPane6, jScrollPane7, jScrollPane8};
        
        java.time.format.DateTimeFormatter fmt =
                java.time.format.DateTimeFormatter.ofPattern("EEE dd.MM", java.util.Locale.ENGLISH);

        for (int i = 0; i < panes.length; i++) {
            panes[i].setBorder(javax.swing.BorderFactory.createTitledBorder(weekStart.plusDays(i).format(fmt)));
        }

        int week = weekStart.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Week " + week));

        jPanel2.revalidate();
        jPanel2.repaint();
    }
    /**
     * @param args the command line arguments
     */
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        //java.awt.EventQueue.invokeLater(() -> new Planner().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JList<String> jList3;
    private javax.swing.JList<String> jList4;
    private javax.swing.JList<String> jList5;
    private javax.swing.JList<String> jList6;
    private javax.swing.JList<String> jList7;
    private javax.swing.JList<String> jList8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    // End of variables declaration//GEN-END:variables
}
