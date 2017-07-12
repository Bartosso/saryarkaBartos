package com.turlygazhy.command;

import com.turlygazhy.Bot;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.*;
import com.turlygazhy.entity.Message;
import org.telegram.telegrambots.api.methods.send.SendContact;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yerassyl_Turlygazhy on 11/27/2016.
 */
public abstract class Command {
    protected long id;
    protected long messageId;

    protected DaoFactory factory = DaoFactory.getFactory();
    protected UserDao userDao = factory.getUserDao();
    protected MessageDao messageDao = factory.getMessageDao();
    protected KeyboardMarkUpDao keyboardMarkUpDao = factory.getKeyboardMarkUpDao();
    protected ButtonDao buttonDao = factory.getButtonDao();
    protected CommandDao commandDao = factory.getCommandDao();
    protected ConstDao constDao = factory.getConstDao();
    protected MemberDao memberDao = factory.getMemberDao();
    protected KeyWordDao keyWordDao = factory.getKeyWordDao();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return is command finished
     */
    public abstract boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException;

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void sendMessage(long messageId, long chatId, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
        sendMessage(messageId, chatId, bot, null);
    }

    public void sendMessage(String text, long chatId, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
        sendMessage(text, chatId, bot, null);
    }

    public void sendMessage(long messageId, long chatId, TelegramLongPollingBot bot, Contact contact) throws SQLException, TelegramApiException {
        Message message = messageDao.getMessage(messageId);
        SendMessage sendMessage = message.getSendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));
        bot.sendMessage(sendMessage);
        if (contact != null) {
            bot.sendContact(new SendContact()
                    .setChatId(chatId)
                    .setFirstName(contact.getFirstName())
                    .setLastName(contact.getLastName())
                    .setPhoneNumber(contact.getPhoneNumber())
            );
        }
    }

    public void sendMessage(String text, long chatId, TelegramLongPollingBot bot, Contact contact) throws SQLException, TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        bot.sendMessage(sendMessage);
        if (contact != null) {
            bot.sendContact(new SendContact()
                    .setChatId(chatId)
                    .setFirstName(contact.getFirstName())
                    .setLastName(contact.getLastName())
                    .setPhoneNumber(contact.getPhoneNumber())
            );
        }
    }

    public void sendMessageToAdmin(long messageId, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
        long adminChatId = getAdminChatId();
        sendMessage(messageId, adminChatId, bot);
    }

    public long getAdminChatId() {
        return userDao.getAdminChatId();
    }

    public void sendMessageToAdmin(long messageId, Bot bot, Contact contact) throws SQLException, TelegramApiException {
        long adminChatId = getAdminChatId();
        sendMessage(messageId, adminChatId, bot, contact);
    }

    public void sendMessageToAdmin(String text, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
        long adminChatId = getAdminChatId();
        sendMessage(text, adminChatId, bot);
    }

    public void sendContactToAdmin(Contact contact, Bot bot) throws TelegramApiException {
        long adminChatId = getAdminChatId();
        bot.sendContact(new SendContact()
                .setChatId(adminChatId)
                .setFirstName(contact.getFirstName())
                .setLastName(contact.getLastName())
                .setPhoneNumber(contact.getPhoneNumber())
        );
    }

    public void sendPhotoToAdmin(String photo, Bot bot) throws TelegramApiException {
        long adminChatId = getAdminChatId();
        bot.sendPhoto(new SendPhoto()
                .setChatId(adminChatId)
                .setPhoto(photo)
        );
    }

    public ReplyKeyboard getAddToSheetKeyboard(Integer id, Long chatId) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton addToGoogleSheetsButton = new InlineKeyboardButton();
        InlineKeyboardButton declineButton = new InlineKeyboardButton();

        String buttonText = buttonDao.getButtonText(52);
        addToGoogleSheetsButton.setText(buttonText);
        addToGoogleSheetsButton.setCallbackData(buttonText + ":" + id);
        row.add(addToGoogleSheetsButton);

        String declineButtonText = buttonDao.getButtonText(53);
        declineButton.setText(declineButtonText);
        declineButton.setCallbackData(declineButtonText + ":" + chatId);
        row.add(declineButton);

        rows.add(row);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

}
