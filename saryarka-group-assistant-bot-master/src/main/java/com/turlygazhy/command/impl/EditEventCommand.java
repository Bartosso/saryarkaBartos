package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Message;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Eshu on 06.07.2017.
 */
public class EditEventCommand extends Command {
    private boolean expectNewValue;
    private long chatId;
    private int messageId;
    private int secondMessageId;
    private String chose;
    private String eventId;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (expectNewValue) {
            org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
            if (updateMessage == null) {
                updateMessage = update.getCallbackQuery().getMessage();
            }
            ListDao listDao = factory.getListDao("EVENTS_LIST");
            bot.deleteMessage(new DeleteMessage(chatId, secondMessageId));
            bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setMessageId(messageId).setReplyMarkup(null).setChatId(chatId));

            switch (chose){
                case "editEventName":
                    listDao.changeStuff(updateMessage.getText(),eventId,"EVENT_NAME");
                    bot.sendMessage(new SendMessage(chatId,"изменения сохранены"));
                    sendMessageForEditEventToAdmin(bot,eventId, listDao, chatId);
                    return true;
                case "editEndedEventName"  :
                    factory.getListDao("ENDED_EVENTS_LIST").changeStuff(updateMessage.getText(),eventId,"EVENT_NAME");
                    bot.sendMessage(new SendMessage(chatId,"изменения сохранены"));
                    sendMessageForEditEndedEventToAdmin(bot,eventId, factory.getListDao("ENDED_EVENTS_LIST"), chatId);
                    return true;
                case "editEventPlace":
                    listDao.changeStuff(updateMessage.getText(), eventId,"PLACE");
                    bot.sendMessage(new SendMessage(chatId,"изменения сохранены"));
                    sendMessageForEditEventToAdmin(bot,eventId, listDao, chatId);
                    return true;
                case "editEndedEventPlace":
                    factory.getListDao("ENDED_EVENTS_LIST").changeStuff(updateMessage.getText(),eventId,"PLACE");
                    bot.sendMessage(new SendMessage(chatId,"изменения сохранены"));
                    sendMessageForEditEndedEventToAdmin(bot,eventId, factory.getListDao("ENDED_EVENTS_LIST"), chatId);
                    return true;
                case "editEventWhen":
                    SimpleDateFormat format = new SimpleDateFormat();
                    format.applyPattern("dd.MM.yy");
                    try {
                        Date date = format.parse(updateMessage.getText());
                        listDao.changeStuff(updateMessage.getText(), eventId, "WHEN");
                        bot.sendMessage(new SendMessage(chatId,"изменения сохранены"));
                        sendMessageForEditEventToAdmin(bot,eventId, listDao, chatId);
                        return true;
                    } catch (ParseException e) {
                        SendMessage sendMessage = new SendMessage().setText("Вы ввели дату проведения в неправильном формате, попробуйте сначала")
                                .setChatId(chatId);
                        bot.sendMessage(sendMessage);
                        return false;
                    }
                case "editEndedEventWhen":
                    SimpleDateFormat formatEnded = new SimpleDateFormat();
                    formatEnded.applyPattern("dd.MM.yy");
                    try {
                        Date date = formatEnded.parse(updateMessage.getText());
                        factory.getListDao("ENDED_EVENTS_LIST").changeStuff(updateMessage.getText(), eventId, "WHEN");
                        bot.sendMessage(new SendMessage(chatId,"изменения сохранены"));
                        sendMessageForEditEndedEventToAdmin(bot,eventId, factory.getListDao("ENDED_EVENTS_LIST"), chatId);
                        return true;
                    } catch (ParseException e) {
                        SendMessage sendMessage = new SendMessage().setText("Вы ввели дату проведения в неправильном формате, попробуйте сначала")
                                .setChatId(chatId);
                        bot.sendMessage(sendMessage);
                        return false;
                    }
                case "editEventCont":
                    listDao.changeStuff(updateMessage.getText(), eventId,"CONTACT_INFORMATION");
                    bot.sendMessage(new SendMessage(chatId,"изменения сохранены"));
                    sendMessageForEditEventToAdmin(bot,eventId, listDao, chatId);
                    return true;
                case "editEventPhoto":
                    try {
                        String photo = updateMessage.getPhoto().get(updateMessage.getPhoto().size() - 1).getFileId();
                        listDao.changeStuff(photo, eventId, "PHOTO");
                        bot.sendMessage(new SendMessage(chatId,"изменения сохранены"));
                        sendMessageForEditEventToAdmin(bot,eventId, listDao, chatId);
                        return true;
                    } catch (Exception e) {
                        if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(51))) {
                            listDao.deleteStuff(eventId,"PHOTO");
                            bot.sendMessage(new SendMessage(chatId,"изменения сохранены"));
                           sendMessageForEditEventToAdmin(bot,eventId, listDao, chatId);
                            return true;
                        }
                    }
                    break;
                case "editEventRules":
                    listDao.changeStuff(updateMessage.getText(), eventId,"RULES");
                    bot.sendMessage(new SendMessage(chatId,"изменения сохранены"));
                   sendMessageForEditEventToAdmin(bot,eventId, listDao, chatId);
                    return true;
                case "editEventDcode":
                    listDao.changeStuff(updateMessage.getText(), eventId,"DRESS_CODE");
                    bot.sendMessage(new SendMessage(chatId,"изменения сохранены"));
                    sendMessageForEditEventToAdmin(bot,eventId, listDao, chatId);
                    return true;
                case "editEventPrgm":
                    String text = update.getMessage().getText();
                    if(text.contains(";")){
                        listDao.changeStuff(updateMessage.getText(), eventId,"PROGRAM");
                        bot.sendMessage(new SendMessage(chatId,"изменения сохранены"));
                        sendMessageForEditEventToAdmin(bot,eventId, listDao, chatId);
                        return true;
                    }else{
                        bot.sendMessage(messageDao.getMessage(157).getSendMessage().setChatId(chatId));
                        return false;
                    }
                case "editEventPage":
                    try {
                        String page = updateMessage.getText();
                        listDao.changeStuff(page, eventId,"PAGE");
                        bot.sendMessage(new SendMessage(chatId,"изменения сохранены"));
                        sendMessageForEditEventToAdmin(bot,eventId, listDao, chatId);
                        return true;
                    } catch (Exception e) {
                        if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(134))) {
                            listDao.deleteStuff(eventId,"PAGE");
                            bot.sendMessage(new SendMessage(chatId,"изменения сохранены"));
                            sendMessageForEditEventToAdmin(bot,eventId, listDao, chatId);
                            return true;
                        }
                    }
                case "editEventDoc":
                    try {
                       String document = update.getMessage().getDocument().getFileId();
                        listDao.changeStuff(document, eventId,"DOCUMENT");
                        bot.sendMessage(new SendMessage(chatId,"изменения сохранены"));
                        sendMessageForEditEventToAdmin(bot,eventId, listDao, chatId);
                        return true;
                    } catch (Exception e) {
                        if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(135))) {
                            listDao.deleteStuff(eventId,"DOCUMENT");
                            bot.sendMessage(new SendMessage(chatId,"изменения сохранены"));
                            sendMessageForEditEventToAdmin(bot,eventId, listDao, chatId);
                            return true;
                        }
                    }
            }
        } else {
            chatId    = update.getCallbackQuery().getFrom().getId();
            chose     = update.getCallbackQuery().getData().substring(0, update.getCallbackQuery().getData().indexOf(":"));
            eventId   = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":") + 1);
            messageId = update.getCallbackQuery().getMessage().getMessageId();
            if (chose.equals("editEventPhoto")) {
                Message message = messageDao.getMessage(28);
                SendMessage sendMessage = message.getSendMessage()
                        .setChatId(chatId)
                        .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));
                secondMessageId = bot.sendMessage(sendMessage).getMessageId();
                expectNewValue = true;
                return false;
            }
            if (chose.equals("editEventDoc")) {
                Message message = messageDao.getMessage(138);
                SendMessage sendMessage = message.getSendMessage()
                        .setChatId(chatId)
                        .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));
                secondMessageId = bot.sendMessage(sendMessage).getMessageId();
                expectNewValue = true;
                return false;
            }
            if (chose.equals("editEventPage")) {
                Message message = messageDao.getMessage(122);
                SendMessage sendMessage = message.getSendMessage()
                        .setChatId(chatId)
                        .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));
                secondMessageId = bot.sendMessage(sendMessage).getMessageId();
                expectNewValue = true;
                return false;
            }
            if(chose.equals("editEventWhen")) {
                secondMessageId = bot.sendMessage(messageDao.getMessage(155).getSendMessage().setChatId(chatId))
                .getMessageId();
                expectNewValue = true;
                return false;
            }
            if(chose.equals("editEventPrgm")) {
                secondMessageId = bot.sendMessage(messageDao.getMessage(156).getSendMessage().setChatId(chatId))
                .getMessageId();
                expectNewValue = true;
                return false;
            }
            else {
                SendMessage sendMessage = new SendMessage().setText("Введите новое значение").setChatId(chatId);
                secondMessageId = bot.sendMessage(sendMessage).getMessageId();
                expectNewValue = true;
                return false;
            }
        }
    return false;}
}
