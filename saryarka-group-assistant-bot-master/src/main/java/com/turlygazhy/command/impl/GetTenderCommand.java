package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.ListData;
import com.turlygazhy.entity.Member;
import com.turlygazhy.entity.Message;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendChatAction;
import org.telegram.telegrambots.api.methods.send.SendContact;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 21.06.2017.
 */
public class GetTenderCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        String chatId      = String.valueOf(update.getCallbackQuery().getFrom().getId());
        String listDataId  = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1,
                update.getCallbackQuery().getData().indexOf("/"));
        String listType    = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf("/")+1);
        String targetList  = "";
        String requestType = "";
        switch (listType){
            case "ищет"       :
               targetList  = "REQUESTS_LIST";
               requestType = "ищет";
               break;
            case "предлагает" :
               targetList  = "OFFER_LIST";
               requestType = "предлагает";
               break;
        }
        ListDao listDao    = factory.getListDao(targetList);
        ListData listData  = listDao.getListDataById(listDataId);
        Message message    = messageDao.getMessage(81);
        Member member      = memberDao.getMemberById(listData.getMemberId());
        SendContact sendContact = new SendContact().setChatId(chatId).setPhoneNumber(member.getPhoneNumber()).setFirstName(member.getFirstName());
        bot.sendContact(sendContact);

//        String text        = message.getSendMessage().getText()
//                    .replaceAll("fio", member.getFIO()).replaceAll("companyName", member.getCompanyName())
//                    .replaceAll("contact", member.getContact()).replaceAll("nisha", member.getNisha())
//                    .replaceAll("naviki", member.getNaviki()).replaceAll("phoneNumber", member.getPhoneNumber())
//                    .replaceAll("text", listData.getText()).replaceAll("date_post", listData.getDate())
//                    .replaceAll("request_type", requestType);
//
//            SendPhoto sendPhoto = new SendPhoto().setPhoto(listData.getPhoto());
//            if(listData.getPhoto() != null){
//                sendPhoto.setPhoto(listData.getPhoto());
//                bot.sendPhoto(sendPhoto.setChatId(chatId));
//            }
//
//            try {
//
//                bot.sendMessage(new SendMessage()
//                        .setChatId(chatId)
//                        .setText(text).setParseMode(ParseMode.HTML)
//
//                );
//            } catch (TelegramApiException e) {
//                throw new RuntimeException(e);
//            }
//

        return true;
    }
}
