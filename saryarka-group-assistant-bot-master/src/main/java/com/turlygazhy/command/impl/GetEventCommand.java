package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Event;
import com.turlygazhy.entity.Message;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.send.SendVideo;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eshu on 20.06.2017.
 */
public class GetEventCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(update.getCallbackQuery()== null){return false;}
        String eventId   = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1,
                update.getCallbackQuery().getData().indexOf("/"));
        String eventType = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf("/")+1);
        long chatId      = update.getCallbackQuery().getFrom().getId();
        showEvent(update, bot,eventId,eventType,chatId);
        return true;
    }



    private void showEvent(Update update, Bot bot, String eventId, String eventType, long chatId) throws SQLException, TelegramApiException {
        String daoListName  = "";
        String eventTypeToVote = "";
        switch (eventType){
            case "было" :
                daoListName       = "ENDED_EVENTS_LIST";
                eventTypeToVote   = "было";
                break;
            case "будет":
                daoListName       = "EVENTS_LIST";
                eventTypeToVote   = "будет";
                break;
        }
        ListDao listDao = factory.getListDao(daoListName);
        Message message = messageDao.getMessage(92);
        Event event     = listDao.getEvent(eventId);
        if(event == null){
            Message messageFoundNothing = messageDao.getMessage(84);
            bot.sendMessage(messageFoundNothing.getSendMessage().setChatId(chatId));
        }
        else {
        String text     = message.getSendMessage().getText()
                .replaceAll("event_name"         , event.getEVENT_NAME())
                .replaceAll("where"              , event.getPLACE())
                .replaceAll("when"               , event.getWHEN())
                .replaceAll("contact_information", event.getCONTACT_INFORMATION());
        SendPhoto sendPhoto = new SendPhoto().setPhoto(event.getPHOTO());
        if(event.getPHOTO() != null){
            sendPhoto.setPhoto(event.getPHOTO());
            bot.sendPhoto(sendPhoto.setChatId(chatId));
        }

//        SendVideo sendVideo = new SendVideo().setVideo(event.getVIDEO());
//        if(event.getVIDEO() != null){
//            sendVideo.setVideo(event.getVIDEO());
//            bot.sendVideo(sendVideo.setChatId(chatId));
//        }

        try {
            ReplyKeyboard replyKeyboard = getKeyBoardForVote(event.getId(),eventTypeToVote,listDao);
//            bot.sendMessage(new SendMessage()
//                    .setChatId(chatId)
//                    .setText(text).setReplyMarkup(replyKeyboard).setParseMode(ParseMode.HTML));
           bot.deleteMessage(new DeleteMessage().setChatId(String.valueOf(chatId)).setMessageId(update
           .getCallbackQuery().getMessage().getMessageId()));

            bot.sendMessage(new SendMessage()
                    .setChatId(chatId)
                    .setText(text).setReplyMarkup(replyKeyboard).setParseMode(ParseMode.HTML));

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }}
    }

}
