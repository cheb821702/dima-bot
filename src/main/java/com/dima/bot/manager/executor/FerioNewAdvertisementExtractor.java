package com.dima.bot.manager.executor;

import com.dima.bot.manager.model.Advertisement;
import com.dima.bot.manager.model.AutoFillEntity;
import com.dima.bot.manager.model.NewAdvertisement;
import com.dima.bot.manager.util.ErrorGUIHandler;
import com.dima.bot.manager.util.ExcelAutoFillUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: CHEB
 */
public class FerioNewAdvertisementExtractor implements AdvertisementExtractor {

    public static final String SITE_URL = "http://www.ferio.ru/";
    private static final int MAX_N_PAGE = 1;
    private static final int NOT_CONNECTION_TIME_SLEEP = 1800;

    final Logger logger = LogManager.getLogger("debugLogger");

    @Override
    public List<Advertisement> extract(String url) {
        //url = URLUtil.checkUrl(url);
        List<Advertisement> advertisements = new ArrayList<Advertisement>();

        if(url==null) return advertisements;
        boolean isSuccessDone = false;
        while(!isSuccessDone){
            isSuccessDone = true;
            try {

                Document doc = Jsoup.connect(url).get();
                Element body = doc.body();
                Elements forms = body.select("form[action=/otvet-php/add.php]");
                if(forms.size()>0) {
                    Element addForm = forms.get(0);
                    Element small = getChild(addForm, "small", 1);
                    Element table1 = getChild(small, "table", 2);
                    Element tbody1 = getChild(table1,"tbody",1);
                    Element tr1 = getChild(tbody1,"tr",1);
                    Element td1 = getChild(tr1,"td",1);
                    Element table2 = getChild(td1,"table",1);
                    Element tbody2 = getChild(table2,"tbody",1);

                    Element tdAnswer = null;
                    for(Element child : tbody2.children()) {
                        if("tr".equals(child.nodeName()) && child.text().contains("До этого Вы ответили:")) {
                            tdAnswer = getChild(child,"td",2);
                            break;
                        }
                    }

                    List<String> zaprosi = new LinkedList<String>();
                    String numberstr = null;
                    boolean marker = false;
                    for(Element child : tbody2.children()) {
                        if("tr".equals(child.nodeName()) && child.className().contains("inputs-")) {
                            if(!child.className().equals(numberstr)) {
                                numberstr = child.className();
                                marker = true;
                            }
                            if(marker) {
                                Element td2 = getChild(child,"td",1);
                                if(td2!= null) {
                                    Element textarea = getChild(td2,"textarea",1);
                                    if(textarea != null && textarea.hasAttr("name") && textarea.attr("name").contains("zapros-")) {
                                        zaprosi.add(textarea.text().trim());
                                        marker = false;
                                    }
                                }
                            }
                        }
                    }

                    String answerText = tdAnswer.text();
                    String answerHtml = tdAnswer.html();
                    Map<Integer,String> posDetails = new HashMap<Integer,String>();
                    for(String zapros : zaprosi) {
                        int pos = answerText.indexOf(zapros);
                        if(pos > 0) {
                            posDetails.put(pos, zapros);
                        }
                    }



                    NewAdvertisement advertisement = new NewAdvertisement();
                    List<Integer> posSet = new ArrayList<Integer>(posDetails.keySet());
                    Collections.sort(posSet);
                    for(int i = 0; i < posSet.size(); i++) {
                        AutoFillEntity entity = new AutoFillEntity();
                        String answerStr = null;
                        if(posSet.size()-1 == i) {
                            answerStr = answerText.substring(posSet.get(i));
                        } else {
                            answerStr = answerText.substring(posSet.get(i),posSet.get(i+1));
                        }
                        answerStr = answerStr.substring(posDetails.get(posSet.get(i)).length()).trim();

                        int cost  = Integer.parseInt(answerStr.substring(0, answerStr.indexOf("руб.")).trim());
                        entity.setCost(cost);
                        answerStr = answerStr.substring(answerStr.indexOf("руб.") + 4).trim();

                        for(Map.Entry<Integer, String> delivTime: ExcelAutoFillUtil.getDeliveryTimeList().entrySet()) {
                            if(answerStr.startsWith(delivTime.getValue())) {
                                answerStr = answerStr.substring(delivTime.getValue().length()).trim();
                                entity.setDeliveryTime(delivTime.getValue());
                                break;
                            }
                        }

                        for(Map.Entry<Integer, String> states: ExcelAutoFillUtil.getStateList().entrySet()) {
                            if(answerStr.startsWith(states.getValue())) {
                                answerStr = answerStr.substring(states.getValue().length()).trim();
                                entity.setState(states.getValue());
                                break;
                            }
                        }
                        advertisement.getAutoFillDetailsMap().put(posDetails.get(posSet.get(i)), entity);
                    }
                    advertisements.add(advertisement);
              }
            } catch (SocketTimeoutException e) {
                isSuccessDone = false;
                try {
                    Thread.sleep(NOT_CONNECTION_TIME_SLEEP*1000);
                } catch (InterruptedException e1) {
                    SimpleDateFormat dt = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");
                    ErrorGUIHandler.INSTANCE.handleError("NOT_CONNECTION with ferio. In " + dt.format(new Date()) + " Sleeping time " + NOT_CONNECTION_TIME_SLEEP + "sec.");
                    logger.trace("NOT_CONNECTION with ferio. Sleeping time " + NOT_CONNECTION_TIME_SLEEP + "sec.");
                    e1.printStackTrace();
                }
            } catch (IOException e) {
                logger.error("Connection error.",e);
                e.printStackTrace();
            }
        }
        return advertisements;
    }

    private Element getChild(Element element, String name, int number) {
        int i = 0;
        for(Element child : element.children()) {
            if(name.equals(child.nodeName())) {
                i++;
                if(number == i) {
                    return  child;
                }
            }
        }
        return null;
    }

    @Override
    public String getSiteUrl() {
        return SITE_URL;
    }

    @Override
    public int getMaxNPage() {
        return MAX_N_PAGE;
    }
}
