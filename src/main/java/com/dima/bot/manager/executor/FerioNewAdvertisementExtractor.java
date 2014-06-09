package com.dima.bot.manager.executor;

import com.dima.bot.manager.model.Advertisement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: CHEB
 */
public class FerioNewAdvertisementExtractor implements AdvertisementExtractor {

    public static final String SITE_URL = "http://www.ferio.ru/";
    private static final int MAX_N_PAGE = 1;

    @Override
    public List<Advertisement> extract(String url) {
        //url = URLUtil.checkUrl(url);
        List<Advertisement> advertisements = new ArrayList<Advertisement>();

        if(url==null) return advertisements;

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
                String numberstr = null;
                boolean marker = false;
                for(Element child : tbody2.children()) {
                    if("tr".equals(child.nodeName()) && child.className().contains("inputs-")) {
                        if(!child.className().equals(numberstr)) {
                            numberstr = child.className().substring(7);
                            marker = true;
                        }
                        if(marker) {
                            Element td2 = getChild(child,"td",1);
                            if(td2!= null) {
                                Element textarea = getChild(child,"textarea",1);
                                if(textarea != null && textarea.className().contains("zapros-")) {

                                    marker = false;
                                }
                            }
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
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
