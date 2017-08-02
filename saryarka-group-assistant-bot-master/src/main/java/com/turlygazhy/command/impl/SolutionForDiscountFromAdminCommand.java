package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 22.06.2017.
 */
public class SolutionForDiscountFromAdminCommand extends Command {
    @SuppressWarnings("unused")
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        ListDao listDao   = factory.getListDao("DISCOUNTS");
        long chatId       = update.getCallbackQuery().getFrom().getId();
        String chose      = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf("/")+1);
        String discountId = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1,
                update.getCallbackQuery().getData().indexOf("/"));
//        SendMessage sendOk = new SendMessage().setChatId(chatId).setText("Ваше решение принято");
//        switch (chose){
//            case "accept" :
//                listDao.makeDiscountBe(discountId);
//                bot.sendMessage(sendOk);
//                break;
//            case "decline":
//                listDao.killDiscount(discountId);
//                bot.sendMessage(sendOk);
//                break;
//            case "edit"   :
////                ReplyKeyboard keyboard      = getEditDiscountKeys(discountId);
////                SendMessage sendMessageToAdmin = new SendMessage().setText("Выберите что хотите изменить").setChatId(chatId)
////                        .setReplyMarkup(keyboard);
////                bot.sendMessage(sendMessageToAdmin);
//
//        }
        return true;
    }




}
