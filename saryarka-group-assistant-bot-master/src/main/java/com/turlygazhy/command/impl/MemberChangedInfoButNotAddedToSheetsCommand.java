package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Member;
import com.turlygazhy.entity.Message;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

public class MemberChangedInfoButNotAddedToSheetsCommand extends Command {
    private long userId;
    public MemberChangedInfoButNotAddedToSheetsCommand(long userId) {
        this.userId = userId;
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Message message = messageDao.getMessage(8);
        Member member = memberDao.selectByUserId(Math.toIntExact(userId));
        String text = message.getSendMessage().getText()
                .replaceAll("fio"        , member.getFIO())
                .replaceAll("companyName", member.getCompanyName())
                .replaceAll("contact"    , member.getContact())
                .replaceAll("nisha"      , member.getNisha())
                .replaceAll("phoneNumber", member.getPhoneNumber())
                .replaceAll("memberCity" , member.getCity());
        SendMessage sendMessage = new SendMessage().setChatId(getAdminChatId()).setText("Пользаватель изменил данные о себе\n" + text)
                .setReplyMarkup(getAddToSheetKeyboard(Math.toIntExact(userId),userId, member.getUserName()));
        bot.sendMessage(sendMessage);
        return true;
    }
}
