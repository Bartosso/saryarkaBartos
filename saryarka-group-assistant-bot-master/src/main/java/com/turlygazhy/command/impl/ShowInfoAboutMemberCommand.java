package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Member;
import com.turlygazhy.entity.Message;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by user on 1/22/17.
 */
public class ShowInfoAboutMemberCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Integer userId = update.getMessage().getFrom().getId();
        Member member = memberDao.selectByUserId(userId);
        Message message = messageDao.getMessage(messageId);
        String text = message.getSendMessage().getText()
                .replaceAll("fio"        , member.getFIO())
                .replaceAll("companyName", member.getCompanyName())
                .replaceAll("contact"    , member.getContact())
                .replaceAll("nisha"      , member.getNisha())
                .replaceAll("phoneNumber", member.getPhoneNumber())
                .replaceAll("memberCity" , member.getCity());

        ReplyKeyboardMarkup keyboard = (ReplyKeyboardMarkup) keyboardMarkUpDao.select(message.getKeyboardMarkUpId());

        //            List<KeyboardRow> keyboardRows = keyboard.getKeyboard();
//            KeyboardRow keyboardButtons = new KeyboardRow();
//            KeyboardButton keyboardButton = new KeyboardButton();
//            keyboardButton.setText(buttonDao.getButtonText(63));
//            keyboardButtons.add(keyboardButton);
//            keyboardRows.add(keyboardRows.size() - 1, keyboardButtons);
//        boolean memberAdded = memberDao.isMemberAdded(userId);
//        if (!memberAdded) {
//            SendMessage sendMessage = new SendMessage().setChatId(getAdminChatId()).setText("Пользаватель изменил данные о себе\n" + text)
//                    .setReplyMarkup(keyboardMarkUpDao.select(44));
//            bot.sendMessage(sendMessage);
//        }

        bot.sendMessage(new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(text)
                .setReplyMarkup(keyboard)
                .setParseMode(ParseMode.HTML)
        );

        return true;
    }
}
