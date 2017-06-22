package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.MessageElement;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Eshu on 15.06.2017.
 */
public class AddToOfferListCommand extends Command {
    private String photo;
    private String text;
    private MessageElement expectedMessageElement;
    private boolean needPhoto = true;
    private ListDao listDao = factory.getListDao("OFFER_LIST");
    private LocalDateTime now;
    private DateTimeFormatter dtf;


    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {

        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
        if (updateMessage == null) {
            updateMessage = update.getCallbackQuery().getMessage();
        }
        Long chatId = updateMessage.getChatId();
        if (expectedMessageElement != null) {
            switch (expectedMessageElement) {
                case PHOTO:
                    try {
                        photo = updateMessage.getPhoto().get(updateMessage.getPhoto().size() - 1).getFileId();
                    } catch (Exception e) {
                        if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(51))) {
                            needPhoto = false;
                        }
                    }
                    break;
                case TEXT:
                    text = updateMessage.getText();
                    break;
            }
        }
        if (text == null) {
            Message message = messageDao.getMessage(79);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId)
                    .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if (photo == null && needPhoto) {
            Message message = messageDao.getMessage(28);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId)
                    .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.PHOTO;
            return false;
        }
        dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        now = LocalDateTime.now();

        listDao.insertIntoLists(photo,text,memberDao.getMemberId(chatId),dtf.format(now));
        Message message = messageDao.getMessage(83);
        SendMessage sendMessage = message.getSendMessage().setChatId(chatId)
                .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));
        bot.sendMessage(sendMessage);

        now = null;
        dtf = null;
        photo = null;
        text = null;
        expectedMessageElement = null;
        return true;
    }
}
