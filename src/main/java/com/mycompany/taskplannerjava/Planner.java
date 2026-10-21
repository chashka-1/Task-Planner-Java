/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.taskplannerjava;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import javax.swing.*;

/**
 *
 * @author Sofi
 */
public class Planner extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Planner.class.getName());
    // used to execute SQL statements
    private Connection conn;
    HashMap<Integer, ArrayList<Task>> week = new HashMap<>();
    JList[] weekDays;
    ListTransferHandler lh;
    
    public String[] fillModel(ArrayList<Task> day) {
        String[] strings = new String[day.size()];
        for (int i = 0; i < strings.length; i++) {
            Task task = day.get(i);
            
            switch (task.getStatus()) {
                case "overdue":
                    // checks if overdue task is dragged into a new, non-overdue day
                    if (!task.getDueDate().isBefore(LocalDate.now())) {
                        task.setStatus("active");
                        strings[i] = task.getName();
                        break;
                    }
                        
                    strings[i] = "<html><font color=red>"
                            + task.getName() + "</font></html>";
                    break;
                case "completed":
                    strings[i] = "<html><s>" + task.getName() + "</s></html>";
                    break;
                default:
                    strings[i] = task.getName();
                    break;
            }
        }
        return strings;
    }
    
    public void fillWeek(HashMap<Integer, ArrayList<Task>> week) {
        for (int i = 0; i < 7; i++) {
            week.put(i, new ArrayList<>());
            readDB(weekStart.plusDays(i), week.get(i));
        }
    }
    
    public void showTasks(HashMap<Integer, ArrayList<Task>> week) {
        fillWeek(week);
        for (int i = 0; i < week.size(); i++) {
            DefaultListModel<String> model = new DefaultListModel<>();
            
            weekDays[i].setModel(model);
            
            if (week.get(i).isEmpty()) {
                continue;
            }
            
            String[] strings = fillModel(week.get(i));
            
            for (String task : strings) {
                model.addElement(task);
//                model.getElementAt(i)
            }
        }      
    }
    
    public void readDB (LocalDate date, ArrayList<Task> day) {
        
        if (conn == null) {
            System.out.println("ERROR: Connection to db not yet established");
            return;
        }
        
        ArrayList<Task> dbTasks = new ArrayList<>();
        ArrayList<Integer> overdueTaskIDs = new ArrayList<>();
        
        String queryStatement = 
            "SELECT task_id, name, date_created, date_completed, status, date FROM task WHERE date = ?";
                
        try (var pstmt = conn.prepareStatement(queryStatement)) {
            pstmt.setString(1, date.toString());
            
            var rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Task task = new Task(
                        rs.getInt("task_id"),
                        rs.getString("name"),
                        LocalDate.parse(rs.getString("date")),
                        LocalDate.parse(rs.getString("date_created")),
                        rs.getString("status"));
                
                // checks if fetched task status should be overdue
                if (task.getDueDate().isBefore(LocalDate.now())
                        && (task.getDueDate().getYear() > 0) 
                        && "active".equals(task.getStatus())) {
                    
                    task.setStatus("overdue"); 
                    overdueTaskIDs.add(task.getID());
                }
                
                dbTasks.add(task);
            }
            
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        // sets status of affected tasks to overdue in database
        if (!overdueTaskIDs.isEmpty()) {
            changeTaskStatuses("overdue", overdueTaskIDs);
        }

        for (int i = 0; i < dbTasks.size(); i++) {
            if (dbTasks.get(i).getDueDate().equals(date)) {
                day.add(dbTasks.get(i));
            }
        }        
    }
    
    public void addTask (String name, LocalDate dueDate) {
        if (conn == null) {
            System.out.println("ERROR: Connection to db not yet established");
            return;
        }
        String addStatement = 
                "INSERT INTO task(name, date_created, date, priority) VALUES(?,?,?,?)";
        
        try (var pstmt = conn.prepareStatement(addStatement)) {
            pstmt.setString(1, name);
            pstmt.setString(2, LocalDate.now().toString());
            pstmt.setString(3, dueDate.toString());
            pstmt.setInt(4, 0);
            
            pstmt.executeUpdate();
            System.out.println("This should have wo rked.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        showTasks(week);
        
    }
    
    static int arrayIndexOf(JList[] jlists, JList target) {
        java.util.List<JList> list = java.util.Arrays.asList(jlists);
        return list.indexOf(target);
    }
    
    public void removeTask (int taskID) {
        
        if (conn == null) {
            System.out.println("ERROR: Connection to db not yet established");
            return;
        }
        String removeStatement = "DELETE FROM task WHERE task_id = ?";
        
        try (var pstmt = conn.prepareStatement(removeStatement)) {
            pstmt.setInt(1, taskID);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("Task with ID " + taskID + " deleted successfuly.");
            } else {
                System.out.println("No record with ID " + taskID + " found.");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        showTasks(week);
    }
    
    private void completeTask(int taskID, String status) {
        
        if (conn == null) {
            System.out.println("ERROR: Connection to db not yet established");
            return;
        }
        
        // allows for uncompletion of tasks
        String dateCompleted;
        
        if ("completed".equals(status)) {
            status = "active";
            dateCompleted = "null";
        } else {
            status = "completed";
            dateCompleted = LocalDate.now().toString();
        }
        
        String completeStatement = "UPDATE task SET date_completed = ? , "
                + "status = ?  WHERE task_id =  ?";
        
        try (var pstmt = conn.prepareStatement(completeStatement)) {
            pstmt.setString(1, dateCompleted);
            pstmt.setString(2, status);
            pstmt.setInt(3, taskID);
            
            pstmt.executeUpdate();
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
                
        showTasks(week);
    }

    private void editTask(int taskID) {
        //TODO: form popups to enter name/date 
        //AddTaskForm popup = new AddTaskForm(this, true, "aaa");
        //TODO: SQL update task
    }
    
    void changeTaskLocation(String taskName, int taskPriority, LocalDate newDate) {
        
        if (conn == null) {
            System.out.println("ERROR: Connection to db not yet established");
            return;
        }

        String changeDateStatement = "UPDATE task SET date = ? , priority = ? WHERE name = ?";
        
        try (var pstmt = conn.prepareStatement(changeDateStatement)) {
            pstmt.setString(1, newDate.toString());
            pstmt.setInt(2, taskPriority);
            pstmt.setString(3, taskName);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Something should have happened");
            } else {
                System.out.println("FUCKKKKKK");
            }
            
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        showTasks(week);
    }
    
    private void changeTaskStatuses(String newStatus, ArrayList<Integer> overdueIDs) {
        
        if (conn == null) {
            System.out.println("ERROR: Connection to db not yet established");
            return;
        }
        
        int size = overdueIDs.size();
        String placeHolders = String.join(",", Collections.nCopies(size, "?"));
        
        String changeStatusStatement = "UPDATE task SET status = ? "
                + "WHERE task_id IN (" + placeHolders + ")";

        try (var pstmt = conn.prepareStatement(changeStatusStatement)) {
            pstmt.setString(1, newStatus);
            
            // sets all of the taskIDs that need to be changed
            for (int i = 0; i < size; i++) {
                pstmt.setInt(i + 2, overdueIDs.get(i)); 
            }
            
            pstmt.executeUpdate();
            
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    /**
     * Creates new form Planner
     */
    public Planner(Connection conn) {
        initComponents();
        updateWeekTitles();
        this.weekDays = new JList[]{jList1, jList2, jList3, jList4, jList5, jList6, jList7};
        this.conn = conn;
        lh = new ListTransferHandler(this);
        showTasks(week);
        
        MouseAdapter listener = new MouseAdapter() {
            @Override 
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }
            @Override 
            public void mouseReleased(MouseEvent e) { 
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                
                if (!e.isPopupTrigger()) {
                    return;
                }
                
                JList<String> list = (JList<String>) e.getComponent();
                int index = list.locationToIndex(e.getPoint());
                
                if (index >= 0 && list.getCellBounds(index, index).contains(e.getPoint())) {
                    list.setSelectedIndex(index);
                    jPopupMenu1.show(list, e.getX(), e.getY());
                } 
                else {
                    list.clearSelection();
                }
            }
            
            //wanted to try to fix built-in selection of jlists
//            private void removeSelection(MouseEvent e) {
//                
//                JList<String> list = (JList<String>) e.getComponent();
//                int minInd = list.getMinSelectionIndex();
//                int maxInd = list.getMaxSelectionIndex();
//                
//                if (list.getCellBounds(minInd, maxInd) != null && !list.getCellBounds(minInd, maxInd).contains(e.getPoint())) {
//                    list.clearSelection();
//                } 
//                else {
//                    list.clearSelection();
//                }
//            }
        };
        for (JList l: weekDays) {
            l.addMouseListener(listener);
            l.setTransferHandler(lh);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
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

        jMenuItem1.setText("Edit");
        jMenuItem1.setName(""); // NOI18N
        jMenuItem1.addActionListener(this::jMenuItem1ActionPerformed);
        jPopupMenu1.add(jMenuItem1);
        jMenuItem1.getAccessibleContext().setAccessibleDescription("");

        jMenuItem2.setText("Mark as completed");
        jMenuItem2.addActionListener(this::jMenuItem2ActionPerformed);
        jPopupMenu1.add(jMenuItem2);

        jMenuItem3.setText("Delete");
        jMenuItem3.addActionListener(this::jMenuItem3ActionPerformed);
        jPopupMenu1.add(jMenuItem3);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Tasks"));

        jList8.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList8.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList8.setDragEnabled(true);
        jList8.setDropMode(javax.swing.DropMode.INSERT);
        jScrollPane1.setViewportView(jList8);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Week 58"));
        jPanel2.setToolTipText("");
        jPanel2.setLayout(new java.awt.GridLayout(1, 7, 2, 0));

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Monday"));

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setToolTipText("");
        jList1.setDragEnabled(true);
        jList1.setDropMode(javax.swing.DropMode.INSERT);
        jScrollPane2.setViewportView(jList1);

        jPanel2.add(jScrollPane2);

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Tuesday"));

        jList2.setDragEnabled(true);
        jList2.setDropMode(javax.swing.DropMode.INSERT);
        jScrollPane3.setViewportView(jList2);

        jPanel2.add(jScrollPane3);

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder("Wednesday"));

        jList3.setDragEnabled(true);
        jList3.setDropMode(javax.swing.DropMode.INSERT);
        jScrollPane4.setViewportView(jList3);

        jPanel2.add(jScrollPane4);

        jScrollPane5.setBorder(javax.swing.BorderFactory.createTitledBorder("Thursday"));

        jList4.setDragEnabled(true);
        jList4.setDropMode(javax.swing.DropMode.INSERT);
        jScrollPane5.setViewportView(jList4);

        jPanel2.add(jScrollPane5);

        jScrollPane6.setBorder(javax.swing.BorderFactory.createTitledBorder("Friday"));

        jList5.setDragEnabled(true);
        jList5.setDropMode(javax.swing.DropMode.INSERT);
        jScrollPane6.setViewportView(jList5);

        jPanel2.add(jScrollPane6);

        jScrollPane7.setBorder(javax.swing.BorderFactory.createTitledBorder("Saturday"));

        jList6.setDragEnabled(true);
        jList6.setDropMode(javax.swing.DropMode.INSERT);
        jScrollPane7.setViewportView(jList6);

        jPanel2.add(jScrollPane7);

        jScrollPane8.setBorder(javax.swing.BorderFactory.createTitledBorder("Sunday"));

        jList7.setDragEnabled(true);
        jList7.setDropMode(javax.swing.DropMode.INSERT);
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
        weekStart = weekStart.minusWeeks(1);
        updateWeekTitles();
        showTasks(week);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        weekStart = weekStart.plusWeeks(1);
        updateWeekTitles();
        showTasks(week);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        AddTaskForm popup = new AddTaskForm(this, true);
        popup.setLocationRelativeTo(this);
        popup.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //remove button here is not needed anymore
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        JList<String> jListInvoked = (JList<String>) jPopupMenu1.getInvoker();
        int dayIndex = arrayIndexOf(weekDays, jListInvoked);
        int taskID = week.get(dayIndex).get(jListInvoked.getSelectedIndex()).getID();
        editTask(taskID);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        JList<String> jListInvoked = (JList<String>) jPopupMenu1.getInvoker();
        int dayIndex = arrayIndexOf(weekDays, jListInvoked);
        int taskID = week.get(dayIndex).get(jListInvoked.getSelectedIndex()).getID();
        String taskStatus = week.get(dayIndex).get(jListInvoked.getSelectedIndex()).getStatus();
        completeTask(taskID, taskStatus);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        JList<String> jListInvoked = (JList<String>) jPopupMenu1.getInvoker();
        int dayIndex = arrayIndexOf(weekDays, jListInvoked);
        int taskID = week.get(dayIndex).get(jListInvoked.getSelectedIndex()).getID();
        removeTask(taskID);
//        System.out.println(weekStart.plusDays(dayIndex).format(fmtSQL) + " " + dayIndex);
//        System.out.println(weekStart.plusDays(dayIndex).toString() + " " + dayIndex);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    
    private void updateWeekTitles() {
        JScrollPane[] panes = {jScrollPane2, jScrollPane3, jScrollPane4, jScrollPane5,
            jScrollPane6, jScrollPane7, jScrollPane8};
        
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE dd.MM", java.util.Locale.ENGLISH);

        for (int i = 0; i < panes.length; i++) {
            if (weekStart.plusDays(i).equals(LocalDate.now())) {
                panes[i].setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(java.awt.Color.RED),
                        weekStart.plusDays(i).format(fmt),
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        null,
                        java.awt.Color.RED
                ));

            } else {
                panes[i].setBorder(BorderFactory.createTitledBorder(weekStart.plusDays(i).format(fmt)));
            }
            
            // attaches a date to the pane (used in ListTransferHandler.java)
            panes[i].putClientProperty("Date", weekStart.plusDays(i));
        }
        var weekNumber = weekStart.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        jPanel2.setBorder(BorderFactory.createTitledBorder("Week " + weekNumber));

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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JList<String> jList3;
    private javax.swing.JList<String> jList4;
    private javax.swing.JList<String> jList5;
    private javax.swing.JList<String> jList6;
    private javax.swing.JList<String> jList7;
    private javax.swing.JList<String> jList8;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPopupMenu jPopupMenu1;
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
