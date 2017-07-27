package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.MessageElement;
import com.turlygazhy.tool.vanderkastTools.Constructors.InlineKeyboardConstructor;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eshu on 30.06.2017.
 */
public class AddBookAdminCommand extends Command {
    private int                step = 0;
    private long               chatId;
    private MessageElement     expectedMessageElement;
    private String             bookName;
    private String             book;
    private String             category;
    private ArrayList<String>  categories;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(expectedMessageElement!= null){
            switch (step){
                case 0:
                    if(!update.getMessage().hasText()){
                        bot.sendMessage(new SendMessage(chatId,"Напишите название книги!"));
                        return false;
                    }
                    bookName = update.getMessage().getText();
                    step = 1;

                    break;
                case 1:
                    try{
                    book     = update.getMessage().getDocument().getFileId();
                    step = 2;} catch (NullPointerException e){
                        bot.sendMessage(new SendMessage(chatId, "Прикрепите книгу!"));
                        return false;
                    }
                    break;
                case 2:
                    if(update.hasCallbackQuery()){
                        int categoryId = Integer.parseInt(update.getCallbackQuery().getData()
                        .substring(update.getCallbackQuery().getData().indexOf(":")+1));
                        category = categories.get(categoryId);
                        step = 3;
                    }
                    else {
                    try {
                        category = update.getMessage().getText();
                    } catch (Exception e){
                        if(update.getCallbackQuery().getData().equals(buttonDao.getButtonText(217))){
                        category = buttonDao.getButtonText(217);
                        step = 3;
                        }
                    }
                    }
                    step = 3;
            }
        }


        if(step == 0){
            chatId                = update.getMessage().getChatId();
            sendMessage(messageId, chatId, bot);
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if(step == 1){
            sendMessage(129,chatId,bot);
            return false;
        }
        if(step == 2){
//            sendMessage(166, chatId, bot);
            categories = factory.getListDao("BOOKS").getBooksCategories();
            categories.add("Без категории");
            bot.sendMessage(new SendMessage(chatId, messageDao.getMessage(166).getSendMessage().getText())
            .setReplyMarkup(getCategoriesViaButtons(categories)
//                    InlineKeyboardConstructor.getKeyboard(categories)
            ));
            return false;
        }
        if(step == 3){
            if(category==null){
                category="Без категории";
            }

            factory.getListDao("BOOKS").addNewBook(bookName,book, category);
            sendMessage(130, chatId,bot);
        }
        chatId = 0;
        step   = 0;
        categories = null;
        return true;
    }

    private InlineKeyboardMarkup getCategoriesViaButtons(ArrayList<String> categories){
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        ArrayList<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++){
            ArrayList<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(categories.get(i));
            button.setCallbackData("category:" + i);
            row.add(button);
            rows.add(row);
        }
        return keyboard.setKeyboard(rows);
    }
}
