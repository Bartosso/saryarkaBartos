package com.turlygazhy.tool;

import com.turlygazhy.dao.impl.MessageDao;
import com.turlygazhy.entity.Event;
import com.turlygazhy.entity.Message;

import java.sql.SQLException;

/**
 * Created by Eshu on 06.07.2017.
 */
public class EventAnonceUtil {
    public static String getEventWithPatternNoByAdmin(Event event, MessageDao messageDao) throws SQLException {
//        com.turlygazhy.entity.Message poolMesage = messageDao.getMessage(92);
        String date = event.getWHEN().substring(0,2) + " " + DateUtil.getMonthInRussian(Integer.parseInt(event
                .getWHEN().substring(3, 5)))
                +event.getWHEN().substring(event.getWHEN().indexOf(" "));
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

        return "Уважаемые предприниматели!\n" + event.getEVENT_NAME() +
                "\n" + event.getPLACE() + "\nНачало в " + date;
    }

    public static String getEventWithPatternByAdmin(Event event, MessageDao messageDao) throws SQLException {
//        com.turlygazhy.entity.Message poolMesage = messageDao.getMessage(139);
        String date = event.getWHEN().substring(0,2) + " " + DateUtil.getMonthInRussian(Integer.parseInt(event
                .getWHEN().substring(3, 5)))
                +event.getWHEN().substring(event.getWHEN().indexOf(" "));
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

        return "Уважаемые предприниматели!\n" + event.getEVENT_NAME() +
                "\n" + event.getPLACE() + "\n Начало в " + date;

}
}
