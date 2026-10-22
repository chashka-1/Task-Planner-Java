package com.mycompany.taskplannerjava;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.LocalDate;
import javax.swing.DefaultListModel;
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
        String taskName = null;
        JList targetDay;
        JScrollPane parentPane;
        LocalDate targetDate;

        if (!canImport(info)) {
            return false;
        }

        JList list = (JList) info.getComponent();
        DefaultListModel model = (DefaultListModel) list.getModel();

        try {
            taskName = (String) info.getTransferable()
                    .getTransferData(DataFlavor.stringFlavor);
            taskName = taskName.replaceAll("<[^>]*>", "");
            
            // identifies the pane into which task is dropped as well as the pane's date
            targetDay = (JList) info.getComponent(); 
            parentPane = (JScrollPane) SwingUtilities
                    .getAncestorOfClass(JScrollPane.class, targetDay);
            targetDate = (LocalDate) parentPane.getClientProperty("Date");
        } catch (UnsupportedFlavorException | IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        if (info.isDrop()) {
            JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
            var taskID = mainFrame.gettingTaskID((JList) info.getComponent());

            int index = dl.getIndex();
            
            if (dl.isInsert()) {
                
                mainFrame.changeTaskLocation(taskID, index, targetDate);
                System.out.println(index);
                return true;
            } else {
                model.set(index, taskName);
                return true;
            }
        }
        return false;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JList list = (JList) c;
        String value = (String) list.getSelectedValue();
        return new StringSelection(value);
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