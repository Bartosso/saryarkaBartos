package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Event;
import com.turlygazhy.entity.Message;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.send.SendVideo;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eshu on 19.06.2017.
 */
public class ShowEventsCommand extends Command {
    private String targetList;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(update.getCallbackQuery()== null){return false;}
        long chatId = update.getCallbackQuery().getFrom().getId();
        String eventType = "";
        switch (update.getCallbackQuery().getData()){
            case "Было"       :
                targetList = "ENDED_EVENTS_LIST";
                eventType = "было";
                break;
            case "Будет"      :
                targetList = "EVENTS_LIST";
                eventType = "будет";
        }
        ListDao listDao = factory.getListDao(targetList);
        ArrayList<Event> events = listDao.getAllEvents(true);
        if (events.isEmpty()){
            Message message = messageDao.getMessage(84);
            bot.sendMessage(message.getSendMessage().setChatId(chatId));
            return true;
        }
        SendMessage sendMessage = new SendMessage().setReplyMarkup(getEventsViaButtons(events,eventType)).setChatId(chatId).setText("Запланированные ивенты");
        bot.sendMessage(sendMessage);

        return true;
    }
    //Создание клавы вне шаблона
    private ReplyKeyboard getEventsViaButtons(ArrayList<Event> eventArrayList, String eventType) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row;

        for (Event event : eventArrayList) {
            row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            String buttonText = event.getEVENT_NAME();
            button.setText(buttonText);
            button.setCallbackData("get_event" + ":" + event.getId() + "/" + eventType);
            row.add(button);
            rows.add(row);
        }
        keyboard.setKeyboard(rows);
        return keyboard;

    }
}
