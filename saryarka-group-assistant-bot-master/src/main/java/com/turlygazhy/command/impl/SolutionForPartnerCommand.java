package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Member;
import com.turlygazhy.entity.MessageElement;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 21.06.2017.
 */
public class SolutionForPartnerCommand extends Command {
    private int    chose;
    private int    step;
    private MessageElement expectedMessageElement;
    private String wannaBePartner;
    private Member member;
    private String messageToWannaBe;
    private String solution;
    private String chatId;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(update.getCallbackQuery()!=null){
        solution = update.getCallbackQuery().getData().substring(0, update.getCallbackQuery().getData().indexOf(":"));
        chatId = String.valueOf(update.getCallbackQuery().getFrom().getId());
        }
        if(expectedMessageElement == null){
        switch (solution){
            case "add_to_partner":
            chose          = 1;
            step           = 1;
            wannaBePartner = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1);
            member         = memberDao.getMember(chatId);
            break;
            case "ignore_add_to_partner":
            chose          = 2;
        }}
        if(expectedMessageElement !=null){
            switch (step){
                    case 1:
                    messageToWannaBe = update.getMessage().getText();
                    step = 2;

            }
        }
        if(chose==1 &step == 1){
            SendMessage sendMessage = new SendMessage().setText("Введите ваше сообщение для ответа").setChatId(chatId);
            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.TEXT;
        }
        if(chose==2){
            SendMessage sendMessage = new SendMessage().setText("Вы решили проигнорировать запрос.").setChatId(chatId);
            bot.sendMessage(sendMessage);
            return true;
        }
        if(step==2){
            SendMessage sendMessage          = new SendMessage().setChatId(chatId).setText("Ваше сообщение отправленно");
            String text                      = "Вам ответили.\nОтветное сообщение: " + messageToWannaBe + "\nКонтакты отправителя\nНомер телефона: " + member.getPhoneNumber();
            if(member.getUserName()!=null){
                text = text.concat("\nTelegram:@" +member.getUserName());
            }
            SendMessage sendMessageToWannaBe = new SendMessage().setText(text).setChatId(wannaBePartner);
//            String partnerMemberId           = String.valueOf(member.getId());
//            String wannaBeMemberId           = String.valueOf(Integer.parseInt(memberDao.getMemberId(Long.parseLong(wannaBePartner))));
//            memberDao.addNewPartner(partnerMemberId,wannaBeMemberId);
//            memberDao.addNewPartner(wannaBeMemberId,partnerMemberId);

            bot.sendMessage(sendMessageToWannaBe);
            bot.sendMessage(sendMessage);
        }
        return false;
    }
}
