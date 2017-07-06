package com.turlygazhy.reminder.timer_task;

import com.turlygazhy.Bot;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Event;
import com.turlygazhy.entity.Message;
import com.turlygazhy.reminder.Reminder;
import com.turlygazhy.tool.DateUtil;
import com.turlygazhy.tool.EventAnonceUtil;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 04.07.2017.
 */
public class RemindEventStartOneDayTask extends AbstractTask {
    private long eventId;
    public RemindEventStartOneDayTask(Bot bot, Reminder reminder, long eventId) {
        super(bot, reminder);
        this.eventId = eventId;
    }

    @Override
    public void run() {
        try {
            answerAllMembersAboutDay();
        } catch (SQLException | TelegramApiException e) {
            e.printStackTrace();
        }

    }
    private void answerAllMembersAboutDay() throws SQLException, TelegramApiException {
        ListDao  listDao   = factory.getListDao("EVENTS_LIST");
        String[] memberIds = listDao.getMembersWhoNeedReminder(String.valueOf(eventId)).split("`");
        if (memberIds != null){
            Event    event     = listDao.getEvent(String.valueOf(eventId));
            String text;

            if(!event.isBY_ADMIN()){
                text    = EventAnonceUtil.getEventWithPatternNoByAdmin(event, messageDao);
            }
            else
            {
                text    = EventAnonceUtil.getEventWithPatternByAdmin(event, messageDao);

            }
            boolean gotPhoto    = false;
            boolean gotDocument = false;
            SendPhoto    sendPhoto    = null;
            SendDocument sendDocument = null;
            String photo        = event.getPHOTO();
            if(photo!=null){
                sendPhoto = new SendPhoto().setPhoto(photo);
                gotPhoto = true;
            }
            String document     = event.getDOCUMENT();
            if(document!=null){
                sendDocument = new SendDocument().setDocument(document);
                gotDocument = true;
            }
            SendMessage sendMessage = new SendMessage().setText(text)
                    .setParseMode(ParseMode.HTML);

            for(String string : memberIds){
                long chatId = memberDao.getMemberChatId(string);
                try{
                if(gotPhoto){
                    sendPhoto.setChatId(chatId);
                    bot.sendPhoto(sendPhoto);
                }
                bot.sendMessage(sendMessage.setChatId(chatId));
                if(gotDocument){
                    sendDocument.setChatId(chatId);
                    bot.sendDocument(sendDocument);
                }}
                catch (TelegramApiException ignored){

                }

            }
        }
        else{
            reminder.getLogger().info("There is no one needs reminder");
        }
    }
}
