package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Member;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 21.06.2017.
 */
public class GetPartnerCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        String chatId           = String.valueOf(update.getCallbackQuery().getFrom().getId());
        String partnerId        = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1);
        Member member           = memberDao.getMemberById(Long.parseLong(partnerId));
        String text             = messageDao.getMessage(37).getSendMessage().getText()
                .replaceAll("fio", member.getFIO())
                .replaceAll("companyName", member.getCompanyName())
                .replaceAll("contact ", member.getContact())
                .replaceAll("nisha", member.getNisha())
                 + "\nНомер телефона: "+ member.getPhoneNumber();
        if (member.getUserName()!=null){
            text = text.concat("\nTelegram:@"+member.getUserName());
        }
        SendMessage sendMessage = new SendMessage().setText(text).setParseMode(ParseMode.HTML).setChatId(chatId);
        bot.sendMessage(sendMessage);

        return true;
    }
}
