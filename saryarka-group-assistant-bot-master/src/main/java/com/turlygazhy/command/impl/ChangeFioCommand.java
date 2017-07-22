package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by user on 2/2/17.
 */
public class ChangeFioCommand extends Command {
    private String fio;
    private WaitingType waitingType;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Message updateMessage = update.getMessage();
        if (waitingType != null) {
            switch (waitingType) {
                case FIO:
                    fio = updateMessage.getText();
                    break;
            }
        }
        Long chatId = updateMessage.getChatId();
        if (fio == null) {
            sendMessage(52, chatId, bot);
            waitingType = WaitingType.FIO;
            return false;
        }
        boolean memberAdded = memberDao.isMemberAdded(Math.toIntExact(chatId));
        if (!memberAdded) {
            MemberChangedInfoButNotAddedToSheetsCommand memberChangedInfoButNotAddedToSheetsCommand = new MemberChangedInfoButNotAddedToSheetsCommand(chatId);
            memberChangedInfoButNotAddedToSheetsCommand.execute(update, bot);
        }

        memberDao.updateFio(updateMessage.getFrom().getId(), fio);
        ShowInfoAboutMemberCommand showInfoAboutMemberCommand = new ShowInfoAboutMemberCommand();
        showInfoAboutMemberCommand.setMessageId(8);
        showInfoAboutMemberCommand.execute(update, bot);
        return true;
    }
}
