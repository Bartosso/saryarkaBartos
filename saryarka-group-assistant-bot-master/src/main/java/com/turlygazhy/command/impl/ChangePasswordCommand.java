package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by user on 2/21/17.
 */
public class ChangePasswordCommand extends Command {
    @SuppressWarnings("FieldCanBeLocal")
    private int buttonId = 19;
    private WaitingType waitingType;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Message updateMessage = update.getMessage();
        Long chatId = updateMessage.getChatId();
        if (waitingType == null) {
            sendMessage(74, chatId, bot);
            waitingType = WaitingType.NEW_PASSWORD;
            return false;
        }
        String text = updateMessage.getText();
        switch (waitingType) {
            case NEW_PASSWORD:
                buttonDao.updateButtonText(buttonId, text);
                sendMessage(75, chatId, bot);
                return true;
        }
        return false;
    }
}
