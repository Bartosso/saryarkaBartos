package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Member;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditMemberBidToGoogleSheetCommand extends Command {
    private boolean expectNewValue;
    private long    chatId;
    private String  chose;
    private String  requestBidID;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (expectNewValue){
            if(!update.getMessage().hasText()){
                bot.sendMessage(new SendMessage(chatId, "Вы не ввели новое значение"));
                return false;
            }
            switch (chose){
                case "editFioBid":
                    memberDao.updateFio(Integer.valueOf(requestBidID), update.getMessage().getText());
                    bot.sendMessage(new SendMessage(chatId, "Изменения сохранены"));
                    sendMessageForEditToAdmin(bot, chatId,requestBidID);
                    return true;
                case "editBidComp":
                    memberDao.updateCompany(Integer.valueOf(requestBidID), update.getMessage().getText());
                    bot.sendMessage(new SendMessage(chatId, "Изменения сохранены"));
                    sendMessageForEditToAdmin(bot, chatId,requestBidID);
                    return true;
                case "editBidSite":
                    memberDao.updateContact(Integer.valueOf(requestBidID), update.getMessage().getText());
                    bot.sendMessage(new SendMessage(chatId, "Изменения сохранены"));
                    sendMessageForEditToAdmin(bot, chatId,requestBidID);
                    return true;
                case "editBidSfer":
                    memberDao.updateNishaByUserId(Integer.valueOf(requestBidID), update.getMessage().getText());
                    bot.sendMessage(new SendMessage(chatId, "Изменения сохранены"));
                    sendMessageForEditToAdmin(bot, chatId,requestBidID);
                    return true;
                case "editBidPhNu":
                    memberDao.updatePhoneNumber(Integer.valueOf(requestBidID), update.getMessage().getText());
                    bot.sendMessage(new SendMessage(chatId, "Изменения сохранены"));
                    sendMessageForEditToAdmin(bot, chatId,requestBidID);
                    return true;
                case "editBidCity":
                    memberDao.updateCity(Integer.valueOf(requestBidID), update.getMessage().getText());
                    bot.sendMessage(new SendMessage(chatId, "Изменения сохранены"));
                    sendMessageForEditToAdmin(bot, chatId,requestBidID);
                    return true;
            }

        } else {
            chatId       = update.getCallbackQuery().getFrom().getId();
            chose        = update.getCallbackQuery().getData().substring(0, update.getCallbackQuery().getData().indexOf(":"));
            requestBidID = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":") + 1);
            messageId    = update.getCallbackQuery().getMessage().getMessageId();
            bot.deleteMessage(new DeleteMessage(chatId, Math.toIntExact(messageId)));
            if (chose.equals("editBid")){
                sendMessageForEditToAdmin(bot,chatId,requestBidID);
//                bot.sendMessage(new SendMessage(chatId, getTextToEdit(requestBidID))
//                .setReplyMarkup(getKeyboardForEditBid(requestBidID)).setParseMode(ParseMode.HTML));
                return true;
            } else {
                bot.sendMessage(new SendMessage(chatId, "Введите новое значение"));
                expectNewValue = true;
                return false;
            }
        }
        expectNewValue = false;
        chatId         = 0;
        chose          = null;
        requestBidID   = null;
        return true;
    }

    private String getTextToEdit(String userId) throws SQLException {
        com.turlygazhy.entity.Message message = messageDao.getMessage(8);
        Member member = memberDao.selectByUserId(Math.toIntExact(Long.parseLong(userId)));
        return message.getSendMessage().getText()
                .replaceAll("fio"        , member.getFIO())
                .replaceAll("companyName", member.getCompanyName())
                .replaceAll("contact"    , member.getContact())
                .replaceAll("nisha"      , member.getNisha())
                .replaceAll("phoneNumber", member.getPhoneNumber())
                .replaceAll("memberCity" , member.getCity());
    }

    private InlineKeyboardMarkup getKeyboardForEditBid(String chatIdOfBid) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton editBidFio     = new InlineKeyboardButton(buttonDao.getButtonText(37));
        editBidFio.setCallbackData("editFioBid"+":" + chatIdOfBid);
        row.add(editBidFio);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton editBidCompany = new InlineKeyboardButton(buttonDao.getButtonText(38));
        editBidCompany.setCallbackData("editBidComp"+ ":" + chatIdOfBid);
        row.add(editBidCompany);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton editBidSite    = new InlineKeyboardButton(buttonDao.getButtonText(39));
        editBidSite.setCallbackData("editBidSite" +":" + chatIdOfBid);
        row.add(editBidSite);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton editBidSfera   = new InlineKeyboardButton(buttonDao.getButtonText(40));
        editBidSfera.setCallbackData("editBidSfer" + ":" + chatIdOfBid);
        row.add(editBidSfera);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton editBidPhoneNumber = new InlineKeyboardButton(buttonDao.getButtonText(42));
        editBidPhoneNumber.setCallbackData("editBidPhNu"+":" + chatIdOfBid);
        row.add(editBidPhoneNumber);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton editBidCity        = new InlineKeyboardButton(buttonDao.getButtonText(203));
        editBidCity.setCallbackData("editBidCity" + ":" + chatIdOfBid);
        row.add(editBidCity);
        rows.add(row);
        row = new ArrayList<>();

        String acceptButtonText = buttonDao.getButtonText(52);
        InlineKeyboardButton addToGoogleSheetsButton = new InlineKeyboardButton();
        addToGoogleSheetsButton.setText(acceptButtonText);
        addToGoogleSheetsButton.setCallbackData(acceptButtonText + "/" + chatIdOfBid);
        row.add(addToGoogleSheetsButton);

        String declineButtonText = buttonDao.getButtonText(53);
        InlineKeyboardButton declineButton = new InlineKeyboardButton();
        declineButton.setText(declineButtonText);
        declineButton.setCallbackData(declineButtonText + "/" + chatIdOfBid);
        row.add(declineButton);

        rows.add(row);
        return  keyboard.setKeyboard(rows);
    }

    private void sendMessageForEditToAdmin(Bot bot, long chatId, String requestBidID) throws SQLException, TelegramApiException {
        bot.sendMessage(new SendMessage(chatId, getTextToEdit(requestBidID)).setReplyMarkup(getKeyboardForEditBid(requestBidID)).setParseMode(
                ParseMode.HTML
        ));
    }
}
