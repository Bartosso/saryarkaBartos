package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Book;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by Eshu on 30.06.2017.
 */
public class ShowDownloadedBooksCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        long chatId       = update.getMessage().getChatId();
        String memberId   = memberDao.getMemberId(chatId);
        String[] booksIds = memberDao.getMemberBooks(memberId);
        if(booksIds == null){
            sendMessage(131,chatId,bot);
            return true;
        }
        else {
            bot.sendMessage(messageDao.getMessage(133).getSendMessage().setChatId(chatId)
            .setReplyMarkup(getDownloadedBooksViaButtons(booksIds)));
            return true;
        }
    }

    private ReplyKeyboard getDownloadedBooksViaButtons(String[] booksIds) throws SQLException {
        InlineKeyboardMarkup keyboard         = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row;
        ListDao listDao = factory.getListDao("BOOKS");
        LinkedHashSet<String> books = new LinkedHashSet<>(Arrays.asList(booksIds));
        for(String string: books) {
            row        = new ArrayList<>();
            Book book  = listDao.getBookById(string);
            if(book == null){
               break;
            }
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
