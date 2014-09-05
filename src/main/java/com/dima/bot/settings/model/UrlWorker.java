package com.dima.bot.settings.model;

/**
 * User: CHEB
 */
public class UrlWorker {

    private static int MIN_SEC_TIME_DEFERENCE = 30;

    private String url = null;
    private int minCost = 0;
    private int maxCost = 0;
    private int percent = 0;
    private int minSecTime = 0;
    private int maxSecTime = 0;
    private boolean seniorStatus = false;

    public UrlWorker() {

    }

    public UrlWorker(String url, int minCost, int maxCost, int percent, int minSecTime, int maxSecTime) {
        setUrl(url);
        setMinCost(minCost);
        setMaxCost(maxCost);
        setPercent(percent);
        setMinSecTime(minSecTime);
        setMaxSecTime(maxSecTime);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMinCost() {
        return minCost;
    }

    public void setMinCost(int minCost) {
        if(minCost<0) {
            this.minCost = 0;
        } else {
            this.minCost = minCost;
        }
        this.minCost = minCost;
    }

    public int getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(int maxCost) {
        if(maxCost<0) {
            this.maxCost = 0;
        } else {
            this.maxCost = maxCost;
        }
        this.maxCost = maxCost;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getMinSecTime() {
        return minSecTime;
    }

    public void setMinSecTime(int minSecTime) {
        if(minSecTime<0) {
            this.minSecTime = 0;
        } else {
            this.minSecTime = minSecTime;
        }
    }

    public int getMaxSecTime() {

        return maxSecTime;
    }

    public void setMaxSecTime(int maxSecTime) {
        if(maxSecTime < (getMinSecTime()+MIN_SEC_TIME_DEFERENCE)) {

        }else {
            this.maxSecTime = maxSecTime;
        }
        this.maxSecTime = maxSecTime;
    }

    public boolean isSeniorStatus() {
        return seniorStatus;
    }

    public void setSeniorStatus(boolean seniorStatus) {
        this.seniorStatus = seniorStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UrlWorker)) return false;

        UrlWorker worker = (UrlWorker) o;

        if (maxCost != worker.maxCost) return false;
        if (minCost != worker.minCost) return false;
        if (percent != worker.percent) return false;
        if (seniorStatus != worker.seniorStatus) return false;
        if (!url.equals(worker.url)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + minCost;
        result = 31 * result + maxCost;
        result = 31 * result + percent;
        result = 31 * result + (seniorStatus ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UrlWorker{" +
                "url='" + url + '\'' +
                ", minCost=" + minCost +
                ", maxCost=" + maxCost +
                ", percent=" + percent +
                ", seniorStatus=" + seniorStatus +
                '}';
    }
}
