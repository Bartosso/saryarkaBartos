package com.turlygazhy.reminder.timer_task;

import com.turlygazhy.Bot;
import com.turlygazhy.reminder.Reminder;

public class EveryHourTask extends AbstractTask {
    public EveryHourTask(Bot bot, Reminder reminder) {
        super(bot, reminder);
    }

    @Override
    public void run() {
    bot.restartFloodCount();
    }
}
