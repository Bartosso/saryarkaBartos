package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 23.06.2017.
 */
public class CommunityMenuCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        long chatId             = update.getMessage().getChatId();
        SendMessage sendMessage = messageDao.getMessage(messageId).getSendMessage().setChatId(chatId).setReplyMarkup(
                keyboardMarkUpDao.select(messageDao.getMessage(messageId).getKeyboardMarkUpId()))
                .setParseMode(ParseMode.HTML);
        bot.sendMessage(sendMessage);
        return true;
    }
}
