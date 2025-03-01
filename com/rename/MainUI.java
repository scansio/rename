package com.rename;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class MainUI extends JFrame{

    private JRadioButton jpegToJPG;
    private JTextField extension1;
    private JTextField extension2;
    //private JTextArea log;
    private JButton close;
    private JButton start;
    private JTextField path;
    private JPanel mainContainer;
    //private JScrollPane logScroll;
    private JProgressBar progressBar;
    private JButton pick;
    private JLabel dropLocation;
    private JPanel leftHolder;
    private final Color pIniColor;
    private final TrayIcon trayIcon;
    private boolean stopThread = false;
    private Image ICON;

    public MainUI() {
        this.setTitle("RENAME");
        Image ICON = Toolkit.getDefaultToolkit().createImage("com/resource/appIcon.png");
        this.setIconImage(ICON);
        this.setContentPane(mainContainer);
        //this.setLocationRelativeTo(null);
        this.setMinimumSize(new Dimension(600, 500));
        this.setPreferredSize(new Dimension(600, 500));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.jpegToJPG.setSelected(true);
        this.jpegToJPG.setBackground(Color.red);
        this.extension1.setEnabled(false);
        this.extension2.setEnabled(false);
        this.extension1.setText("jpeg");
        this.extension2.setText("JPG");
        this.pIniColor = progressBar.getForeground();

        /*JScrollBar vSBar = logScroll.getVerticalScrollBar();
        vSBar.setAutoscrolls(false);
        vSBar.setForeground(Color.red);
        vSBar.setBlockIncrement(1000);
        vSBar.setUnitIncrement(1000);
        vSBar.setMinimumSize(new Dimension(10, 10));
        vSBar.setPreferredSize(new Dimension(8, 25));

        JScrollBar hSBar = logScroll.getHorizontalScrollBar();
        hSBar.setAutoscrolls(false);
        hSBar.setForeground(Color.red);
        hSBar.setUnitIncrement(500);
        hSBar.setBlockIncrement(500);
        hSBar.setMinimumSize(new Dimension(10, 10));
        hSBar.setPreferredSize(new Dimension(25, 8));
*/
        Insets insets = mainContainer.getInsets();
        insets.set(-3, -3 , -3 , -3);
        Logger.log("Testing JWINDOW");
        this.trayIcon = new TrayIcon(ICON);
        this.trayIcon.setActionCommand("tray");
        this.trayIcon.setToolTip("Click on me to view Rename");
        this.trayIcon.displayMessage("RENAME", "Click on me to view Rename", TrayIcon.MessageType.INFO);
        this.trayIcon.setImageAutoSize(true);
        this.trayIcon.addActionListener(e -> {
            if (e.getSource() == trayIcon){
                if(!this.isVisible())
                this.setVisible(true);
            }
        });
        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            log("TrayIcon Says: " + e.getMessage());
        }

        Image f = Toolkit.getDefaultToolkit().createImage("com/resource/myFolder.png");
        f = f.getScaledInstance(15, 15, 1);
        Icon i = new ImageIcon(f);
        this.pick.setIcon(i);
        this.pick.setIconTextGap(4);

        this.jpegToJPG.addActionListener(e -> {
            if(e.getSource().equals(this.jpegToJPG)){
                if (jpegToJPG.isSelected()) {
                    extension1.setEnabled(false);
                    extension2.setEnabled(false);
                    extension1.setText("jpeg");
                    extension2.setText("JPG");
                }else {
                    extension1.setEnabled(true);
                    extension2.setEnabled(true);
                }
            }

        });

        this.close.addActionListener(e -> {
                if (e.getSource() == close) {
                    if(e.getActionCommand().equals("stop")){
                        setclosetext();
                        stopThread = true;
                    }else if (e.getActionCommand().equals("close")){
                        this.getToolkit().beep();
                        System.exit(0);
                    }
    
                }
            });

        this.start.addActionListener(e -> operation3("OperationFromStart", null));
        this.path.addActionListener(e -> operation3("OperationFromPath", null));
        this.extension1.addActionListener(e -> operation3("OperationFromExtension1", null));
        this.extension2.addActionListener(e -> operation3("OperationFromExtension2", null));
        this.pick.addActionListener(this::pickOperation);
        
        this.pick.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "open");
        this.pick.getActionMap().put("open", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pickOperation(e);
            }
        });
        pick.setBorder(new BevelBorder(0, Color.blue, Color.green));
        pick.setBorderPainted(true);
        pick.setBackground(Color.white);

        new DropTargetListenerHelper(this);
        pick.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(pick.isEnabled()){
                    pick.setContentAreaFilled(true);
                    pick.setBackground(Color.BLUE);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(pick.isEnabled())
                    pick.setContentAreaFilled(false);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if(pick.isEnabled()) {
                    pick.setContentAreaFilled(true);
                    pick.setBackground(Color.green);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(pick.isEnabled())
                    pick.setContentAreaFilled(false);
            }
        });
        this.ICON = ICON;
    }

    private void pickOperation(ActionEvent e) {
        operation3("OperationFromPick", PickFiles.getFiles(this, "Renamev2.1.0 - Pick File(s)",
                0, ICON));
    }

    public JLabel getDropLocation() {
        return dropLocation;
    }

    private void operation3(String threadName, List<File> fl) {
        new Thread(null, () -> operation0(fl), threadName).start();
        close.setText("stop");
        close.setActionCommand("stop");
    }

    public void e(int millis) {
        new Thread(()->{
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dropLocation.setText("DROP HERE");
        }).start();
    }

    /**
     * This method uses File {@param f} to create storage directory.
     * @param f File to use it parent path as base to create its hardcoded {@code rename_files} directory.
     * @return Directory file created as the base of the operation.
     */
    private File getDestination(File f) {
        File destination = new File(f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(File.separator))
                + "/renamed_files");
        if(!destination.exists())destination.mkdir();
        return destination;
    }

    /**
     * This enable/disable operation tiggering UI based on {@param b}.
     * This is to prevent tiggering another operation without first stoping the already running one.
     * @param b Boolean parameter indicating to {@code false} disable or {@code true} to enable them.
     */
    private void disableExtension(boolean b) {
        path.setEnabled(b);
        pick.setEnabled(b);
        start.setEnabled(b);
        if(!jpegToJPG.isSelected()){
            jpegToJPG.setEnabled(b);
            jpegToJPG.setBackground(Color.gray);
            extension1.setEnabled(b);
            extension2.setEnabled(b);
        }else {
            jpegToJPG.setEnabled(b);
            jpegToJPG.setBackground(Color.red);
        }

    }

    protected void operation0(List<File> fl){
        if(fl != null && !fl.isEmpty()) {
            operation1(fl);
        }else{
            log("\nDirectory: " + path.getText());
            String filePath = path.getText();
            File file = new File(filePath);
            if (!file.exists()) {
                log("\nDirectory " + filePath + "does not exists try again with valid one.");
                setclosetext();
            } else {
                List<File> fileList = file.listFiles() != null ? Arrays.asList(Objects.requireNonNull(file.listFiles())) : null;
                if (fileList == null) {
                    log("\nNo file in " + filePath + " directory.");
                    setclosetext();
                } else {
                    try {
                        if (extension1.getText().equals("")) {
                            log("\nYou've entered no extension, please run it again.");
                            return;
                        }
                        List<File> names = new ArrayList<>();
                        for (File value : fileList) {
                            if (value.getName().endsWith(extension1.getText()) || value.getName().endsWith(
                                    extension1.getText().toLowerCase()) ||
                                    value.getName().endsWith(extension1.getText().toUpperCase())) {
                                names.add(value);
                            }
                        }
                        if (names.size() != 0) {
                            operation1(names);
                        } else {
                            log("\n" + "No " + extension1.getText() + " extension found in " + filePath);
                            setclosetext();
                        }
                    } catch (Exception e) {
                        log("\n" + e.getMessage());
                    }
                }
            }

        }
    }

    private void setclosetext() {
        close.setText("close");
        close.setActionCommand("close");
        start.setEnabled(true);
        progressBar.setForeground(pIniColor);
        progressBar.setVisible(false);
        dropLocation.setEnabled(false);
        stopThread = false;
        Toolkit.getDefaultToolkit().beep();
    }

    private void operation1(List<File> fl) {
        progressBar.setMaximum(fl.size());
        progressBar.setMinimum(0);
        progressBar.setVisible(true);
        close.setActionCommand("stop");
        close.setText("stop");
        File destination = getDestination(fl.get(0));
        log("Processing please wait............................");
        String cacheType = extension1.getText();
        double percentage = 100.0 / fl.size();
        int percentComplete;
        MainUI.this.disableExtension(false);
        int operation1Int;
        for (operation1Int = 0; operation1Int < fl.size(); operation1Int++) {
            if (stopThread) break;
            String currentFile = (operation1Int + 1) + ": " + fl.get(operation1Int).getPath();
            if(!fl.get(operation1Int).isDirectory()) b(fl.get(operation1Int), cacheType, getOutputFile(destination.getPath(), fl.get(operation1Int).getName()));
            log(currentFile);
            percentComplete = (int) (percentage * operation1Int);
            dropLocation.setEnabled(true);
            dropLocation.setText("Processing..." + percentComplete + "%");
            progressBar.setValue(operation1Int + 1);
            double oneThird = (fl.size() + 1) / 1.3;
            if (operation1Int >= oneThird) {
                progressBar.setForeground(Color.green);
            }
        }
        if(stopThread){
            log("Operation Cancelled");
            dropLocation.setText("Operation Cancelled");
            setclosetext();
        }else{
            log("\n......DONE......");
            dropLocation.setText("DONE");
            setclosetext();
        }
        disableExtension(true);
        e(2000);
    }

    private File getOutputFile(String parent, String child) {
        String strippedName = child.substring(0, child.lastIndexOf('.'));
        return new File(parent + "/" + strippedName +
                (extension2.getText().lastIndexOf('.') != 0 ? "." + extension2.getText() :
                        extension2.getText()));
    }

    private void b(File in, String cacheType, File out) {
        File[] ioFiles = {in, out};
        try{
            convert(in, cacheType, extension2.getText(), out);
        }catch(Exception e){
            try{
                d(cacheType, "jpeg" ,ioFiles);
            }catch(Exception e1){
                try{
                    d(cacheType, "png" ,ioFiles);
                }catch(Exception e2){
                    try{
                        d(cacheType, "wbmp" ,ioFiles);
                    }catch(Exception e3){
                        try{
                            d(cacheType, "gif" ,ioFiles);
                        }catch(Exception e4){
                            try{
                                d(cacheType, "tiff" ,ioFiles);
                            }catch(Exception e5){
                                try{
                                    d(cacheType, "bmp" ,ioFiles);
                                }catch(Exception ignored){
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void d(String cacheType, String tryType, File... ioFiles) throws Exception{
        if(!(cacheType.toLowerCase().equals(tryType))){
            cacheType = tryType;
            String finalCacheType = cacheType;
            convert(ioFiles[0], finalCacheType, extension2.getText(), ioFiles[1]);
        }else{
            throw new DesameException();
        }
    }

    /**
 * This logs information to the application internal log pane.
 * @param o object to log into the pane e.g String, primitive types*/
    public<T>  void  log(T o){
        Logger.log("\n\t" + o +"\t");
    }

    /**
     * This method handles the image conversion base on {@code inType}, every exception thrown is catch
     * using Exception super class, it uses {@code BufferedImage.TYPE_INT_RGB} for the output type.
     * @param image Image file to be converted.
     * @param inType Type used for input reading which throws an exception if it cannot be read with {@param inType} type.
     * @param outType Type of image to be converted which throws an exception if {@param outType} type is unsupported.
     * @param outFile File name to write the output to.
     * @throws Exception Exception thrown as convert result of reading an unsupported image type or error writing to {@code outFile}.
     */
    public void convert(File image, String inType, String outType, File outFile) throws Exception{
        try(
                ImageInputStream inputStream = new FileImageInputStream(image)
        ){
            ImageReader iReader = ImageIO.getImageReadersByFormatName(inType).next();
            iReader.setInput(inputStream);
            Image originalImage = iReader.read(0);
            BufferedImage bi = new BufferedImage(originalImage.getWidth(null), originalImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bi.createGraphics();
            g2d.drawImage(originalImage, 0, 0, null);
            g2d.dispose();
            outFile.createNewFile();
            new Thread(Thread.currentThread().getThreadGroup(), () -> {
                try {
                    ImageIO.write(bi, outType, outFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    outFile.delete();
                }
            }, "OperationWriteFile").start();
        }
    }

    public static void main(String[] args){
        MainUI app = new MainUI();
        app.setVisible(true);
        Logger.setVisible(true);
    }

    public JButton getStart() {
        return start;
    }
}