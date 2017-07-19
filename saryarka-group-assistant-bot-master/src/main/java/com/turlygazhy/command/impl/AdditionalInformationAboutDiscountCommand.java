package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Message;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 18.07.2017.
 */
public class AdditionalInformationAboutDiscountCommand extends Command {
    long chatId;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        chatId = update.getMessage().getChatId();
        Message message = messageDao.getMessage(messageId);
        if(message.getSendPhoto()!=null){
            bot.sendPhoto(new SendPhoto().setChatId(chatId).setPhoto(message.getSendPhoto().getPhoto()));
        }
        bot.sendMessage(message.getSendMessage().setChatId(chatId).setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId())));
        return true;
    }
}
