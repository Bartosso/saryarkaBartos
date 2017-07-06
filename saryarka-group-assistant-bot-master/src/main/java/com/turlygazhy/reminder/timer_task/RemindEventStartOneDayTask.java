package com.turlygazhy.reminder.timer_task;

import com.turlygazhy.Bot;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Event;
import com.turlygazhy.entity.Message;
import com.turlygazhy.reminder.Reminder;
import com.turlygazhy.tool.DateUtil;
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
            String date = event.getWHEN().substring(0,2) + " " + DateUtil.getMonthInRussian(Integer.parseInt(event
                    .getWHEN().substring(3, 5)))
                    +event.getWHEN().substring(event.getWHEN().indexOf(" "));
            if(!event.isBY_ADMIN()){
                Message message = messageDao.getMessage(92);

                text    = messageDao.getMessage(151).getSendMessage().getText()
                        + "\n" + message.getSendMessage().getText()
                        .replaceAll("event_text"         , event.getEVENT_NAME())
                        .replaceAll("event_address"      , event.getPLACE())
                        .replaceAll("event_time"         , date)
                        .replaceAll("event_contact"      , event.getCONTACT_INFORMATION())
                        .replaceAll("event_program"      , event.getPROGRAM())
                        .replaceAll("event_dress_code"   , event.getDRESS_CODE())
                        .replaceAll("event_rules"        , event.getRULES());
                if(event.getPAGE()!= null){
                    text = text+"\n\n<b>Страница мероприятия/регистрация</b>:"+event.getPAGE();
                }
            }
            else
            {
                Message message = messageDao.getMessage(139);

                text    = messageDao.getMessage(151).getSendMessage().getText()
                + "\n" + message.getSendMessage().getText()
                        .replaceAll("event_text"         , event.getEVENT_NAME())
                        .replaceAll("event_address"      , event.getPLACE())
                        .replaceAll("event_time"         , date)
                        .replaceAll("event_contact"      , event.getCONTACT_INFORMATION())
                        .replaceAll("event_program"      , event.getPROGRAM())
                        .replaceAll("event_dress_code"   , event.getDRESS_CODE())
                        .replaceAll("event_rules"        , event.getRULES());
                if(event.getPAGE()!= null){
                    text = text+"\n\n<b>Регистрация</b>:"+event.getPAGE();
                }
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
                if(gotPhoto){
                    sendPhoto.setChatId(chatId);
                    bot.sendPhoto(sendPhoto);
                }
                bot.sendMessage(sendMessage.setChatId(chatId));
                if(gotDocument){
                    sendDocument.setChatId(chatId);
                    bot.sendDocument(sendDocument);
                }

            }
        }
        else{
            reminder.getLogger().info("There is no one needs reminder");
        }
    }
}
