package com.dima.bot.settings;

import com.dima.bot.settings.model.UrlWorker;

import java.util.List;

/**
 * User: CHEB
 */
public interface SettingsKeeper {

    public String addSeniorUrlWorker(UrlWorker urlWorker);
    public String addVassalUrlWorker(UrlWorker urlWorker);
    public String removeUrlWorker(UrlWorker urlWorker);
    public List<UrlWorker> getUrlWorkers();
    public String getAutoCompleteTemplatesPath();
    public String setAutoCompleteTemplatesPath(String path);
}
