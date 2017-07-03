package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Member;
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
 * Created by Eshu on 26.06.2017.
 */
public class GetMyVacanciesCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        String memberId = memberDao.getMemberId(update.getMessage().getChatId());
        ListDao listDao = factory.getListDao("VACANCIES");
        ArrayList<Vacancy> vacancyArrayList = listDao.getAllVacancyById(memberId,true);
        if(vacancyArrayList.isEmpty()){
            SendMessage sendFalse   = new SendMessage().setChatId(update.getMessage().getChatId()).setText("У вас нет активных вакансий");
            bot.sendMessage(sendFalse);
            return true;
        }
        else{
            SendMessage sendSuccess = new SendMessage().setChatId(update.getMessage().getChatId()).setText("Ваши вакансии")
            .setReplyMarkup(getVacancyViaButtonsForMy(vacancyArrayList));
            bot.sendMessage(sendSuccess);
        return true;
    }}
    private ReplyKeyboard getVacancyViaButtonsForMy(ArrayList<Vacancy> arrayList){
        InlineKeyboardMarkup keyboard         = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row;


        for(Vacancy vacancy: arrayList) {
            row        = new ArrayList<>();
            InlineKeyboardButton vacancyButton = new InlineKeyboardButton();
            vacancyButton.setText(vacancy.getCompanyName()+ ". Кто нужен: "+ vacancy.getSfera() + " ("+ vacancy.getSalary() +")");
            vacancyButton.setCallbackData("get_vacancy" + ":" + vacancy.getId());
            row.add(vacancyButton);
            rows.add(row);

            row        = new ArrayList<>();
            InlineKeyboardButton deleteVacancyButton = new InlineKeyboardButton();
            deleteVacancyButton.setText("Удалить эту вакансию ⬆️ ");
            deleteVacancyButton.setCallbackData("delete_vacancy" + ":" + vacancy.getId());
            row.add(deleteVacancyButton);
            rows.add(row);
        }
        keyboard.setKeyboard(rows);
        return keyboard;
    }
}
