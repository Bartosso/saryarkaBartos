package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Discount;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.MessageElement;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eshu on 22.06.2017.
 */
public class EditDiscountCommand extends Command {
    private boolean expectNewValue;
    private long    chatId;
    private String  chose;
    private String  discountId;
    private boolean nextphoto;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {

        if(expectNewValue){
            ListDao listDao = factory.getListDao("DISCOUNTS");


        switch (chose){
            case "changeAddress"  :
                listDao.updateDiscountAddress(discountId,update.getMessage().getText());
                makeNewMessageDiscountForAdminEdit(bot,discountId, listDao, chatId);
                return true;
            case "changeName"     :
                listDao.updateDiscountName(discountId,update.getMessage().getText());
                makeNewMessageDiscountForAdminEdit(bot,discountId, listDao, chatId);
                return true;
            case "changeAbout"    :
                listDao.updateDiscountTextAbout(discountId,update.getMessage().getText());
                makeNewMessageDiscountForAdminEdit(bot,discountId, listDao, chatId);
                return true;
            case "changePhoto"    :
                try {
                 listDao.updateDiscountPhoto(discountId,update.getMessage().getPhoto().get(update.getMessage().getPhoto().size() - 1).getFileId());
                } catch (Exception e) {
                    if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(51))) {
                        return true;
                    }
                }
                makeNewMessageDiscountForAdminEdit(bot,discountId, listDao, chatId);
                return true;
            case "changePage"     :
                listDao.updateDiscountPage(discountId, update.getMessage().getText());
                makeNewMessageDiscountForAdminEdit(bot,discountId, listDao, chatId);
                return true;
            case "changeDiscount" :
                listDao.updateDiscountDiscount(discountId, update.getMessage().getText());
                makeNewMessageDiscountForAdminEdit(bot,discountId, listDao, chatId);
                return true;

        }
        return true;
        }
        else {
            chatId     = update.getCallbackQuery().getFrom().getId();
            chose      = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf("/")+1);
            discountId = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1,
                    update.getCallbackQuery().getData().indexOf("/"));
            if(chose.equals("changePhoto")){
                Message message = messageDao.getMessage(28);
                SendMessage sendMessage = message.getSendMessage()
                        .setChatId(chatId)
                        .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));
                bot.sendMessage(sendMessage);
                expectNewValue = true;
                return false;
            }
            else {
            SendMessage sendMessage = new SendMessage().setText("Введите новое значение").setChatId(chatId);
            bot.sendMessage(sendMessage);
            expectNewValue = true;

        return false;}}
    }
}
