package com.dima.bot.executor;

import com.dima.bot.executor.model.Advertisement;
import com.dima.bot.executor.model.AutoFillAdvertisement;
import com.dima.bot.executor.model.AutoFillEntity;
import com.dima.bot.settings.model.UrlWorker;
import com.dima.bot.util.URLUtil;

import java.util.Date;
import java.util.Map;

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
        for(UrlWorker worker : manager.getKeeper().getUrlWorkers()) {
            AdvertisementExtractor extractor = manager.factoryAdvertisementExtractor(worker.getUrl());
            if(extractor != null) {
                for(int i = 1; i <= extractor.getMaxNPage(); i++) {
                    boolean isBreak = false;
                    for(Advertisement advertisement : extractor.extract(URLUtil.getUrlForPage(worker.getUrl(), i))) {
                        if(checkDate(advertisement.getDate())) {
                            if(!advertisement.isPerformed()) {

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
        }
        manager.setDateOfLastAutoFill(lastDate);
        manager.finishAutoFillDetector();
    }

    private boolean checkDate(Date date) {
        if (date != null) {
            if(lastDate == null || date.after(lastDate)) {
                lastDate = date;
            }
        }
        return manager.isAfterDateLastAutoFill(date);
    }
}
