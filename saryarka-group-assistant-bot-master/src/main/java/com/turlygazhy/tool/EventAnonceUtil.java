package com.turlygazhy.tool;

import com.turlygazhy.dao.impl.MessageDao;
import com.turlygazhy.entity.Event;


import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by Eshu on 06.07.2017.
 */
public class EventAnonceUtil {
    public static String getEventWithPatternNoByAdmin(Event event, MessageDao messageDao) throws SQLException {
//        com.turlygazhy.entity.Message poolMesage = messageDao.getMessage(92);
        Date eventDate;
        SimpleDateFormat format     = new SimpleDateFormat();
        format.applyPattern("dd.MM.yy");
        LocalDateTime localDateTime = null;
        try {
            eventDate = format.parse(event.getWHEN());
            localDateTime = LocalDateTime.ofInstant(eventDate.toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        @SuppressWarnings("ConstantConditions")
        String date = event.getWHEN().substring(0,2) + " " + DateUtil.getMonthInRussian(Integer.parseInt(event
                .getWHEN().substring(3, 5)))
                + ", " + DateUtil.dayOfWeekInRussian(localDateTime.getDayOfWeek().getValue());

//        String[] program   = event.getPROGRAM().split(";");
//        String programText;
//        StringBuilder sb = new StringBuilder();
//        for(String string : program){
//            int i;
//            for( i = 0; i<string.length(); i++ ){
//                if(Character.isLetter(string.charAt(i)))
//                {
//                    sb.append("<b>").append(string.substring(0, i)).append("</b> ").append(string.substring(i)).append("\n\n");
//                    break;
//                }
//            }
//        }
//        programText = sb.toString();
//        String text = poolMesage.getSendMessage().getText()
//                .replaceAll("event_text"         , event.getEVENT_NAME())
//                .replaceAll("event_address"      , event.getPLACE())
//                .replaceAll("event_time"         , date)
//                .replaceAll("event_contact"      , event.getCONTACT_INFORMATION())
//                .replaceAll("event_program"      , programText)
//                .replaceAll("event_dress_code"   , event.getDRESS_CODE())
//                .replaceAll("event_rules"        , event.getRULES());
//        if(event.getPAGE()!= null){
//            text = text+"\n\n<b>Страница мероприятия/регистрация</b>:"+event.getPAGE();
//        }
//        return text;

        return "<b>Ивент:</b> " + event.getEVENT_NAME() +
                "\n<b>Дата:</b> " + date + "\n<b>Подробности:</b> " + event.getPLACE() ;
    }

    public static String getEventWithPatternByAdmin(Event event, MessageDao messageDao) throws SQLException {
//        com.turlygazhy.entity.Message poolMesage = messageDao.getMessage(139);
//        String date = event.getWHEN().substring(0,2) + " " + DateUtil.getMonthInRussian(Integer.parseInt(event
//                .getWHEN().substring(3, 5)))
//                +event.getWHEN().substring(event.getWHEN().indexOf(" "));


        Date eventDate;
        SimpleDateFormat format     = new SimpleDateFormat();
        format.applyPattern("dd.MM.yy");
        LocalDateTime localDateTime = null;
        try {
            eventDate = format.parse(event.getWHEN());
            localDateTime = LocalDateTime.ofInstant(eventDate.toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        @SuppressWarnings("ConstantConditions")
        String date = event.getWHEN().substring(0,2) + " " + DateUtil.getMonthInRussian(Integer.parseInt(event
                .getWHEN().substring(3, 5)))
                + ", " + DateUtil.dayOfWeekInRussian(localDateTime.getDayOfWeek().getValue());
//        String[] program   = event.getPROGRAM().split(";");
//        String programText;
//        StringBuilder sb = new StringBuilder();
//        for(String string : program){
//            int i;
//            for( i = 0; i<string.length(); i++ ){
//                if(Character.isLetter(string.charAt(i)))
//                {
//                    sb.append("<b>").append(string.substring(0, i)).append("</b>").append(string.substring(i)).append("\n\n");
//                    break;
//                }
//            }
//        }
//        programText = sb.toString();
//        String text = poolMesage.getSendMessage().getText()
//                .replaceAll("event_text"         , event.getEVENT_NAME())
//                .replaceAll("event_address"      , event.getPLACE())
//                .replaceAll("event_time"         , date)
//                .replaceAll("event_contact"      , event.getCONTACT_INFORMATION())
//                .replaceAll("event_program"      , programText)
//                .replaceAll("event_dress_code"   , event.getDRESS_CODE())
//                .replaceAll("event_rules"        , event.getRULES())
//                .replaceAll("event_page"         , event.getPAGE());
//        if(event.getPAGE()!= null){
//            text = text+"\n\n<b>Регистрация</b>:"+event.getPAGE();
//        }
//        return text;

//        return "Уважаемые предприниматели!\n" + event.getEVENT_NAME() +
//                "\n" + event.getPLACE() + "\n Начало в " + date;
        return "<b>Ивент:</b> " + event.getEVENT_NAME() +
                "\n<b>Дата:</b> " + date + "\n<b>Подробности:</b> " + event.getPLACE() ;

}
}
