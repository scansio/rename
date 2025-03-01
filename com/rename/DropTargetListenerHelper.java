package com.rename;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DropTargetListenerHelper implements DropTargetListener {
    MainUI callee;

    public DropTargetListenerHelper(MainUI callee){
        this.callee = callee;
        callee.getDropLocation().setDropTarget(new DropTarget(callee.getDropLocation(), DnDConstants.ACTION_COPY, this));
    }
    
    @Override
    public void dragEnter(DropTargetDragEvent dragEvent) {
        callee.getDropLocation().setText("DROP HERE");
        callee.getDropLocation().setEnabled(true);
    }

    @Override
    public void dragOver(DropTargetDragEvent dragEvent) {
        callee.getDropLocation().setEnabled(true);
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dragEvent) {

    }

    @Override
    public void dragExit(DropTargetEvent dragEvent) {
        callee.getDropLocation().setEnabled(false);
    }
    @SuppressWarnings("unchecked")
    @Override
    public void drop(DropTargetDropEvent dragEvent) {
        boolean matches = false;
        DataFlavor[] flavor = dragEvent.getCurrentDataFlavors();
        for(DataFlavor dataFlavor:flavor){
            if(dataFlavor.match(DataFlavor.javaFileListFlavor)) {
                matches = true;
            }
        }
        if(matches){
            dragEvent.acceptDrop(DnDConstants.ACTION_COPY);
            Transferable t = dragEvent.getTransferable();
            try {
                callee.getStart().setEnabled(false);
                Object data = t.getTransferData(DataFlavor.javaFileListFlavor);
                List<File> fl = (List<File>) data;
                new Thread(() -> callee.operation0(fl)).start();
            } catch (UnsupportedFlavorException | IOException e) {
                callee.log(e.getMessage());
                e.printStackTrace();
            }

        }else{
            dragEvent.rejectDrop();
            callee.getDropLocation().setEnabled(false);
            callee.getDropLocation().setText("Unsupported format only Image required here");
            callee.e(2000);
        }

    }
    
}
