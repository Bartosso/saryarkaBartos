package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Member;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 20.06.2017.
 */
public class SearchMemberIterationCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(update.getCallbackQuery()== null){return false;}
        long chatId                        = update.getCallbackQuery().getFrom().getId();
        String buttonData                  = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1);
        if(buttonData.length()==0){
            listIsEnded(bot, chatId);
            return true;
        }
        else {
            String dataForWriteButton = buttonData.substring(0, buttonData.indexOf("|"));
            String dataForNextButton = buttonData.substring(buttonData.indexOf("|") + 1);
            Member member = memberDao.getMemberById(Long.parseLong(dataForWriteButton));
            ReplyKeyboard inlineKeyboardMarkup = getKeyBoardForSearch(dataForNextButton, dataForWriteButton);
            String pattern = messageDao.getMessage(37).getSendMessage().getText()
                    .replaceAll("fio", member.getFIO()).replaceAll("companyName", member.getCompanyName())
                    .replaceAll("contact", member.getContact()).replaceAll("nisha", member.getNisha());
            SendMessage sendMessage = new SendMessage().setChatId(chatId).setText(pattern).setReplyMarkup(inlineKeyboardMarkup)
                    .setParseMode(ParseMode.HTML);
            bot.sendMessage(sendMessage);

            return true;
        }
    }
    private void listIsEnded(Bot bot,long chatId) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage().setText("К сожалению больше кандидатов нет").setChatId(chatId);
        bot.sendMessage(sendMessage);
    }


}
