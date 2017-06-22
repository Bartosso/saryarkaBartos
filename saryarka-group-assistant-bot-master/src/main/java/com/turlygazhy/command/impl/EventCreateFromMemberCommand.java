package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.MessageElement;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.send.SendVideo;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by Eshu on 19.06.2017.
 */
public class EventCreateFromMemberCommand extends Command {
    private String             photo;
    private String             event;
    private String             where;
    private String             when;
    private String             rules;
    private String             video;
    private String             contactInformation;
    private MessageElement     expectedMessageElement;
    private boolean            needPhoto     = true;
    private boolean            needVideo     = true;
    private ListDao            listDao       = factory.getListDao("EVENTS_LIST");
    private int                step          = 1;
    private Date date;


    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
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
            long adminChatId = userDao.getAdminChatId();
            //Сообщение "Ваше предложение отправлено администратору"
            Message message = messageDao.getMessage(95);
            SendMessage sendMessage = message.getSendMessage().setChatId(chatId);
            bot.sendMessage(sendMessage);
            //Новый ивент создаем
            listDao.createNewEvent(event, where, when, rules, photo, video,contactInformation, false);
            //Предупреждаем админа о новом предложении
            Message notifyAdmin = messageDao.getMessage(96);
            long eventId = listDao.getEventId(event,where,when,rules,false);


            Message patternPoolInfo = messageDao.getMessage(97);
            String text = patternPoolInfo.getSendMessage().getText()
                    .replaceAll("contact_information", contactInformation)
                    .replaceAll("event_name"         , event)
                    .replaceAll("where"              , where)
                    .replaceAll("when"               , when)
                    .replaceAll("rules"              , rules);

            SendMessage sendToAdmin = patternPoolInfo.getSendMessage().setChatId(userDao.getAdminChatId()).setParseMode(ParseMode.HTML);
//                    .setReplyMarkup(keyboardMarkUpDao.select(patternPoolInfo.getKeyboardMarkUpId()));
            sendToAdmin.setText(text).setReplyMarkup(getAdditionalInfoToKeyboard(Integer.valueOf(memberDao.getMemberId(chatId)), String.valueOf(eventId),null, 90,91));
//            sendToAdmin.setText(text);
            if (photo != null) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setPhoto(photo);
                bot.sendPhoto(sendPhoto.setChatId(adminChatId));
            }
            if (video !=null)  {
                SendVideo sendVideo = new SendVideo();
                sendVideo.setVideo(video);
                bot.sendVideo(sendVideo.setChatId(adminChatId));
            }
            bot.sendMessage(notifyAdmin.getSendMessage().setChatId(adminChatId));
            bot.sendMessage(sendToAdmin);


            contactInformation     = null;
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
