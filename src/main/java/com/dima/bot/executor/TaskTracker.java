package com.dima.bot.executor;

import com.dima.bot.executor.model.AutoFillAdvertisement;
import com.dima.bot.settings.model.UrlWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ShemiareiD
 * Date: 4/28/14
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaskTracker {

    private Map<UrlWorker,List<AutoFillAdvertisement>> autoFillTracks = new HashMap<UrlWorker, List<AutoFillAdvertisement>>();

    public TaskTracker(List<UrlWorker> workers) {
        if(workers != null) {
            for(UrlWorker worker : workers) {
                autoFillTracks.put(worker, new ArrayList<AutoFillAdvertisement>());
            }
        }
    }

    public List<AutoFillAdvertisement> getAutoFillTrack(UrlWorker urlWorker) {
        return autoFillTracks.get(urlWorker);
    }

    public void addUrlWorkerTrack(UrlWorker urlWorker) {
        if(urlWorker != null) {
            if(!autoFillTracks.keySet().contains(urlWorker)) {
                autoFillTracks.put(urlWorker,new ArrayList<AutoFillAdvertisement>());
            }
        }
    }

    public void removeUrlWorkerTrack(UrlWorker urlWorker) {
        if(urlWorker != null) {
            autoFillTracks.remove(urlWorker);
        }
    }

    public boolean addAutoFillTask(UrlWorker urlWorker, AutoFillAdvertisement autoFillAdvertisement) {
        if(urlWorker != null || autoFillAdvertisement != null) {
            List<AutoFillAdvertisement> autoFillAdvertisements = autoFillTracks.get(urlWorker);
            if(autoFillAdvertisements != null) {
                return autoFillAdvertisements.add(autoFillAdvertisement);
            }
        }
        return false;
    }



}
