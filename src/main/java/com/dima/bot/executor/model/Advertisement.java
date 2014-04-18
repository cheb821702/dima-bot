package com.dima.bot.executor.model;

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




}
