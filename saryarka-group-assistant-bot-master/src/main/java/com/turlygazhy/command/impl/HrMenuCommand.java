package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 26.06.2017.
 */
public class HrMenuCommand extends Command {
    private long chatId;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
        if (updateMessage == null) {
            updateMessage = update.getCallbackQuery().getMessage();
        }
        chatId = updateMessage.getChatId();
        if(updateMessage.getText().equals(buttonDao.getButtonText(109))){
            return getMainHrMenu(bot,chatId);
        }
        if(updateMessage.getText().equals(buttonDao.getButtonText(110))){
            return getFindMenu(bot,chatId);
        }
        if(updateMessage.getText().equals(buttonDao.getButtonText(111))){
            return getMyMenu(bot,chatId);
        }
       return true;
    }


    private boolean getMainHrMenu(Bot bot, long chatId) throws SQLException, TelegramApiException {
        SendMessage sendMessage = messageDao.getMessage(messageId).getSendMessage();
        sendMessage.setChatId(chatId).setReplyMarkup(keyboardMarkUpDao
                .select(messageDao.getMessage(messageId).getKeyboardMarkUpId()));
        bot.sendMessage(sendMessage);
        return true;
    }

    private boolean getFindMenu(Bot bot, long chatId) throws SQLException, TelegramApiException {
        SendMessage sendMessage = messageDao.getMessage(110).getSendMessage();
        sendMessage.setChatId(chatId).setReplyMarkup(keyboardMarkUpDao.select(
                messageDao.getMessage(110).getKeyboardMarkUpId()));
        bot.sendMessage(sendMessage);
        return true;
    }

    private boolean getMyMenu(Bot bot, long chatId) throws SQLException, TelegramApiException {
        SendMessage sendMessage = messageDao.getMessage(111).getSendMessage();
        sendMessage.setChatId(chatId).setReplyMarkup(keyboardMarkUpDao.select(
                messageDao.getMessage(111).getKeyboardMarkUpId()));
        bot.sendMessage(sendMessage);
        return true;
    }
}
