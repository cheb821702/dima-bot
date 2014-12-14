package com.dima.bot.manager.detector;

import com.dima.bot.manager.executor.AdvertisementExtractor;
import com.dima.bot.manager.model.Advertisement;
import com.dima.bot.manager.BotsManager;
import com.dima.bot.manager.model.AutoFillEntity;
import com.dima.bot.manager.model.DetectorOfAdvertisement;
import com.dima.bot.manager.model.NewAdvertisement;
import com.dima.bot.settings.model.UrlWorker;
import com.dima.bot.util.URLUtil;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ShemiareiD
 * Date: 5/2/14
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExecutedAdvertisementDetector implements Runnable {

    private BotsManager manager;
    private Map<UrlWorker, CircularFifoQueue<Advertisement>> vassalWorkers = new HashMap<UrlWorker, CircularFifoQueue<Advertisement>>();

    final Logger logger = LogManager.getLogger("debugLogger");

    public ExecutedAdvertisementDetector(BotsManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        while(!manager.isPauseProcessingExecutedAnswer()) {
            vassalWorkers.clear();
            List<UrlWorker> workers = manager.getKeeper().getUrlWorkers();
            for(UrlWorker worker : workers) {
                if(!worker.isSeniorStatus()) {
                    vassalWorkers.put(worker, new CircularFifoQueue<Advertisement>(200));
                    AdvertisementExtractor extractor = manager.factoryAdvertisementExtractor(worker.getUrl());
                    if(extractor != null) {
                        for(int i = 1; i <= extractor.getMaxNPage(); i++) {
                            boolean isBreak = false;
                            for(Advertisement advertisement : extractor.extract(URLUtil.getUrlForPage(worker.getUrl(), i))) {
                                if(vassalWorkers.get(worker).contains(advertisement)) {
                                    isBreak = true;
                                } else {
                                    vassalWorkers.get(worker).add(advertisement);
                                }
                            }
                            if(isBreak) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            for(int workindex = 0; workindex < workers.size() && !manager.isPauseProcessingExecutedAnswer(); workindex++  ) {
                UrlWorker worker = manager.getKeeper().getUrlWorkers().get(workindex);
                AdvertisementExtractor extractor = manager.factoryAdvertisementExtractor(worker.getUrl());
                logger.debug("Обрабатывается worker (ExecutedAdvertisementDetector):" + worker.getUrl());
                if(extractor != null && worker.isSeniorStatus()) {
                    for(int i = 1; i <= extractor.getMaxNPage(); i++) {
                        boolean isBreak = false;
                        for(Advertisement advertisement : extractor.extract(URLUtil.getUrlForPage(worker.getUrl(), i))) {
                            if(!manager.getCashExecutedAnswer(worker).contains(advertisement.getNumber())) {
                                if(advertisement.isPerformed()) {
                                    AdvertisementExtractor answerExtractor = manager.factoryAdvertisementExtractor(advertisement.getOpenURL());
                                    if(answerExtractor != null) {
                                        List<Advertisement> answers = answerExtractor.extract(advertisement.getOpenURL());
                                        for(Advertisement temp : answers) {
                                            temp.setDate(advertisement.getDate());
                                            temp.setNumber(advertisement.getNumber());
                                            temp.setAuto(advertisement.getAuto());
                                            temp.setAutoYear(advertisement.getAutoYear());
                                            temp.setOpenURL(advertisement.getOpenURL());
                                            temp.setPerformed(advertisement.isPerformed());
                                            manager.getCashExecutedAnswer(worker).add(temp.getNumber());
                                            // разослать всем подчиненным адрессам
                                            for(Map.Entry<UrlWorker,CircularFifoQueue<Advertisement>> vassalTemp : vassalWorkers.entrySet()) {
                                                UrlWorker vassalWorker = vassalTemp.getKey();
                                                CircularFifoQueue<Advertisement> vassalList = vassalTemp.getValue();
                                                for(Advertisement vassalAdvertisement : vassalList) {
                                                    if(vassalAdvertisement.getNumber() == temp.getNumber()) {
                                                        NewAdvertisement vassalNewTemp = new NewAdvertisement(vassalAdvertisement);
                                                        for(Map.Entry<String,AutoFillEntity> entity : ((NewAdvertisement) temp).getAutoFillDetailsMap().entrySet()) {
                                                            if(vassalWorker.getMinCost() < entity.getValue().getCost() && entity.getValue().getCost() <= vassalWorker.getMaxCost()) {
                                                                AutoFillEntity autoFillEntity = new AutoFillEntity();
                                                                autoFillEntity.setDeliveryTime(entity.getValue().getDeliveryTime());
                                                                autoFillEntity.setState(entity.getValue().getState());
                                                                autoFillEntity.setNote(entity.getValue().getNote());
                                                                autoFillEntity.setCost(entity.getValue().getCost()*(vassalWorker.getPercent() + 100)/100);
                                                                vassalNewTemp.getAutoFillDetailsMap().put(entity.getKey(),autoFillEntity);
                                                            }
                                                        }
                                                        if(!vassalNewTemp.getAutoFillDetailsMap().isEmpty()) {
                                                            vassalNewTemp.setSignOfDetector(DetectorOfAdvertisement.EXECUTED);
                                                            manager.getTaskTracker().addFirstAutoFillTask(vassalWorker,vassalNewTemp);
                                                            logger.debug("ADD to TASK TRACKER(ExecutedAdvertisementDetector):" + vassalNewTemp.toString());
                                                        }
                                                        break;
                                                    }
                                                }
                                            }
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
            }
            try {
                Thread.sleep(manager.getRepeatDetectorSec()*1000);
            } catch (InterruptedException e) {
                logger.error("Sleep error.",e);
                e.printStackTrace();
            }
        }
        manager.finishExecutedAdvertisementDetector();
    }
}
