package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Button;
import com.turlygazhy.entity.Member;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 1/5/17.
 */
public class RequestCallCommand extends Command {

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Message updateMessage = update.getMessage();
        Long chatId = updateMessage.getChatId();
        Integer userId = updateMessage.getFrom().getId();
        Member member = memberDao.selectByUserId(userId);

        String text = messageDao.getMessage(56).getSendMessage().getText();
        text = text.replaceAll("fio", member.getFIO()).replaceAll("companyName", member.getCompanyName())
                .replaceAll("contact", member.getContact()).replaceAll("nisha", member.getNisha())
                .replaceAll("naviki", member.getNaviki()).replaceAll("phoneNumber", member.getPhoneNumber());
        sendMessageToAdmin(text, bot);
        sendMessage(57, chatId, bot);
        return true;
    }
}
