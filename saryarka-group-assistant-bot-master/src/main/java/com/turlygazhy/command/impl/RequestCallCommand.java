package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Member;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by user on 1/5/17.
 */
public class RequestCallCommand extends Command {
    private boolean expectNewElement;
    private String  timeToCall;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(expectNewElement){
            if(!update.hasMessage()){
                bot.sendMessage(new SendMessage(update.getMessage().getChatId(), "Ошибка!\nВам нужно отправить сообщение"));
                return false;
            }
            Message updateMessage = update.getMessage();
            Integer userId = updateMessage.getFrom().getId();
            Member member = memberDao.selectByUserId(userId);
            timeToCall = update.getMessage().getText();

            if(member==null){
                bot.sendMessage(new SendMessage().setChatId(String.valueOf(userId)).setText("Ошибка, вы не зарегистрированы, нажмите /start чтобы пройти регистрацию"));
                return true;
            }
            String text = messageDao.getMessage(56).getSendMessage().getText();
            text = text.replaceAll("fio", member.getFIO()).replaceAll("companyName", member.getCompanyName())
                    .replaceAll("contact", member.getContact()).replaceAll("nisha", member.getNisha())
                    .replaceAll("phoneNumber", member.getPhoneNumber()+"\nTelegram:@"+member.getUserName()
                    +"\nУдобное время для звонка: "+ timeToCall);
//            bot.sendMessage(new SendMessage(bot.getSenimManagerChatId(),text));
            sendMessage(57, updateMessage.getChatId(), bot);
            return true;

        }
        else {
            Long chatId = Long.valueOf(update.getCallbackQuery().getFrom().getId());
            bot.sendMessage(new SendMessage(chatId, messageDao.getMessage(168).getSendMessage().getText()));
            expectNewElement = true;
            return false;
        }

    }
}
