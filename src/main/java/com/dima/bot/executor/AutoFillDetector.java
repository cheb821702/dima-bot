package com.dima.bot.executor;

import com.dima.bot.executor.model.AutoFillEntity;
import com.dima.bot.settings.model.UrlWorker;

import java.util.List;

/**
 * User: CHEB
 */
public class AutoFillDetector implements Runnable{

    private BotsManager manager;

    public AutoFillDetector(BotsManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        manager.getAutoFillEntities();
        for(UrlWorker worker : manager.getKeeper().getUrlWorkers()) {
            AdvertisementExtractor extractor = manager.factoryAdvertisementExtractor(worker.getUrl());
            if(extractor != null) {

            }
        }
        manager.finishAutoFillDetector();
    }
}
