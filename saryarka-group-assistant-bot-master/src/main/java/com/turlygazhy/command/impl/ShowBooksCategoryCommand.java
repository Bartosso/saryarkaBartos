package com.turlygazhy.command.impl;

import Constructors.InlineKeyboardConstructor;
import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Book;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShowBooksCategoryCommand extends Command {
    private long chatId;
    private ArrayList<String> categories;
    private int step = 0;
    private ListDao listDao;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(step == 0){
        chatId = update.getMessage().getChatId();
        InlineKeyboardMarkup keyboard;
        try{
            keyboard = getCategories();

        }catch (Exception e){
            bot.sendMessage(new SendMessage(chatId, messageDao.getMessage(131).getSendMessage().getText()));
            return true;
        }
        bot.sendMessage(messageDao.getMessage(165).getSendMessage().setChatId(chatId)
        .setParseMode(ParseMode.HTML).setReplyMarkup(keyboard));
        step = 1;
        return false;
        }
        else {
            bot.deleteMessage(new DeleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId()));
            String categoryId = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1);
            ArrayList<Book> chosenBooks = listDao.getAllBooksInDistinctCategories(categories.get(Integer.parseInt(categoryId)));

            bot.sendMessage(new SendMessage(chatId,messageDao.getMessage(132).getSendMessage().getText())
            .setReplyMarkup(getBooksViaButtons(chosenBooks)));
            return true;
        }
    }
    private InlineKeyboardMarkup getCategories() throws SQLException {
        listDao = factory.getListDao("BOOKS");
        categories = listDao.getBooksCategories();
        ArrayList<String> buttonText  = new ArrayList<>();
        ArrayList<String> buttonsData = new ArrayList<>();
        for(int i = 0; i<categories.size(); i++){
            if(categories.get(i)==null){
                buttonText.add(buttonDao.getButtonText(217));
            }
            else {
                buttonText.add(categories.get(i));
            }
            buttonsData.add("getBookCat:" + i);
        }
        return InlineKeyboardConstructor.getKeyboard(buttonText, buttonsData);
    }
    private ReplyKeyboard getBooksViaButtons(ArrayList<Book> bookArrayList){
        InlineKeyboardMarkup keyboard         = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row;


        for(Book book: bookArrayList) {
            row        = new ArrayList<>();
            InlineKeyboardButton bookButton = new InlineKeyboardButton();
            bookButton.setText(book.getBookName());
            bookButton.setCallbackData("get_book" + ":" + book.getId());
            row.add(bookButton);
            rows.add(row);
        }
        keyboard.setKeyboard(rows);
        return keyboard;
    }
}
