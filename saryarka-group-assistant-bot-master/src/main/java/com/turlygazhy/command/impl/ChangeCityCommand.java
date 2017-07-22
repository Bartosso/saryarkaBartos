package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 14.07.2017.
 */
public class ChangeCityCommand extends Command {
    private String city;
    private WaitingType waitingType;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Message updateMessage = update.getMessage();
        if (waitingType != null) {
            switch (waitingType) {
                case CITY:
                    city = updateMessage.getText();
                    break;
            }
        }
        Long chatId = updateMessage.getChatId();
        if (city == null) {
            sendMessage(159, chatId, bot);
            waitingType = WaitingType.CITY;
            return false;
        }


        memberDao.updateCity(updateMessage.getFrom().getId(), city);
        ShowInfoAboutMemberCommand showInfoAboutMemberCommand = new ShowInfoAboutMemberCommand();
        showInfoAboutMemberCommand.setMessageId(8);
        showInfoAboutMemberCommand.execute(update, bot);
        return true;
    }
}
