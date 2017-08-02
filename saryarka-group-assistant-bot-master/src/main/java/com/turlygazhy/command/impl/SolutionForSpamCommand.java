package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.PoorGuysDao;
import org.telegram.telegrambots.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public class SolutionForSpamCommand extends Command {
    private String chose;
    private String spammerUsername;
    private String snitchUsername;
    private String messageText;
    private long   spammerChatId;
    private long   snitchChatId;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (update.hasCallbackQuery()){
            PoorGuysDao poorGuysDao = factory.getPoorGuysDao();
            chose           = update.getCallbackQuery().getData();
            snitchUsername  = update.getCallbackQuery().getMessage().getText().substring(0, update.getCallbackQuery().getMessage()
            .getText().indexOf(","));
            spammerUsername = update.getCallbackQuery().getMessage().getText().substring(update.getCallbackQuery().getMessage()
            .getText().indexOf("\n")+1, update.getCallbackQuery().getMessage().getText().indexOf("."));
            snitchChatId    = Long.parseLong(update.getCallbackQuery().getMessage().getText().substring(update.getCallbackQuery().getMessage()
            .getText().indexOf("(")+1, update.getCallbackQuery().getMessage().getText().indexOf(")")));
            spammerChatId   = Long.parseLong(update.getCallbackQuery().getMessage().getText().substring(update.getCallbackQuery().getMessage()
            .getText().indexOf("[")+1, update.getCallbackQuery().getMessage().getText().indexOf("]")));
            messageText     = update.getCallbackQuery().getMessage().getText().substring(update
            .getCallbackQuery().getMessage().getText().indexOf(":")+1);


            //Ban
            if (chose.equals(buttonDao.getButtonText(242))){
                if (poorGuysDao.getPoorGuy(spammerChatId)!=0){
                    bot.kickMember(new KickChatMember().setUserId(Math.toIntExact(spammerChatId)).setChatId(bot.getGROUP_FOR_VOTE()));
                    poorGuysDao.deletePoorGuy(spammerChatId);
                    bot.sendMessage(new SendMessage(spammerChatId, messageDao.getMessage(178).getSendMessage().getText()));
                    bot.sendMessage(new SendMessage(String.valueOf(update.getCallbackQuery().getFrom().getId()), messageDao.getMessage(174).getSendMessage()
                            .getText()));
                } else {
                bot.deleteMessage(new DeleteMessage(String.valueOf(update.getCallbackQuery()
                        .getFrom().getId()),update.getCallbackQuery().getMessage().getMessageId()));
                bot.sendMessage(new SendMessage(spammerChatId,spammerUsername + messageDao.getMessage(175).getSendMessage().getText()));
                bot.sendMessage(new SendMessage(String.valueOf(update.getCallbackQuery().getFrom().getId()), messageDao.getMessage(174).getSendMessage()
                .getText()));
                poorGuysDao.addNewGuy(spammerChatId);
                }

            }
            //Don't ban
            if (chose.equals(buttonDao.getButtonText(243))){
                bot.deleteMessage(new DeleteMessage(String.valueOf(update.getCallbackQuery()
                        .getFrom().getId()),update.getCallbackQuery().getMessage().getMessageId()));
                bot.sendMessage(new SendMessage(bot.getGROUP_FOR_VOTE(), messageText + "\nОтправил: "+ spammerUsername));
                bot.sendMessage(new SendMessage(snitchChatId, snitchUsername + messageDao
                .getMessage(176).getSendMessage().getText()));
                bot.sendMessage(new SendMessage(getAdminChatId(), messageDao.getMessage(174).getSendMessage()
                .getText()));

            }

        }
        return true;
    }
}
