package com.turlygazhy;

import com.turlygazhy.command.impl.EventVoteCommand;
import com.turlygazhy.command.impl.SearchCommand;
import com.turlygazhy.command.impl.SearchMembersCommand;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.KeyWordDao;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.dao.impl.MemberDao;
import com.turlygazhy.dao.impl.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.ForwardMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.print.DocFlavor;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

/**
 * Created by Yerassyl_Turlygazhy on 11/24/2016.
 */
public class Bot extends TelegramLongPollingBot {
    private Map<Long, Conversation> conversations = new HashMap<>();
    private static final Logger logger            = LoggerFactory.getLogger(Bot.class);
    private DaoFactory factory                    = DaoFactory.getFactory();
    private KeyWordDao keyWordDao                 = factory.getKeyWordDao();
    private UserDao userDao                       = factory.getUserDao();
    private ListDao listDao                       = factory.getListDao("EVENTS_LIST");
    private String EVENTS_TABLE_NAME              = "EVENTS_LIST";
    private final String GROUP_FOR_VOTE           = "-224196565";



    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message == null) {
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
        List<String> keywords   = keyWordDao.selectAll();
        List<String> eventsVote = listDao.getEvents(EVENTS_TABLE_NAME);
        Message updateMessage = update.getMessage();
        if (updateMessage == null) {
            updateMessage = update.getCallbackQuery().getMessage();
        }
        String text = updateMessage.getText();
        for (String keyword : keywords) {
            if (text.toLowerCase().contains(keyword.toLowerCase())) {
                SearchCommand searchCommand = new SearchCommand();
                searchCommand.setSearchString(text.toLowerCase().replace(keyword.toLowerCase(), "").trim());
                Conversation conversation = getConversation(updateMessage.getChatId());
//                conversation.setCommand(searchCommand);
                SearchMembersCommand searchMembersCommand = new SearchMembersCommand();
                Conversation askMan = getConversation(update.getMessage().getFrom().getId().longValue());
                conversation.setCommand(searchMembersCommand);
                conversation.handleUpdate(update, this);
            }
        }
        for (String string: eventsVote){
            if(text.toLowerCase().contains(string.toLowerCase())) {
                Conversation conversation = getConversation(updateMessage.getChatId());
                conversation.handleUpdate(update, this);
            }
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

    public String getBotUsername() {
        return "Bartosso_bot";
    }

    public String getBotToken() {
        return "435288602:AAFd_H07sYWmB0LpGtWAfBrcQHC8PsmOkaQ";
    }

    public String getGROUP_FOR_VOTE(){ return  GROUP_FOR_VOTE; }

}
