package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.send.SendContact;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by user on 2/19/17.
 */
public class MakeMeAdminCommand extends Command {
    private WaitingType waitingType;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Message updateMessage = update.getMessage();
        Long chatId = updateMessage.getChatId();
        if (waitingType == null) {
            sendMessage(67, chatId, bot);
            waitingType = WaitingType.CONTACT;
            return false;
        }
        switch (waitingType) {
            case CONTACT:
                Long adminChatId = userDao.getAdminChatId();
                userDao.setAdminChatId(chatId);
                Contact contact = updateMessage.getContact();
                sendMessage(68, adminChatId, bot);
                bot.sendContact(new SendContact()
                        .setChatId(adminChatId)
                        .setFirstName(contact.getFirstName())
                        .setLastName(contact.getLastName())
                        .setPhoneNumber(contact.getPhoneNumber())
                );
                sendMessage(69, chatId, bot);
                return true;
        }
        return false;
    }
}
