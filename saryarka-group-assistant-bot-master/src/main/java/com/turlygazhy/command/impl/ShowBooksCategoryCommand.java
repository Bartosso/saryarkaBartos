//package com.turlygazhy.command.impl;
//
//import Constructors.InlineKeyboardConstructor;
//import com.turlygazhy.Bot;
//import com.turlygazhy.command.Command;
//import com.turlygazhy.dao.impl.ListDao;
//import com.turlygazhy.entity.Book;
//import org.telegram.telegrambots.api.methods.ParseMode;
//import org.telegram.telegrambots.api.methods.send.SendMessage;
//import org.telegram.telegrambots.api.objects.Update;
//import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
//import org.telegram.telegrambots.exceptions.TelegramApiException;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//
//public class ShowBooksCategoryCommand extends Command {
//    private long chatId;
//    @Override
//    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
//        chatId = update.getMessage().getChatId();
//        bot.sendMessage(messageDao.getMessage(165).getSendMessage().setChatId(chatId)
//        .setParseMode(ParseMode.HTML).setReplyMarkup(getCategories()));
//        return true;
//    }
//    private InlineKeyboardMarkup getCategories() throws SQLException {
//        ListDao listDao = factory.getListDao("BOOKS");
//        ArrayList<String> booksCategories = listDao.getBooksCategories();
//
//        for(String string : booksCategories){
//
//        }
//    }
//}
