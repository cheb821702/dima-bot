package com.dima.bot.manager.detector;

import com.dima.bot.manager.executor.AdvertisementExtractor;
import com.dima.bot.manager.model.Advertisement;
import com.dima.bot.manager.model.DetectorOfAdvertisement;
import com.dima.bot.manager.model.NewAdvertisement;
import com.dima.bot.manager.model.AutoFillEntity;
import com.dima.bot.manager.BotsManager;
import com.dima.bot.settings.model.UrlWorker;
import com.dima.bot.util.URLUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Map;

/**
 * User: CHEB
 */
public class AutoFillDetector implements Runnable{

    private BotsManager manager;
    private Date lastDate = null;

    final Logger logger = LogManager.getLogger("debugLogger");

    public AutoFillDetector(BotsManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        while(!manager.isPauseProcessingAutoFilling()) {
            for(int workindex = 0; workindex < manager.getKeeper().getUrlWorkers().size() && !manager.isPauseProcessingAutoFilling(); workindex++  ) {
                UrlWorker worker = manager.getKeeper().getUrlWorkers().get(workindex);
                AdvertisementExtractor extractor = manager.factoryAdvertisementExtractor(worker.getUrl());
                if(extractor != null) {
                    logger.debug("Обрабатывается worker (AutoFillDetector):" + worker.getUrl());
                    for(int i = 1; i <= extractor.getMaxNPage(); i++) {
                        boolean isBreak = false;
                        for(Advertisement advertisement : extractor.extract(URLUtil.getUrlForPage(worker.getUrl(), i))) {
                            if(checkDate(worker, advertisement.getDate())) {
                                if(!advertisement.isPerformed()) {                // не обрабатывать уже отвеченные
                                    NewAdvertisement autoFillAdvertisement = getNewAdvertisement(advertisement);
                                    if(autoFillAdvertisement != null) {
                                        autoFillAdvertisement.setSignOfDetector(DetectorOfAdvertisement.AUTO_FILL);
                                        manager.getTaskTracker().addFirstAutoFillTask(worker, autoFillAdvertisement);
                                        logger.debug("ADD to TASK TRACKER(AutoFillDetector):" + autoFillAdvertisement.toString());
                                    }
                                } else {
                                    logger.debug("Уже отвеченные(AutoFillDetector):" + advertisement.toString());
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
                manager.setDateOfLastAutoFill(worker, lastDate);
            }
            try {
                Thread.sleep(manager.getRepeatDetectorSec()*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        manager.finishAutoFillDetector();
    }

    public NewAdvertisement getNewAdvertisement(Advertisement advertisement) {
        NewAdvertisement autoFillAdvertisement = null;
        AutoFillEntity checkedAutoFillEntity = null;
        String detailKey = null;
        for(AutoFillEntity autoFillEntity : manager.getAutoFillEntities()) {
            if(checkAuto(advertisement, autoFillEntity)) {
                for(Map.Entry<String,String> detail : advertisement.getDetails().entrySet()) {
                    if(checkDetail(detail.getKey().trim(), autoFillEntity.getDetail().trim())) {
                        detailKey = detail.getKey();
                        checkedAutoFillEntity = autoFillEntity;
                    }
                }
            }
        }
        if(detailKey != null && checkedAutoFillEntity != null) {
            if(autoFillAdvertisement == null) {
                autoFillAdvertisement = new NewAdvertisement(advertisement);
            }
            autoFillAdvertisement.getAutoFillDetailsMap().put(detailKey, checkedAutoFillEntity);
        }
        return  autoFillAdvertisement;
    }

    private boolean checkDate(UrlWorker worker, Date date) {
        if (date != null) {
            if(lastDate == null || date.after(lastDate)) {
                lastDate = date;
            }
        }
        return manager.isAfterDateLastAutoFill(worker, date);
    }

    public static boolean checkDetail(String  advertisementDetail, String  autoFillEntityDetail) {
        if(advertisementDetail != null && advertisementDetail.trim().length() > autoFillEntityDetail.trim().length()) {
            return false;
        }

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

    public static boolean checkAuto(Advertisement advertisement, AutoFillEntity autoFillEntity) {
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
