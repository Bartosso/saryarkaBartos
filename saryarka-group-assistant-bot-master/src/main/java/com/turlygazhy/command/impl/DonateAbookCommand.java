package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Member;
import com.turlygazhy.entity.MessageElement;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 30.06.2017.
 */
public class DonateAbookCommand extends Command {
    private long chatId;
    private int  step = 0;
    private MessageElement expectedMessageElement;
    private String bookName;
    private String book;
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
            long adminChatId = getAdminChatId();
            Member member = memberDao.getMember(String.valueOf(chatId));
            String textPattern = messageDao.getMessage(135).getSendMessage().getText()
                    .replaceAll("donator_name",member.getFIO())
                    .replaceAll("donator_userName", member.getUserName())
                    .replaceAll("book_name", bookName);
            SendMessage sendMessageToAdmin   = new SendMessage().setChatId(adminChatId)
                    .setText(textPattern);
            SendDocument sendDocumentToAdmin = new SendDocument().setDocument(book).setChatId(adminChatId);
            SendMessage sendMessage          = messageDao.getMessage(136).getSendMessage().setChatId(chatId);
            bot.sendMessage(sendMessageToAdmin);
            bot.sendDocument(sendDocumentToAdmin);
            bot.sendMessage(sendMessage);
        }
        chatId                   = 0;
        step                     = 0;
        expectedMessageElement   = null;
        bookName                 = null;
        book                     = null;
        return true;
    }
}
