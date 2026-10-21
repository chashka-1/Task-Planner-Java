/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.taskplannerjava;

import java.time.LocalDate;

/**
 *
 * @author Sofi
 */
public class Task {
    private int id;
    private String name;
    private LocalDate dueDate;
    private LocalDate createdDate;
    private LocalDate completedDate;
    private String status;
    
    public Task (int id, String name, LocalDate dueDate, LocalDate createdDate, String status) {
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        this.createdDate = createdDate;
        this.status = status;
    }
    
    public Task (String name, LocalDate dueDate) {
        this.name = name;
        this.dueDate = dueDate;
    }
    
    public String getName () {
        return name;
    }
    
    public LocalDate getDueDate () {
        return dueDate;
    }
    
    public int getID () {
        return id;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String newStatus) {
        status = newStatus;
    }
}
