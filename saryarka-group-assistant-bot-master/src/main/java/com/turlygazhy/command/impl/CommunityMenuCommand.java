package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Message;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
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
        Message message         = messageDao.getMessage(messageId);
        if(message.getSendPhoto()!=null){
            bot.sendPhoto(new SendPhoto().setChatId(chatId).setPhoto(message.getSendPhoto().getPhoto()));
        }
        if(memberDao.isMemberAdded(Math.toIntExact(chatId))){
        SendMessage sendMessage = message.getSendMessage().setChatId(chatId).setReplyMarkup(
                keyboardMarkUpDao.select(messageDao.getMessage(messageId).getKeyboardMarkUpId()))
                .setParseMode(ParseMode.HTML);
            bot.execute(sendMessage);
        }
                else {
            SendMessage sendMessage = messageDao.getMessage(43).getSendMessage().setChatId(chatId);
            bot.execute(sendMessage);
        }
        return true;
    }
}
