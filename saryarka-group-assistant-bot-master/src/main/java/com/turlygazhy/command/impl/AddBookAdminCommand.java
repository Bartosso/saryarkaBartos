package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.MessageElement;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.objects.Document;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 30.06.2017.
 */
public class AddBookAdminCommand extends Command {
    private int            step = 0;
    private long           chatId;
    private MessageElement expectedMessageElement;
    private String         bookName;
    private String         book;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(expectedMessageElement!= null){
            switch (step){
                case 0:
                    bookName = update.getMessage().getText();
                    step = 1;
                    break;
                case 1:
                    book     = update.getMessage().getDocument().getFileId();
                    step = 2;
            }
        }


        if(step == 0){
            chatId                = update.getMessage().getChatId();
            sendMessage(messageId, chatId, bot);
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if(step == 1){
            sendMessage(129,chatId,bot);
            return false;
        }
        if(step == 2){

            factory.getListDao("BOOKS").addNewBook(bookName,book);
            sendMessage(130, chatId,bot);
        }
        chatId = 0;
        step   = 0;
        return true;
    }
}
