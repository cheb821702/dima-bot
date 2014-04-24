package com.dima.bot.executor.model;

/**
 * Created with IntelliJ IDEA.
 * User: ShemiareiD
 * Date: 4/24/14
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class AutoFillEntity {

    private String series;
    private String carcass;
    private Integer startYear;
    private Integer stopYear;
    private String detail;
    private Integer cost;
    private String deliveryTime;
    private String state;
    private String note;

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getCarcass() {
        return carcass;
    }

    public void setCarcass(String carcass) {
        this.carcass = carcass;
    }

    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    public Integer getStopYear() {
        return stopYear;
    }

    public void setStopYear(Integer stopYear) {
        this.stopYear = stopYear;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
