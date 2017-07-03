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
    private ArrayList<Member> members;
    private int iterator;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();


        if (updateMessage == null) {
            updateMessage = update.getCallbackQuery().getMessage();
            String data = update.getCallbackQuery().getData();
            }

        String text = updateMessage.getText();
        long chatId = Long.valueOf(updateMessage.getFrom().getId());
//        String searchString = text.substring(text.indexOf(""));

//        if(text.substring(text.indexOf(""))==null){
//            bot.sendMessage(new SendMessage().setChatId(chatId).setText(messageDao.getMessage(36)
//                    .getSendMessage().getText()));
//            return true;
//        }
        members = giveMeList(text);
        if(members.isEmpty()){
            bot.sendMessage(new SendMessage().setChatId(chatId).setText(messageDao.getMessage(38)
                    .getSendMessage().getText()));
            return true;
        }

         String infoForButtonWrite = String.valueOf(members.get(0).getId());
         String infoForButtonNext = "";
         String tempInfoForButtonNext = "";



         for(int count = 0; count<7; count++){
             Member memberIn = members.get(count);
           tempInfoForButtonNext = tempInfoForButtonNext.concat(String.valueOf(memberIn.getId())+"|");
           if(count==members.size()-1){
               break;
             }
         }
         infoForButtonNext                 = tempInfoForButtonNext.substring(tempInfoForButtonNext.indexOf("|")+1);
         Member first                      = members.get(0);
        ReplyKeyboard inlineKeyboardMarkup = getKeyBoardForSearch(infoForButtonNext,infoForButtonWrite);
         String pattern = messageDao.getMessage(37).getSendMessage().getText()
                 .replaceAll("fio", first.getFIO()).replaceAll("companyName", first.getCompanyName())
                 .replaceAll("contact", first.getContact()).replaceAll("nisha", first.getNisha());

         SendMessage sendMessage = new SendMessage().setChatId(chatId).setText(pattern).setReplyMarkup(inlineKeyboardMarkup);
         SendMessage sendHello   = messageDao.getMessage(63).getSendMessage().setChatId(chatId);
         bot.sendMessage(sendHello);
         bot.sendMessage(sendMessage);





    return true;}

    private ArrayList<Member> giveMeList(String whatToFind) throws SQLException {
        ArrayList<Member> memberList;
        memberList = memberDao.search(whatToFind);
    return memberList;}

    private void listIsEnded(Bot bot,long chatId) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage().setText("К сожалению больше кандидатов нет").setChatId(chatId);
        bot.sendMessage(sendMessage);
    }


}
