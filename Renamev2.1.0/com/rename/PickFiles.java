package com.rename;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class PickFiles {

    public static java.util.List<File> getFiles(JFrame frame, String title, int mode, Image icon) {
        FileDialog dialog = new FileDialog(frame, title, mode);
        dialog.setMultipleMode(true);
        dialog.setIconImage(icon);
        dialog.setVisible(true);
        return dialog.getFiles() != null ? Arrays.asList(dialog.getFiles()) : null;
    }
}
