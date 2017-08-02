package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Vacancy;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Eshu on 28.06.2017.
 */
@SuppressWarnings("FieldCanBeLocal")
public class ShowVacanciesBySferaCommand extends Command {
    private int    step = 0;
    private String sfera;
    private ArrayList<Vacancy> vacancyArrayList;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(step == 0){
            bot.sendMessage(new SendMessage(update.getMessage().getChatId(),"Введите название специальности по которой вы " +
                    "хотите искать вакансии (например юрист)"));
            step = 1;
            return false;
        }
        if(step == 1){
            long chatId      = update.getMessage().getChatId();
            sfera            = update.getMessage().getText();
            vacancyArrayList = factory.getListDao("VACANCIES").getAllVacancyBySfera(sfera,true);
            if(vacancyArrayList.isEmpty()){
                bot.sendMessage(new SendMessage(chatId,"К сожалению вакансий соответствующих вашему критерию не найдено"));
                return true;
            }
            bot.sendMessage(new SendMessage(chatId, "Список вакансий соответствующих вашему критерию").setReplyMarkup(
                    getVacancyViaButtons(vacancyArrayList)));

        }
        step = 0;
        return true;
    }

}
