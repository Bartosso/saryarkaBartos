package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Event;
import com.turlygazhy.entity.ListData;
import com.turlygazhy.entity.Member;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;


/**
 * Created by Eshu on 05.07.2017.
 */
public class EditOfferTenderByAdminCommand extends Command {
    private boolean  expectNewValue;
    private long     chatId;
    private String   tenderId;
    private String   chose;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(expectNewValue){
            ListDao listDao = factory.getListDao("OFFER_LIST");
           if(chose.equals(buttonDao.getButtonText(153))){
            listDao.updateTenderText(tenderId, update.getMessage().getText());
            sendNewMessageForEditOfferTender(bot, tenderId, listDao, chatId);
            return true;
           }
        }
        else {
            chatId     = update.getCallbackQuery().getFrom().getId();
            chose      = update.getCallbackQuery().getData().substring(0,update.getCallbackQuery().getData().indexOf(":"));
            tenderId = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1);
            if(chose.equals(buttonDao.getButtonText(154))){
                ListDao listDao = factory.getListDao("OFFER_LIST");
                String newTenderId = listDao.moveTenderInAnotherType(tenderId, "REQUESTS_LIST");
                listDao.delete(tenderId);
                sendNewMessageForEditRequestTender(bot, newTenderId, factory.getListDao("REQUESTS_LIST"), chatId);
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
