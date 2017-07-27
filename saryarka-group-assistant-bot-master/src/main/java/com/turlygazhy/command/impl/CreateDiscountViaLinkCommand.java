package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.MessageElement;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 29.06.2017.
 */
public class CreateDiscountViaLinkCommand extends Command {
    private int    step = 0;
    private long   chatid;
    private String discountType;
    private String discountName;
    private String discountAmount;
    private String disocountPage;
    private MessageElement expectedMessageElement;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {

        if (expectedMessageElement != null) {
            switch (step) {
                case 0:
                    if(update.getCallbackQuery().getData().equals(buttonDao.getButtonText(101))){
                        discountType = "Restaurants";
                        step = 1;
                        break;
                    }
                    if(update.getCallbackQuery().getData().equals(buttonDao.getButtonText(102))){
                        discountType = "Hotels";
                        step = 1;
                        break;
                    }
                    if(update.getCallbackQuery().getData().equals(buttonDao.getButtonText(103))){
                        discountType = "BusinessSchools";
                        step = 1;
                        break;
                    }
                    break;
                case 1:
                    discountName = update.getMessage().getText();
                    step = 2;
                    break;
                case 2:
                    discountAmount = update.getMessage().getText();
                    step = 3;
                    break;
                case 3:
                    disocountPage  = update.getMessage().getText();
                    step = 4;
            }
        }

        if(step == 0){
            chatid = update.getMessage().getChatId();
            bot.sendMessage(new SendMessage(chatid, "В какую категорию добавить дисконт?").
            setReplyMarkup(keyboardMarkUpDao.select(28)));
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if(step == 1){
            bot.sendMessage(new SendMessage(chatid,messageDao.getMessage(123)
            .getSendMessage().getText()));
            return false;
        }
        if(step == 2){
            bot.sendMessage(new SendMessage(chatid,messageDao.getMessage(124)
            .getSendMessage().getText()));
            return false;
        }
        if(step == 3){
            bot.sendMessage(new SendMessage(chatid,messageDao.getMessage(125)
            .getSendMessage().getText()));
            return false;
        }
        if(step == 4){
            factory.getListDao("DISCOUNTS_LIST").createDiscountVersion2(discountType, discountName, discountAmount,disocountPage);
            bot.sendMessage(new SendMessage(chatid,messageDao.getMessage(126)
            .getSendMessage().getText()));
            return true;
        }
        discountType   = null;
        discountName   = null;
        discountAmount = null;
        disocountPage  = null;

        return true;
    }
}
