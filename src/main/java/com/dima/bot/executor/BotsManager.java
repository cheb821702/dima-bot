package com.dima.bot.executor;

import com.dima.bot.executor.model.AutoFillEntity;
import com.dima.bot.settings.SettingsKeeper;
import com.dima.bot.settings.model.UrlWorker;
import com.dima.bot.util.URLUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: CHEB
 */
public class BotsManager implements SettingsKeeper{

    private SettingsKeeper keeper;
    private List<AutoFillEntity> autoFillEntities;

    private boolean processingAutofillingEnable = true;

    public BotsManager(SettingsKeeper keeper) {
        this.keeper = keeper;
        ExcelAutoFillUtil autoFillUtil = new ExcelAutoFillUtil();
        this.autoFillEntities = autoFillUtil.getEntities(getAutoCompleteTemplatesPath());


    }

    public void startAutoFillDetector() {
        if(processingAutofillingEnable) {
            processingAutofillingEnable = false;
            AutoFillDetector detector = new AutoFillDetector(this);
            ExecutorService autofillEx = Executors.newFixedThreadPool(1);
            autofillEx.execute(detector);
        }
    }

    public void finishAutoFillDetector() {
        processingAutofillingEnable = true;
    }

    public SettingsKeeper getKeeper() {
        return keeper;
    }

    public List<AutoFillEntity> getAutoFillEntities() {
        return autoFillEntities;
    }

    public AdvertisementExtractor factoryAdvertisementExtractor(String url) {
        if(url != null) {
            if(url.startsWith(FerioAdvertisementExtractor.SITE_URL)) {
                return new FerioAdvertisementExtractor();
            }
        }
        return null;
    }

    @Override
    public String getAutoCompleteTemplatesPath() {
        if(keeper != null) {
            return keeper.getAutoCompleteTemplatesPath();
        }
        return null;
    }

    @Override
    public String setAutoCompleteTemplatesPath(String path) {
        if(keeper != null) {
            String resPath = keeper.setAutoCompleteTemplatesPath(path);
            ExcelAutoFillUtil autoFillUtil = new ExcelAutoFillUtil();
            this.autoFillEntities = autoFillUtil.getEntities(getAutoCompleteTemplatesPath());
            return resPath;
        }
        return null;
    }

    @Override
    public List<UrlWorker> getUrlWorkers() {
        if(keeper != null) {
            return keeper.getUrlWorkers();
        }
        return new ArrayList<UrlWorker>();
    }

    @Override
    public String addSeniorUrlWorker(UrlWorker urlWorker) {
        if(keeper != null) {
            return keeper.addSeniorUrlWorker(urlWorker);
        }
        return null;
    }

    @Override
    public String addVassalUrlWorker(UrlWorker urlWorker) {
        if(keeper != null) {
            return keeper.addVassalUrlWorker(urlWorker);
        }
        return null;
    }

    @Override
    public String removeUrlWorker(UrlWorker urlWorker) {
        if(keeper != null) {
            return keeper.removeUrlWorker(urlWorker);
        }
        return null;
    }
}
