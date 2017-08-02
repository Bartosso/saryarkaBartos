package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public class CurfewCommand extends Command {
    private long   chatId;
    private String messageText;
    private String username;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(update.hasMessage()){
            chatId      = update.getMessage().getFrom().getId();
            messageText = update.getMessage().getText();

            username    = "@" + update.getMessage().getFrom().getUserName();
            if (username.equals("@null")){
                username = update.getMessage().getFrom().getFirstName();
            }

            bot.deleteMessage(new DeleteMessage(update.getMessage().getChatId(), update.getMessage().getMessageId()));
        }
        if(update.hasCallbackQuery()){
            chatId      = update.getCallbackQuery().getFrom().getId();
            messageText = update.getCallbackQuery().getMessage().getText();

               username = "@" + update.getCallbackQuery().getFrom().getUserName();
               if (username.equals("@null")){
                   username = update.getCallbackQuery().getFrom().getFirstName();
               }

           bot.deleteMessage(new DeleteMessage(update.getCallbackQuery().getMessage().getChatId(),
                   update.getCallbackQuery().getMessage().getMessageId()));
        }
        if(update.hasChannelPost()){
            return true;
        }
        String textToUser = messageDao.getMessage(172).getSendMessage().getText()
                .replaceAll("User_name", username);
        bot.sendMessage(new SendMessage(chatId, textToUser));

        //Its important to send chatId of sender in this message
        String textToAdmin = "Сообщение в ночное время от " + username + "\nУникальный идентификатор ("+ chatId +") :\n" + messageText;
        bot.sendMessage(new SendMessage(getAdminChatId(), textToAdmin).setReplyMarkup(keyboardMarkUpDao.select(46)));
        return true;
    }
}
