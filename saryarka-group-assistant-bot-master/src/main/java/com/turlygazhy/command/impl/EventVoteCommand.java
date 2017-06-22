package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 15.06.2017.
 */
public class EventVoteCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger(ShowInfoCommand.class);
    private ListDao listDao = factory.getListDao("EVENTS_LIST");

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException {
        String  idToDB;
        String  chose;
        String eventID    = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1,
                update.getCallbackQuery().getData().indexOf("/"));
        chose             = update.getCallbackQuery().getData().substring(0,update.getCallbackQuery().getData().indexOf(":"));
        String eventsList = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf("/")+1);
        long chatId       = update.getCallbackQuery().getMessage().getChatId();

        if(memberDao.getMemberId(update.getCallbackQuery().getFrom().getId())!= null){
           idToDB = memberDao.getMemberId(update.getCallbackQuery().getFrom().getId());
            if(isVoted(idToDB,eventID)){
                return true;
            }
        }
        else{
            idToDB = update.getCallbackQuery().getFrom().getId().toString() + " " + update.getCallbackQuery()
                    .getFrom().getFirstName() + " " + update.getCallbackQuery().getFrom().getLastName() + " " +
            update.getCallbackQuery().getFrom().getUserName();
        }


        switch (chose){
            case "Пойду":
            listDao.voteEvent(eventID, idToDB, "WILL_GO_USERS_ID");
            memberDao.addEventsWhereVoted(idToDB, eventID);
                break;
            case "Планирую":
            listDao.voteEvent(eventID, idToDB, "MAYBE_USERS_ID");
            memberDao.addEventsWhereVoted(idToDB, eventID);
                break;
            case "Пропустить":
            listDao.voteEvent(eventID, idToDB, "NOT_GO_USERS_ID");
            memberDao.addEventsWhereVoted(idToDB, eventID);
                break;
        }

        try {
                bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setReplyMarkup((InlineKeyboardMarkup) getKeyBoardForVote(eventID,eventsList,listDao))
                        .setChatId(chatId).setMessageId(update.getCallbackQuery().getMessage().getMessageId()));

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return true;}

        private boolean isVoted(String memberId, String eventId) throws SQLException {
        boolean isVoted  = false;
        String string    = memberDao.getEventsWhereVoted(memberId);
        if(string == null){
            return isVoted;
        }
        else{
        String[] votes   = string.split("/");
        for(String stringo : votes){
            if(stringo.equals(eventId)){
                isVoted  = true;
            }
        }}
        return isVoted;
        }
}
