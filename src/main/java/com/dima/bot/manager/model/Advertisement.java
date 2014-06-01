package com.dima.bot.manager.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ShemiareiD
 * Date: 4/14/14
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class Advertisement {

    private Date date;
    private long number;
    private String auto;
    private int autoYear = -1;
    private String autoCode;
    private Map<String,String> details = new HashMap<String,String>();
    private String openURL;
    private boolean performed = false;


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getAuto() {
        return auto;
    }

    public void setAuto(String auto) {
        this.auto = auto;
    }

    public int getAutoYear() {
        return autoYear;
    }

    public void setAutoYear(int autoYear) {
        this.autoYear = autoYear;
    }

    public String getAutoCode() {
        return autoCode;
    }

    public void setAutoCode(String autoCode) {
        this.autoCode = autoCode;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public String getOpenURL() {
        return openURL;
    }

    public void setOpenURL(String openURL) {
        this.openURL = openURL;
    }

    public boolean isPerformed() {
        return performed;
    }

    public void setPerformed(boolean performed) {
        this.performed = performed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Advertisement)) return false;

        Advertisement that = (Advertisement) o;

        if (number != that.number) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (int) (number ^ (number >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Advertisement{" +
                "date=" + date +
                ", number=" + number +
                ", auto='" + auto + '\'' +
                ", autoYear=" + autoYear +
                ", autoCode='" + autoCode + '\'' +
                ", details=" + details +
                ", openURL='" + openURL + '\'' +
                ", performed=" + performed +
                '}';
    }
}
