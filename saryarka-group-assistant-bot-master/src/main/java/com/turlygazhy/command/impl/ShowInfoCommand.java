package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**Й
 * Created by user on 1/2/17.
 */
public class ShowInfoCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger(ShowInfoCommand.class);
    public static final String FIRST_NAME_VAR = "firstName";

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException {
        Long chatId = update.getMessage().getChatId();

        Message message = messageDao.getMessage(messageId);

        SendPhoto sendPhoto = message.getSendPhoto();
        SendMessage sendMessage = message.getSendMessage();
        String text = message.getSendMessage().getText();
        if (text.contains(FIRST_NAME_VAR)) {
            text = text.replaceAll(FIRST_NAME_VAR, update.getMessage().getFrom().getFirstName());
            sendMessage.setText(text);
        }
        try {
            if (sendPhoto != null) {
                bot.sendPhoto(sendPhoto.setChatId(chatId));
            }
            bot.sendMessage(sendMessage
                    .setChatId(chatId)
                    .setReplyMarkup(
                            keyboardMarkUpDao.select(message.getKeyboardMarkUpId())
//                    getInfoVoteKeyboard()
                    ).setParseMode(ParseMode.HTML)
            );
            return true;
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
//    private ReplyKeyboard getInfoVoteKeyboard() throws SQLException {
//        InlineKeyboardMarkup keyboard         = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
//        List<InlineKeyboardButton> row;
//        int[] votes = factory.getListDao("INFO_VOTE").getFakeVote();
//
//
//
//
//            row        = new ArrayList<>();
//            InlineKeyboardButton like = new InlineKeyboardButton();
//            like.setText("Лайк \uD83D\uDC4D " + getFakeVoteCount(votes[0]));
//            like.setCallbackData("info_vote_like");
//            row.add(like);
//            rows.add(row);
//
//            row        = new ArrayList<>();
//            InlineKeyboardButton supir = new InlineKeyboardButton();
//            supir.setText("Супер! \uD83D\uDE03 " + getFakeVoteCount(votes[1]));
//            supir.setCallbackData("info_vote_super");
//            row.add(supir);
//            rows.add(row);
//
//
//        keyboard.setKeyboard(rows);
//        return keyboard;
//
//
//    }
//    private long getFakeVoteCount(int votes) {
//        long count = 0;
//        if (votes == 0) {
//            return count;
//        } else {
//            return votes;
//        }
//    }
}
