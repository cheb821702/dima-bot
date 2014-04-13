package com.dima.bot.ui;

import com.dima.bot.settings.model.UrlWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.dima.bot.util.NumericTextField;

/**
 * User: CHEB
 */
public abstract class InputWorkerDialog extends JDialog{

    private UrlWorker inputWorker = null;
    private TextField urlField = new TextField();
    private NumericTextField minCostField = new NumericTextField();
    private NumericTextField maxCostField = new NumericTextField();
    private NumericTextField percentField = new NumericTextField();
    private NumericTextField minTimeField = new NumericTextField();
    private NumericTextField maxTimeField = new NumericTextField();
    private JCheckBox statusCheckBox = new JCheckBox();

    public InputWorkerDialog(Frame owner, String title, UrlWorker worker) {
        super(owner, true);

        inputWorker = worker;

        setTitle(title);
        UI();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setSize(500, 120);
    }

    private void UI() {

        JPanel urlPane = new JPanel();
        urlPane.setLayout(new BoxLayout(urlPane,BoxLayout.LINE_AXIS));

        urlPane.add(new JLabel("URL:"));
        urlPane.add(urlField);

        JPanel inputPane = new JPanel();
        inputPane.setLayout(new BoxLayout(inputPane,BoxLayout.LINE_AXIS));

        inputPane.add(new JLabel("стоимость мин.:"));
        inputPane.add(minCostField);

        inputPane.add(new JLabel(" макс.:"));
        inputPane.add(maxCostField);

        inputPane.add(new JLabel(" процент:"));
        inputPane.add(percentField);

        inputPane.add(new JLabel("время мин.:"));
        inputPane.add(minTimeField);

        inputPane.add(new JLabel(" макс.:"));
        inputPane.add(maxTimeField);

        inputPane.add(new JLabel(" основной:"));
        inputPane.add(statusCheckBox);

        if(inputWorker != null) {
            if(inputWorker.getUrl() != null) {
                urlField.setText(inputWorker.getUrl());
            }
            minCostField.setText(String.valueOf(inputWorker.getMinCost()));
            maxCostField.setText(String.valueOf(inputWorker.getMaxCost()));
            percentField.setText(String.valueOf(inputWorker.getPercent()));
            minTimeField.setText(String.valueOf(inputWorker.getMinSecTime()));
            maxTimeField.setText(String.valueOf(inputWorker.getMaxSecTime()));
            statusCheckBox.setSelected(inputWorker.isSeniorStatus());
        } else {
            minCostField.setText("0");
            maxCostField.setText("0");
            percentField.setText("0");
            minTimeField.setText("0");
            maxTimeField.setText("30");
        }


        JPanel buttonsPane = new JPanel();
        buttonsPane.setLayout(new FlowLayout());
        JButton addButton = new JButton("Ок");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processDialogOK();
                InputWorkerDialog.this.dispose();
            }
        });
        buttonsPane.add(addButton);
        JButton cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InputWorkerDialog.this.dispose();
            }
        });
        buttonsPane.add(cancelButton);

        Container contentPane = getContentPane();
        contentPane.add(urlPane, BorderLayout.PAGE_START);
        contentPane.add(inputPane, BorderLayout.CENTER);
        contentPane.add(buttonsPane, BorderLayout.PAGE_END);
    }

    public UrlWorker getDialogWorker() {
        UrlWorker dialogWorker = new UrlWorker();
        dialogWorker.setUrl(urlField.getText());
        if(minCostField.getText() == null || minCostField.getText().isEmpty()) return null;
        dialogWorker.setMinCost(Integer.parseInt(minCostField.getText()));
        if(maxCostField.getText() == null || maxCostField.getText().isEmpty()) return null;
        dialogWorker.setMaxCost(Integer.parseInt(maxCostField.getText()));
        if(percentField.getText() == null || percentField.getText().isEmpty()) return null;
        dialogWorker.setPercent(Integer.parseInt(percentField.getText()));
        if(minTimeField.getText() == null || minTimeField.getText().isEmpty()) return null;
        dialogWorker.setMinSecTime(Integer.parseInt(minTimeField.getText()));
        if(maxTimeField.getText() == null || maxTimeField.getText().isEmpty()) return null;
        dialogWorker.setMaxSecTime(Integer.parseInt(maxTimeField.getText()));
        dialogWorker.setSeniorStatus(statusCheckBox.isSelected());
        return dialogWorker;
    }


    public abstract void processDialogOK();
}
