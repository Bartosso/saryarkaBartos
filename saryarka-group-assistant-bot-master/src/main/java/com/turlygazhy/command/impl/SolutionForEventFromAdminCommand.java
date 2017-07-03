package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Event;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

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
                if(listDao.checkEventStatus(eventId)){
                    bot.sendMessage(new SendMessage(chatId,"Вы уже одобрили этот ивент"));
                    return true;
                }
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
        listDao.makeStuffBe(eventId);
        Event event = listDao.getEvent(eventId);
        if(event != null){
        com.turlygazhy.entity.Message poolMesage = messageDao.getMessage(92);
            String date = event.getWHEN().substring(0,2) + " " + getMonthInRussian(Integer.parseInt(event
                    .getWHEN().substring(3, 5)))
                    +" "+event.getWHEN().substring(event.getWHEN().indexOf(" "));
        String text = poolMesage.getSendMessage().getText()
                .replaceAll("event_text"         , event.getEVENT_NAME())
                .replaceAll("event_address"      , event.getPLACE())
                .replaceAll("event_time"        , date)
                .replaceAll("event_contact"      , event.getCONTACT_INFORMATION())
                .replaceAll("event_program"      , event.getPROGRAM())
                .replaceAll("event_dress_code"   , event.getDRESS_CODE())
                .replaceAll("event_rules"        , event.getRULES());
            if(event.getPAGE()!= null){
                text = text+"\n\n<b>Страница мероприятия/регистрация</b>:"+event.getPAGE();
            }
        ReplyKeyboard replyKeyboard = getKeyBoardForVote(eventId,"будет",listDao);
        SendMessage sendPool = poolMesage.getSendMessage().setText(text).setReplyMarkup(replyKeyboard).setChatId(GROUP_FOR_VOTE)
                .setParseMode(ParseMode.HTML);

        String photo = event.getPHOTO();
//        String video = event.getVIDEO();
        if (photo != null) {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setPhoto(photo);
            bot.sendPhoto(sendPhoto.setChatId(GROUP_FOR_VOTE));
        }
//        if (video !=null)  {
//            SendVideo sendVideo = new SendVideo();
//            sendVideo.setVideo(video);
//            bot.sendVideo(sendVideo.setChatId(GROUP_FOR_VOTE));
//        }
        SendMessage sendMessage = new SendMessage().setChatId(chatId).setText("Ивент принят");
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
