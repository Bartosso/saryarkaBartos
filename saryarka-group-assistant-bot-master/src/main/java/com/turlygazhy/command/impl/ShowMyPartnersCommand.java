//package com.turlygazhy.command.impl;
//
//import com.turlygazhy.Bot;
//import com.turlygazhy.command.Command;
//import com.turlygazhy.entity.Event;
//import com.turlygazhy.entity.Member;
//import org.telegram.telegrambots.api.methods.send.SendMessage;
//import org.telegram.telegrambots.api.objects.Update;
//import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
//import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
//import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
//import org.telegram.telegrambots.exceptions.TelegramApiException;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Eshu on 21.06.2017.
// */
//public class ShowMyPartnersCommand extends Command{
//    @Override
//    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
//        String chatId     = String.valueOf(update.getMessage().getChatId());
//        String memberId   = String.valueOf(memberDao.getMember(String.valueOf(update.getMessage().getFrom().getId())).getId());
//        String[] partners = memberDao.getPartnersId(memberId);
//        if(partners == null){
//            SendMessage sendMessage = new SendMessage().setText("К сожалению у вас нет партнеров")
//                    .setChatId(chatId);
//            bot.sendMessage(sendMessage);
//            return true;
//        }
//        ArrayList<Member> memberArrayList = new ArrayList<>();
//        for(String string : partners){
//            Member member = new Member();
//            member        = memberDao.getMemberById(Long.parseLong(string));
//            memberArrayList.add(member);
//        }
////        if(memberArrayList.isEmpty()){
////            SendMessage sendMessage = new SendMessage().setText("К сожалению у вас нет партнеров")
////                    .setChatId(chatId);
////            bot.sendMessage(sendMessage);
////        }
//
//            ReplyKeyboard keyboard = partnersViaButtons(memberArrayList);
//            SendMessage sendMessage = new SendMessage().setText("Ваши партнеры")
//                    .setReplyMarkup(keyboard)
//                    .setChatId(chatId);
//            bot.sendMessage(sendMessage);
//
//        return true;
//    }
//
//    private ReplyKeyboard partnersViaButtons(ArrayList<Member> memberArrayList){
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
//        List<InlineKeyboardButton> row;
//
//        for (Member member : memberArrayList) {
//            row = new ArrayList<>();
//            InlineKeyboardButton button = new InlineKeyboardButton();
//            String buttonText = member.getFIO();
//            button.setText(buttonText);
//            button.setCallbackData("get_partner" + ":" + member.getId());
//            row.add(button);
//            rows.add(row);
//        }
//        inlineKeyboardMarkup.setKeyboard(rows);
//
//        return inlineKeyboardMarkup;
//    }
//}
