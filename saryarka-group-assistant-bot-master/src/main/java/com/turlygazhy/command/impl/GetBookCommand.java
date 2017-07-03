package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Book;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 30.06.2017.
 */
public class GetBookCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        long   chatId = update.getCallbackQuery().getFrom().getId();
        String bookId = update.getCallbackQuery().getData().substring(update.getCallbackQuery()
        .getData().indexOf(":")+1);
        Book book     = factory.getListDao("BOOKS").getBookById(bookId);
        if(book == null){
            sendMessage("Упс! Книга больше не доступна!", chatId,bot);
            return true;
        }
        else{
            if (chatId!=getAdminChatId()){
                memberDao.addDownloadedBook(bookId, Integer.parseInt(memberDao.getMemberId(chatId)));
            }

            bot.sendDocument(new SendDocument().setDocument(book.getBook()).setChatId(chatId));
        return true;
        }
    }
}
