package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Member;
import com.turlygazhy.entity.MessageElement;
import org.h2.jdbc.JdbcSQLException;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eshu on 16.06.2017.
 */
public class RequestToAddPartnerCommand extends Command {
    private String message;
    private String chatId;
    private int step   = 0;
    private MessageElement expectedMessageElement;
    private String idPartner;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(expectedMessageElement != null){
        switch (step){
            case 0:
                message = update.getMessage().getText();
                step = 1;
                break;
            case 1:

        }
        }

        if(step == 0){
        chatId = String.valueOf(update.getCallbackQuery().getFrom().getId());
        SendMessage sendMessage = new SendMessage().setChatId(chatId).setText(
                "Введите сообщение которое вы хотите отправить");
        bot.sendMessage(sendMessage);
        expectedMessageElement = MessageElement.TEXT;
        idPartner = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":") + 1);
        return false;
        }

        if(step == 1) {
            try {
                Member wannaBePartner = memberDao.getMember(chatId);

                String chatPartner = String.valueOf(memberDao.getMemberById(Long.parseLong(idPartner)).getUserId());
                ReplyKeyboard inlineKeyboardMarkup = keyboardForPartner(String.valueOf(wannaBePartner.getChatId()));
                String pattern = messageDao.getMessage(37).getSendMessage().getText()
                        .replaceAll("fio", wannaBePartner.getFIO())
                        .replaceAll("companyName", wannaBePartner.getCompanyName())
                        .replaceAll("contact", wannaBePartner.getContact())
                        .replaceAll("nisha", wannaBePartner.getNisha());
                SendMessage sendNotificationToPartner = new SendMessage().setText("<b>Вам написали:</b>\n" + message + "\n" + pattern).setChatId(chatPartner)
                        .setReplyMarkup(inlineKeyboardMarkup).setParseMode(ParseMode.HTML);
                SendMessage sendMessageToWannaBe = new SendMessage().setText("Ваше сообщение отправленно").setChatId(wannaBePartner.getChatId());
                bot.sendMessage(sendNotificationToPartner);
                bot.sendMessage(sendMessageToWannaBe);
            } catch (JdbcSQLException e){
                bot.sendMessage(messageDao.getMessage(71).getSendMessage().setChatId(chatId));
            }
            idPartner              = null;
            step                   = 0;
            message                = null;
            expectedMessageElement = null;
            return true;
        }
        return true;
    }

    private ReplyKeyboard keyboardForPartner(String idWannaBePartner){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows     = new ArrayList<>();
        List<InlineKeyboardButton> row            = new ArrayList<>();

        InlineKeyboardButton      yesAddPartner   = new InlineKeyboardButton();
        yesAddPartner.setText("Ответить");
        yesAddPartner.setCallbackData("add_to_partner:"+idWannaBePartner);
        row.add(yesAddPartner);
        rows.add(row);

        row = new ArrayList<>();
        InlineKeyboardButton      ignorePartner   = new InlineKeyboardButton();
        ignorePartner.setText("Проигнорировать");
        ignorePartner.setCallbackData("ignore_add_to_partner:"+idWannaBePartner);
        row.add(ignorePartner);
        rows.add(row);

        inlineKeyboardMarkup.setKeyboard(rows);

    return inlineKeyboardMarkup;
    }
}
