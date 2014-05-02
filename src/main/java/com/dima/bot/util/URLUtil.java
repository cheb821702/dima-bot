package com.dima.bot.util;

import com.dima.bot.executor.FerioAdvertisementParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ShemiareiD
 * Date: 4/21/14
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class URLUtil {

    public static String checkUrl(String url) {
        if(url != null) {
            if(url.startsWith(FerioAdvertisementParser.SITE_URL)) {
                return checkFerio(url);
            }
        }
        return null;
    }

    private static String checkFerio(String url) {
        String urlRegEx = "http://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        if(url != null && url.matches(urlRegEx)) {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(url.substring(0,url.indexOf('?')+1));
            Pattern pattern = Pattern.compile("ch=[^&]*&?");
            Matcher m = pattern.matcher(url);
            if(!m.find()) {
                return null;
            } else {
                urlBuilder.append(m.group(0));
            }
            pattern = Pattern.compile("st=[^&]*&?");
            m = pattern.matcher(url);
            if(!m.find()) {
                return null;
            } else {
                urlBuilder.append(m.group(0));
            }
            pattern = Pattern.compile("agent=[^&]*&?");
            m = pattern.matcher(url);
            if(!m.find()) {
                return null;
            } else {
                urlBuilder.append(m.group(0));
            }
            pattern = Pattern.compile("cp=[^&]*&?");
            m = pattern.matcher(url);
            if(!m.find()) {
                return null;
            } else {
                urlBuilder.append(m.group(0));
            }
            pattern = Pattern.compile("brand=[^&]*");
            m = pattern.matcher(url);
            if(!m.find()) {
                return null;
            } else {
                urlBuilder.append(m.group(0));
            }
            return urlBuilder.toString();
        }
        return null;
    }

    public static String getUrlForPage(String url, int nPage) {
        if(url != null) {
            int index = url.indexOf("list=");
            if(index > -1) {
                return url.substring(0,index + 5) + nPage + url.substring(index + 6);
            } else {
                return url + "&list=" + nPage;
            }
        }
        return url;
    }
}
