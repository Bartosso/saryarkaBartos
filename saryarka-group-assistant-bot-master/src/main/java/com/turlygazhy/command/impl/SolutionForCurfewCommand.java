package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.Main;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

@SuppressWarnings("FieldCanBeLocal")
public class SolutionForCurfewCommand extends Command {
    private long    chatId = getAdminChatId();
    private String  chose;
    private String  messageText;
    private boolean changeText;
    private String  senderChatId;
    private String  username;
    private int     messageId;
    private int     changeTextMessageId;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (changeText){
            if (update.hasMessage()){
                if (update.getMessage().hasText()){
                    messageText = update.getMessage().getText();
                    changeText  = false;
                    bot.deleteMessage(new DeleteMessage(chatId, changeTextMessageId));
                    changeTextMessageId = 0;
                    String textToAdmin = "Сообщение в ночное время от " + username
                            + "\nУникальный идентификатор ("+ senderChatId +") :\n" + messageText;
                    bot.sendMessage(new SendMessage(chatId, textToAdmin).setReplyMarkup(keyboardMarkUpDao.select(46)));
                    return false;
                }
            }
            bot.sendMessage(new SendMessage(chatId, "Вам нужно отправить текст"));
        }
        else
        if (update.hasCallbackQuery()){
            chose        = update.getCallbackQuery().getData();
            senderChatId = update.getCallbackQuery().getMessage().getText().substring(update.getCallbackQuery().getMessage().getText().indexOf("(")+1,
                    update.getCallbackQuery().getMessage().getText().indexOf(")"));
            messageText  = update.getCallbackQuery().getMessage().getText().substring(update.getCallbackQuery().getMessage()
            .getText().indexOf(":")+1);

            username     = update.getCallbackQuery().getMessage().getText().substring(update.getCallbackQuery().getMessage().getText()
            .indexOf("от")+2, update.getCallbackQuery().getMessage().getText().indexOf("У"));

            messageId    = update.getCallbackQuery().getMessage().getMessageId();
            if (chose.equals(buttonDao.getButtonText(239))){
                bot.deleteMessage(new DeleteMessage(chatId, messageId));
                Main.getReminder().addNewMessageToMorning(new SendMessage(bot.getGROUP_FOR_VOTE(),messageText + "\nОтправил:" + username));
                bot.sendMessage(new SendMessage(chatId, messageDao.getMessage(174).getSendMessage().getText()));
                return true;

            }
            if (chose.equals(buttonDao.getButtonText(240))){
                bot.deleteMessage(new DeleteMessage(chatId, messageId));
                bot.sendMessage(new SendMessage(senderChatId, messageDao.getMessage(173).getSendMessage().getText()));
                bot.sendMessage(new SendMessage(chatId,messageDao.getMessage(174).getSendMessage().getText()));
                return true;
            }
            if (chose.equals(buttonDao.getButtonText(241))){
                bot.deleteMessage(new DeleteMessage(chatId, messageId));
                String adminMessageText = messageDao.getMessage(29).getSendMessage().getText() + "\n Старый текст: "
                        + messageText;
                changeText = true;
                changeTextMessageId = bot.sendMessage(new SendMessage(chatId, adminMessageText)).getMessageId();
                return false;
            }
        }
        return true;
    }
}
