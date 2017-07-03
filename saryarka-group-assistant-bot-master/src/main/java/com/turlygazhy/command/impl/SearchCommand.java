package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Member;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by user on 1/25/17.
 */
public class SearchCommand extends Command {
    private String searchString;
    private boolean waitInput;
    private int i = 0;
    private List<Member> members;
    private String requestInGroup;
    private boolean askDidWeHelp;
    private Long groupId;
    private Message requestMessage;


    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Message updateMessage = update.getMessage();
        if (updateMessage == null) {
            updateMessage = update.getCallbackQuery().getMessage();
            String data = update.getCallbackQuery().getData();
            if (data.equals(buttonDao.getButtonText(30))) {
//                i = i + 2;
//                members = memberDao.search(searchString);
                i++;
                showMembers(bot, update.getCallbackQuery().getMessage().getChatId());
//                return false;
            }
        }

        if (updateMessage.getChatId() < 0) {
            if (groupId == null) {
                groupId = updateMessage.getChatId();
            }
            return executeRequestInGroup(update, bot);
        }

        String text = updateMessage.getText();
        Long chatId = updateMessage.getChatId();


        if (waitInput) {
            searchString = text;
            members = memberDao.search(searchString);
            if (members.isEmpty()) {
                sendMessage(38, chatId, bot);
//                return true;
                return false;
            }
            showMembers(bot, chatId);
            return false;
        }


        if (searchString == null) {
            sendMessage(36, chatId, bot);
            waitInput = true;
            return false;
        }

        return false;
    }

    private void showMembers(Bot bot, Long chatId) throws SQLException, TelegramApiException {
//        members = memberDao.search(searchString);
        int membersSize = members.size();
        if(i<membersSize){
        String template = messageDao.getMessage(37).getSendMessage().getText();
        Member first = members.get(i);
        String messageInfo = template.replaceAll("fio", first.getFIO()).replaceAll("companyName", first.getCompanyName())
                .replaceAll("contact", first.getContact()).replaceAll("nisha", first.getNisha());

        SendMessage sendMessage = new SendMessage().setChatId(chatId).setText(messageInfo);
        InlineKeyboardMarkup keyboard = (InlineKeyboardMarkup) keyboardMarkUpDao.select(13);
        List<List<InlineKeyboardButton>> keyboard1 = keyboard.getKeyboard();
        sendMessage.setReplyMarkup(keyboard);
        bot.sendMessage(sendMessage);}




//        if (first.getUserName() != null) {
//            firstData = firstData + "\nTelegram: @" + first.getUserName();
//        }

//        int secondIndex = i + 1;
//        if (membersSize > secondIndex) {
//            Member second = members.get(secondIndex);
//            String secondData = template.replaceAll("fio", second.getFIO()).replaceAll("companyName", second.getCompanyName())
//                    .replaceAll("contact", second.getContact()).replaceAll("nisha", second.getNisha()).replaceAll("naviki", second.getNaviki());
////            if (second.getUserName() != null) {
////                secondData = secondData + "\n Telegram: @" + second.getUserName();
////            }
//
//            SendMessage sendMessage = new SendMessage()
//                    .setChatId(chatId)
//                    .setText(firstData + "\n\n" + secondData);
//            if (membersSize > secondIndex + 1) {
//                sendMessage = sendMessage.setReplyMarkup(keyboardMarkUpDao.select(13));
//            }
//            bot.sendMessage(sendMessage);
//        } else {
//            bot.sendMessage(new SendMessage()
//                    .setChatId(chatId)
//                    .setText(firstData)
//            );
        }


    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public boolean executeRequestInGroup(Update update, Bot bot) throws SQLException, TelegramApiException {
        members = memberDao.search(searchString);
        if (members.size() == 0) {
//            return true;
              return false;
        }

        Message updateMessage = update.getMessage();
        if (updateMessage == null) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            updateMessage = callbackQuery.getMessage();
        }

        long chatId = memberDao.selectByUserId(updateMessage.getFrom().getId()).getChatId();
        sendMessage(63, chatId, bot);
        showMembers(bot, chatId);
//        askDidWeHelp(update, bot);
        return false;
    }

    private void askDidWeHelp(Update update, Bot bot) throws SQLException, TelegramApiException {
        askDidWeHelp = true;
        InlineKeyboardMarkup keyboard = (InlineKeyboardMarkup) keyboardMarkUpDao.select(17);
        List<List<InlineKeyboardButton>> keyboard1 = keyboard.getKeyboard();
        List<InlineKeyboardButton> keyboardButtons = keyboard1.get(0);
        keyboardButtons.get(0).setCallbackData(constDao.select(4) + ":" + update.getMessage().getMessageId() + "/" + groupId);
        keyboardButtons.get(1).setCallbackData(constDao.select(5));
        bot.sendMessage(new SendMessage()
                .setChatId(memberDao.selectByUserId(update.getMessage().getFrom().getId()).getChatId())
                .setText(messageDao.getMessage(64).getSendMessage().getText())
                .setReplyMarkup(keyboard)
        );
    }
}
