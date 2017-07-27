package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Button;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eshu on 18.07.2017.
 */
public class ShowAllButtonsToChange extends Command {
    private int                             page;
    private long                            chatId;
    private ArrayList<InlineKeyboardMarkup> keyboards;


    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(update.hasCallbackQuery()){
            if (update.getCallbackQuery().getData().equals("nextPageInButtons")){
            page++;
            bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setChatId(chatId).setReplyMarkup(keyboards.get(page))
            .setMessageId(update.getCallbackQuery().getMessage().getMessageId()));
            return false;
            }
            if(update.getCallbackQuery().getData().equals("previousPageInButtons")){
                page--;
                bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setChatId(chatId).setReplyMarkup(keyboards.get(page))
                        .setMessageId(update.getCallbackQuery().getMessage().getMessageId()));
                return false;
            }

        }
     if(update.getMessage().getText().equals(buttonDao.getButtonText(208))){
         keyboards = getAllButtonsToChange(buttonDao.getAllButtons(true));
         chatId =  update.getMessage().getChatId();
         page = 0;
         bot.sendMessage(new SendMessage().setChatId(chatId).setText(messageDao.getMessage(messageId).getSendMessage().getText()
         ).setReplyMarkup(keyboards.get(page)));
     }

        return false;
    }

    private ArrayList<InlineKeyboardMarkup> getAllButtonsToChange(ArrayList<Button> buttonsArrayList){
        ArrayList<InlineKeyboardMarkup> keyboards = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows;
        int pages =  1+(buttonsArrayList.size() / 25);
        int counter = 0;
        for (int b = 0; b<pages;b++){
            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
            rows = new ArrayList<>();
        for (int i = 0; i < 25; i++){
            if(counter<buttonsArrayList.size()){
            ArrayList<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton buttonToChange = new InlineKeyboardButton();
            buttonToChange.setText(buttonsArrayList.get(counter).getText());
            buttonToChange.setCallbackData("changeButtonText:"+buttonsArrayList.get(counter).getId());
            row.add(buttonToChange);
            rows.add(row);
            counter++;}
        }

            ArrayList<InlineKeyboardButton> row = new ArrayList<>();

            if(keyboards.size()!=0){
                InlineKeyboardButton buttonToPreviousPage = new InlineKeyboardButton();
                buttonToPreviousPage.setText("⬅️ Сюда");
                buttonToPreviousPage.setCallbackData("previousPageInButtons");
                row.add(buttonToPreviousPage);}


            if(pages>(b+1)){
            InlineKeyboardButton buttonToNextPage = new InlineKeyboardButton();
            buttonToNextPage.setText("Туда ➡️");
            buttonToNextPage.setCallbackData("nextPageInButtons");
            row.add(buttonToNextPage);
        }

            

            rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        keyboards.add(keyboardMarkup);
        }
        return keyboards;
    }
}
