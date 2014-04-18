package com.dima.bot.executor;

import com.dima.bot.util.HttpManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ShemiareiD
 * Date: 4/14/14
 * Time: 12:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdvertisementExtractor {

    String url = "http://www.ferio.ru/clad/?ch=orders&st=ok&agent=53983&cp=901726&brand=2&criterion=&day=&month=&year=&keywrd=&list=1";

    public void extract() {

        Document doc;
        try {

            // need http protocol
            doc = Jsoup.connect(url).get();
            String text = doc.outerHtml();
            // get page title
            String title = doc.title();
            System.out.println("title : " + title);


            Element body = doc.body();
            for(Element bodyChild : body.children()) {
                if("form".equals(bodyChild.tagName()) && bodyChild.hasAttr("method") && "post".equals(bodyChild.attr("method"))) {
                    for(Element formChild : bodyChild.children()) {
                        if("table".equals(formChild.tagName()))
                        for(Element tableChild : formChild.children()) {
                            if("tbody".equals(tableChild.tagName())) {
                                int numTr = 0;
                                for(Element tbodyChild : tableChild.children()) {
                                    if("tr".equals(tbodyChild.tagName())) {
                                        if(0 < numTr && numTr < 21) {
                                            int numTd = 0;
                                            for (Element trChild : tbodyChild.children()) {
                                                if("td".equals(trChild.tagName())) {
                                                    if(numTd == 1) {
                                                        for (TextNode tdtext : trChild.textNodes()) {
                                                            if (tdtext.text().trim().matches("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}")) {
                                                                try {
                                                                    Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(tdtext.text().trim());
                                                                    System.out.println(date.toString());
                                                                } catch (ParseException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                        for (Element tdChild : trChild.children()) {
                                                            if("small".equals(tdChild.tagName())) {
                                                                System.out.println(tdChild.text().trim());
                                                            }
                                                        }
                                                    } else if(numTd == 2) {
                                                        if(trChild.textNodes().size() != 0) {
                                                            System.out.println(trChild.textNodes().get(0).text().trim());
                                                            if(trChild.textNodes().size() != 1) {
                                                                System.out.println(trChild.textNodes().get(1).text().trim());
                                                            }
                                                        }
                                                        for (Element tdChild : trChild.children()) {
                                                            if("small".equals(tdChild.tagName())) {
                                                                for (Element smallChild : tdChild.children()) {
                                                                    if("font".equals(smallChild.tagName())) {
                                                                        System.out.println(smallChild.text().trim());
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else if(numTd == 3) {
                                                        String html = trChild.html();
                                                        html = html.substring(html.indexOf("</i>")+4).trim();
                                                        for(String htmlEl : html.split("<br />")) {
                                                            if(!htmlEl.isEmpty()) {
                                                                System.out.println(htmlEl.trim());
                                                            }
                                                        }
                                                    } else if(numTd == 6) {
                                                        for (Element tdChild : trChild.children()) {
                                                            if("a".equals(tdChild.tagName())) {
                                                                if("обработан".equals(tdChild.text().trim())) {
                                                                    System.out.println("Зеленый");
                                                                } else {
                                                                    System.out.println("Красный");
                                                                }
                                                                if(tdChild.hasAttr("href")) {
                                                                    System.out.println(tdChild.attr("href").trim());
                                                                }
                                                            }
                                                        }
                                                    }
                                                    numTd++;
                                                }
                                            }
                                        }
                                       numTr++;
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
