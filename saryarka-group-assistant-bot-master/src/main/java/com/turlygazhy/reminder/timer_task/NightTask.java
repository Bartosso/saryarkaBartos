package com.turlygazhy.reminder.timer_task;

import com.turlygazhy.Bot;
import com.turlygazhy.reminder.Reminder;
import com.turlygazhy.tool.DateUtil;



public class NightTask extends AbstractTask {
    public NightTask(Bot bot, Reminder reminder) {
        super(bot, reminder);
    }

    @Override
    public void run() {
        bot.setCurfew(true);
        reminder.setNightTask(DateUtil.getNextNight());
    }
}
