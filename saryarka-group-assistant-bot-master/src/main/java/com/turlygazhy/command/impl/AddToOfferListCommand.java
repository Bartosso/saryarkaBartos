package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.MessageElement;
import com.turlygazhy.tool.DateUtil;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Eshu on 15.06.2017.
 */
public class AddToOfferListCommand extends Command {
    private String text;
    private MessageElement expectedMessageElement;
//    private boolean needPhoto = true;
    private ListDao listDao = factory.getListDao("OFFER_LIST");



    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {

        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
        if (updateMessage == null) {
            updateMessage = update.getCallbackQuery().getMessage();
        }
        Long chatId = updateMessage.getChatId();
        if (expectedMessageElement != null) {
            switch (expectedMessageElement) {
                case TEXT:
                    text = updateMessage.getText();
                    break;
            }
        }
        if (text == null) {
            Message message = messageDao.getMessage(79);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId)
                    .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if(update.getMessage().getFrom().getUserName()==null){
            bot.sendMessage(new SendMessage(chatId, "Ошибка, у вас не указан username"));
            return true;
        }
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        Date dateIn = new Date();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(dateIn.toInstant(), ZoneId.systemDefault());
        localDateTime               = localDateTime.plusDays(7);

        int tenderId    = listDao.insertIntoLists(text,memberDao.getMemberId(chatId),date, false);
        Message message = messageDao.getMessage(83);
        SendMessage sendMessage = new SendMessage().setChatId(chatId)
                .setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()))
                .setText(message.getSendMessage().getText().replaceAll("day_and_month",
                        localDateTime.getDayOfMonth() +" " + DateUtil.getMonthInRussian(localDateTime.getMonthValue())));
        bot.sendMessage(getTextToAdmin(tenderId,update,text).setChatId(chatId));
        bot.sendMessage(sendMessage);


        text                   = null;
        expectedMessageElement = null;
//        date                   = null;
        return true;
    }
    private SendMessage getTextToAdmin(int tenderId, Update update, String tender_text) throws SQLException {
        String text = messageDao.getMessage(145).getSendMessage().getText()
                .replaceAll("tender_type_name"  , "Список предложений")
                .replaceAll("tender_type_word"  , "предлагает")
                .replaceAll("tender_text"       ,  tender_text)
                .replaceAll("tender_member"     , "@" + update.getMessage().getFrom().getUserName());
        return new SendMessage().setText(text)
                .setParseMode(ParseMode.HTML).setReplyMarkup(keyBoardForAdmin(tenderId));
    }

    private ReplyKeyboard keyBoardForAdmin(int tenderId) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton makeBe = new InlineKeyboardButton();
        makeBe.setText(buttonDao.getButtonText(141));
        makeBe.setCallbackData("acceptOfferTender" + ":" + tenderId);
        row.add(makeBe);

        InlineKeyboardButton deleteTender = new InlineKeyboardButton();
        deleteTender.setText(buttonDao.getButtonText(142));
        deleteTender.setCallbackData("rejectOfferTender" + ":" + tenderId);
        row.add(deleteTender);

        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton editTender = new InlineKeyboardButton();
        editTender.setText(buttonDao.getButtonText(147));
        editTender.setCallbackData("editOfferTender" + ":" + tenderId);
        row.add(editTender);

        rows.add(row);
        keyboard.setKeyboard(rows);

        return keyboard;
    }
}
