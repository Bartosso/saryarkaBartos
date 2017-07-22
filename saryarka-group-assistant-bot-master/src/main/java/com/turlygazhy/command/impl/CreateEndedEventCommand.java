package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.MessageElement;
import com.turlygazhy.tool.EventAnonceUtil;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Eshu on 17.07.2017.
 */
public class CreateEndedEventCommand extends Command {
    private String             photo;
    private String             event;
    private String             where;
    private String             when;
    //    private String             video;
    private String             contactInformation;
    private String             program;
    private String             dresscode;
    private String             rules;
    private String             page;
    private String             document;
    private MessageElement expectedMessageElement;
    private boolean            needPhoto      = true;
    private boolean            needVideo      = true;
    private ListDao listDao        = factory.getListDao("ENDED_EVENTS_LIST");
    private int                step           = 1;
    private long               chatId;
    private Date date;


    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        String GROUP_FOR_VOTE = bot.getGROUP_FOR_VOTE();
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
        if (updateMessage == null) {
            updateMessage = update.getCallbackQuery().getMessage();
        }
        chatId = updateMessage.getChatId();
        if (expectedMessageElement != null) {
            switch (step){
                case 1:
                    event = updateMessage.getText();
                    step = 2;
                    break;
                case 2:
                    where = updateMessage.getText();
                    step = 3;
                    break;
                case 3:
                    when  = updateMessage.getText();
                    SimpleDateFormat format = new SimpleDateFormat();
                    format.applyPattern("dd.MM.yy");
                    try {
                        date = format.parse(when);
                        step = 11;
                    } catch (ParseException e) {
                        SendMessage sendMessage = new SendMessage().setText("Вы ввели дату проведения в неправильном формате, попробуйте сначала")
                                .setChatId(chatId);
                        bot.sendMessage(sendMessage);
                    }

                    break;
                case 4:
                    try {
                        photo = updateMessage.getPhoto().get(updateMessage.getPhoto().size() - 1).getFileId();
                    } catch (Exception e) {
                        if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(51))) {
                            needPhoto = false;
                        }
                    }
                    step = 5;
                    break;

                case 5:
                    contactInformation = updateMessage.getText();
                    step = 6;
                    break;
                case 6:
                    program            = update.getMessage().getText();
                    step = 7;
                    break;
                case 7:
                    dresscode          = update.getMessage().getText();
                    step = 8;
                    break;
                case 8:
                    rules              = update.getMessage().getText();
                    step = 9;
                    break;
                case 9:
                    try {
                        page = update.getMessage().getText();
                    } catch (Exception e) {
                        if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(134))) {
                            page = null;
                        }
                    }
                    step = 10;
                    break;
                case 10:
                    try {
                        document = update.getMessage().getDocument().getFileId();
                    } catch (Exception e) {
                        if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(135))) {
                            document = null;
                        }
                    }
                    step = 11;
            }
        }
        if (step == 1 && event == null) {
            Message message = messageDao.getMessage(88);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if (step == 2 && where == null) {
            Message message = messageDao.getMessage(89);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if (step == 3 && when == null) {
            Message message = messageDao.getMessage(90);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if (step == 4 && photo == null && needPhoto) {
            Message message = messageDao.getMessage(28);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId)
                    .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.PHOTO;
            return false;
        }

        if (step == 5 & contactInformation == null){
            Message message = messageDao.getMessage(98);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);
            bot.sendMessage(sendMessage);
            return false;
        }
        if(step == 6){
            Message message = messageDao.getMessage(91);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);
            bot.sendMessage(sendMessage);
            return false;
        }
        if(step == 7){
            Message message = messageDao.getMessage(120);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);
            bot.sendMessage(sendMessage);
            return false;
        }
        if(step == 8){
            Message message = messageDao.getMessage(121);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);
            bot.sendMessage(sendMessage);
            return false;
        }
        if(step == 9){
            Message message = messageDao.getMessage(122);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML)
                    .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));
            bot.sendMessage(sendMessage);
            return false;
        }
        if(step == 10){
            Message message = messageDao.getMessage(138);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML)
                    .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));
            bot.sendMessage(sendMessage);
            return false;
        }

        if(step == 11) {
            long eventId = listDao.createNewEvent(event, where, when,photo,  contactInformation,rules, dresscode,program, page,document, true, true);
            Message message = messageDao.getMessage(30);
            SendMessage sendMessage = message.getSendMessage().setChatId(chatId);
            bot.sendMessage(sendMessage);

            photo                  = null;
            event                  = null;
            when                   = null;
            where                  = null;
            step                   = 0;
            expectedMessageElement = null;
            program                = null;
            dresscode              = null;
            rules                  = null;
            page                   = null;
            document               = null;
            return true;
        }
        return true;
    }
}
