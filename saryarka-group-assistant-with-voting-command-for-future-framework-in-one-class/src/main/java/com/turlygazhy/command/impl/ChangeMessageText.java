package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by user on 1/31/17.
 */
public class ChangeMessageText extends Command {
    private boolean linkAsked;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        if (!linkAsked) {
            sendMessage(29, chatId, bot);
            linkAsked = true;
            return false;
        }
        messageDao.updateText(message.getText(), messageId);
        sendMessage(30, chatId, bot);
        return true;
    }
}
