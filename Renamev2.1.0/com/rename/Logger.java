package com.rename;

import javax.swing.*;
import java.awt.*;

public final class Logger {
    /**
     * Information logging utility class
     */

    private static final JTextArea log = new JTextArea();

    private static final JWindow window = new JWindow();

    //I don't need instances
    private Logger(){}

    //setting
    static {
        window.getContentPane().add(log);
        //window.setVisible(true);
        //window.getContentPane().setVisible(true);
        //window.setAlwaysOnTop(true);
        window.setPreferredSize(new Dimension(500, 500));
        window.setMinimumSize(new Dimension(500, 500));
        log.setPreferredSize(new Dimension(500, 500));
        log.setMinimumSize(new Dimension(500, 500));
        log.setBackground(new Color(-2163460));
        log.setFont(new Font("Time New Roman", Font.ITALIC, 14));
        log.setAutoscrolls(true);
        log.setEditable(false);
        log.setTabSize(2);
    }

    /**
     * <p>This logs information to the application internal log pane. </p>
     * @param o Object to log into the TextArea e.g String, primitive types
     * */
    public static void log(Object o){
        log.append("\n" + o.toString());
        log.setCaretPosition((log.getText().length() - String.valueOf(o).length()));
    }

    /**
     * <p><b>Shows</b> or <b>Hides</b> <b>{@code JWindow}</b> window.</p><hr/>
     * @param visibility {@code true} to show or {@code false} to hide.
     */
    public static void setVisible(boolean visibility){
        window.setVisible(visibility);
    }
}
