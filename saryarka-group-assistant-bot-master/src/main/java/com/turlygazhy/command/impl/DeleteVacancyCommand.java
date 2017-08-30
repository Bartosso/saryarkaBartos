package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 28.06.2017.
 */
public class DeleteVacancyCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        long   chatId          = update.getCallbackQuery().getFrom().getId();
        String vacancyId       = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1);
        factory.getListDao("VACANCIES").deleteVacancy(vacancyId);
        bot.execute(new DeleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId()));
        bot.execute(new SendMessage(chatId, "Вакансия успешно удалена."));

        return true;
    }
}
