package com.mycompany.taskplannerjava;

/*
 * ListTransferHandler.java is used by the ListCutPaste example.
 */

import java.io.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import static javax.swing.TransferHandler.COPY_OR_MOVE;
import static javax.swing.TransferHandler.MOVE;

class ListTransferHandler extends TransferHandler {

    /**
     * Perform the actual data import.
     */
    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        String data = null;

        //If we can't handle the import, bail now.
        if (!canImport(info)) {
            return false;
        }

        JList list = (JList)info.getComponent();
        DefaultListModel model = (DefaultListModel)list.getModel();
        //Fetch the data -- bail if this fails
        try {
            data = (String)info.getTransferable().getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException ufe) {
            System.out.println("importData: unsupported data flavor");
            return false;
        } catch (IOException ioe) {
            System.out.println("importData: I/O exception");
            return false;
        }

        if (info.isDrop()) { //This is a drop
            JList.DropLocation dl = (JList.DropLocation)info.getDropLocation();
            int index = dl.getIndex();
            if (dl.isInsert()) {
                model.add(index, data);
                return true;
            } else {
                model.set(index, data);
                return true;
            }
        } else { //This is a paste
            int index = list.getSelectedIndex();
            // if there is a valid selection,
            // insert data after the selection
            if (index >= 0) {
                model.add(list.getSelectedIndex()+1, data);
            // else append to the end of the list
            } else {
                model.addElement(data);
            }
            return true;
        }
    }

    /**
     * Bundle up the data for export.
     */
    @Override
    protected Transferable createTransferable(JComponent c) {
        JList list = (JList)c;
        String value = (String)list.getSelectedValue();
        return new StringSelection(value);
    }

    /**
     * The list handles both copy and move actions.
     */
    

    /** 
     * When the export is complete, remove the old list entry if the
     * action was a move.
     */
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        if (action != MOVE) {
            return;
        }
        JList list = (JList)c;
        DefaultListModel model = (DefaultListModel)list.getModel();
        int index = list.getSelectedIndex();
        model.remove(index);
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        //checking if import Strings
        return support.isDataFlavorSupported(DataFlavor.stringFlavor);
    }
}

