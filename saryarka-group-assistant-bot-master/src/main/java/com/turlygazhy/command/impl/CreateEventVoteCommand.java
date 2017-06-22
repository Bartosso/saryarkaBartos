package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Event;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.MessageElement;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.send.SendVideo;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Eshu on 15.06.2017.
 */
public class CreateEventVoteCommand extends Command {
    private String             photo;
    private String             event;
    private String             where;
    private String             when;
    private String             rules;
    private String             video;
    private String             contactInformation;
    private MessageElement     expectedMessageElement;
    private boolean            needPhoto      = true;
    private boolean            needVideo      = true;
    private ListDao            listDao        = factory.getListDao("EVENTS_LIST");
    private int                step           = 1;
    private Date date;


    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        String GROUP_FOR_VOTE = bot.getGROUP_FOR_VOTE();
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
        if (updateMessage == null) {
            updateMessage = update.getCallbackQuery().getMessage();
        }
        Long chatId = updateMessage.getChatId();
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
                    format.applyPattern("hh:mm dd/MM/yyyy");
                    try {
                        date = format.parse(when);
                        step = 4;
                    } catch (ParseException e) {
                        SendMessage sendMessage = new SendMessage().setText("Вы ввели дату проведения в неправильном формате, попробуйте сначала")
                                .setChatId(chatId);
                        bot.sendMessage(sendMessage);
                    }

                    break;
                case 4:
                    rules = updateMessage.getText();
                    step = 5;
                    break;
                case 5:
                    try {
                        photo = updateMessage.getPhoto().get(updateMessage.getPhoto().size() - 1).getFileId();
                    } catch (Exception e) {
                        if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(51))) {
                            needPhoto = false;
                        }
                    }
                    step = 6;
                    break;
                case 6:
                    try {
                        video = updateMessage.getVideo().getFileId();
                    } catch (Exception e){
                        if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(89)))
                            needVideo = false;
                    }
                    step = 7;
                    break;
                case 7:
                    contactInformation = updateMessage.getText();
                    step = 8;

            }
        }
        if (step == 1 && event == null) {
            SendMessage sendHello = messageDao.getMessage(87)
                    .getSendMessage().setChatId(chatId);
            Message message = messageDao.getMessage(88);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);

            bot.sendMessage(sendHello);
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
        if ( step == 4 && rules == null) {
            Message message = messageDao.getMessage(91);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if (step == 5 && photo == null && needPhoto) {
            Message message = messageDao.getMessage(28);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId)
                    .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.PHOTO;
            return false;
        }
        if (step == 6 & video == null && needVideo) {
            Message message = messageDao.getMessage(94);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId)
                    .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));
            bot.sendMessage(sendMessage);
            return false;
        }
        if (step == 7 & contactInformation == null){
            Message message = messageDao.getMessage(98);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);
            bot.sendMessage(sendMessage);
            return false;
        }

        if(step == 8) {
            listDao.createNewEvent(event, where, when, rules, photo, video, contactInformation,true);
            Message message = messageDao.getMessage(30);
            SendMessage sendMessage = message.getSendMessage().setChatId(chatId);
            bot.sendMessage(sendMessage);
            String eventId = String.valueOf(listDao.getEventId(event,where,when,rules,true));

            ReplyKeyboard replyKeyboard = getKeyBoardForVote(eventId, "EVENTS_LIST", listDao);

            Message poolNotificationMessage = messageDao.getMessage(92);
            SendMessage sendPool = poolNotificationMessage.getSendMessage().setChatId(GROUP_FOR_VOTE)
                    .setReplyMarkup(replyKeyboard);
            Message patternPoolInfo = messageDao.getMessage(92);
            String text = patternPoolInfo.getSendMessage().getText()
                    .replaceAll("contact_information", contactInformation)
                    .replaceAll("event_name"         , event)
                    .replaceAll("where"              , where)
                    .replaceAll("when"               , when)
                    .replaceAll("rules"              , rules);

            if (photo != null) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setPhoto(photo);
                bot.sendPhoto(sendPhoto.setChatId(GROUP_FOR_VOTE));
            }
            if (video !=null)  {
                SendVideo sendVideo = new SendVideo();
                sendVideo.setVideo(video);
                bot.sendVideo(sendVideo.setChatId(GROUP_FOR_VOTE));
            }
            sendPool.setText(text).setParseMode(ParseMode.HTML);
            bot.sendMessage(sendPool);
            video                  = null;
            photo                  = null;
            event                  = null;
            when                   = null;
            where                  = null;
            rules                  = null;
            step                   = 0;
            expectedMessageElement = null;
            return true;
        }
        return true;
    }
}
