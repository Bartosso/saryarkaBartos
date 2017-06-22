package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Event;
import com.turlygazhy.entity.ListData;
import com.turlygazhy.entity.Member;
import com.turlygazhy.entity.Message;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Eshu on 16.06.2017.
 */
public class GetAllFromListCommand extends Command {
    private String targetList;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(update.getCallbackQuery()== null){
            return false;
        }
        long chatId = update.getCallbackQuery().getFrom().getId();
        String requestType    = null;
        String additionalText = null;
        switch (update.getCallbackQuery().getData()){
            case "Категория ищу"       :
                targetList     = "REQUESTS_LIST";
                requestType    = "ищет";
                additionalText = "поиска";
                break;
            case "Категория Предлагаю" :
                targetList     = "OFFER_LIST";
                requestType    = "предлагает";
                additionalText = "предложений";
        }
        ListDao listDao = factory.getListDao(targetList);
        ArrayList<ListData> listDataList = listDao.getAllFromList();
        if(listDataList.isEmpty()){
            Message message = messageDao.getMessage(84);
            bot.sendMessage(message.getSendMessage().setChatId(chatId));
            return true;
        }

        ReplyKeyboard keyboard  = getTendersViaButtons(listDataList, requestType);
        SendMessage sendMessage = new SendMessage().setChatId(chatId).setText("Список "+additionalText).setReplyMarkup(keyboard);
        bot.sendMessage(sendMessage);


        return true;
    }
    //Создание клавы вне шаблона
    private ReplyKeyboard getTendersViaButtons(ArrayList<ListData> listDataArrayList, String listType) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row;

        for (ListData listData : listDataArrayList) {
            row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            String buttonText = listData.getText();
            button.setText(buttonText);
            button.setCallbackData("get_tender" + ":" + listData.getId() + "/" + listType);
            row.add(button);
            rows.add(row);
        }
        keyboard.setKeyboard(rows);
        return keyboard;

    }
}
