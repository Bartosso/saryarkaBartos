package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Event;
import com.turlygazhy.entity.Member;
import com.turlygazhy.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Eshu on 15.06.2017.
 */
public class EventVoteCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger(ShowInfoCommand.class);
    private ListDao listDao;


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
        String  additionalText = "";
        ReplyKeyboard replyKeyboard = null;
        switch (eventsList){
            case "будет":
                EVENT_TYPE  = "EVENTS_WHERE_VOTED";
                daoListName = "EVENTS_LIST";
                listDao     = factory.getListDao(daoListName);
                replyKeyboard   = getReminderKeyboatd(eventID, listDao);
                additionalText = messageDao.getMessage(148).getSendMessage().getText();
                break;
            case "было" :
                EVENT_TYPE  = "ENDED_EVENTS_VOTED";
                daoListName = "ENDED_EVENTS_LIST";
                listDao     = factory.getListDao(daoListName);
                break;
        }


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
            SendMessage sendMessage = new SendMessage(privateChatID, whoWillGo(listDao,eventID)+"\n"+additionalText)
                    .setReplyMarkup(replyKeyboard);
            bot.sendMessage(sendMessage);

//            memberDao.addEventsWhereVoted(idToDB, eventID, EVENT_TYPE);
                break;
            case "Планирую":
            listDao.voteEvent(eventID, idToDB, "MAYBE_USERS_ID");
//            memberDao.addEventsWhereVoted(idToDB, eventID, EVENT_TYPE);
                break;
//            case "Пропустить":
//            listDao.voteEvent(eventID, idToDB, "NOT_GO_USERS_ID");
////            memberDao.addEventsWhereVoted(idToDB, eventID, EVENT_TYPE);
//                break;
        }

        if(memberDao.getMemberId(update.getCallbackQuery().getFrom().getId())!= null){
            memberDao.addEventsWhereVoted(idToDB, eventID, EVENT_TYPE);
        }



        try {
            if(chatId<0){
                bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setReplyMarkup((InlineKeyboardMarkup) getKeyBoardForVote(Long.parseLong(eventID),eventsList,listDao))
                        .setChatId(chatId).setMessageId(update.getCallbackQuery().getMessage().getMessageId()));}
            else {
                bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setReplyMarkup((InlineKeyboardMarkup) getKeyBoardForVote(Long.parseLong(eventID),eventsList,listDao))
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

        private ReplyKeyboard getReminderKeyboatd(String eventId,ListDao listDao) throws SQLException {
            InlineKeyboardMarkup keyboard         = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            List<InlineKeyboardButton> row        = new ArrayList<>();

            InlineKeyboardButton yesNeedReminder = new InlineKeyboardButton();
            yesNeedReminder.setText(buttonDao.getButtonText(143));
            yesNeedReminder.setCallbackData(buttonDao.getButtonText(145)+":"+ eventId);
            row.add(yesNeedReminder);
            rows.add(row);

            row        = new ArrayList<>();
            InlineKeyboardButton reminderDontNeeded = new InlineKeyboardButton();
            reminderDontNeeded.setText(buttonDao.getButtonText(144));
            reminderDontNeeded.setCallbackData(buttonDao.getButtonText(146)+ ":"+ eventId);
            row.add(reminderDontNeeded);
            rows.add(row);

           return keyboard.setKeyboard(rows);
        }

}
