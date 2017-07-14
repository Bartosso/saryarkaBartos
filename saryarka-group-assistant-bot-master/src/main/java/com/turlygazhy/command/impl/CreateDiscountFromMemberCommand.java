package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Discount;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.MessageElement;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eshu on 22.06.2017.
 */
public class CreateDiscountFromMemberCommand extends Command {
    private String             discountType;
    private String             name;
    private String             textAbout;
    private String             photo;
    private String             address;
    private String             page;
    private String             discount;
    private String             memberId;
    private MessageElement     expectedMessageElement;
    private boolean            needPhoto                 = true;
    private ListDao            listDao                   = factory.getListDao("DISCOUNTS");
    private int                step                      = 1;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
        if (updateMessage == null) {
            updateMessage = update.getCallbackQuery().getMessage();
        }
        Long chatId = updateMessage.getChatId();
        if (expectedMessageElement != null) {
            switch (step){
                case 1:
                    if(update.getCallbackQuery().getData().equals(buttonDao.getButtonText(101))){
                        discountType = "Restaurants";
                        step = 2;
                        break;
                    }
                    if(update.getCallbackQuery().getData().equals(buttonDao.getButtonText(102))){
                        discountType = "Hotels";
                        step = 2;
                        break;
                    }
                    if(update.getCallbackQuery().getData().equals(buttonDao.getButtonText(103))){
                        discountType = "BusinessSchools";
                        step = 2;
                        break;
                    }

                case 2:
                    name         = updateMessage.getText();
                    step         = 3;
                    break;
                case 3:
                    textAbout    = updateMessage.getText();
                    step         = 4;
                    break;
                case 4:
                    address      = updateMessage.getText();
                    step         = 5;
                    break;
                case 5:
                    page         = updateMessage.getText();
                    step = 6;
                    break;
                case 6:
                    try {
                        photo    = updateMessage.getPhoto().get(updateMessage.getPhoto().size() - 1).getFileId();
                    } catch (Exception e) {
                        if (update.getCallbackQuery().getData().equals(buttonDao.getButtonText(51))) {
                            needPhoto = false;
                        }
                    }
                    step         = 7;
                    break;
                case 7:
                    discount = updateMessage.getText();
                    step = 8;
                    break;
            }
        }

        if (step == 1 && discountType == null) {
            ReplyKeyboard keyboard = keyboardMarkUpDao.select(28);
            SendMessage sendMessage = new SendMessage().setChatId(chatId).setText("В какую категорию добавить ваше предложение?")
                    .setReplyMarkup(keyboard);

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if (step == 2 && name == null) {
            Message message = messageDao.getMessage(101);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if (step == 3 && textAbout == null) {
            Message message = messageDao.getMessage(102);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if (step == 4 && address == null) {
            Message message = messageDao.getMessage(103);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId)
                    .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()))
                    .setParseMode(ParseMode.HTML);

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if (step == 5 & page == null) {
            Message message = messageDao.getMessage(104);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId)
                    .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()))
                    .setParseMode(ParseMode.HTML);
            bot.sendMessage(sendMessage);
            return false;
        }
        if ( step == 6 && photo == null) {
            Message message = messageDao.getMessage(28);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId)
                    .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.PHOTO;
            return false;
        }
        if (step == 7 & discount == null){
            Message message = messageDao.getMessage(105);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);
            bot.sendMessage(sendMessage);
            return false;
        }
        if (step == 8){
            Discount discountReady = new Discount(discountType, name, textAbout, photo, address, page, discount,
                    memberDao.getMemberId(chatId));
//            listDao.createDiscount(discountReady, false);
//            discountReady.setId(listDao.getDiscountId(discountReady,false));
//            ReplyKeyboard keyboard =  getAdminKeys(discountReady);
            SendMessage sendMessageToAdmin = new SendMessage().setText(messageDao.getMessage(106).getSendMessage()
                    .getText().replaceAll("discount_type", discountType)
                    .replaceAll("company_name", name)
                    .replaceAll("text_about"  , textAbout)
                    .replaceAll("address"     , address)
                    .replaceAll("page"        , page)
                    .replaceAll("discount"    , discount)
                    .replaceAll("memberCity" , memberDao.getMemberCityByChatId(chatId)))
                    .setChatId(getAdminChatId()).setParseMode(ParseMode.HTML);
//                    .setReplyMarkup(keyboard);
            SendMessage sendMessageToMember = messageDao.getMessage(147).getSendMessage().setChatId(chatId);
            if (photo != null) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setPhoto(photo);
                bot.sendPhoto(sendPhoto.setChatId(getAdminChatId()));
            }
            discountType           = null;
            name                   = null;
            textAbout              = null;
            photo                  = null;
            address                = null;
            page                   = null;
            discount               = null;
            memberId               = null;
            expectedMessageElement = null;
            bot.sendMessage(sendMessageToAdmin);
            bot.sendMessage(sendMessageToMember);
        return true;
        }

        return true;
    }

    private ReplyKeyboard getAdminKeys(Discount discount) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton decline = new InlineKeyboardButton();
        decline.setText("Отклонить");
        decline.setCallbackData("solution_for_discount_from_admin" + ":" +  discount.getId() + "/" + "decline");
        row.add(decline);

        InlineKeyboardButton accept = new InlineKeyboardButton();
        accept.setText("Утвердить");
        accept.setCallbackData("solution_for_discount_from_admin" + ":" +  discount.getId() + "/" + "accept");
        row.add(accept);

        InlineKeyboardButton edit = new InlineKeyboardButton();
        edit.setText("Редактировать и опубликовать");
        edit.setCallbackData("solution_for_discount_from_admin" + ":" +  discount.getId() + "/" + "edit");
        row.add(edit);
        rows.add(row);

       return keyboard.setKeyboard(rows);
    }

}
