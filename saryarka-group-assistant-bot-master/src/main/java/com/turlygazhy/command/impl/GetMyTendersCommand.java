package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.ListData;
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
 * Created by Eshu on 21.06.2017.
 */
public class GetMyTendersCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        String chatId   = String.valueOf(update.getMessage().getFrom().getId());
        String memberId = String.valueOf(memberDao.getMember(chatId).getId());
        ArrayList<ListData> arrayDataRequest = factory.getListDao("REQUESTS_LIST").getAllListDataByMemberId(memberId);
        ArrayList<ListData> arrayDataOffer   = factory.getListDao("OFFER_LIST").getAllListDataByMemberId(memberId);

        if(!arrayDataRequest.isEmpty()){
            SendMessage sendMessage = new SendMessage().setReplyMarkup(getTendersViaButtons(arrayDataRequest,"ищет"))
                    .setText("Ваши запросы").setChatId(chatId);
            bot.sendMessage(sendMessage);
        }
        if(arrayDataRequest.isEmpty()){
            SendMessage sendMessage = new SendMessage().setChatId(chatId).setText("К сожалению у вас нет запросов");
            bot.sendMessage(sendMessage);
        }
        if(!arrayDataOffer.isEmpty()){
            SendMessage sendMessage = new SendMessage().setReplyMarkup(getTendersViaButtons(arrayDataOffer,"предлагает"))
                    .setText("Ваши предложения").setChatId(chatId);
            bot.sendMessage(sendMessage);
        }
        if(arrayDataOffer.isEmpty()){
            SendMessage sendMessage = new SendMessage().setChatId(chatId).setText("К сожалению у вас нет предложений");
            bot.sendMessage(sendMessage);
        }
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
