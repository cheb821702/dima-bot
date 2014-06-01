package com.dima.bot.manager.executor;

import com.dima.bot.manager.model.Advertisement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ShemiareiD
 * Date: 4/21/14
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class FerioAdvertisementExtractor implements AdvertisementExtractor{

    public static final String SITE_URL = "http://www.ferio.ru/clad/";
    private static final int MAX_N_PAGE = 10;

    @Override
    public List<Advertisement> extract(String url) {
//        url = URLUtil.checkUrl(url);
        List<Advertisement> advertisements = new ArrayList<Advertisement>();

        if(url==null) return advertisements;

        try {

            Document doc = Jsoup.connect(url).get();

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
                                                Date date = null;
                                                Long number = null;
                                                String auto = null;
                                                Integer autoYear = null;
                                                String autoCode = null;
                                                Map<String,String> details = new HashMap<>();
                                                Boolean performed = null;
                                                String openUrl = null;
                                                int numTd = 0;
                                                for (Element trChild : tbodyChild.children()) {
                                                    if("td".equals(trChild.tagName())) {
                                                        if(numTd == 1) {
                                                            for (TextNode tdtext : trChild.textNodes()) {
                                                                if (tdtext.text().trim().matches("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}")) {
                                                                    try {
                                                                         date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(tdtext.text().trim());
                                                                    } catch (ParseException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                            for (Element tdChild : trChild.children()) {
                                                                if("small".equals(tdChild.tagName())) {
                                                                    String tnumber = tdChild.text().trim();
                                                                    tnumber = tnumber.substring(tnumber.indexOf(" ")).trim();
                                                                    try {
                                                                        number = Long.parseLong(tnumber);
                                                                    } catch(NumberFormatException e) {
                                                                        // Log
                                                                    }
                                                                }
                                                            }
                                                        } else if(numTd == 2) {
                                                            if(trChild.textNodes().size() != 0) {
                                                                auto = trChild.textNodes().get(0).text().trim();
                                                                if(trChild.textNodes().size() != 1) {
                                                                    try {
                                                                        autoYear = Integer.parseInt(trChild.textNodes().get(1).text().trim());
                                                                    } catch(NumberFormatException e) {

                                                                    }
                                                                }
                                                            }
                                                            for (Element tdChild : trChild.children()) {
                                                                if("small".equals(tdChild.tagName())) {
                                                                    for (Element smallChild : tdChild.children()) {
                                                                        if("font".equals(smallChild.tagName())) {
                                                                            autoCode = smallChild.text().trim();
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        } else if(numTd == 3) {
                                                            String html = trChild.html();
                                                            html = html.substring(html.indexOf("</i>")+4).trim();
                                                            for(String htmlEl : html.split("<br />")) {
                                                                if(!htmlEl.isEmpty()) {
                                                                    String[] detailParts = htmlEl.trim().split("\\|");
                                                                    if(detailParts.length >= 2) {
                                                                        details.put(detailParts[0].trim(),detailParts[1].trim());
                                                                    }
                                                                }
                                                            }
                                                        } else if(numTd == 6) {
                                                            for (Element tdChild : trChild.children()) {
                                                                if("a".equals(tdChild.tagName())) {
                                                                    if("обработан".equals(tdChild.text().trim())) {
                                                                        performed = true;
                                                                    } else {
                                                                        performed = false;
                                                                    }
                                                                    if(tdChild.hasAttr("href")) {
                                                                        openUrl = tdChild.attr("href").trim();
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        numTd++;
                                                    }
                                                }

                                                if(date!=null && number!=null && auto!=null && autoYear!=null && details.size()!=0 && performed!=null && openUrl!=null) {
                                                    Advertisement advertisement = new Advertisement();
                                                    advertisement.setDate(date);
                                                    advertisement.setNumber(number);
                                                    advertisement.setAuto(auto);
                                                    advertisement.setAutoYear(autoYear);
                                                    advertisement.setAutoCode(autoCode);
                                                    advertisement.setDetails(details);
                                                    advertisement.setPerformed(performed);
                                                    advertisement.setOpenURL("http://www.ferio.ru" + openUrl);
                                                    advertisements.add(advertisement);
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
        return advertisements;
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
