package com.turlygazhy.reminder;

import com.turlygazhy.Bot;
import com.turlygazhy.reminder.timer_task.*;
import com.turlygazhy.tool.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Timer;

/**
 * Created by Yerassyl_Turlygazhy on 02-Mar-17.
 */
public class Reminder {
    private static final Logger logger = LoggerFactory.getLogger(Reminder.class);

    private Bot bot;
    private Timer timer = new Timer(true);

    public Reminder(Bot bot) {
        this.bot = bot;
        setCheckEveryNightDb(0);
    }

    public void setCheckEveryNightDb(int hour) {
        Date date = DateUtil.getHour(hour);
        logger.info("Next check db task set to " + date);

        CheckEveryNightDbTask checkEveryNightDbTask = new CheckEveryNightDbTask(bot, this);
        timer.schedule(checkEveryNightDbTask, date);
    }

    public void setRemindEventStartOneDay(Date eventDateStartMinusDay, long eventId){
        logger.info("New event remind before day at " + eventDateStartMinusDay);

        RemindEventStartOneDayTask remindEventStartOneDayTask = new RemindEventStartOneDayTask(bot,this,eventId);
        timer.schedule(remindEventStartOneDayTask, eventDateStartMinusDay);
    }

    public void setRemindEventsStartOneHour(Date eventsStartOneHour, long eventId){
        logger.info("New event remind before hour at " + eventsStartOneHour);

        RemindEventStartOneHourTask remindEventStartOneHourTask = new RemindEventStartOneHourTask(bot,this, eventId);
        timer.schedule(remindEventStartOneHourTask, eventsStartOneHour);
    }
    public Logger getLogger(){
        return logger;
    }
}