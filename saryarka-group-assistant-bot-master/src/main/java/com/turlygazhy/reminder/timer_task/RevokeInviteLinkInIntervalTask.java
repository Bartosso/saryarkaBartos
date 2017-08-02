package com.turlygazhy.reminder.timer_task;

import com.turlygazhy.Bot;
import com.turlygazhy.reminder.Reminder;
import org.telegram.telegrambots.api.methods.groupadministration.ExportChatInviteLink;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public class RevokeInviteLinkInIntervalTask extends AbstractTask {
    public RevokeInviteLinkInIntervalTask(Bot bot, Reminder reminder) {
        super(bot, reminder);
    }

    @Override
    public void run() {
        ExportChatInviteLink exportChatInviteLink = new ExportChatInviteLink(bot.getGROUP_FOR_VOTE());
        try {
            String newLink = bot.execute(exportChatInviteLink);
            buttonDao.updateButtonUrl(16, newLink);
            buttonDao.updateButtonUrl(62, newLink);
        } catch (SQLException | TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
