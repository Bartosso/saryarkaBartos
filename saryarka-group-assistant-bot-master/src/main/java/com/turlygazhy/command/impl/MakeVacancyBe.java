package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 02.07.2017.
 */
public class MakeVacancyBe extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        ListDao listDao  = factory.getListDao("VACANCIES");
        long chatId      = update.getCallbackQuery().getFrom().getId();
        String vacancyId = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData()
        .indexOf(":")+1);
        bot.execute(new DeleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId()));
        if(!listDao.isStuffActive(vacancyId)){
        factory.getListDao("VACANCIES").makeStuffBe(vacancyId);
        sendMessage(142,chatId,bot);
        return true;
        }
        else {
            sendMessage(143,chatId,bot);
            return true;
        }
    }
}
