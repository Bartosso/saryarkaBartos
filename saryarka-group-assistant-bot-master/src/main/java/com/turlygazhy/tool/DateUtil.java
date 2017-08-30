package com.turlygazhy.tool;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Yerassyl_Turlygazhy on 06-Mar-17.
 */
public class DateUtil {

    public static Date getHour(int hour) {
        Date date = new Date();
        if (date.getHours() >= hour) {
            date.setDate(date.getDate() + 1);
        }
        date.setHours(hour);
        date.setMinutes(0);
        date.setSeconds(1);
        return date;
    }

    public static String getMonthInRussian(int month) {
        String string = null;

        switch (month){
            case 1:
                string = "января";
                break;
            case 2:
                string = "февраля";
                break;
            case 3:
                string = "марта";
                break;
            case 4:
                string = "апреля";
                break;
            case 5:
                string = "мая";
                break;
            case 6:
                string = "июня";
                break;
            case 7:
                string = "июля";
                break;
            case 8:
                string = "августа";
                break;
            case 9:
                string = "сентября";
                break;
            case 10:
                string = "октября";
                break;
            case 11:
                string = "ноября";
                break;
            case 12:
                string = "декабря";
                break;
        }

        return string;
    }

    static String dayOfWeekInRussian(int day){
        String string = null;
        switch (day){
            case 1:
                string = "понедельник";
                break;
            case 2:
                string = "вторник";
                break;
            case 3:
                string = "среда";
                break;
            case 4:
                string = "четверг";
                break;
            case 5:
                string = "пятница";
                break;
            case 6:
                string = "суббота";
                break;
            case 7:
                string = "воскресенье";
                break;
        }
        return string;
    }

    public static Date getNextMonthEnd(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.DAY_OF_MONTH,Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static Date getLastDayOfThisMonth(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.DAY_OF_MONTH,Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static Date getNextMorning(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 0);
        if (new Date().after(cal.getTime())){
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return cal.getTime();
    }

    public static Date getNextNight(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 0);
        if (new Date().after(cal.getTime())){
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return cal.getTime();
    }

    public static Date getThisNight(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 0);
        return cal.getTime();
    }

//    public static Date getThisMorning(){
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.HOUR_OF_DAY, 9);
//        cal.set(Calendar.MINUTE, 0);
//        return cal.getTime();
//    }

    public static Date getNextHour(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY,1);
        return cal.getTime();
    }


}