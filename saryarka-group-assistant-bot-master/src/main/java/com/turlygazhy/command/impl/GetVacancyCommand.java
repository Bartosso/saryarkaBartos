package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Vacancy;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 28.06.2017.
 */
public class GetVacancyCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        String vacancyId        = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1);
        long   chatId           = update.getCallbackQuery().getFrom().getId();
        Vacancy vacancy         = factory.getListDao("VACANCIES").getVacancy(vacancyId);
        String pattern          = messageDao.getMessage(119).getSendMessage().getText()
                .replaceAll("company_name"       , vacancy.getCompanyName())
                .replaceAll("sfera"              , vacancy.getSfera())
                .replaceAll("experience"         , vacancy.getExperience())
                .replaceAll("place"              , vacancy.getPlace())
                .replaceAll("working_coniditions", vacancy.getWorkingConditions())
                .replaceAll("salary"             , vacancy.getSalary())
                .replaceAll("contact_info"       , vacancy.getContact());
        SendMessage sendMessage = new SendMessage().setChatId(chatId).setText(pattern);
        if(vacancy.getPhoto()!=null){
            SendPhoto sendPhoto = new SendPhoto().setPhoto(vacancy.getPhoto());
            bot.sendPhoto(sendPhoto.setChatId(chatId));
        }
        bot.sendMessage(sendMessage);
        return true;
    }
//    private ReplyKeyboard getKeyForOwner(Vacancy vacancy)
}
