package com.dima.bot.manager;

import com.dima.bot.manager.model.NewAdvertisement;
import com.dima.bot.settings.model.UrlWorker;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ShemiareiD
 * Date: 4/28/14
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaskTracker {

    private Map<UrlWorker,List<NewAdvertisement>> autoFillTracks = new HashMap<UrlWorker, List<NewAdvertisement>>();

    public TaskTracker(List<UrlWorker> workers) {
        if(workers != null) {
            for(UrlWorker worker : workers) {
                autoFillTracks.put(worker, new LinkedList<NewAdvertisement>());
            }
        }
    }

    public List<NewAdvertisement> getAutoFillTrack(UrlWorker urlWorker) {
        return autoFillTracks.get(urlWorker);
    }

    public void addUrlWorkerTrack(UrlWorker urlWorker) {
        if(urlWorker != null) {
            if(!autoFillTracks.keySet().contains(urlWorker)) {
                autoFillTracks.put(urlWorker,new LinkedList<NewAdvertisement>());
            }
        }
    }

    public void removeUrlWorkerTrack(UrlWorker urlWorker) {
        if(urlWorker != null) {
            autoFillTracks.remove(urlWorker);
        }
    }

    public void addFirstAutoFillTask(UrlWorker urlWorker, NewAdvertisement autoFillAdvertisement) {
        if(urlWorker != null || autoFillAdvertisement != null) {
            addUrlWorkerTrack(urlWorker);
            LinkedList<NewAdvertisement> autoFillAdvertisements = (LinkedList<NewAdvertisement>) autoFillTracks.get(urlWorker);
            if(autoFillAdvertisements != null) {
                autoFillAdvertisements.addFirst(autoFillAdvertisement);
            }
        }
    }

    public NewAdvertisement getLastAutoFillTask(UrlWorker urlWorker) {
        if(urlWorker != null) {
            LinkedList<NewAdvertisement> autoFillAdvertisements = (LinkedList<NewAdvertisement>) autoFillTracks.get(urlWorker);
            if(autoFillAdvertisements != null && !autoFillAdvertisements.isEmpty()) {
                return autoFillAdvertisements.getLast();
            }
        }
        return null;
    }

    public boolean removeAutoFillTask(UrlWorker urlWorker, NewAdvertisement autoFillAdvertisement) {
        if(urlWorker != null || autoFillAdvertisement != null) {
            LinkedList<NewAdvertisement> autoFillAdvertisements = (LinkedList<NewAdvertisement>) autoFillTracks.get(urlWorker);
            if(autoFillAdvertisements != null) {
                return autoFillAdvertisements.remove(autoFillAdvertisement);
            }
        }
        return false;
    }
}
