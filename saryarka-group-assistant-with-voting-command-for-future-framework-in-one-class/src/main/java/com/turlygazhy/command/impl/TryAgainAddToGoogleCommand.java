package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Member;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2/21/17.
 */
public class TryAgainAddToGoogleCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Message updateMessage = update.getMessage();
        Integer userId = updateMessage.getFrom().getId();
        Long chatId = updateMessage.getChatId();
        Member member = memberDao.selectByUserId(userId);
        String textToAdmin = messageDao.getMessage(42).getSendMessage().getText();
        textToAdmin = textToAdmin.replaceAll("fio", member.getFIO()).replaceAll("companyName", member.getCompanyName())
                .replaceAll("contact", member.getContact()).replaceAll("nisha", member.getNisha())
                .replaceAll("naviki", member.getNaviki());

        SendMessage sendMessage = new SendMessage().setText(textToAdmin + "\nphone: " + member.getPhoneNumber())
                .setChatId(userDao.getAdminChatId())
                .setReplyMarkup(getAddToSheetKeyboard(userId, chatId));
        bot.sendMessage(sendMessage);

        sendMessage(43, chatId, bot);
        return false;
    }
}
