package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Member;
import com.turlygazhy.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendDocument;
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


    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        String  idToDB;
        String  chose;
        String  eventID      = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1,
                update.getCallbackQuery().getData().indexOf("/"));
        chose                = update.getCallbackQuery().getData().substring(0,update.getCallbackQuery().getData().indexOf(":"));
        String  eventsList   = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf("/")+1);
        long    chatId       = update.getCallbackQuery().getMessage().getChatId();
        long    privateChatID= update.getCallbackQuery().getFrom().getId();
        String  EVENT_TYPE   = "";
        String  daoListName  = "";
        switch (eventsList){
            case "будет":
                EVENT_TYPE  = "EVENTS_WHERE_VOTED";
                daoListName = "EVENTS_LIST";
                break;
            case "было" :
                EVENT_TYPE = "ENDED_EVENTS_VOTED";
                daoListName = "ENDED_EVENTS_LIST";
                break;
        }
        ListDao listDao = factory.getListDao(daoListName);

        if(memberDao.getMemberId(update.getCallbackQuery().getFrom().getId())!= null){
           idToDB = memberDao.getMemberId(update.getCallbackQuery().getFrom().getId());
            if(isVoted(idToDB,eventID,EVENT_TYPE)){
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
            bot.sendMessage(new SendMessage(privateChatID, whoWillGo(listDao,eventID)));
//            memberDao.addEventsWhereVoted(idToDB, eventID, EVENT_TYPE);
                break;
            case "Планирую":
            listDao.voteEvent(eventID, idToDB, "MAYBE_USERS_ID");
//            memberDao.addEventsWhereVoted(idToDB, eventID, EVENT_TYPE);
                break;
            case "Пропустить":
            listDao.voteEvent(eventID, idToDB, "NOT_GO_USERS_ID");
//            memberDao.addEventsWhereVoted(idToDB, eventID, EVENT_TYPE);
                break;
        }

        if(memberDao.getMemberId(update.getCallbackQuery().getFrom().getId())!= null){
            memberDao.addEventsWhereVoted(idToDB, eventID, EVENT_TYPE);
        }



        try {
            if(chatId<0){
                bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setReplyMarkup((InlineKeyboardMarkup) getKeyBoardForVote(eventID,eventsList,listDao))
                        .setChatId(chatId).setMessageId(update.getCallbackQuery().getMessage().getMessageId()));}
            else {
                bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setReplyMarkup((InlineKeyboardMarkup) getKeyBoardForVote(eventID,eventsList,listDao))
                        .setChatId(chatId).setMessageId(update.getCallbackQuery().getMessage().getMessageId()));}

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return true;}

        private boolean isVoted(String memberId, String eventId, String EVENT_TYPE) throws SQLException {
        boolean isVoted  = false;
        String string    = memberDao.getEventsWhereVoted(memberId, EVENT_TYPE);
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

        private String whoWillGo(ListDao listDao, String eventID) throws SQLException {
        int i = 1;
        String   ids       = listDao.getVotes(eventID,"WILL_GO_USERS_ID");
        String[] membersId = ids.split("/");
        StringBuilder sb   = new StringBuilder();
        Member member;
        for(String string: membersId){
            try{
            member = memberDao.getMemberById(Long.parseLong(string));
            sb.append(i).append(". ").append(member.getFIO()).append("\n");
            i++;
            }catch (Exception e){
                member = null;
            }
        }
        return messageDao.getMessage(140).getSendMessage().getText() + "\n "+sb.toString();}
}
