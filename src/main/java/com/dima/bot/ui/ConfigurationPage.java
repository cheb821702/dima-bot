package com.dima.bot.ui;

import com.dima.bot.manager.BotsManager;
import com.dima.bot.manager.util.ThreadManager;
import com.dima.bot.settings.model.UrlWorker;
import com.dima.bot.ui.component.IntFilter;
import com.dima.bot.util.WorkersTableModel;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.PlainDocument;


/**
 * User: CHEB
 */
public class ConfigurationPage extends JFrame {

    Logger logger = Logger.getLogger("debugLogger");

    private TrayIcon trayIcon;
    private SystemTray tray;
    private BotsManager manager;

    private JButton chooseACTFileButton;
    private final JTable table;
    private final JTable timerTable;
    private JButton addButton;
    private JButton removeButton;
    private JButton editButton;
    private JButton pauseButton;
    private JTextField timerField;

    public ConfigurationPage(BotsManager initManager){
        super("Staff Worker");

        this.manager = initManager;

        initTray();

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        JPanel actPane = new JPanel();
        actPane.setLayout(new FlowLayout());
        final JLabel chooseACTFileLabel = new JLabel();
        if(manager != null) {
            String actPath = manager.getAutoCompleteTemplatesPath();
            if(actPath != null) {
                chooseACTFileLabel.setText(actPath);
            } else {
                chooseACTFileLabel.setText("ничего не выбрано");
            }
        }
        actPane.add(chooseACTFileLabel);
        actPane.add(Box.createRigidArea(new Dimension(0, 5)));
        chooseACTFileButton = new JButton("Файл автозаполнения");
        chooseACTFileButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                fc.setAcceptAllFileFilterUsed(false);
                fc.addChoosableFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory()) {
                            return true;
                        }

                        String extension = FileFilterUtils.getExtension(f);
                        if (extension != null) {
                            if (extension.equals(FileFilterUtils.xls) ||
                                    extension.equals(FileFilterUtils.xlsx)
                                    ) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                        return false;
                    }

                    @Override
                    public String getDescription() {
                        return "Just Excel";
                    }
                });

                int returnVal = fc.showOpenDialog(ConfigurationPage.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    String actPath = manager.setAutoCompleteTemplatesPath(file.getPath());
                    if(actPath != null) {
                        chooseACTFileLabel.setText(actPath);
                    } else {
                        chooseACTFileLabel.setText("ничего не выбрано");
                    }
                    logger.debug("Opening: " + file.getName() + ".");
                } else {
                    logger.debug("Open command cancelled by user.");
                }
            }
        });
        actPane.add(chooseACTFileButton);
        actPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel tablePane = new JPanel();
        tablePane.setLayout(new BoxLayout(tablePane, BoxLayout.X_AXIS));
        table = new JTable(new WorkersTableModel(new String[]{"Адрес","Цена","Процент","Время","Статус"}, manager) {
            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if(manager != null && columnIndex>=0 && columnIndex<=4) {
                    List<UrlWorker> workers = manager.getUrlWorkers();
                    if(rowIndex < workers.size()) {
                        UrlWorker worker = workers.get(rowIndex);
                        if(columnIndex == 0) return worker.getUrl();
                        if(columnIndex == 1) return String.valueOf(worker.getMinCost()) + "-" + String.valueOf(worker.getMaxCost());
                        if(columnIndex == 2) return String.valueOf(worker.getPercent());
                        if(columnIndex == 3) return String.valueOf(worker.getMinSecTime()) + "-" + String.valueOf(worker.getMaxSecTime());
                        if(columnIndex == 4) return worker.isSeniorStatus()?"основной":"вторичный";
                    }
                }
                return null;
            }
        });
        JScrollPane tableScrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        table.setRowSelectionAllowed(true);
        tablePane.add(tableScrollPane);


        timerTable = new JTable(new WorkersTableModel(new String[]{"Отсчет ответа"}, manager) {
            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if(manager != null && columnIndex==0) {
                    List<UrlWorker> workers = manager.getUrlWorkers();
                    if(rowIndex < workers.size()) {
                        UrlWorker worker = workers.get(rowIndex);
                        Date date = manager.getTimerLastAnswer().get(worker);
                        if(date != null) {
                            return ((new Date()).getTime()-date.getTime())/1000;
                        }
                    }
                }
                return null;
            }
        });
        tableScrollPane = new JScrollPane(timerTable);
        timerTable.setFillsViewportHeight(true);
        timerTable.setRowSelectionAllowed(false);
        tableScrollPane.setPreferredSize(new Dimension(100,Short.MAX_VALUE));
        tableScrollPane.setMaximumSize(new Dimension(100,Short.MAX_VALUE));
        tablePane.add(tableScrollPane);


        JPanel tableButtonsPane = new JPanel();
        tableButtonsPane.setLayout(new FlowLayout());
        addButton = new JButton("Добавить");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InputWorkerDialog addDialog = new InputWorkerDialog(ConfigurationPage.this,"Добавить",null) {
                    @Override
                    public void processDialogOK() {
                        UrlWorker dialogWorker = getDialogWorker();
                        if(dialogWorker != null) {
                            if(dialogWorker.isSeniorStatus()) {
                                manager.addSeniorUrlWorker(dialogWorker);
                            } else {
                                manager.addVassalUrlWorker(dialogWorker);
                            }
                            table.repaint();
                        }
                    }
                };
                addDialog.setVisible(true);
            }
        });
        tableButtonsPane.add(addButton);
        removeButton = new JButton("Удалить");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rowIndex = table.getSelectedRow();
                if(manager != null && rowIndex>=0) {
                    UrlWorker removedWorker = new UrlWorker();
                    removedWorker.setUrl((String) table.getModel().getValueAt(rowIndex,0));
                    String cost = (String) table.getModel().getValueAt(rowIndex, 1);
                    removedWorker.setMinCost(Integer.parseInt(cost.substring(0,cost.indexOf('-'))));
                    removedWorker.setMaxCost(Integer.parseInt(cost.substring(cost.indexOf('-')+1)));
                    removedWorker.setPercent(Integer.parseInt((String) table.getModel().getValueAt(rowIndex, 2)));
                    String time = (String) table.getModel().getValueAt(rowIndex, 3);
                    removedWorker.setMinSecTime(Integer.parseInt(time.substring(0,time.indexOf('-'))));
                    removedWorker.setMaxSecTime(Integer.parseInt(time.substring(time.indexOf('-')+1)));
                    String status = (String) table.getModel().getValueAt(rowIndex, 4);
                    if("основной".equals(status)) {
                        removedWorker.setSeniorStatus(true);
                    } else {
                        removedWorker.setSeniorStatus(false);
                    }
                    manager.removeUrlWorker(removedWorker);
                    table.repaint();
                }
            }
        });
        tableButtonsPane.add(removeButton);
        editButton = new JButton("Редактировать");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rowIndex = table.getSelectedRow();
                if(manager != null && rowIndex>=0) {
                    final UrlWorker editedWorker = new UrlWorker();
                    editedWorker.setUrl((String) table.getModel().getValueAt(rowIndex,0));
                    String cost = (String) table.getModel().getValueAt(rowIndex, 1);
                    editedWorker.setMinCost(Integer.parseInt(cost.substring(0,cost.indexOf('-'))));
                    editedWorker.setMaxCost(Integer.parseInt(cost.substring(cost.indexOf('-')+1)));
                    editedWorker.setPercent(Integer.parseInt((String) table.getModel().getValueAt(rowIndex, 2)));
                    String time = (String) table.getModel().getValueAt(rowIndex, 3);
                    editedWorker.setMinSecTime(Integer.parseInt(time.substring(0,time.indexOf('-'))));
                    editedWorker.setMaxSecTime(Integer.parseInt(time.substring(time.indexOf('-')+1)));
                    String status = (String) table.getModel().getValueAt(rowIndex, 4);
                    if("основной".equals(status)) {
                        editedWorker.setSeniorStatus(true);
                    } else {
                        editedWorker.setSeniorStatus(false);
                    }
                    InputWorkerDialog editDialog = new InputWorkerDialog(ConfigurationPage.this,"Редактировать",editedWorker) {
                        @Override
                        public void processDialogOK() {
                            UrlWorker dialogWorker = getDialogWorker();
                            if(dialogWorker != null) {
                                manager.removeUrlWorker(editedWorker);
                                if(dialogWorker.isSeniorStatus()) {
                                    manager.addSeniorUrlWorker(dialogWorker);
                                } else {
                                    manager.addVassalUrlWorker(dialogWorker);
                                }
                                table.repaint();
                            }
                        }
                    };
                    editDialog.setVisible(true);
                }
            }
        });
        tableButtonsPane.add(editButton);
        pauseButton = new JButton("Старт");
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if("Пауза".equals(pauseButton.getText())) {
                    logger.debug("PAUSE BUTTON");
                    pauseButton.setText("Старт");
                    chooseACTFileButton.setEnabled(true);
                    table.setEnabled(true);
                    addButton.setEnabled(true);
                    removeButton.setEnabled(true);
                    editButton.setEnabled(true);
                    timerField.setEnabled(true);
                    manager.pauseProcessingAutoFilling();
                    manager.pauseProcessingExecutedAnswer();
                    manager.pauseTaskSender();
                } else {
                    logger.debug("START BUTTON");
                    pauseButton.setText("Пауза");
                    chooseACTFileButton.setEnabled(false);
                    table.setEnabled(false);
                    addButton.setEnabled(false);
                    removeButton.setEnabled(false);
                    editButton.setEnabled(false);
                    manager.setRepeatDetectorSec(Integer.parseInt(timerField.getText()));
                    timerField.setEnabled(false);
                    manager.startProcessingAutoFilling();
                    manager.startProcessingExecutedAnswer();
                    manager.startTaskSender();
                }
            }
        });
        tableButtonsPane.add(pauseButton);

        tableButtonsPane.add(new JLabel("Время обновления:"));

        timerField = new JTextField(Integer.toString(manager.getRepeatDetectorSec()));
        final PlainDocument doc = (PlainDocument) timerField.getDocument();
        doc.setDocumentFilter(new IntFilter());
        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    int sec  = Integer.parseInt(doc.getText(0,doc.getLength()));
                    manager.setRepeatDetectorSec(sec);
                } catch (Exception ex) {
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    int sec  = Integer.parseInt(doc.getText(0,doc.getLength()));
                    manager.setRepeatDetectorSec(sec);
                } catch (Exception ex) {
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    int sec  = Integer.parseInt(doc.getText(0,doc.getLength()));
                    manager.setRepeatDetectorSec(sec);
                } catch (Exception ex) {
                }
            }
        });
        tableButtonsPane.add(timerField);

        chooseACTFileButton.setEnabled(true);
        table.setEnabled(true);
        addButton.setEnabled(true);
        removeButton.setEnabled(true);
        editButton.setEnabled(true);
        timerField.setEnabled(true);

        Container contentPane = getContentPane();
        contentPane.add(actPane, BorderLayout.PAGE_START);
        contentPane.add(tablePane, BorderLayout.CENTER);
        contentPane.add(tableButtonsPane, BorderLayout.PAGE_END);


        setVisible(true);
        setSize(550, 300);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                ThreadManager.INSTANCE.shutdown();
                System.exit(0);
            }
        });
    }

    private void initTray() {
        try{
            logger.debug("setting look and feel");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){
            logger.debug("Unable to set LookAndFeel");
        }
        if(SystemTray.isSupported()){
            logger.debug("system tray supported");
            tray=SystemTray.getSystemTray();

            Image image=Toolkit.getDefaultToolkit().getImage(getClass().getResource("../trayIcon.gif"));

            ActionListener exitListener=new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //Exiting....
                    ThreadManager.INSTANCE.shutdown();
                    System.exit(0);
                }
            };
            PopupMenu popup=new PopupMenu();
            MenuItem defaultItem=new MenuItem("Exit");
            defaultItem.addActionListener(exitListener);
            popup.add(defaultItem);
            defaultItem=new MenuItem("Open");
            defaultItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(true);
                    setExtendedState(JFrame.NORMAL);
                }
            });
            popup.add(defaultItem);
            trayIcon=new TrayIcon(image, "Staff Worker in tray", popup);
            trayIcon.setImageAutoSize(true);
        }else{
            logger.error("System tray not supported.");
        }
        addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                if(e.getNewState()==ICONIFIED){
                    try {
                        tray.add(trayIcon);
                        setVisible(false);
                    } catch (AWTException ex) {
                        logger.error("Unable to add to tray.", ex);
                    }
                }
                if(e.getNewState()==7){
                    try{
                        tray.add(trayIcon);
                        setVisible(false);
                    }catch(AWTException ex){
                        logger.error("Unable to add to system tray.", ex);
                    }
                }
                if(e.getNewState()==MAXIMIZED_BOTH){
                    tray.remove(trayIcon);
                    setVisible(true);
                    logger.debug("Tray icon removed.");
                }
                if(e.getNewState()==NORMAL){
                    tray.remove(trayIcon);
                    setVisible(true);
                    logger.debug("Tray icon removed.");
                }
            }
        });
        setIconImage(Toolkit.getDefaultToolkit().getImage("trayIcon.png"));
    }
}
