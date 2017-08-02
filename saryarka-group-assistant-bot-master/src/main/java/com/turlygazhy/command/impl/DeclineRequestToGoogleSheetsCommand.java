package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by user on 2/14/17.
 */
public class DeclineRequestToGoogleSheetsCommand extends Command {
    private String chatId;
    private boolean commentAsked;

    public DeclineRequestToGoogleSheetsCommand(String chatId) {
        super();
        this.chatId = chatId;
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (!commentAsked) {
            bot.execute(new DeleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId()));
            sendMessage(61, update.getCallbackQuery().getMessage().getChatId(), bot);
            commentAsked = true;
            return false;
        }
        Message updateMessage = update.getMessage();
        sendMessage(updateMessage.getText(), Long.parseLong(chatId), bot);
        sendMessage(62, updateMessage.getChatId(), bot);
        return true;
    }
}
