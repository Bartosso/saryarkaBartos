package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Discount;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 22.06.2017.
 */
public class GetDiscountCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        long chatId       = update.getCallbackQuery().getFrom().getId();
        String discountId  = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1);
        Discount discount = factory.getListDao("DISCOUNTS").getDiscountById(discountId);
        SendMessage sendMessage = new SendMessage().setChatId(chatId).setText(messageDao.getMessage(messageId).getSendMessage().getText()
                .replaceAll("company_name"        , discount.getName())
                .replaceAll("companyDescription"  , discount.getTextAbout())
                .replaceAll("address"             , discount.getAddress())
                .replaceAll("page"                , discount.getPage())
                .replaceAll("discount_count", discount.getDiscount()))
                .setParseMode(ParseMode.HTML);
        if (discount.getPhoto() != null) {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setPhoto(discount.getPhoto());
            bot.sendPhoto(sendPhoto.setChatId(chatId));
        }
        bot.sendMessage(sendMessage.setReplyMarkup(keyboardMarkUpDao.select(messageDao.getMessage(messageId).getKeyboardMarkUpId())));

        return true;
    }
}
