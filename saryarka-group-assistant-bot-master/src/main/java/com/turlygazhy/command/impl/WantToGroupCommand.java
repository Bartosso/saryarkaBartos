package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Member;
import org.h2.jdbc.JdbcSQLException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by user on 1/31/17.
 */
public class WantToGroupCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Message updateMessage = update.getMessage();
        Integer userId        = updateMessage.getFrom().getId();
        boolean memberAdded   = memberDao.isMemberAdded(userId);
        Long chatId           = updateMessage.getChatId();

        if (memberAdded) {
            if(!memberDao.checkMemberAgreeToRules(memberDao.getMemberId(chatId))) {
                bot.execute(messageDao.getMessage(70).getSendMessage()
                .setChatId(chatId).setReplyMarkup(keyboardMarkUpDao.select(36)));
            }
            else{
                sendMessage(70, chatId, bot);
            }
//            sendMessage(11, chatId, bot);
            return true;
        }

        try {
            Member member = memberDao.selectByUserId(userId);
            if(member == null){
                bot.execute(new SendMessage().setChatId(String.valueOf(userId)).setText("Вы не заполнили анкету, нажмите /start"));
                return true;
            }
            String text = messageDao.getMessage(42).getSendMessage().getText();
            text = text.replaceAll("fio", member.getFIO()).replaceAll("companyName", member.getCompanyName())
                    .replaceAll("contact", member.getContact()).replaceAll("nisha", member.getNisha())
                    .replaceAll("phoneNumber", member.getPhoneNumber())
                    .replaceAll("memberCity", member.getCity());

//            bot.execute(new SendMessage()
//                    .setChatId(getAdminChatId())
//                    .setText(text)
//                    .setReplyMarkup(getAddToSheetKeyboard(userId, chatId, member.getUserName()))
//            );

            sendMessage(43, chatId, bot);
            return true;
        } catch (JdbcSQLException e) {
            sendMessage(71, chatId, bot);
            return true;
        }
    }
}
