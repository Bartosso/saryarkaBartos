package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.MessageElement;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public class CreateDiscountVersion3Command extends Command {
    private int    step = 0;
    private long   chatid;
    private String discountType;
    private String discountName;
    private String discountAmount;
    private String discountPhone;
    private String discountDescription;
    private String discountAddress;
    private String discountPhoto;
    private MessageElement expectedMessageElement;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {

        if (expectedMessageElement != null) {
            switch (step) {
                case 0:
                    if(update.getCallbackQuery().getData().equals(buttonDao.getButtonText(101))){
                        discountType = "services";
                        step = 1;
                        break;
                    }
                    if(update.getCallbackQuery().getData().equals(buttonDao.getButtonText(102))){
                        discountType = "goods";
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
                    discountPhone = update.getMessage().getText();
                    step = 4;
                    break;
                case 4:
                    discountDescription = update.getMessage().getText();
                    step = 5;
                    break;
                case 5:
                    discountAddress = update.getMessage().getText();
                    step = 6;
                    break;
                case 6:
                    try {
                        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
                        discountPhoto = updateMessage.getPhoto().get(updateMessage.getPhoto().size() - 1).getFileId();
                    } catch (Exception e) {
                        if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(51))) {
                            discountPhoto = null;
                        }
                    }
                    step = 7;

            }
        }

        if(step == 0){
            chatid = update.getMessage().getChatId();
            bot.sendMessage(new SendMessage(chatid, "В какую категорию добавить дисконт?").
                    setReplyMarkup(keyboardMarkUpDao.select(28))
            .setParseMode(ParseMode.HTML));
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if(step == 1){
            bot.sendMessage(new SendMessage(chatid,messageDao.getMessage(123)
                    .getSendMessage().getText())
                    .setParseMode(ParseMode.HTML));
            return false;
        }
        if(step == 2){
            bot.sendMessage(new SendMessage(chatid,messageDao.getMessage(124)
                    .getSendMessage().getText())
                    .setParseMode(ParseMode.HTML));
            return false;
        }
        if(step == 3){
            bot.sendMessage(new SendMessage(chatid,messageDao.getMessage(125)
                    .getSendMessage().getText())
                    .setParseMode(ParseMode.HTML));
            return false;
        }
        if(step == 4){
            bot.sendMessage(new SendMessage(chatid,messageDao.getMessage(163)
                    .getSendMessage().getText())
                    .setParseMode(ParseMode.HTML));
            return false;
        }
        if(step == 5){
            bot.sendMessage(new SendMessage(chatid,messageDao.getMessage(164)
                    .getSendMessage().getText())
                    .setParseMode(ParseMode.HTML));
            return false;
        }
        if(step == 6){
            Message message = messageDao.getMessage(28);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatid)
                    .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()))
                    .setParseMode(ParseMode.HTML);
            bot.sendMessage(sendMessage);
            return false;
        }
        if(step == 7){
            factory.getListDao("DISCOUNTS").createDiscountVersion3(discountType, discountDescription, discountName, discountAmount,
                    discountAddress, discountPhone,discountPhoto );
            bot.sendMessage(new SendMessage(chatid,messageDao.getMessage(126)
                    .getSendMessage().getText()).setParseMode(ParseMode.HTML));
        }
        discountType        = null;
        discountName        = null;
        discountAmount      = null;
        discountPhone = null;
        discountDescription = null;
        discountAddress     = null;
        discountPhoto       = null;

        return true;
    }
}
