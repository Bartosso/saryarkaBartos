package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Member;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 19.06.2017.
 */
public class ShowCabinetCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        com.turlygazhy.entity.Message message = messageDao.getMessage(messageId);
        long chatId = update.getMessage().getChatId();

        Member member = memberDao.selectByUserId(Math.toIntExact(chatId));
        if(member == null ){
            bot.sendMessage(new SendMessage(chatId, messageDao.getMessage(71).getSendMessage().getText()));
            return true;
        }
        if(memberDao.isMemberAdded(Math.toIntExact(chatId))){
        SendMessage sendMessage = message.getSendMessage().setChatId(chatId).setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));
        bot.sendMessage(sendMessage);
        ShowInfoAboutMemberCommand showInfoAboutMemberCommand = new ShowInfoAboutMemberCommand();
        showInfoAboutMemberCommand.setMessageId(8);
        showInfoAboutMemberCommand.execute(update, bot);}
        else {
            bot.sendMessage(new SendMessage(chatId, messageDao.getMessage(43).getSendMessage().getText()));
        }
        return true;
    }
}
