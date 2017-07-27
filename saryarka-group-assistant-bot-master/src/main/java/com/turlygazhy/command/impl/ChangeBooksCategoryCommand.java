package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChangeBooksCategoryCommand extends Command {
    private boolean expectNewValue;
    private long                 chatId;
    private ArrayList<String>    categories;
    private ListDao              listDao;
    private int                  categoryId;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (expectNewValue){
            if(update.hasMessage()){
                if (update.getMessage().hasText()){
                    listDao.changeBooksCategory(categories.get(categoryId), update.getMessage().getText());
                    categories = listDao.getBooksCategories();
                    bot.sendMessage(messageDao.getMessage(30).getSendMessage().setChatId(chatId)
                    .setReplyMarkup(getCategories(categories)));
                    expectNewValue = false;
                    return false;
                }
                else {
                    bot.sendMessage(new SendMessage(chatId, "Ошибка! Текст не обнаружен!"));
                    return false;
                }
            }
            else {
                bot.sendMessage(new SendMessage(chatId, "Ошибка! Вам нужно отправить текст с новым названием категории!"));
                return false;
            }
        }
        else {
            if (update.hasMessage()){
                chatId          = update.getMessage().getChatId();
                listDao         = factory.getListDao("BOOKS");
                categories      = listDao.getBooksCategories();
                bot.sendMessage(new SendMessage(chatId,"Выберите категорию которую хотите переименовать")
                .setReplyMarkup(getCategories(categories)));
                return false;
            }
            if (update.hasCallbackQuery()){
                if (update.getCallbackQuery().getData().contains("editBookCat")){
                    bot.deleteMessage(new DeleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId()));
                expectNewValue = true;
                categoryId     = Integer.parseInt(update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData()
                        .indexOf(":")+1));
                bot.sendMessage(new SendMessage(chatId,"Введите новое значение"));
                return false;}
            }


        }
        return true;
    }

    private InlineKeyboardMarkup getCategories(ArrayList<String> categories) throws SQLException {
        ArrayList<InlineKeyboardButton>       row;
        ArrayList<List<InlineKeyboardButton>> rows     = new ArrayList<>();
        InlineKeyboardMarkup                  keyboard = new InlineKeyboardMarkup();
        for(int i = 0; i<categories.size(); i++){
            row      = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(categories.get(i));
            button.setCallbackData("editBookCat:" + i);

            row.add(button);
            rows.add(row);
        }

        return keyboard.setKeyboard(rows);
    }
}
