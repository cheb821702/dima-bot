package com.dima.bot.manager.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ShemiareiD
 * Date: 8/29/14
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class FerioDataOnPageUtil {

    private static Map<Integer,String> deliveryTimeList = new HashMap<Integer,String>();
    private static Map<Integer,String> stateList = new HashMap<Integer,String>();

    static {
        deliveryTimeList.put(1,"в наличии");
        deliveryTimeList.put(2,"1 день");
        deliveryTimeList.put(3,"1-2 дня");
        deliveryTimeList.put(4,"2-3 дня");

        deliveryTimeList.put(13,"3 - 5 дней");
        deliveryTimeList.put(5,"5-7 дней");
        deliveryTimeList.put(6,"7-10 дней");
        deliveryTimeList.put(7,"10-12 дней");
        deliveryTimeList.put(8,"12-15 суток");
        deliveryTimeList.put(9,"15-20 суток");
        deliveryTimeList.put(10,"20-30 дней");
        deliveryTimeList.put(11,"30-45 дней");
        deliveryTimeList.put(12,"от 45 суток");

        stateList.put(1,"новый(я) оригинал");
        stateList.put(2,"новый(я) неоригинал");
        stateList.put(3,"б.у. оригинал");
        stateList.put(4,"б.у. БЕЗ дефектов");
        stateList.put(5,"б.у. с дефектом");
        stateList.put(6,"восстановленный(я)");
        stateList.put(7,"б.у. неоригинал");
        stateList.put(8,"ремонт");
    }

    public static Map<Integer, String> getDeliveryTimeList() {
        return deliveryTimeList;
    }

    public static Map<Integer, String> getStateList() {
        return stateList;
    }
}
