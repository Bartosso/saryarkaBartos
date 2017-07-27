package com.turlygazhy.command.impl;


import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Message;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;


/**
 * Created by Eshu on 17.07.2017.
 */
public class ChangeButtonTextCommand extends Command {
    private long    chatId;
    private int     buttonId;
    private boolean expectNewValue;
    private String  buttonDeniedText;
    @SuppressWarnings("FieldCanBeLocal")
    private String  newValue;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (expectNewValue){
            if(update.hasCallbackQuery()){
                bot.deleteMessage(new DeleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId()));
                if(update.getCallbackQuery().getData().equals(buttonDeniedText)) {
                    bot.sendMessage(new SendMessage(chatId,"Вы нажали кнопку "+ buttonDeniedText));
                }
            }
            if(update.hasMessage()){
                if(update.getMessage().getChatId()==getAdminChatId()){
                newValue = update.getMessage().getText();
                buttonDao.updateButtonText(buttonId, newValue);
                bot.sendMessage(new SendMessage(chatId, "Текст кнопки успешно изменен").setReplyMarkup(
                        keyboardMarkUpDao.select(11)));}
                        else {
                    bot.sendMessage(new SendMessage(chatId, "Вы не Админ"));
                }
            }
        }
       else {
            chatId = update.getCallbackQuery().getFrom().getId();

            buttonDeniedText = buttonDao.getButtonText(214);

            buttonId = Integer.parseInt(update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData()
            .indexOf(":")+1));

            bot.deleteMessage(new DeleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId()));

            Message message = messageDao.getMessage(messageId);

            bot.sendMessage(message.getSendMessage().setChatId(chatId).setText(message.getSendMessage()
            .getText().replaceAll("button_name", buttonDao.getButtonText(buttonId))
            .replaceAll("button_denied", buttonDeniedText)).setReplyMarkup(
                    keyboardMarkUpDao.select(message.getKeyboardMarkUpId())));

            expectNewValue = true;
            return false;
        }
        chatId           = 0;
        buttonId         = 0;
        expectNewValue   = false;
        buttonDeniedText = null;
        newValue         = null;
        return true;
    }


}
