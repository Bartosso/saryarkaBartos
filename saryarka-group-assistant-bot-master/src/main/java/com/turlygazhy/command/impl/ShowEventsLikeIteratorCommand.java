package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.Main;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Event;
import com.turlygazhy.entity.Member;
import com.turlygazhy.entity.Message;
import com.turlygazhy.tool.DateUtil;
import com.turlygazhy.tool.EventAnonceUtil;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
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
 * Created by Eshu on 27.06.2017.
 */
@SuppressWarnings("Duplicates")
public class ShowEventsLikeIteratorCommand extends Command {
    private int              photoId;
    private int              messageId;
    private int              documentId;
    private int              whoGoId;
    private int              wannaReminderId;
    private String           targetList;
    private long             chatId;
    private ArrayList<Event> events;
    private String           eventType  = "";
    private ListDao          listDao;
    private boolean          photoPosted = false;
    private Event            event;
    private String           eventTypeInMember;
    private int              page = 0;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        String chose;
        if(update.hasCallbackQuery()){
            chose = update.getCallbackQuery().getData();
        }
        else {
            chose = update.getMessage().getText();
        }
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
        if (updateMessage == null) {
           updateMessage = update.getCallbackQuery().getMessage();
        }
        chatId          = updateMessage.getChatId();


        if(chose.equals(buttonDao.getButtonText(83))){
            targetList        = "ENDED_EVENTS_LIST";
            eventTypeInMember = "ENDED_EVENTS_VOTED";
           listDao            = factory.getListDao(targetList);
            events            = listDao.getAllEvents(true);
            eventType         = "было";
            return getFirst(bot, eventType, chatId);
        }
        if(chose.equals(buttonDao.getButtonText(84))){
            targetList        = "EVENTS_LIST";
            eventTypeInMember = "EVENTS_WHERE_VOTED";
           listDao            = factory.getListDao(targetList);
            events            = listDao.getAllEvents(true);
            eventType         = "будет";
            return getFirst(bot, eventType, chatId);
        }
        if(chose.equals(buttonDao.getButtonText(116))){
            page++;
            return getNext(bot, update);
        }
        if(chose.equals(buttonDao.getButtonText(235))){
            page--;
            return getNext(bot, update);
        }
        if(chose.equals("willGo")||chose.equals("maybeGo")||chose.equals("notGo")||
                chose.equals("likeEvent")||chose.equals("superEvent")||chose.equals("haha")){
            voteInLs(bot, update,chose, listDao, String.valueOf(event.getId()),memberDao.getMemberId(update.getCallbackQuery().getFrom().getId()),eventType, eventTypeInMember);
            return false;
        }
        if(chose.equals(buttonDao.getButtonText(143))){
            try {
                return addToReminderList(bot,update);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(chose.equals(buttonDao.getButtonText(144))){
            bot.deleteMessage(new DeleteMessage().setChatId(String.valueOf(chatId)).setMessageId(update
            .getCallbackQuery().getMessage().getMessageId()));
            wannaReminderId = bot.sendMessage(new SendMessage(chatId,messageDao.getMessage(153).getSendMessage()
                    .getText())).getMessageId();
            return false;
        }

        photoId           = 0;
        messageId         = 0;
        documentId        = 0;
        whoGoId           = 0;
        wannaReminderId   = 0;
        targetList        = null;
        chatId            = 0;
        event             = null;
        events            = null;
        eventType         = null;
        photoPosted       = false;
        eventTypeInMember = null;
        return true;
    }




    private boolean getFirst(Bot bot, String eventType, long chatId) throws SQLException, TelegramApiException {
        if(events.isEmpty()){
            Message messageFoundNothing = messageDao.getMessage(84);
            bot.sendMessage(messageFoundNothing.getSendMessage().setChatId(chatId));
            return true;
        }
        else{
        event     = events.get(page);
            String text = "";
            if(!event.isBY_ADMIN()){

                text   = EventAnonceUtil.getEventWithPatternNoByAdmin(event, messageDao);
            }
            else
            {
                text   = EventAnonceUtil.getEventWithPatternByAdmin(event, messageDao);
            }

        SendPhoto sendPhoto = new SendPhoto().setPhoto(event.getPHOTO());

            if(event.getPHOTO() != null){
                sendPhoto.setPhoto(event.getPHOTO());
               photoId = bot.sendPhoto(sendPhoto.setChatId(chatId)).getMessageId();
            }
            else {
               photoId = 0;
            }
            boolean gotNext = events.size()>1;
            ReplyKeyboard keyboard = getKeyBoardForVoteWithSomeThings(String.valueOf(event.getId()),eventType,listDao, false, gotNext);
            messageId = bot.sendMessage(new SendMessage().setChatId(chatId).setText(text)
        .setReplyMarkup(keyboard)
        .setParseMode(ParseMode.HTML)).getMessageId();
            if(event.getDOCUMENT() != null) {
                documentId = bot.sendDocument(new SendDocument().setChatId(chatId).setDocument(event.getDOCUMENT()))
                        .getMessageId();
            }
            else{
                documentId = 0;
            }
//                events.remove(0);

    return false;}

    }





    private boolean getNext(Bot bot, Update update) throws TelegramApiException, SQLException {
        long chatId       = update.getCallbackQuery().getFrom().getId();

        if(events.isEmpty()){
            Message messageFoundNothing = messageDao.getMessage(84);
            bot.deleteMessage(new DeleteMessage(chatId, messageId));
            if(photoId!=0){
                bot.deleteMessage(new DeleteMessage(chatId, photoId));
            }
            if(documentId!=0){
                bot.deleteMessage(new DeleteMessage(chatId, documentId));
            }
            if(whoGoId !=0){
                bot.deleteMessage(new DeleteMessage(chatId, whoGoId));
                whoGoId = 0;
            }
            if(wannaReminderId !=0){
                bot.deleteMessage(new DeleteMessage(chatId, wannaReminderId));
                wannaReminderId = 0;
            }
            bot.sendMessage(messageFoundNothing.getSendMessage().setChatId(chatId));
            return true;
        }
        else {
            if(photoId != 0){
            bot.deleteMessage(new DeleteMessage().setChatId(String.valueOf(chatId)).setMessageId(photoId));
            }
            if(documentId != 0){
            bot.deleteMessage(new DeleteMessage().setChatId(String.valueOf(chatId)).setMessageId(documentId));
            }
            if(whoGoId != 0) {
                bot.deleteMessage(new DeleteMessage(chatId, whoGoId));
                whoGoId = 0;
            }
            if(wannaReminderId !=0){
                bot.deleteMessage(new DeleteMessage(chatId, wannaReminderId));
                wannaReminderId = 0;
            }
            bot.deleteMessage(new DeleteMessage().setChatId(String.valueOf(chatId)).setMessageId(update.getCallbackQuery().getMessage().getMessageId()));
            event = events.get(page);
            String text = "";
            if(!event.isBY_ADMIN()){
                text      = EventAnonceUtil.getEventWithPatternNoByAdmin(event,messageDao);
            }
            else
            {

                text  = EventAnonceUtil.getEventWithPatternByAdmin(event, messageDao);
            }
            SendPhoto sendPhoto = new SendPhoto().setPhoto(event.getPHOTO());
            if (event.getPHOTO() != null) {
                sendPhoto.setPhoto(event.getPHOTO());
                photoId     = bot.sendPhoto(sendPhoto.setChatId(chatId)).getMessageId();
            }
            else{
                photoId    = 0;
            }
            boolean gotPrevious = page != 0;
            boolean gotNext     = (page+1)<events.size();

            messageId = bot.sendMessage(new SendMessage().setChatId(chatId).setText(text)
                    .setReplyMarkup(getKeyBoardForVoteWithSomeThings(String.valueOf(event.getId()),eventType, listDao,gotPrevious, gotNext ))
                    .setParseMode(ParseMode.HTML))
                    .getMessageId();
            if(event.getDOCUMENT() != null) {
               documentId = bot.sendDocument(new SendDocument().setChatId(chatId).setDocument(event.getDOCUMENT()))
               .getMessageId();
            }
            else{
                documentId = 0;
            }
//            events.remove(0);
            return false;
        }
    }





    @SuppressWarnings("Duplicates")
    private ReplyKeyboard getKeyBoardForVoteWithSomeThings(String eventId, String eventTypeToVote, ListDao listDao, boolean gotPrevius, boolean gotNext) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        switch (eventTypeToVote) {

            case "было":

                InlineKeyboardButton like = new InlineKeyboardButton();
                String likeVotes = listDao.getVotes(eventId, "WILL_GO_USERS_ID");
                like.setText("Лайк \uD83D\uDC4D " + getVoteCount(likeVotes));
                like.setCallbackData("likeEvent");
                row.add(like);
                rows.add(row);

                row = new ArrayList<>();
                String supirVotes = listDao.getVotes(eventId, "MAYBE_USERS_ID");
                InlineKeyboardButton supir = new InlineKeyboardButton();
                supir.setText("Супер! \uD83D\uDE03 " + getVoteCount(supirVotes));
                supir.setCallbackData("superEvent");
                row.add(supir);
                rows.add(row);

//                row = new ArrayList<>();
//                String hahaVotes = listDao.getVotes(eventId, "NOT_GO_USERS_ID");
//                InlineKeyboardButton haha = new InlineKeyboardButton();
//                haha.setText("Ха ха! \uD83D\uDE02 " + getVoteCount(hahaVotes));
//                haha.setCallbackData("haha");
//                row.add(haha);
//                rows.add(row);

                if (gotNext){
                row = new ArrayList<>();
                InlineKeyboardButton next = new InlineKeyboardButton();
                next.setText(buttonDao.getButtonText(116));
                next.setCallbackData(buttonDao.getButtonText(116));
                row.add(next);
                rows.add(row);
                }


                if (gotPrevius){
                    row = new ArrayList<>();
                    InlineKeyboardButton next = new InlineKeyboardButton();
                    next.setText(buttonDao.getButtonText(235));
                    next.setCallbackData(buttonDao.getButtonText(235));
                    row.add(next);
                    rows.add(row);
                }
                break;

            case "будет":
                InlineKeyboardButton will_go = new InlineKeyboardButton();
                String will_go_votes = listDao.getVotes(eventId, "WILL_GO_USERS_ID");
                will_go.setText("Пойду " + getVoteCount(will_go_votes));
                will_go.setCallbackData("willGo");
                row.add(will_go);
                rows.add(row);

                row = new ArrayList<>();
                String maybe_go_votes = listDao.getVotes(eventId, "MAYBE_USERS_ID");
                InlineKeyboardButton maybe_go = new InlineKeyboardButton();
                maybe_go.setText("Планирую " + getVoteCount(maybe_go_votes));
                maybe_go.setCallbackData("maybeGo");
                row.add(maybe_go);
                rows.add(row);

//                row = new ArrayList<>();
//                String not_go_votes = listDao.getVotes(eventId, "NOT_GO_USERS_ID");
//                InlineKeyboardButton not_go = new InlineKeyboardButton();
//                not_go.setText("Пропустить " + getVoteCount(not_go_votes));
//                not_go.setCallbackData("notGo");
//                row.add(not_go);
//                rows.add(row);

                if (gotNext){
                    row = new ArrayList<>();
                    InlineKeyboardButton next = new InlineKeyboardButton();
                    next.setText(buttonDao.getButtonText(116));
                    next.setCallbackData(buttonDao.getButtonText(116));
                    row.add(next);
                    rows.add(row);
                }


                if (gotPrevius){
                    row = new ArrayList<>();
                    InlineKeyboardButton next = new InlineKeyboardButton();
                    next.setText(buttonDao.getButtonText(235));
                    next.setCallbackData(buttonDao.getButtonText(235));
                    row.add(next);
                    rows.add(row);
                }
        }
        keyboard.setKeyboard(rows);
        return keyboard;

    }

    private long getVoteCount(String votes) {
        long count = 0;
        if (votes == null) {
            return count;
        } else {
            for (char element : votes.toCharArray()) {
                if (element == '/') count++;
            }
        }
        return count;
    }



    private void voteInLs(Bot bot,Update update, String chose, ListDao listDao, String eventID,String idToDB, String eventType, String eventTypeInMember ) throws SQLException, TelegramApiException {

        if(!isVoted(idToDB,eventID,eventTypeInMember)) {


            switch (eventType) {

                case "будет":
                    switch (chose) {
                        case "willGo":
                            listDao.voteEvent(eventID, idToDB, "WILL_GO_USERS_ID");
                            memberDao.addEventsWhereVoted(idToDB, eventID, eventTypeInMember);
                            whoGoId         = bot.sendMessage(new SendMessage(chatId, whoWillGo(listDao,eventID)))
                                    .getMessageId();
                            wannaReminderId = bot.sendMessage(messageDao.getMessage(148).getSendMessage()
                            .setChatId(chatId).setReplyMarkup(keyboardMarkUpDao.select(messageDao.getMessage(148)
                                    .getKeyboardMarkUpId()))).getMessageId();
                            break;
                        case "maybeGo":
                            listDao.voteEvent(eventID, idToDB, "MAYBE_USERS_ID");
                            memberDao.addEventsWhereVoted(idToDB, eventID, eventTypeInMember);
                            break;
                        case "notGo":
                            listDao.voteEvent(eventID, idToDB, "NOT_GO_USERS_ID");
                            memberDao.addEventsWhereVoted(idToDB, eventID, eventTypeInMember);
                            break;
                    }
                case "было":
                    switch (chose) {
                        case "likeEvent":
                            listDao.voteEvent(eventID, idToDB, "WILL_GO_USERS_ID");
                            memberDao.addEventsWhereVoted(idToDB, eventID, eventTypeInMember);
                            break;
                        case "superEvent":
                            listDao.voteEvent(eventID, idToDB, "MAYBE_USERS_ID");
                            memberDao.addEventsWhereVoted(idToDB, eventID, eventTypeInMember);
                            break;
                        case "haha":
                            listDao.voteEvent(eventID, idToDB, "NOT_GO_USERS_ID");
                            memberDao.addEventsWhereVoted(idToDB, eventID, eventTypeInMember);
                            break;
                    }
            }
            boolean gotPrevious = page != 0;
            boolean gotNext     = (page+1)<events.size();
            bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setReplyMarkup((InlineKeyboardMarkup) getKeyBoardForVoteWithSomeThings(eventID, eventType, listDao, gotPrevious,gotNext))
                    .setChatId(chatId).setMessageId(update.getCallbackQuery().getMessage().getMessageId()));
        }
    }


    @SuppressWarnings("ConstantConditions")
    private boolean isVoted(String memberId, String eventId, String eventTypeInMember) throws SQLException {
        boolean isVoted  = false;
        String string    = memberDao.getEventsWhereVoted(memberId, eventTypeInMember);
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
                sb.append(i).append(". ").append(member.getFIO()).append("\n ");
                i++;
            }catch (Exception e){
                member = null;
            }
        }
        return messageDao.getMessage(140).getSendMessage().getText() + "\n "+sb.toString();}

    private boolean addToReminderList(Bot bot, Update update) throws ParseException, SQLException, TelegramApiException {
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
    return false;
    }


}
