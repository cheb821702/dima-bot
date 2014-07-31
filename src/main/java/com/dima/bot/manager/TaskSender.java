package com.dima.bot.manager;

import com.dima.bot.settings.model.UrlWorker;

/**
 * Created with IntelliJ IDEA.
 * User: ShemiareiD
 * Date: 7/31/14
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class TaskSender implements  Runnable {

    private BotsManager manager;
    private UrlWorker worker;

    @Override
    public void run() {
        while(this.worker != null && this.manager.getUrlWorkers().contains(this.worker) && !this.manager.isPauseTaskSender()) {

        }
    }

    public TaskSender(UrlWorker worker, BotsManager manager) {
        this.worker = worker;
        this.manager = manager;
    }
}
