package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Book;
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
 * Created by Eshu on 30.06.2017.
 */
public class ShowBooksToMemberCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        long chatId = update.getMessage().getChatId();
        ArrayList<Book> bookArrayList = factory.getListDao("BOOKS").getAllBooks();
        if(bookArrayList.isEmpty()){
            sendMessage(131,chatId, bot);
            return true;
        }
        else {
            bot.sendMessage(new SendMessage().setChatId(chatId)
            .setReplyMarkup(getBooksViaButtons(bookArrayList)).setText(bookArrayList.get(0).getCategory()));
            return true;
        }
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
