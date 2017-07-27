package com.turlygazhy.tool.vanderkastTools.Constructors;

import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

/**
 * Created by Vanderkast on 05.07.2017.
 *
 * this class uses for construct messages
 */
public class MessageConstructor {
    //returns SendMessage without parse mod
    public SendMessage getSendMessage(String text, long chat_id){
        return getSendMessage(text, chat_id, "null");
    }

    //returns SendMessage with parse mod
    public SendMessage getSendMessage(String text, long chat_id, String parseMode){
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setChatId(chat_id);
        if(parseMode != null && !parseMode.equals("null")) {
            message.setParseMode(parseMode);
        }
        return message;
    }

    public SendMessage getSendMessage(String text, long chat_id, ReplyKeyboardMarkup replyKeyboardMarkup){
        return getSendMessage(text, chat_id, null, replyKeyboardMarkup);
    }

    public SendMessage getSendMessage(String text, long chat_id, String parseMode, ReplyKeyboardMarkup replyKeyboardMarkup){
        SendMessage message = getSendMessage(text, chat_id, parseMode);
        message.setReplyMarkup(replyKeyboardMarkup);
        return message;
    }

    public SendMessage getSendMessage(String text, long chat_id, InlineKeyboardMarkup inlineKeyboardMarkup){
        return getSendMessage(text, chat_id, null, inlineKeyboardMarkup);
    }

    public SendMessage getSendMessage(String text, long chat_id, String parseMode, InlineKeyboardMarkup inlineKeyboardMarkup){
        SendMessage message = getSendMessage(text, chat_id, parseMode);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }



    /*public SendPhoto getPhoto(String path, String caption, long chatId){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(caption);
        sendPhoto.setPhoto(path);
        return sendPhoto;
    }*/
}
