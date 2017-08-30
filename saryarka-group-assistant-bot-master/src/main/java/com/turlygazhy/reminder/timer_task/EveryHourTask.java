package com.turlygazhy.reminder.timer_task;

import com.turlygazhy.Bot;
import com.turlygazhy.reminder.Reminder;

public class EveryHourTask extends AbstractTask {
    private long chatId;
    public EveryHourTask(Bot bot, Reminder reminder, long chatId) {
        super(bot, reminder);
        this.chatId = chatId;
    }

    @Override
    public void run() {
    bot.restartFloodCountByChatId(chatId);
    }
}
