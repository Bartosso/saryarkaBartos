package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by user on 2/7/17.
 */
public class ChangePhoneNumberCommand extends Command {
    private boolean expectContact;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Message updateMessage = update.getMessage();
        Long chatId = updateMessage.getChatId();
        Integer userId = updateMessage.getFrom().getId();
        if (expectContact) {
            Contact contact = updateMessage.getContact();
            if (contact == null) {
                memberDao.updatePhoneNumber(userId, updateMessage.getText());
            } else {
                memberDao.updatePhoneNumber(userId, contact);
            }
            ShowInfoAboutMemberCommand showInfoAboutMemberCommand = new ShowInfoAboutMemberCommand();
            showInfoAboutMemberCommand.setMessageId(8);
            showInfoAboutMemberCommand.execute(update, bot);
            return true;
        }
        boolean memberAdded = memberDao.isMemberAdded(Math.toIntExact(chatId));
        if (!memberAdded) {
            MemberChangedInfoButNotAddedToSheetsCommand memberChangedInfoButNotAddedToSheetsCommand = new MemberChangedInfoButNotAddedToSheetsCommand(chatId);
            memberChangedInfoButNotAddedToSheetsCommand.execute(update, bot);
        }

        sendMessage(39, chatId, bot);
        expectContact = true;
        return false;
    }
}
