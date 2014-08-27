package com.dima.bot.manager;

import com.dima.bot.manager.model.AutoFillEntity;
import com.dima.bot.manager.model.NewAdvertisement;
import com.dima.bot.manager.util.ExcelAutoFillUtil;
import com.dima.bot.settings.model.UrlWorker;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.*;

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

                    if(answerText != null) {
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
                    }
                } catch (NoSuchElementException e) {

                }

                try {
                    WebElement tbody = driver.findElement(By.xpath("//form[@action='/otvet-php/add.php']/small/table/tbody/tr/td/table/tbody"));
                    for(int i = 1; i < 100; i++) {
                        try {
                            WebElement zapros = tbody.findElement(By.name("zapros-" + Integer.toString(i)));
                            String text = zapros.getText();
                            if(!answeredDetails.contains(text)) {
                                if(advertisement.getAutoFillDetailsMap().containsKey(text)){
                                    AutoFillEntity entity = advertisement.getAutoFillDetailsMap().get(text);
                                    WebElement price = tbody.findElement(By.name("price-" + Integer.toString(i) + "-1"));
                                    price.sendKeys(Integer.toString(entity.getCost()));

                                    Select nalichieBox = new Select(tbody.findElement(By.name("nalichie-" + Integer.toString(i) + "-1")));
                                    String nalichie = entity.getDeliveryTime();
                                    for(Map.Entry entry: ExcelAutoFillUtil.getDeliveryTimeList().entrySet()) {
                                        if(nalichie.equals(entry.getValue())) {
                                            nalichieBox.deselectAll();
                                            nalichieBox.selectByIndex((Integer) entry.getKey());
                                            break;
                                        }
                                    }

                                    Select sostoyanieBox = new Select(tbody.findElement(By.name("sostoyanie-" + Integer.toString(i) + "-1")));
                                    String sostoyanie = entity.getState();
                                    for(Map.Entry entry: ExcelAutoFillUtil.getStateList().entrySet()) {
                                        if(sostoyanie.equals(entry.getValue())) {
                                            sostoyanieBox.deselectAll();
                                            sostoyanieBox.selectByIndex((Integer) entry.getKey());
                                            break;
                                        }
                                    }
                                }
                            }
                        } catch (NoSuchElementException e) {
                              break;
                        }
                    }
                    driver.findElement(By.name("submit")).submit();
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
