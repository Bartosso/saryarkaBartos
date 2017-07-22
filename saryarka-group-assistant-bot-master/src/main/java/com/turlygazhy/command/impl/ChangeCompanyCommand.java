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
public class ChangeCompanyCommand extends Command {
    private String company;
    private WaitingType waitingType;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Message updateMessage = update.getMessage();
        if (waitingType != null) {
            switch (waitingType) {
                case COMPANY_NAME:
                    company = updateMessage.getText();
                    break;
            }
        }
        Long chatId = updateMessage.getChatId();
        if (company == null) {
            sendMessage(54, chatId, bot);
            waitingType = WaitingType.COMPANY_NAME;
            return false;
        }
        boolean memberAdded = memberDao.isMemberAdded(Math.toIntExact(chatId));
        if (!memberAdded) {
            MemberChangedInfoButNotAddedToSheetsCommand memberChangedInfoButNotAddedToSheetsCommand = new MemberChangedInfoButNotAddedToSheetsCommand(chatId);
            memberChangedInfoButNotAddedToSheetsCommand.execute(update, bot);
        }

        memberDao.updateCompany(updateMessage.getFrom().getId(), company);
        ShowInfoAboutMemberCommand showInfoAboutMemberCommand = new ShowInfoAboutMemberCommand();
        showInfoAboutMemberCommand.setMessageId(8);
        showInfoAboutMemberCommand.execute(update, bot);
        return true;
    }
}
