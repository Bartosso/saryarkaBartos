package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 22.06.2017.
 */
public class ChangeDiscountCategoryCommand extends Command {
    private boolean waitingAnswer;
    private long    chatId;
    private String  discountId;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {

        if(waitingAnswer){
            ListDao listDao = factory.getListDao("DISCOUNTS");
            if(update.getCallbackQuery().getData().equals(buttonDao.getButtonText(101))){
                listDao.updateDiscountType(discountId,"Restaurants");
                makeNewMessageDiscountForAdminEdit(bot,discountId,listDao,chatId);
               return true;
            }
            if(update.getCallbackQuery().getData().equals(buttonDao.getButtonText(102))){
                listDao.updateDiscountType(discountId,"Hotels");
                makeNewMessageDiscountForAdminEdit(bot,discountId,listDao,chatId);
                return true;
            }
            if(update.getCallbackQuery().getData().equals(buttonDao.getButtonText(103))){
                listDao.updateDiscountType(discountId,"BusinessSchools");
                makeNewMessageDiscountForAdminEdit(bot,discountId,listDao,chatId);
                return true;
            }
        }


        discountId              = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1);
        chatId                  = update.getCallbackQuery().getFrom().getId();
        ReplyKeyboard keyboard  = keyboardMarkUpDao.select(28);
        SendMessage sendMessage = new SendMessage().setChatId(chatId).setText("Выберите категорию в которую хотите переместить discount")
                .setReplyMarkup(keyboard);
        bot.sendMessage(sendMessage);
        waitingAnswer = true;
        return false;
    }
}
