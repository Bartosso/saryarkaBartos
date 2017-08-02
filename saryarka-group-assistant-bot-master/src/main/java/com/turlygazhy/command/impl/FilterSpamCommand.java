package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.PoorGuysDao;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public class FilterSpamCommand extends Command {
    private long   spammerChatId;
    private long   snitchChatId;
    private String messageText;
    private String spammerUsername;
    private String snitchUsername;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {

        messageText = update.getMessage().getReplyToMessage().getText();


        spammerUsername = "@" + update.getMessage().getReplyToMessage().getFrom().getUserName();
        spammerChatId   = update.getMessage().getReplyToMessage().getFrom().getId();
        if (spammerUsername.equals("@@null")){
            spammerUsername = update.getMessage().getReplyToMessage().getFrom().getFirstName();
        }
        snitchUsername = "@" + update.getMessage().getFrom().getUserName();
        snitchChatId   = update.getMessage().getFrom().getId();
        if (snitchUsername.equals("@@null")){
            snitchUsername = update.getMessage().getFrom().getFirstName();
        }
        bot.deleteMessage(new DeleteMessage(update.getMessage().getChatId(),update.getMessage().getReplyToMessage().getMessageId()));
        bot.deleteMessage(new DeleteMessage(update.getMessage().getChatId(),update.getMessage().getMessageId()));
        String messageTextToAdmin = snitchUsername + ", уникальный идентификатор (" + snitchChatId + ") пометил сообщение \n"
        +spammerUsername + ". Уникальный идентификатор [" + spammerChatId + "] спамом.\nСообщение:\n" + messageText;

        PoorGuysDao poorGuysDao = factory.getPoorGuysDao();
        if (poorGuysDao.getPoorGuy(spammerChatId)!=0){
            messageTextToAdmin = snitchUsername + ", уникальный идентификатор (" + snitchChatId + ") пометил сообщение \n"
                    +spammerUsername + ". Уникальный идентификатор [" + spammerChatId + "] спамом.<b>ПОВТОРНОЕ НАРУШЕНИЕ</b>\nСообщение:\n" + messageText;
            bot.sendMessage(new SendMessage(getAdminChatId(), messageTextToAdmin ).setReplyMarkup(keyboardMarkUpDao.select(47)).setParseMode(ParseMode.HTML));
        }
        else {
            bot.sendMessage(new SendMessage(getAdminChatId(), messageTextToAdmin).setReplyMarkup(keyboardMarkUpDao.select(47)));
        }
        return true;
    }
}
