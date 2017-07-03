package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 02.07.2017.
 */
public class SolutionForOfferTenderCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        long chatID = update.getCallbackQuery().getFrom().getId();
        String tenderId = update.getCallbackQuery().getData().substring(update.getCallbackQuery()
                .getData().indexOf(":") + 1);
        String chose = update.getCallbackQuery().getData().substring(0, update.getCallbackQuery()
                .getData().indexOf(":"));
        ListDao listDao = factory.getListDao("OFFER_LIST");
        if (listDao.isStuffActive(tenderId)) {
            sendMessage(143, chatID, bot);
            return true;
        } else {
            if (chose.equals(buttonDao.getButtonText(141))) {
                listDao.makeStuffBe(tenderId);
                sendMessage(144, chatID, bot);
            }
            if (chose.equals(buttonDao.getButtonText(142))) {
                listDao.delete(tenderId);
                sendMessage(146, chatID, bot);
            }
            return true;
        }
    }
}
