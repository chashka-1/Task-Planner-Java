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
    private String name;
    private LocalDate dueDate;
    
    
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

//    @Override
//    public int getSize() {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }
//
//    @Override
//    public Object getElementAt(int index) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }
}
