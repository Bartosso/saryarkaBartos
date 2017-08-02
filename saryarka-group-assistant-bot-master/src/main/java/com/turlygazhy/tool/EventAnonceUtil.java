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
        String date = localDateTime.getDayOfMonth() + " " + DateUtil.getMonthInRussian(localDateTime.getMonthValue())
                + ", " + DateUtil.dayOfWeekInRussian(localDateTime.getDayOfWeek().getValue());
        return "<b>Ивент:</b> " + event.getEVENT_NAME() +
                "\n<b>Дата:</b> " + date + "\n<b>Подробности:</b> " + event.getPLACE() ;
    }

    public static String getEventWithPatternByAdmin(Event event, MessageDao messageDao) throws SQLException {
        return getEventWithPatternNoByAdmin(event, messageDao);

}
}
