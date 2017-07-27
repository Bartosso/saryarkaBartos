package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Event;
import com.turlygazhy.tool.DateUtil;
import com.turlygazhy.tool.EventAnonceUtil;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.text.ParseException;

/**
 * Created by Eshu on 19.06.2017.
 */
public class SolutionForEventFromAdminCommand extends Command {
    private ListDao listDao = factory.getListDao("events_list");
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(update.getCallbackQuery()== null){return false;}
        String eventId  = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1);
        String solution = update.getCallbackQuery().getData().substring(0,update.getCallbackQuery().getData().indexOf(":"));

        long chatId = update.getCallbackQuery().getFrom().getId();
        switch (solution){
            case "acceptEvent"      :
                if(listDao.checkEventStatus(eventId)){
                    bot.sendMessage(new SendMessage(chatId,"Вы уже одобрили этот ивент"));
                    return true;
                }
                makePool(bot, eventId, chatId);
                return true;
            case "declineEvent" :
                listDao.declineEvent(eventId);
                SendMessage sendMessage = new SendMessage().setChatId(chatId).setText("Вы решили не принимать ивент");
                bot.sendMessage(sendMessage);
                return true;
            case "editEvent" :
                bot.deleteMessage(new DeleteMessage().setChatId(String.valueOf(chatId))
                .setMessageId(update.getCallbackQuery().getMessage().getMessageId()));
                sendMessageForEditEventToAdmin(bot, eventId,listDao, chatId);
                return true;
            case "editEndedEvent":
                bot.deleteMessage(new DeleteMessage().setChatId(String.valueOf(chatId))
                        .setMessageId(update.getCallbackQuery().getMessage().getMessageId()));
                sendMessageForEditEndedEventToAdmin(bot, eventId,factory.getListDao("ENDED_EVENTS_LIST"), chatId);
                return true;
            case "acceptEndedEvent":
                bot.sendMessage(new SendMessage(chatId,"Изменения сохранены"));
                return true;
            case "declineEndedEvent":
                factory.getListDao("ENDED_EVENTS_LIST").declineEvent(eventId);
                SendMessage sendMessageEnded = new SendMessage().setChatId(chatId).setText("Вы решили удалить ивент");
                bot.sendMessage(sendMessageEnded);
                return true;
        }
        com.turlygazhy.entity.Message messageToAdmin = messageDao.getMessage(messageId);
        SendMessage sendMessageToAdmin = messageToAdmin.getSendMessage().setChatId(chatId);
        bot.sendMessage(sendMessageToAdmin);
        return true;
    }

    private void makePool(Bot bot,String eventId,long chatId) throws SQLException, TelegramApiException {
        String GROUP_FOR_VOTE = bot.getGROUP_FOR_VOTE();
        listDao.makeStuffBe(eventId);
        Event event = listDao.getEvent(eventId);
        if(event != null){
        com.turlygazhy.entity.Message poolMesage = messageDao.getMessage(92);

        String text = EventAnonceUtil.getEventWithPatternNoByAdmin(event, messageDao);

        ReplyKeyboard replyKeyboard = getKeyBoardForVote(Long.parseLong(eventId),"будет",listDao);
        SendMessage sendPool = poolMesage.getSendMessage().setText(text).setReplyMarkup(replyKeyboard).setChatId(GROUP_FOR_VOTE)
                .setParseMode(ParseMode.HTML);

        String photo = event.getPHOTO();
        if (photo != null) {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setPhoto(photo);
            bot.sendPhoto(sendPhoto.setChatId(GROUP_FOR_VOTE));
        }
        SendMessage sendMessage = new SendMessage().setChatId(chatId).setText("Ивент принят");
            try {
                createRemind(bot,Long.parseLong(eventId), event.getWHEN(), chatId);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            bot.sendMessage(sendPool);
        bot.sendMessage(sendMessage);
            if(event.getDOCUMENT() != null){
                SendDocument sendDocument = new SendDocument();
                sendDocument.setDocument(event.getDOCUMENT());
                bot.sendDocument(sendDocument.setChatId(GROUP_FOR_VOTE));
            }
        }
        else {
            SendMessage sendMessage = new SendMessage().setText("Поздно что то менять").setChatId(chatId);
            bot.sendMessage(sendMessage);
        }
    }

}
