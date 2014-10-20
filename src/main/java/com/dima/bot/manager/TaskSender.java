package com.dima.bot.manager;

import com.dima.bot.manager.detector.AutoFillDetector;
import com.dima.bot.manager.model.AutoFillEntity;
import com.dima.bot.manager.model.DetectorOfAdvertisement;
import com.dima.bot.manager.model.NewAdvertisement;
import com.dima.bot.manager.util.FerioDataOnPageUtil;
import com.dima.bot.settings.model.UrlWorker;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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

    final Logger logger = LogManager.getLogger("reportsLogger");
    final Logger debugLogger = LogManager.getLogger("debugLogger");

    @Override
    public void run() {
        debugLogger.info("Run TaskSender");
        while(this.worker != null && this.manager.getUrlWorkers().contains(this.worker) && !this.manager.isPauseTaskSender()) {
            NewAdvertisement advertisement = manager.getTaskTracker().getLastAutoFillTask(worker);
            boolean isSkippedAdvertisement = true;
            List<String> logSenderList = new LinkedList<String>();
            if(advertisement == null) {
                break;
            } else {
                debugLogger.debug("Opening FireFox by " + advertisement.getOpenURL());
                WebDriver driver = new FirefoxDriver();
                driver.get(advertisement.getOpenURL());

                // поиск уже отвеченных деталей
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
                                    break;
                                } else {
                                    pos = answerText.indexOf(zapros);
                                }
                            }
                        }
                    }
                } catch (NoSuchElementException e) {
                    debugLogger.error("Selenium didn't find answered element.",e);
                }

                // заполнеие формы запроса
                try {
                    WebElement tbody = driver.findElement(By.xpath("//form[@action='/otvet-php/add.php']/small/table/tbody/tr/td/table/tbody"));

                    for(int i = 1; i < 100; i++) {
                        try {
                            // поиск ориг. номера детали
                            String nomberText = null;
                            try {
                                WebElement nomber = tbody.findElement(By.name("nomber-" + Integer.toString(i)));
                                nomberText = nomber.getAttribute("value");
                            } catch (NoSuchElementException e) {
                                debugLogger.debug("Selenium didn't find origin number.",e);
                            }

                            // выбор детали на форме
                            WebElement zapros = tbody.findElement(By.name("zapros-" + Integer.toString(i)));
                            String zaprosText = zapros.getText();
                            if(!zaprosText.isEmpty() && !answeredDetails.contains(zaprosText)) {

                                // поиск заполняемой информации о детали
                                AutoFillEntity detailEntity = null;
                                if(nomberText != null && !nomberText.isEmpty() ) {
                                    for (Map.Entry<String,AutoFillEntity> detailEntry: advertisement.getAutoFillDetailsMap().entrySet()) {
                                        if(detailEntry.getKey().startsWith(zaprosText) && zaprosText.equals(detailEntry.getKey().replace('(' + nomberText + ')', "").trim())) {
                                            detailEntity = detailEntry.getValue();
                                        }
                                    }
                                } else if(advertisement.getAutoFillDetailsMap().containsKey(zaprosText)) {
                                    detailEntity = advertisement.getAutoFillDetailsMap().get(zaprosText);
                                }

                                // заполнение формы детали информацией
                                if(detailEntity != null) {

                                    // поиск деталей в списке автоответов\
                                    boolean isAutoAnswerDetail = false;
                                    for(AutoFillEntity autoFillEntity : manager.getAutoFillEntities()) {
                                        if(AutoFillDetector.checkAuto(advertisement, autoFillEntity)) {
                                            for(Map.Entry<String,String> detail : advertisement.getDetails().entrySet()) {
                                                if(AutoFillDetector.checkDetail(detail.getKey().trim(), autoFillEntity.getDetail().trim())) {
                                                    isAutoAnswerDetail = true;
                                                }
                                            }
                                        }
                                    }

                                    if(!(isAutoAnswerDetail && DetectorOfAdvertisement.EXECUTED.equals(advertisement.getSignOfDetector()))) {
                                        isSkippedAdvertisement = false;

                                        StringBuilder logBuilder = new StringBuilder();
                                        logBuilder.append("№");
                                        logBuilder.append(advertisement.getNumber());
                                        logBuilder.append(" ");
                                        logBuilder.append(detailEntity.getCost());
                                        logBuilder.append("руб. ");
                                        logBuilder.append(detailEntity.getDeliveryTime());
                                        logBuilder.append(" ");
                                        logBuilder.append(detailEntity.getState());
                                        logBuilder.append(" ");
                                        logBuilder.append(zaprosText);


                                        WebElement price = tbody.findElement(By.name("price-" + Integer.toString(i) + "-1"));
                                        price.sendKeys(Integer.toString(detailEntity.getCost()));

                                        Select nalichieBox = new Select(tbody.findElement(By.name("nalichie-" + Integer.toString(i) + "-1")));
                                        String nalichie = detailEntity.getDeliveryTime();
                                        for(Map.Entry entry: FerioDataOnPageUtil.getDeliveryTimeList().entrySet()) {
                                            if(nalichie.equals(entry.getValue())) {
                                                nalichieBox.selectByValue(Integer.toString((Integer) entry.getKey()));
                                                break;
                                            }
                                        }

                                        Select sostoyanieBox = new Select(tbody.findElement(By.name("sostoyanie-" + Integer.toString(i) + "-1")));
                                        String sostoyanie = detailEntity.getState();
                                        for(Map.Entry entry: FerioDataOnPageUtil.getStateList().entrySet()) {
                                            if(sostoyanie.equals(entry.getValue())) {
                                                sostoyanieBox.selectByValue(Integer.toString((Integer) entry.getKey()));
                                                break;
                                            }
                                        }

                                        if(detailEntity.getDetail() != null && !zaprosText.equals(detailEntity.getDetail())) {
                                            WebElement rubric = tbody.findElement(By.name("rubric-" + Integer.toString(i) + "-1"));
                                            rubric.sendKeys(detailEntity.getDetail());
                                            logBuilder.append(" (");
                                            logBuilder.append(detailEntity.getDetail());
                                            logBuilder.append(")");
                                        }
                                        logSenderList.add(logBuilder.toString());
                                    }
                                }
                            }
                        } catch (NoSuchElementException e) {
                              break;
                        }
                    }
                    if(!isSkippedAdvertisement) {
                        driver.findElement(By.name("submit")).click();
                        for(String log : logSenderList) {
                            logger.info(log);
                        }
                    }
                } catch (NoSuchElementException e) {
                    debugLogger.error("Selenium didn't find element.Form filling.",e);
                }

                manager.getTaskTracker().removeAutoFillTask(worker,advertisement);
                driver.close();
            }

            if(!isSkippedAdvertisement) {
                manager.putTimerLastAnswerDate(this.worker,new Date());
                // выставление задержки
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
                    debugLogger.debug("TaskSender sleep by " + randomNum + " sec.");
                    Thread.sleep(randomNum*1000);
                } catch (InterruptedException e) {
                    debugLogger.error(e.getMessage(),e);
                }
            }
        }
    }

    public TaskSender(UrlWorker worker, BotsManager manager) {
        this.worker = worker;
        this.manager = manager;
    }
}
