package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 29.06.2017.
 */
public class DeleteDiscountCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        long   chatId          = update.getCallbackQuery().getFrom().getId();
        String discountId      = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1);
        factory.getListDao("DISCOUNTS_LIST").delete(discountId);
        bot.sendMessage(new SendMessage(chatId, "Дисконт удален"));
        return true;
    }
}
