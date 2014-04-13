package com.dima.bot.ui;

import java.io.File;

/**
 * User: CHEB
 */

public class FileFilterUtils {

    public final static String xlsx = "xlsx";
    public final static String xls = "xls";


    /*
    * Get the extension of a file.
    */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    }

