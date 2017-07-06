package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 05.07.2017.
 */
public class EditRequestTenderByAdminCommand extends Command {
    private boolean  expectNewValue;
    private long     chatId;
    private String   tenderId;
    private String   chose;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(expectNewValue){
            ListDao listDao = factory.getListDao("REQUESTS_LIST");
            if(chose.equals(buttonDao.getButtonText(156))){
                listDao.updateTenderText(tenderId, update.getMessage().getText());
                sendNewMessageForEditRequestTender(bot, tenderId, listDao, chatId);
                return true;
            }
        }
        else {
            chatId     = update.getCallbackQuery().getFrom().getId();
            chose      = update.getCallbackQuery().getData().substring(0,update.getCallbackQuery().getData().indexOf(":"));
            tenderId = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1);
            if(chose.equals(buttonDao.getButtonText(155))){
                ListDao listDao = factory.getListDao("REQUESTS_LIST");
                String newTenderId = listDao.moveTenderInAnotherType(tenderId, "OFFER_LIST");
                listDao.delete(tenderId);
                sendNewMessageForEditOfferTender(bot, newTenderId, factory.getListDao("OFFER_LIST"), chatId);
                return true;
            }
            else{
                SendMessage sendMessage = new SendMessage().setText("Введите новое значение").setChatId(chatId);
                bot.sendMessage(sendMessage);
                expectNewValue = true;}
        }

        return false;
    }
}
