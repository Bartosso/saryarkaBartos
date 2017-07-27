package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public class ChangeLastPuttedRowCommand extends Command {
    private boolean expectedInput;
    @SuppressWarnings("FieldCanBeLocal")
    private String  newLastPuttedRow;
    private long    chatId;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (expectedInput){
            try {
                //noinspection ResultOfMethodCallIgnored
                Integer.parseInt(update.getMessage().getText());
                newLastPuttedRow = update.getMessage().getText();
                constDao.update(3,newLastPuttedRow);
                bot.sendMessage(new SendMessage(chatId,"Данные сохранены"));
                return true;
            } catch (Exception e){
                bot.sendMessage(new SendMessage(chatId,"Введите цифру"));
                return false;
            }

        } else {
        chatId = update.getMessage().getChatId();
        bot.sendMessage(messageDao.getMessage(171).getSendMessage().setChatId(chatId));
        expectedInput = true;
        return false;
        }
    }
}
