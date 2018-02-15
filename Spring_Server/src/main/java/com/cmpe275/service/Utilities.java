package com.cmpe275.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author arunabh.shrivastava
 */
public class Utilities {

    private static DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    private static DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");

    public static Date stringToDate(String stringDate){
        Date date = null;
        try {
            date = df.parse(stringDate);
        } catch (ParseException e) {
            try {
                date = df2.parse(stringDate);
            } catch (ParseException e1) {
                e1.getMessage();
            }
            e.getMessage();
        }
        return date;
    }

    public static String dateToString(Date date){
        return df.format(date);
    }
}
