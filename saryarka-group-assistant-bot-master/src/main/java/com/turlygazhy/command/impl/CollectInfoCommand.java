package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Button;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 1/21/17.
 */
public class CollectInfoCommand extends Command {
    private String nisha;
    private String naviki;
    private String contact;
    private WaitingType waitingType;
    private String companyName;
    private String fio;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
        String text;
        if (updateMessage == null) {
            updateMessage = update.getCallbackQuery().getMessage();
            text = update.getCallbackQuery().getData();
        } else {
            text = updateMessage.getText();
        }

        Long chatId = updateMessage.getChatId();

        if (waitingType != null) {
            switch (waitingType) {
                case FIO:
                    fio = text;
                    sendMessage(47, chatId, bot);
                    waitingType = WaitingType.COMPANY_NAME;
                    return false;
                case COMPANY_NAME:
                    companyName = text;
                    sendMessage(48, chatId, bot);
                    waitingType = WaitingType.NISHA;
                    return false;
                case NISHA:
                    nisha = text;
                    sendMessage(49, chatId, bot);
                    waitingType = WaitingType.CONTACT;
                    return false;
                case CONTACT:
                    contact = text;
                    sendMessage(50, chatId, bot);
                    waitingType = WaitingType.NAVIKI;
                    return false;
                case NAVIKI:
                    naviki = text;
                    sendMessage(51, chatId, bot);
                    waitingType = WaitingType.PHONE_NUMBER;
                    return false;
                case PHONE_NUMBER:
                    Contact contact = updateMessage.getContact();
                    User user = updateMessage.getFrom();
                    memberDao.insert(nisha, naviki, chatId, user.getUserName(), user.getId(), companyName, this.contact, fio, contact);

//                    String textToAdmin = messageDao.getMessage(42).getSendMessage().getText();
//                    textToAdmin = textToAdmin.replaceAll("fio", fio).replaceAll("companyName", companyName)
//                            .replaceAll("contact", this.contact).replaceAll("nisha", nisha).replaceAll("naviki", naviki);
//
//                    SendMessage sendMessage = new SendMessage().setText(textToAdmin + "\nphone: " + contact.getPhoneNumber())
//                            .setChatId(userDao.getAdminChatId())
//                            .setReplyMarkup(getAddToSheetKeyboard(user.getId(), chatId));
//                   bot.sendMessage(sendMessage);

                    sendMessage(43, chatId, bot);
                    return true;
            }
        }

        if (text.equals(buttonDao.getButtonText(1))) {
            sendMessage(46, chatId, bot);
            waitingType = WaitingType.FIO;
            return false;
        }

        return false;
    }
}
