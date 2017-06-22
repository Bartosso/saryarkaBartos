package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Member;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.MessageElement;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eshu on 16.06.2017.
 */
public class SearchMembersCommand extends Command {

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();

        if (updateMessage == null) {
            updateMessage = update.getCallbackQuery().getMessage();
            String data = update.getCallbackQuery().getData();
            }

        String text = updateMessage.getText();
        long chatId = Long.valueOf(updateMessage.getFrom().getId());
        ArrayList<Member> memberList = giveMeList(text);
         String infoForButtonWrite = String.valueOf(memberList.get(0).getId());
         String infoForButtonNext = "";
         String tempInfoForButtonNext = "";

         for(Member member : memberList){
           tempInfoForButtonNext = tempInfoForButtonNext.concat(String.valueOf(member.getId())+"|");
         }
         infoForButtonNext                 = tempInfoForButtonNext.substring(tempInfoForButtonNext.indexOf("|")+1);
         Member first                      = memberList.get(0);
        ReplyKeyboard inlineKeyboardMarkup = getKeyBoardForSearch(infoForButtonNext,infoForButtonWrite);
         String pattern = messageDao.getMessage(37).getSendMessage().getText()
                 .replaceAll("fio", first.getFIO()).replaceAll("companyName", first.getCompanyName())
                 .replaceAll("contact", first.getContact()).replaceAll("nisha", first.getNisha()).replaceAll("naviki", first.getNaviki());

         SendMessage sendMessage = new SendMessage().setChatId(chatId).setText(pattern).setReplyMarkup(inlineKeyboardMarkup);
         SendMessage sendHello   = messageDao.getMessage(63).getSendMessage().setChatId(chatId);
         bot.sendMessage(sendHello);
         bot.sendMessage(sendMessage);

        return true;
    }

    private ArrayList<Member> giveMeList(String whatToFind) throws SQLException {
        ArrayList<Member> memberList;
        memberList = memberDao.search(whatToFind);
    return memberList;}

}
