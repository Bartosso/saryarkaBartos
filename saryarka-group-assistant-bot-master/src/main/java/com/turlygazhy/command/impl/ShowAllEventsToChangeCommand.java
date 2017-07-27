package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Event;
import com.turlygazhy.tool.vanderkastTools.Constructors.InlineKeyboardConstructor;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShowAllEventsToChangeCommand extends Command {
    private int  page = 0;
    private long chatId;
    private ArrayList<InlineKeyboardMarkup> keyboards;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().equals("nextPageInEvents")){
                page++;
                bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setChatId(chatId).setReplyMarkup(keyboards.get(page))
                        .setMessageId(update.getCallbackQuery().getMessage().getMessageId()));
                return false;
            }
            if(update.getCallbackQuery().getData().equals("previousPageInEvents")){
                page--;
                bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setChatId(chatId).setReplyMarkup(keyboards.get(page))
                        .setMessageId(update.getCallbackQuery().getMessage().getMessageId()));
                return false;
            }
        }
        else {
            String chose = update.getMessage().getText();
            chatId       = update.getMessage().getChatId();
            if (chose.equals(buttonDao.getButtonText(227))) {
                ArrayList<Event> events = factory.getListDao("ENDED_EVENTS_LIST").getAllEvents(true);
                if(events.size()==0){
                    bot.sendMessage(messageDao.getMessage(38).getSendMessage().setChatId(chatId));
                    return true;
                }
                keyboards = getEventsForDeleteAndEdit(events, true);
                bot.sendMessage(new SendMessage().setChatId(chatId)
                        .setText(messageDao.getMessage(messageId).getSendMessage().getText()).setReplyMarkup(keyboards.get(page)));
            }
            if(chose.equals(buttonDao.getButtonText(228))) {
                ArrayList<Event> events = factory.getListDao("EVENTS_LIST").getAllEvents(true);
                if(events.size()==0){
                    bot.sendMessage(messageDao.getMessage(38).getSendMessage().setChatId(chatId));
                    return true;
                }
                keyboards = getEventsForDeleteAndEdit(events,false);
                bot.sendMessage(new SendMessage().setChatId(chatId)
                        .setText(messageDao.getMessage(messageId).getSendMessage().getText()).setReplyMarkup(keyboards.get(page)));
            }
            return false;
        }

        return true;
    }
    private ArrayList<InlineKeyboardMarkup> getEventsForDeleteAndEdit(ArrayList<Event> eventArrayList, boolean isEventEnded){
        ArrayList<InlineKeyboardMarkup> keyboards = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows;
        String whatKindOfEvent = "editEvent:";
        if (isEventEnded){
            whatKindOfEvent = "editEndedEvent:";
        }
        int pages =  1+(eventArrayList.size() / 5);
        int counter = 0;
        for (int b = 0; b<pages;b++){
            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
            rows = new ArrayList<>();
            for (int i = 0; i < 5; i++){
                if(counter<eventArrayList.size()){
                    ArrayList<InlineKeyboardButton> row = new ArrayList<>();
                    InlineKeyboardButton buttonToChange = new InlineKeyboardButton();
                    buttonToChange.setText(eventArrayList.get(counter).getEVENT_NAME());
                    buttonToChange.setCallbackData(whatKindOfEvent + eventArrayList.get(counter).getId());
                    row.add(buttonToChange);
                    rows.add(row);
                    counter++;}
            }

            ArrayList<InlineKeyboardButton> row = new ArrayList<>();

            if(keyboards.size()!=0){
                InlineKeyboardButton buttonToPreviousPage = new InlineKeyboardButton();
                buttonToPreviousPage.setText("⬅️ Сюда");
                buttonToPreviousPage.setCallbackData("previousPageInEvents");
                row.add(buttonToPreviousPage);}


            if(pages>(b+1)){
                InlineKeyboardButton buttonToNextPage = new InlineKeyboardButton();
                buttonToNextPage.setText("Туда ➡️");
                buttonToNextPage.setCallbackData("nextPageInEvents");
                row.add(buttonToNextPage);
            }



            rows.add(row);
            keyboardMarkup.setKeyboard(rows);
            keyboards.add(keyboardMarkup);
        }
        return keyboards;
    }
}
