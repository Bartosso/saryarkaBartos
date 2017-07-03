package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eshu on 30.06.2017.
 */
public class InfoVoteCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        long chatId     = update.getCallbackQuery().getFrom().getId();
        String vote     = update.getCallbackQuery().getData();
        ListDao listDao = factory.getListDao("INFO_VOTE");
        switch (vote){
            case "info_vote_like" :
                listDao.fakeVote("LIKE_SECTION");
                break;
            case "info_vote_super":
                listDao.fakeVote("SUPER_SECTION");

        }
        bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setReplyMarkup(getInfoVoteKeyboard())
                .setChatId(chatId).setMessageId(update.getCallbackQuery().getMessage().getMessageId()));
        return true;
    }




    private InlineKeyboardMarkup getInfoVoteKeyboard() throws SQLException {
        InlineKeyboardMarkup keyboard         = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row;
        int[] votes = factory.getListDao("INFO_VOTE").getFakeVote();




        row        = new ArrayList<>();
        InlineKeyboardButton like = new InlineKeyboardButton();
        like.setText("Лайк \uD83D\uDC4D " + getFakeVoteCount(votes[0]));
        like.setCallbackData("info_vote_like");
        row.add(like);
        rows.add(row);

        row        = new ArrayList<>();
        InlineKeyboardButton supir = new InlineKeyboardButton();
        supir.setText("Супер! \uD83D\uDE03 " + getFakeVoteCount(votes[1]));
        supir.setCallbackData("info_vote_super");
        row.add(supir);
        rows.add(row);


        keyboard.setKeyboard(rows);
        return keyboard;


    }
    private long getFakeVoteCount(int votes) {
        long count = 0;
        if (votes == 0) {
            return count;
        } else {
            return votes;
        }
    }

}
