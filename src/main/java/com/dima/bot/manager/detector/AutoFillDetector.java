package com.dima.bot.manager.detector;

import com.dima.bot.manager.executor.AdvertisementExtractor;
import com.dima.bot.manager.model.Advertisement;
import com.dima.bot.manager.model.NewAdvertisement;
import com.dima.bot.manager.model.AutoFillEntity;
import com.dima.bot.manager.BotsManager;
import com.dima.bot.settings.model.UrlWorker;
import com.dima.bot.util.URLUtil;

import java.util.Date;
import java.util.Map;

/**
 * User: CHEB
 */
public class AutoFillDetector implements Runnable{

    private BotsManager manager;
    private Date lastDate = null;

    public AutoFillDetector(BotsManager manager) {
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
                                NewAdvertisement autoFillAdvertisement = null;
                                for(AutoFillEntity autoFillEntity : manager.getAutoFillEntities()) {
                                    if(checkAuto(advertisement, autoFillEntity)) {
                                        for(Map.Entry<String,String> detail : advertisement.getDetails().entrySet()) {
                                            if(checkDetail(detail.getKey().trim(), autoFillEntity.getDetail().trim())) {
                                                if(autoFillAdvertisement == null) {
                                                    autoFillAdvertisement = new NewAdvertisement(advertisement);
                                                }
                                                autoFillAdvertisement.getAutoFillDetailsMap().put(detail.getKey(), autoFillEntity);
                                            }
                                        }
                                    }
                                }
                                if(autoFillAdvertisement != null) {
                                    manager.getTaskTracker().addAutoFillTask(worker,autoFillAdvertisement);
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

    private boolean checkDetail(String  advertisementDetail, String  autoFillEntityDetail) {
        boolean confirmDetail = true;
        advertisementDetail = advertisementDetail.toLowerCase();
        for(String wordAutoFill : autoFillEntityDetail.toLowerCase().split("\\s+")) {
            if(!advertisementDetail.contains(wordAutoFill)) {
                confirmDetail = false;
            }
//            boolean confirmWord = false;
//            for(String wordAdvertisement : advertisementDetail.split("\\s+")) {
//                if(wordAdvertisement.equals(wordAutoFill)) {
//                    confirmWord = true;
//                }
//            }
//            if(!confirmWord) {
//                confirmDetail = false;
//            }
        }
        return confirmDetail;
    }

    private boolean checkAuto(Advertisement advertisement, AutoFillEntity autoFillEntity) {
        if(autoFillEntity.getStartYear() == null || autoFillEntity.getStartYear() <= advertisement.getAutoYear() ) {
            if(autoFillEntity.getStopYear() == null || autoFillEntity.getStopYear() >= advertisement.getAutoYear() ) {
                String auto = advertisement.getAuto().trim().toLowerCase();
                String series  = autoFillEntity.getSeries().toLowerCase();
                if(auto.startsWith("bmw")) {
                    auto = auto.substring(3).trim();
                }
                if(auto.startsWith(series)) {
                    auto = auto.substring(series.length()).trim();
                    if(auto.contains(autoFillEntity.getCarcass().toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
