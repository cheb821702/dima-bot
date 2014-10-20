package com.dima.bot.util;

import com.dima.bot.manager.BotsManager;
import com.dima.bot.settings.model.UrlWorker;

import javax.swing.table.AbstractTableModel;

/**
 * User: CHEB
 */
public abstract class WorkersTableModel extends AbstractTableModel {

    private String[] columnNames = {};
    private BotsManager manager;

    public WorkersTableModel(String[] columnNames, BotsManager manager) {
        this.columnNames = columnNames;
        this.manager = manager;
    }

    @Override
    public int getRowCount() {
        if(manager != null) {
            return manager.getUrlWorkers().size();
        }
        return 0;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Class getColumnClass(int c) {
        return String.class;
    }
}
