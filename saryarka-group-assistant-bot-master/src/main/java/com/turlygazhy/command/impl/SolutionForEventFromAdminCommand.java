package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Event;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.send.SendVideo;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            case "Принять ивент"      :
                makePool(bot, eventId, chatId);
                return true;
            case "Не принимать ивент" :
                listDao.declineEvent(eventId);
                SendMessage sendMessage = new SendMessage().setChatId(chatId).setText("Вы решили не принимать ивент");
                bot.sendMessage(sendMessage);
                return true;
        }
        com.turlygazhy.entity.Message messageToAdmin = messageDao.getMessage(messageId);
        SendMessage sendMessageToAdmin = messageToAdmin.getSendMessage().setChatId(chatId);
        bot.sendMessage(sendMessageToAdmin);
        return true;
    }

    private void makePool(Bot bot,String eventId,long chatId) throws SQLException, TelegramApiException {
        String GROUP_FOR_VOTE = bot.getGROUP_FOR_VOTE();
        listDao.makeEventBe(eventId);
        Event event = listDao.getEvent(eventId);
        if(event != null){
        com.turlygazhy.entity.Message poolMesage = messageDao.getMessage(92);
        String text = poolMesage.getSendMessage().getText()
                .replaceAll("contact_information", event.getCONTACT_INFORMATION())
                .replaceAll("event_name"         , event.getEVENT_NAME())
                .replaceAll("where"              , event.getPLACE())
                .replaceAll("when"               , event.getWHEN())
                .replaceAll("rules"              , event.getRULES());
        ReplyKeyboard replyKeyboard = getKeyBoardForVote(eventId,"EVENTS_LIST",listDao);
        SendMessage sendPool = poolMesage.getSendMessage().setText(text).setReplyMarkup(replyKeyboard).setChatId(GROUP_FOR_VOTE)
                .setParseMode(ParseMode.HTML);

        String photo = event.getPHOTO();
        String video = event.getVIDEO();
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
        SendMessage sendMessage = new SendMessage().setChatId(chatId).setText("Ивент принят");
        bot.sendMessage(sendPool);
        bot.sendMessage(sendMessage);
        }
        else {
            SendMessage sendMessage = new SendMessage().setText("Теперь это уже не сделать").setChatId(chatId);
            bot.sendMessage(sendMessage);
        }
    }

}
