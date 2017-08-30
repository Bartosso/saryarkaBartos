package com.turlygazhy;

import com.turlygazhy.command.impl.CurfewCommand;
import com.turlygazhy.command.impl.FilterSpamCommand;
import com.turlygazhy.command.impl.SearchCommand;
import com.turlygazhy.command.impl.SearchMembersCommand;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.KeyWordDao;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.dao.impl.UserDao;
import com.turlygazhy.service.FloodFilterService;
import com.turlygazhy.tool.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Yerassyl_Turlygazhy on 11/24/2016.
 */
public class Bot extends TelegramLongPollingBot {
    private Map<Long, Conversation>          conversations      = new HashMap<>();
    private ConcurrentHashMap<Long, Integer> floodCount         = new ConcurrentHashMap<>();
    private static final Logger              logger             = LoggerFactory.getLogger(Bot.class);
    private DaoFactory                       factory            = DaoFactory.getFactory();
    private KeyWordDao                       keyWordDao         = factory.getKeyWordDao();
    private UserDao                          userDao            = factory.getUserDao();
    private boolean                          curfew             = false;
    private ListDao                          listDao            = factory.getListDao("EVENTS_LIST");
    private String                           EVENTS_TABLE_NAME  = "EVENTS_LIST";
    //todo put group for vote id here
    private final String GROUP_FOR_VOTE                =
            null;




    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message == null) {
            if (update.hasChannelPost()){
                message = update.getChannelPost();
            } else
            message = update.getCallbackQuery().getMessage();
        }
        String title = message.getChat().getTitle();
        if (title != null) {
            try {
                handleGroupUpdate(update);
            } catch (SQLException | TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }
        Conversation conversation = getConversation(update);
        try {
            conversation.handleUpdate(update, this);
        } catch (SQLException | TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private Conversation getConversation(Update update) {
        Message message = update.getMessage();
        if (message == null) {
            message = update.getCallbackQuery().getMessage();
        }
        Long chatId = message.getChatId();
        Conversation conversation = conversations.get(chatId);
        if (conversation == null) {
            logger.info("init new conversation for '{}'", chatId);
            conversation = new Conversation();
            conversations.put(chatId, conversation);
        }
        return conversation;
    }


    private void handleGroupUpdate(Update update) throws SQLException, TelegramApiException {
        if (curfew){
            CurfewCommand curfewCommand = new CurfewCommand();
            curfewCommand.execute(update,this);
        }
        else {
            List<String> keywords = keyWordDao.selectAll();
            List<String> eventsVote = listDao.getEvents(EVENTS_TABLE_NAME);
            Message updateMessage = update.getMessage();
            if (updateMessage == null) {
                if (update.hasChannelPost()) {
                    updateMessage = update.getChannelPost();
                } else
                    updateMessage = update.getCallbackQuery().getMessage();
            }
            String text = updateMessage.getText();
            if (update.hasMessage()){
                if (update.getMessage().hasText()){
                    if (text.toLowerCase().contains("#спам")){
                        if (update.hasMessage()){
                             try {
                                 update.getMessage().getReplyToMessage();
                                 FilterSpamCommand filterSpamCommand = new FilterSpamCommand();
                                 filterSpamCommand.execute(update,this);
                                    } catch (NullPointerException ignored){
                                      }
                             }
                    }
                }
                new FloodFilterService().floodFilter(Long.valueOf(update.getMessage().getFrom().getId()),update, this, floodCount, factory);
                new FloodFilterService().textFilter(update, this);
            }
            try {

                for (String keyword : keywords) {
                    if (text.toLowerCase().contains(keyword.toLowerCase())) {
                        SearchCommand searchCommand = new SearchCommand();
                        searchCommand.setSearchString(text.toLowerCase().replace(keyword.toLowerCase(), "").trim());
                        Conversation conversation = getConversation(updateMessage.getChatId());
                        SearchMembersCommand searchMembersCommand = new SearchMembersCommand();
                        conversation.setCommand(searchMembersCommand);
                        conversation.handleUpdate(update, this);
                    }
                }
            }catch (NullPointerException ignored){}
            if (update.hasCallbackQuery()){
            for (String string : eventsVote) {
                if (text.toLowerCase().contains(string.toLowerCase())) {
                    Conversation conversation = getConversation(updateMessage.getChatId());
                    conversation.handleUpdate(update, this);
                }
            }}
        }
    }


    private Conversation getConversation(Long chatId) {
        Conversation conversation = conversations.get(chatId);
        if (conversation == null) {
            logger.info("init new conversation for '{}'", chatId);
            conversation = new Conversation();
            conversations.put(chatId, conversation);
        }
        return conversation;
    }

    //todo put bot username here
    public String getBotUsername() { return ""; }

    //todo put bot token here
    public String getBotToken() {
        return "";
    }

    public String getGROUP_FOR_VOTE(){ return  GROUP_FOR_VOTE; }

    public void setCurfew(boolean curfew) { this.curfew = curfew; }

    public void restartFloodCountByChatId(long chatId) {
        this.floodCount.remove(chatId);
    }
    public void createNewTimerForFloodCountByChatId(long chatId) {
        Main.getReminder().setEveryHourTask(DateUtil.getNextHour(),chatId);
    }
}
