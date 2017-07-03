package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 16.06.2017.
 */
public class MakeBIdCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        long chatID = update.getMessage().getChatId();
       com.turlygazhy.entity.Message message = messageDao.getMessage(82);
       SendMessage sendMessage = message.getSendMessage().setChatId(chatID).setReplyMarkup(keyboardMarkUpDao.
       select(message.getKeyboardMarkUpId()));
       bot.sendMessage(sendMessage);

        return true;
    }
}
