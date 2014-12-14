package com.dima.bot.manager.util;

import com.dima.bot.ui.ConfigurationPage;

import javax.swing.*;

/**
 * User: CHEB
 */
public enum ErrorGUIHandler {

    INSTANCE;

    ConfigurationPage page;

    public void setConfigurationPage(ConfigurationPage page) {
        this.page = page;
    }

    public void handleError(String msg) {
        if(page != null) {
            page.showMessageDialog(msg);
        }
    }
}
