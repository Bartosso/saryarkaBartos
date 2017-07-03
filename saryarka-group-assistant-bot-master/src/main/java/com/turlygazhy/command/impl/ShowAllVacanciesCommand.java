package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Vacancy;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eshu on 28.06.2017.
 */
public class ShowAllVacanciesCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        ArrayList<Vacancy> vacancies = factory.getListDao("VACANCIES").getAllVacancy(true);
        if(vacancies.isEmpty()){
            SendMessage sendFalse   = new SendMessage().setChatId(update.getMessage().getChatId()).setText("Вакансий не обнаружено");
            bot.sendMessage(sendFalse);
            return true;
        }
        else{
            SendMessage sendSuccess = new SendMessage().setChatId(update.getMessage().getChatId()).setText("Все вакансии")
                    .setReplyMarkup(getVacancyViaButtons(vacancies));
            bot.sendMessage(sendSuccess);
            return true;
            }
    }

//    private ReplyKeyboard getVacancyViaButtons(ArrayList<Vacancy> arrayList){
//        InlineKeyboardMarkup keyboard         = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
//        List<InlineKeyboardButton> row;
//
//
//        for(Vacancy vacancy: arrayList) {
//            row        = new ArrayList<>();
//            InlineKeyboardButton vacancyButton = new InlineKeyboardButton();
//            vacancyButton.setText(vacancy.getCompanyName()+ ". Кто нужен: "+ vacancy.getSfera() + " ("+ vacancy.getSalary() +")");
//            vacancyButton.setCallbackData("get_vacancy" + ":" + vacancy.getId());
//            row.add(vacancyButton);
//            rows.add(row);
//
//        }
//        keyboard.setKeyboard(rows);
//        return keyboard;
//    }
}
