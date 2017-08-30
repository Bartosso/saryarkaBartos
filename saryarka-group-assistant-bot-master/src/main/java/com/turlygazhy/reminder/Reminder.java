package com.turlygazhy.reminder;

import com.turlygazhy.Bot;
import com.turlygazhy.reminder.timer_task.*;
import com.turlygazhy.tool.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

/**
 * Created by Yerassyl_Turlygazhy on 02-Mar-17.
 */
public class Reminder {
    private static final Logger logger = LoggerFactory.getLogger(Reminder.class);

    private Bot bot;
    private Timer timer = new Timer(true);
    private ArrayList<SendMessage> messagesToMorning;

    public Reminder(Bot bot) {
        this.bot = bot;
        setCheckEveryNightDb(0);
        setNightTask(DateUtil.getThisNight());
        setMorningTask(DateUtil.getNextMorning());
        setRevokeInviteLinkInIntervalTask();
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


//    public void setEndOfMonthTask(Date endOfMonth){
//        logger.info("New end of month task at " + endOfMonth);
//
//        EndOfMonthTask endOfMonthTask = new EndOfMonthTask(bot, this);
//        timer.schedule(endOfMonthTask, endOfMonth);
//    }

    public void setMorningTask(Date morningDate){
        messagesToMorning = new ArrayList<>();
        logger.info("New morning task at " + morningDate);

        MorningTask morningTask = new MorningTask(bot, this);
        timer.schedule(morningTask, morningDate);
    }

    public void setNightTask(Date nightDate){
        logger.info("New night task at " + nightDate);

        NightTask nightTask = new NightTask(bot, this);
        timer.schedule(nightTask, nightDate);
    }

    private void setRevokeInviteLinkInIntervalTask(){
        RevokeInviteLinkInIntervalTask revokeInviteLinkInIntervalTask = new RevokeInviteLinkInIntervalTask(bot, this);
        long interval = 60000;
        timer.schedule(revokeInviteLinkInIntervalTask,new Date(), interval);
    }

   public void setEveryHourTask(Date nextHour, long chatId){
        timer.schedule(new EveryHourTask(bot,this,chatId), nextHour);
    }

    public void addNewMessageToMorning(SendMessage sendMessage){
        messagesToMorning.add(sendMessage);
    }

    public ArrayList<SendMessage> getMessagesToMorning(){
        return messagesToMorning;
    }

    public Logger getLogger(){
        return logger;
    }
}