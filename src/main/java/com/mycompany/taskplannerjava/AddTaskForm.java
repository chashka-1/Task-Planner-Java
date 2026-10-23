package com.mycompany.taskplannerjava;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class AddTaskForm extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger LOGGER = 
            java.util.logging.Logger.getLogger(AddTaskForm.class.getName());

    private final Planner mainFrame;
    private DatePicker datePicker;
    
    private void setupDatePicker() {
        DatePickerSettings settings = 
                new DatePickerSettings(java.util.Locale.ENGLISH);
        settings.setFirstDayOfWeek(DayOfWeek.MONDAY);
        datePicker = new DatePicker(settings);
        datePanel.add(datePicker, java.awt.BorderLayout.CENTER);
        pack();
    }
    
    public AddTaskForm(Planner parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.mainFrame = parent;
        setupDatePicker();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        datePanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Create New Task");
        setResizable(false);

        jPanel2.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        jButton2.setText("Cancel");
        jButton2.addActionListener(this::jButton2ActionPerformed);
        jPanel2.add(jButton2);

        jButton1.setText("OK");
        jButton1.addActionListener(this::jButton1ActionPerformed);
        jPanel2.add(jButton1);

        jPanel1.setLayout(new java.awt.GridLayout(4, 0, 0, 10));

        jLabel1.setText("New task name");
        jPanel1.add(jLabel1);
        jPanel1.add(jTextField1);

        jLabel2.setText("Due date");
        jPanel1.add(jLabel2);

        datePanel.setLayout(new java.awt.BorderLayout());
        jPanel1.add(datePanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        jPanel1.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String name = jTextField1.getText();
        LocalDate dueDate = datePicker.getDate();
        
        if (name.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                    "Please enter a task name.");
            return;
        }

        if (dueDate == null) {
            mainFrame.addTask(name, LocalDate.of(-999999999, 01, 01));
        } else {
            if (dueDate.getYear() <= 0) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Please enter a valid date.");
                return;
            }
            mainFrame.addTask(name, dueDate);        
        }
        
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel datePanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
