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
import static javax.swing.TransferHandler.COPY_OR_MOVE;
import static javax.swing.TransferHandler.MOVE;

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

        //If we can't handle the import, bail now.
        if (!canImport(info)) {
            return false;
        }

        JList list = (JList) info.getComponent();
        DefaultListModel model = (DefaultListModel) list.getModel();
        //Fetch the data -- bail if this fails
        try {
            taskName = (String) info.getTransferable()
                    .getTransferData(DataFlavor.stringFlavor);
            taskName = taskName.replaceAll("<[^>]*>", "");
            
            // identifies the pane into which task is dropped as well as the pane's date
            targetDay = (JList) info.getComponent(); 
            parentPane = (JScrollPane) SwingUtilities
                    .getAncestorOfClass(JScrollPane.class, targetDay);
            targetDate = (LocalDate) parentPane.getClientProperty("Date");
        } catch (UnsupportedFlavorException ufe) {
            System.out.println("importData: unsupported data flavor");
            return false;
        } catch (IOException ioe) {
            System.out.println("importData: I/O exception");
            return false;
        }

        if (info.isDrop()) { //This is a drop 
            JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
            int index = dl.getIndex();
            
            if (dl.isInsert()) {
                mainFrame.changeTaskLocation(taskName, index, targetDate);
                System.out.println(index);
                return true;
            } else {
                model.set(index, taskName);
                return true;
            }
        } else { //This is a paste
            int index = list.getSelectedIndex();
            // if there is a valid selection,
            // insert data after the selection
            if (index >= 0) {
                model.add(list.getSelectedIndex() + 1, taskName);
            // else append to the end of the list
            } else {
                model.addElement(taskName);
            }
            return true;
        }
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JList list = (JList) c;
        String value = (String) list.getSelectedValue();
        return new StringSelection(value);
    }

    /**
     * The list handles both copy and move actions.
     */
    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    /** 
     * When the export is complete, remove the old list entry if the
     * action was a move.
     */
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {

    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        //checking if import Strings
        return support.isDataFlavorSupported(DataFlavor.stringFlavor);
    }
}

