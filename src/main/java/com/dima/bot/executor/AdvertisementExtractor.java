package com.dima.bot.executor;

import com.dima.bot.util.HttpManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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

            // get page title
            String title = doc.title();
            System.out.println("title : " + title);

            // get all links
            Element body = doc.body();
            for(Element form : body.getElementsByTag("form")) {
                if(form.hasAttr("method") && "post".equals(form.attr("method"))) {
                    for(Element table : form.getElementsByTag("table")) {
                        for(Element tbody : table.getElementsByTag("tbody")) {
                            for(Element tr : tbody.getElementsByTag("tr")) {
                                if(tr.hasAttr("bgcolor")) {

                                }
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
