package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 30.06.2017.
 */
public class DeleteBookCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        long   chatId = update.getCallbackQuery().getFrom().getId();
        String bookId = update.getCallbackQuery().getData().substring(update.getCallbackQuery()
                .getData().indexOf(":")+1);
        factory.getListDao("BOOKS").deleteBook(bookId);
        sendMessage("Книга успешно удалена ✅", chatId, bot);
        return true;
    }
}
