package com.mycompany.taskplannerjava;

import java.time.LocalDate;

public class Task {
    private final int id;
    private final String name;
    private final LocalDate dueDate;
    private final LocalDate createdDate;
    private LocalDate completedDate;
    private String status;
    private final int priority;
    
    public Task(int id, String name, LocalDate dueDate, LocalDate createdDate, String status, int priority) {
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        this.createdDate = createdDate;
        this.status = status;
        this.priority = priority;
    }
    
    public String getName() {
        return name;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public int getID() {
        return id;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String newStatus) {
        status = newStatus;
    }
    
    public int getPriority() {
        return priority;
    }
}
