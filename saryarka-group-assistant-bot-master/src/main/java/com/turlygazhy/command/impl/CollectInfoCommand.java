package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
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
    private String city;
    private String contact;
    private WaitingType waitingType;
    private String companyName;
    private String fio;
    private Contact phoneNumber;

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
        if(update.hasMessage()){
            if(update.getMessage().getFrom().getUserName()==null){
                bot.execute(new SendMessage(chatId,messageDao.getMessage(170).getSendMessage().getText()));
                return true;
            }
        }

        if (waitingType != null) {
            switch (waitingType) {
                case FIO:
                    fio = text;
//                    sendMessage(47, chatId, bot);
                    bot.execute(new SendMessage(chatId,messageDao.getMessage(47)
                            .getSendMessage().getText()).setParseMode(ParseMode.HTML));
                    waitingType = WaitingType.COMPANY_NAME;
                    return false;
                case COMPANY_NAME:
                    companyName = text;
                    bot.execute(new SendMessage(chatId,messageDao.getMessage(48)
                            .getSendMessage().getText()).setParseMode(ParseMode.HTML));
//                    sendMessage(48, chatId, bot);
                    waitingType = WaitingType.NISHA;
                    return false;
                case NISHA:
                    nisha = text;
                    bot.execute(new SendMessage(chatId,messageDao.getMessage(49)
                            .getSendMessage().getText()).setParseMode(ParseMode.HTML));
//                    sendMessage(49, chatId, bot);
                    waitingType = WaitingType.CONTACT;
                    return false;
                case CONTACT:
                    contact = text;
                    sendMessage(158, chatId, bot);
//                    bot.sendMessage(new SendMessage(chatId,messageDao.getMessage(51)
//                    .getSendMessage().getText()).setParseMode(ParseMode.HTML)
//                    .setReplyMarkup(keyboardMarkUpDao.select(messageDao.getMessage(51).getKeyboardMarkUpId())));
//                    sendMessage(51, chatId, bot);
                    waitingType = WaitingType.CITY;
                    return false;
                case CITY:
                    city = text;
//                    sendMessage(51, chatId, bot);
                    bot.execute(new SendMessage(chatId,messageDao.getMessage(51)
                            .getSendMessage().getText()).setParseMode(ParseMode.HTML)
                            .setReplyMarkup(keyboardMarkUpDao.select(messageDao.
                                    getMessage(51).getKeyboardMarkUpId())));
                    waitingType = WaitingType.PHONE_NUMBER;
                    return false;
                case PHONE_NUMBER:

                    phoneNumber = updateMessage.getContact();
                    if(phoneNumber==null){
                        bot.execute(new SendMessage(chatId,"Вы не нажали на кнопку ''Отправить контакт''"));
                        return false;
                    }


                    bot.execute(new SendMessage(chatId, messageDao.getMessage(167).getSendMessage().getText()+"\n\n"+
                    getTextPattern()).setReplyMarkup(getKeyboardForAcceptOrStart()));
                    waitingType = WaitingType.ACCEPT;
                    return false;
                case ACCEPT:
                    if(update.hasCallbackQuery()){
                        if (update.getCallbackQuery().getData().equals("AcceptSendToAdmin")){
                            User user = update.getCallbackQuery().getFrom();
                            memberDao.insert(nisha,  chatId, user.getUserName(), user.getId(), companyName, this.contact, fio, phoneNumber, city);
                            SendMessage sendMessage = new SendMessage().setText("Заявка на добавление в группу\n"+getTextPattern())
                                    .setChatId(userDao.getAdminChatId())
                                    .setReplyMarkup(getAddToSheetKeyboard(user.getId(), chatId, user.getUserName()));
                            bot.execute(sendMessage);

                            sendMessage(43, chatId, bot);
                            return true;
                        }
                    }
                    else {
                        waitingType = WaitingType.FIO;
                    }
//                    memberDao.insert(nisha,  chatId, user.getUserName(), user.getId(), companyName, this.contact, fio, phoneNumber, city);
//                    String textToAdmin = messageDao.getMessage(42).getSendMessage().getText();
//                    textToAdmin = textToAdmin.replaceAll("fio", fio).replaceAll("companyName", companyName)
//                            .replaceAll("contact", this.contact).replaceAll("nisha", nisha)
//                    .replaceAll("phoneNumber", contact.getPhoneNumber())
//                    .replaceAll("memberCity", city);
//                    User user = updateMessage.getFrom();
//                    memberDao.insert(nisha,  chatId, user.getUserName(), user.getId(), companyName, this.contact, fio, phoneNumber, city);
//                    SendMessage sendMessage = new SendMessage().setText(getTextPattern())
//                            .setChatId(userDao.getAdminChatId())
//                            .setReplyMarkup(getAddToSheetKeyboard(user.getId(), chatId));
//                   bot.sendMessage(sendMessage);
//
//                    sendMessage(43, chatId, bot);
//                    return true;
            }
        }
       if (text!=null){
        if (text.equals(buttonDao.getButtonText(1))) {
            sendMessage(46, chatId, bot);
            waitingType = WaitingType.FIO;
            return false;
        }
        }

        return false;
    }

    private String getTextPattern() throws SQLException {
        String textToAdmin = messageDao.getMessage(42).getSendMessage().getText();
        textToAdmin = textToAdmin.replaceAll("fio", fio).replaceAll("companyName", companyName)
                .replaceAll("contact", contact).replaceAll("nisha", nisha)
                .replaceAll("phoneNumber", phoneNumber.getPhoneNumber())
                .replaceAll("memberCity", city);
        return textToAdmin;
    }
    private InlineKeyboardMarkup getKeyboardForAcceptOrStart() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        ArrayList<List<InlineKeyboardButton>> arrayListArrayList = new ArrayList<>();
        ArrayList<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        InlineKeyboardButton accept = new InlineKeyboardButton();
        accept.setText("Отправить");
        accept.setCallbackData("AcceptSendToAdmin");
        inlineKeyboardButtons.add(accept);
        arrayListArrayList.add(inlineKeyboardButtons);
        return keyboard.setKeyboard(arrayListArrayList);
    }
}
