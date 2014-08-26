package com.dima.bot.manager;

import com.dima.bot.manager.model.Advertisement;
import com.dima.bot.manager.model.NewAdvertisement;
import com.dima.bot.settings.model.UrlWorker;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.LinkedList;
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

                List<String> answeredDetails = new ArrayList<String>();

                try {
                    List<WebElement> trs = driver.findElements(By.xpath("//form[@action='/otvet-php/add.php']/small/table/tbody/tr/td/table/tbody/tr"));
                    String answerText = null;
                    for(WebElement child : trs) {
                        if(child.getText().contains("До этого Вы ответили:")) {
                            answerText = child.findElement(By.xpath("./td[2]")).getText();
                            break;
                        }
                    }

                    List<String> zaprosi = new LinkedList<>();
                    String numberstr = null;
                    boolean marker = false;
                    for(WebElement child : trs) {
                        String className= child.getAttribute("class");
                        if(className.contains("inputs-")) {
                            if(!className.equals(numberstr)) {
                                numberstr = className;
                                marker = true;
                            }
                            if(marker) {
                                WebElement textarea = child.findElement(By.xpath("./td/textarea"));
                                if(textarea.getAttribute("name").contains("zapros-")) {
                                    zaprosi.add(textarea.getText().trim());
                                    marker = false;
                                }
                            }
                        }
                    }

                    for(String zapros : zaprosi) {
                        int pos = answerText.indexOf(zapros);
                        while(pos > 0) {
                            if(answerText.substring(pos + zapros.length(),answerText.indexOf(' ',pos + zapros.length()+1)).trim().matches("[0-9]+")) {
                                answeredDetails.add(zapros);
                            } else {
                                pos = answerText.indexOf(zapros);
                            }
                        }
                    }
                } catch (NoSuchElementException e) {

                }

                manager.getTaskTracker().removeAutoFillTask(worker,advertisement);
                driver.close();
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
