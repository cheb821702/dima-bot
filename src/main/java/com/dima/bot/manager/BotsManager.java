package com.dima.bot.manager;

import com.dima.bot.manager.detector.AutoFillDetector;
import com.dima.bot.manager.detector.ExecutedAdvertisementDetector;
import com.dima.bot.manager.executor.*;
import com.dima.bot.manager.model.AutoFillEntity;
import com.dima.bot.manager.util.ExcelAutoFillUtil;
import com.dima.bot.manager.util.ThreadManager;
import com.dima.bot.settings.SettingsKeeper;
import com.dima.bot.settings.model.UrlWorker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: CHEB
 */
public class BotsManager implements SettingsKeeper{

    private SettingsKeeper keeper;
    private TaskTracker taskTracker;
    private List<AutoFillEntity> autoFillEntities;
    private Date dateOfLastAutoFill = null;
    private boolean processingAutoFillingEnable = true;
    private Date dateOfLastExecutedAnswer = null;
    private boolean processingExecutedAnswerEnable = true;


    public BotsManager(SettingsKeeper keeper) {
        this.keeper = keeper;
        this.taskTracker = new TaskTracker(keeper.getUrlWorkers());

        ExcelAutoFillUtil autoFillUtil = new ExcelAutoFillUtil();
        this.autoFillEntities = autoFillUtil.getEntities(getAutoCompleteTemplatesPath());

    }

    public boolean isAfterDateLastExecutedAnswer(Date date) {
        if (date != null) {
            if(dateOfLastExecutedAnswer == null || date.after(dateOfLastExecutedAnswer)) {
                return true;
            }
        }
        return false;
    }

    public void setDateOfLastExecutedAnswer(Date dateOfLastExecutedAnswer) {
        if(dateOfLastExecutedAnswer != null) {
            this.dateOfLastExecutedAnswer = dateOfLastExecutedAnswer;
        }
    }

    public void startExecutedAdvertisementDetector() {
        if(processingExecutedAnswerEnable) {
            processingExecutedAnswerEnable = false;
            ExecutedAdvertisementDetector detector = new ExecutedAdvertisementDetector(this);
            ThreadManager.INSTANCE.execute(detector);
//            ExecutorService autoFillEx = Executors.newFixedThreadPool(1);
//            autoFillEx.execute(detector);
        }
    }

    public void finishExecutedAdvertisementDetector() {
        processingExecutedAnswerEnable = true;
    }

    public boolean isAfterDateLastAutoFill(Date date) {
        if (date != null) {
            if(dateOfLastAutoFill == null || date.after(dateOfLastAutoFill)) {
                return true;
            }
        }
        return false;
    }

    public void setDateOfLastAutoFill(Date dateOfLastAutoFill) {
        if(dateOfLastAutoFill != null) {
            this.dateOfLastAutoFill = dateOfLastAutoFill;
        }
    }

    public void startAutoFillDetector() {
        if(processingAutoFillingEnable) {
            processingAutoFillingEnable = false;
            AutoFillDetector detector = new AutoFillDetector(this);
            ThreadManager.INSTANCE.execute(detector);
//            ExecutorService autoFillEx = Executors.newFixedThreadPool(1);
//            autoFillEx.execute(detector);
        }
    }

    public void finishAutoFillDetector() {
        processingAutoFillingEnable = true;
    }

    public SettingsKeeper getKeeper() {
        return keeper;
    }

    public TaskTracker getTaskTracker() {
        return taskTracker;
    }

    public List<AutoFillEntity> getAutoFillEntities() {
        return autoFillEntities;
    }

    public AdvertisementExtractor factoryAdvertisementExtractor(String url) {
        if(url != null) {
            if(url.startsWith(FerioAdvertisementExtractor.SITE_URL)) {
                return new FerioAdvertisementExtractor();
            } else if(url.startsWith(FerioNewAdvertisementExtractor.SITE_URL)) {
                return new FerioNewAdvertisementExtractor();
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
