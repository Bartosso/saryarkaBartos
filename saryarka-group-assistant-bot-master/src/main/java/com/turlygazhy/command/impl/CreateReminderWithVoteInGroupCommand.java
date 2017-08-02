package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Event;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by Eshu on 05.07.2017.
 */
public class CreateReminderWithVoteInGroupCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        String  eventId      = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1);
        String  chose        = update.getCallbackQuery().getData().substring(0, update.getCallbackQuery().getData().indexOf(":"));
        long    chatId       = update.getCallbackQuery().getFrom().getId();

        if (chose.equals(buttonDao.getButtonText(145))){
            try {
                return addToReminderList(bot,update,eventId,chatId);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (chose.equals(buttonDao.getButtonText(146))){
            bot.deleteMessage(new DeleteMessage().setChatId(String.valueOf(chatId)).setMessageId(update.getCallbackQuery().getMessage().getMessageId()));
            bot.sendMessage(new SendMessage(chatId, messageDao.getMessage(153).getSendMessage().getText()));
        }
        return true;
    }
    private boolean addToReminderList(Bot bot, Update update,String eventId, long chatId) throws ParseException, SQLException, TelegramApiException {
        ListDao listDao          = factory.getListDao("EVENTS_LIST");
        Event event              = listDao.getEvent(eventId);
        Date now                 = new Date();
        SimpleDateFormat format  = new SimpleDateFormat();
        format.applyPattern("dd.MM.yy");
        Date eventDate           = format.parse(event.getWHEN());
        LocalDateTime eventLocal = LocalDateTime.ofInstant(eventDate.toInstant(), ZoneId.systemDefault());
        Date dateEventMinusDay   = Date.from(eventLocal.minusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        Date dateEventMinusHour  = Date.from(eventLocal.minusHours(1).atZone(ZoneId.systemDefault()).toInstant());
        if (now.before(dateEventMinusDay)) {
            listDao.addMemberWhoNeedReminder(String.valueOf(event.getId()),memberDao.getMemberId(chatId));
            bot.editMessageText(new EditMessageText().setChatId(chatId).setMessageId(
                    update.getCallbackQuery().getMessage().getMessageId())
                    .setText(messageDao.getMessage(150).getSendMessage().getText()).setReplyMarkup(null));

        } else {
            if (now.before(dateEventMinusHour)){
                listDao.addMemberWhoNeedReminder(String.valueOf(event.getId()),memberDao.getMemberId(chatId));
                bot.editMessageText(new EditMessageText().setChatId(chatId).setMessageId(
                        update.getCallbackQuery().getMessage().getMessageId())
                        .setText(messageDao.getMessage(150).getSendMessage().getText()).setReplyMarkup(null));
            }
            else {
                bot.sendMessage(messageDao.getMessage(149).getSendMessage().setChatId(chatId));
            }

        }
        return true;
    }
}
