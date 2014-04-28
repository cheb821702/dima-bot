package com.dima.bot.executor;

import com.dima.bot.executor.model.Advertisement;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ShemiareiD
 * Date: 4/14/14
 * Time: 12:08 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AdvertisementExtractor {

    List<Advertisement> extract(String url);
    String getSiteUrl();
    int getMaxNPage();
}
