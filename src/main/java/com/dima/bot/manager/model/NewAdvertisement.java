package com.dima.bot.manager.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ShemiareiD
 * Date: 4/28/14
 * Time: 12:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewAdvertisement extends Advertisement{

    private Map<String,AutoFillEntity> autoFillDetailsMap = new HashMap<String,AutoFillEntity>();
    private DetectorOfAdvertisement signOfDetector;

    public NewAdvertisement() {
    }

    public NewAdvertisement(Advertisement advertisement) {
        setDate(advertisement.getDate());
        setNumber(advertisement.getNumber());
        setAuto(advertisement.getAuto());
        setAutoYear(advertisement.getAutoYear());
        setAutoCode(advertisement.getAutoCode());
        setDetails(advertisement.getDetails());
        setOpenURL(advertisement.getOpenURL());
        setPerformed(advertisement.isPerformed());
    }

    public Map<String, AutoFillEntity> getAutoFillDetailsMap() {
        return autoFillDetailsMap;
    }

    public void setAutoFillDetailsMap(Map<String, AutoFillEntity> autoFillDetailsMap) {
        this.autoFillDetailsMap = autoFillDetailsMap;
    }

    public DetectorOfAdvertisement getSignOfDetector() {
        return signOfDetector;
    }

    public void setSignOfDetector(DetectorOfAdvertisement signOfDetector) {
        this.signOfDetector = signOfDetector;
    }
}
