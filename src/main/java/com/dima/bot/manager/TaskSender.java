package com.dima.bot.manager;

import com.dima.bot.manager.model.Advertisement;
import com.dima.bot.manager.model.NewAdvertisement;
import com.dima.bot.settings.model.UrlWorker;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.List;
import java.util.Random;

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
            NewAdvertisement advertisement = manager.getTaskTracker().getLastAutoFillTask(worker);
            if(advertisement == null) {
                break;
            } else {
                WebDriver driver = new FirefoxDriver();
                driver.get(advertisement.getOpenURL());

                driver.findElement(By.id("submit")).submit();


//                manager.getTaskTracker().removeAutoFillTask(worker,advertisement);
            }

            int minSec = worker.getMinSecTime();
            int maxSec = worker.getMaxSecTime();
            if(minSec <= 0) {
                minSec = 1;
            }
            if(maxSec < minSec + 30) {
                maxSec = minSec + 30;
            }
            Random rand = new Random();
            int randomNum = rand.nextInt((maxSec - minSec) + 1) + minSec;
            try {
                Thread.sleep(randomNum*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public TaskSender(UrlWorker worker, BotsManager manager) {
        this.worker = worker;
        this.manager = manager;
    }
}
