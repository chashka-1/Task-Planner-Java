package com.mycompany.taskplannerjava;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.LocalDate;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

class ListTransferHandler extends TransferHandler {
    private final Planner mainFrame;
    
    ListTransferHandler(Planner parent) {
        this.mainFrame = parent;
    }
    
    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        LocalDate targetDate;
        
        if (!canImport(info)) {
            return false;
        }

        JList targetDay = (JList) info.getComponent();
        int taskID;
        
        try {
            String data = (String) info.getTransferable()
                    .getTransferData(DataFlavor.stringFlavor);
            taskID = Integer.parseInt(data.trim());
            
            JScrollPane parentPane = (JScrollPane) SwingUtilities
                    .getAncestorOfClass(JScrollPane.class, targetDay);
            targetDate = (LocalDate) parentPane.getClientProperty("Date");
        } catch (UnsupportedFlavorException | IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        if (info.isDrop()) {
            JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
            
            if (dl.isInsert()) {
                mainFrame.changeTaskLocation(taskID, targetDate);
                return true;
            }
        }
        return false;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JList list = (JList) c;
        
        if (list.getSelectedIndex() < 0) {
            return null;
        }
        
        int taskID = mainFrame.gettingTaskID(list);
        return new StringSelection(String.valueOf(taskID));
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.stringFlavor);
    }
}