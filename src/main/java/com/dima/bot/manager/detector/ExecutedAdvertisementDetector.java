package com.dima.bot.manager.detector;

import com.dima.bot.manager.executor.AdvertisementExtractor;
import com.dima.bot.manager.model.Advertisement;
import com.dima.bot.manager.BotsManager;
import com.dima.bot.manager.model.NewAdvertisement;
import com.dima.bot.settings.model.UrlWorker;
import com.dima.bot.util.URLUtil;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ShemiareiD
 * Date: 5/2/14
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExecutedAdvertisementDetector implements Runnable {

    private BotsManager manager;
    private Date lastDate = null;

    public ExecutedAdvertisementDetector(BotsManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        while(!manager.isPauseProcessingExecutedAnswer()) {
            for(int workindex = 0; workindex < manager.getKeeper().getUrlWorkers().size() && !manager.isPauseProcessingExecutedAnswer(); workindex++  ) {
                UrlWorker worker = manager.getKeeper().getUrlWorkers().get(workindex);
                AdvertisementExtractor extractor = manager.factoryAdvertisementExtractor(worker.getUrl());
                if(extractor != null) {
                    for(int i = 1; i <= extractor.getMaxNPage(); i++) {
                        boolean isBreak = false;
                        for(Advertisement advertisement : extractor.extract(URLUtil.getUrlForPage(worker.getUrl(), i))) {
                            if(checkDate(worker, advertisement.getDate())) {
                                if(advertisement.isPerformed()) {
                                    AdvertisementExtractor answerExtractor = manager.factoryAdvertisementExtractor(advertisement.getOpenURL());
                                    if(answerExtractor != null) {
                                        List<Advertisement> answers = answerExtractor.extract (advertisement.getOpenURL());
                                        for(Advertisement temp : answers) {
                                            temp.setDate(advertisement.getDate());
                                            temp.setNumber(advertisement.getNumber());
                                            temp.setAuto(advertisement.getAuto());
                                            temp.setAutoYear(advertisement.getAutoYear());
                                            temp.setOpenURL(advertisement.getOpenURL());
                                            temp.setPerformed(advertisement.isPerformed());
                                            manager.getTaskTracker().addAutoFillTask(worker, (NewAdvertisement) temp);
                                        }
                                    }
                                }
                            } else {
                                isBreak = true;
                            }
                        }
                        if(isBreak) {
                            break;
                        }
                    }
                }
                manager.setDateOfLastExecutedAnswer(worker, lastDate);
            }
            try {
                Thread.sleep(manager.getRepeatDetectorSec());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        manager.finishExecutedAdvertisementDetector();
    }

    private boolean checkDate(UrlWorker worker, Date date) {
        if (date != null) {
            if(lastDate == null || date.after(lastDate)) {
                lastDate = date;
            }
        }
        return manager.isAfterDateLastExecutedAnswer(worker, date);
    }
}
