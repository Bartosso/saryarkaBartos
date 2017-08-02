package com.turlygazhy.reminder.timer_task;

import com.turlygazhy.Bot;
import com.turlygazhy.reminder.Reminder;
import com.turlygazhy.tool.DateUtil;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;

public class MorningTask extends AbstractTask {
    public MorningTask(Bot bot, Reminder reminder) {
        super(bot, reminder);
    }

    @Override
    public void run() {
        bot.setCurfew(false);
        ArrayList<SendMessage> messages = reminder.getMessagesToMorning();
        for (SendMessage sendMessage : messages){
            try {
                bot.sendMessage(sendMessage);
            } catch (TelegramApiException ignored) {
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        reminder.setMorningTask(DateUtil.getNextMorning());
    }
}
