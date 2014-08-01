package com.dima.bot.manager;

import com.dima.bot.manager.detector.AutoFillDetector;
import com.dima.bot.manager.detector.ExecutedAdvertisementDetector;
import com.dima.bot.manager.executor.*;
import com.dima.bot.manager.model.AutoFillEntity;
import com.dima.bot.manager.util.ExcelAutoFillUtil;
import com.dima.bot.manager.util.ThreadManager;
import com.dima.bot.settings.SettingsKeeper;
import com.dima.bot.settings.model.UrlWorker;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: CHEB
 */
public class BotsManager implements SettingsKeeper{

    private SettingsKeeper keeper;
    private TaskTracker taskTracker;
    private List<AutoFillEntity> autoFillEntities;
    private Map<UrlWorker, Date> dateOfLastAutoFill = new HashMap<UrlWorker,Date>();
    private boolean processingAutoFillingEnable = true;
    private boolean pauseProcessingAutoFilling = false;
    private Map<UrlWorker, Date> dateOfLastExecutedAnswer = new HashMap<UrlWorker,Date>();
    private boolean processingExecutedAnswerEnable = true;
    private boolean pauseProcessingExecutedAnswer = false;
    private boolean pauseTaskSender = true;
    private int repeatDetectorSec = 1200;


    public BotsManager(SettingsKeeper keeper) {
        this.keeper = keeper;
        this.taskTracker = new TaskTracker(keeper.getUrlWorkers());

        ExcelAutoFillUtil autoFillUtil = new ExcelAutoFillUtil();
        this.autoFillEntities = autoFillUtil.getEntities(getAutoCompleteTemplatesPath());
    }

    public boolean isPauseTaskSender() {
        return pauseTaskSender;
    }

    public void setPauseTaskSender(boolean pauseTaskSender) {
        this.pauseTaskSender = pauseTaskSender;
    }

    public void startTaskSender() {
        if(isPauseTaskSender()) {
            setPauseTaskSender(false);
            for(UrlWorker worker : this.keeper.getUrlWorkers()) {
                TaskSender sender = new TaskSender(worker,this);
                ThreadManager.INSTANCE.execute(sender);
            }
        }
    }

    public void pauseTaskSender() {
        setPauseTaskSender(true);
    }

    public boolean isAfterDateLastExecutedAnswer(UrlWorker worker, Date date) {
        if (worker != null && date != null) {
            if(!dateOfLastExecutedAnswer.keySet().contains(worker) || dateOfLastExecutedAnswer.get(worker) == null || date.after(dateOfLastExecutedAnswer.get(worker))) {
                return true;
            }
        }
        return false;
    }

    public void setDateOfLastExecutedAnswer(UrlWorker worker, Date dateOfLastExecutedAnswer) {
        if(worker != null && dateOfLastExecutedAnswer != null) {
            this.dateOfLastExecutedAnswer.put(worker, dateOfLastExecutedAnswer);
        }
    }

    public boolean isPauseProcessingExecutedAnswer() {
        return pauseProcessingExecutedAnswer;
    }

    public void startProcessingExecutedAnswer() {
        this.pauseProcessingExecutedAnswer = false;
    }

    public void pauseProcessingExecutedAnswer() {
        this.pauseProcessingExecutedAnswer = true;
    }

    public void runExecutedAdvertisementDetector() {
        if(!pauseProcessingExecutedAnswer && processingExecutedAnswerEnable) {
            processingExecutedAnswerEnable = false;
            ExecutedAdvertisementDetector detector = new ExecutedAdvertisementDetector(this);
            ThreadManager.INSTANCE.execute(detector);
        }
    }

    public void finishExecutedAdvertisementDetector() {
        processingExecutedAnswerEnable = true;
    }

    public boolean isAfterDateLastAutoFill(UrlWorker worker, Date date) {
        if (worker != null && date != null) {
            if(!dateOfLastAutoFill.keySet().contains(worker) || dateOfLastAutoFill.get(worker) == null || date.after(dateOfLastAutoFill.get(worker))) {
                return true;
            }
        }
        return false;
    }

    public void setDateOfLastAutoFill(UrlWorker worker,Date dateOfLastAutoFill) {
        if(worker != null && dateOfLastAutoFill != null) {
            this.dateOfLastAutoFill.put(worker, dateOfLastAutoFill);
        }
    }

    public boolean isPauseProcessingAutoFilling() {
        return pauseProcessingAutoFilling;
    }

    public void startProcessingAutoFilling() {
        this.pauseProcessingAutoFilling = false;
    }

    public void pauseProcessingAutoFilling() {
        this.pauseProcessingAutoFilling = true;
    }

    public void runAutoFillDetector() {
        if(!pauseProcessingAutoFilling && processingAutoFillingEnable) {
            processingAutoFillingEnable = false;
            AutoFillDetector detector = new AutoFillDetector(this);
            ThreadManager.INSTANCE.execute(detector);
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

    public int getRepeatDetectorSec() {
        return repeatDetectorSec;
    }

    public void setRepeatDetectorSec(int repeatDetectorSec) {
        this.repeatDetectorSec = repeatDetectorSec;
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
            String res = keeper.removeUrlWorker(urlWorker);
            if(res != null) {
                taskTracker.removeUrlWorkerTrack(urlWorker);
            }
            return res;
        }
        return null;
    }
}
