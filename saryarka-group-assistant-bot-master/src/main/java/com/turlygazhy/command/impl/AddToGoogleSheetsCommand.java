package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Member;
import com.turlygazhy.google_sheets.SheetsAdapter;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by user on 2/14/17.
 */
public class AddToGoogleSheetsCommand extends Command {
    private static final int LAST_ROW_DATA_ID = 3;
    private String userId;
    private static final String KEY = "src/main/resources/members-36a5849089da.json";
//    private static final String KEY = "C:\\bots-data\\members-36a5849089da.json";
    //todo don't forget about json

    private static final String SPREAD_SHEET_ID = "1HyLocKj3xc-auD2zCbk5zpXNioHveMJEYYvpHHVvCEM";

    public AddToGoogleSheetsCommand(String userId) {
        super();
        this.userId = userId;
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Integer userId = Integer.valueOf(this.userId);
        Member member = memberDao.selectByUserId(userId);
        SheetsAdapter sheets = new SheetsAdapter();

        ArrayList<Member> list = new ArrayList<>();
        list.add(member);
        int lastRow = Integer.parseInt(constDao.select(LAST_ROW_DATA_ID));
        int puttedRow = lastRow + 1;
        try {
            sheets.authorize(KEY);
            sheets.writeData(SPREAD_SHEET_ID, "list", 'A', puttedRow, list);
            constDao.update(LAST_ROW_DATA_ID, String.valueOf(puttedRow));
            memberDao.setAddedToGroup(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        sendMessage(60, update.getCallbackQuery().getMessage().getChatId(), bot);
        sendMessage(72, member.getChatId(), bot);
        if (!memberDao.checkMemberAgreeToRules(String.valueOf(member.getId()))) {
            bot.sendMessage(messageDao.getMessage(70).getSendMessage()
                    .setChatId(member.getChatId()).setReplyMarkup(keyboardMarkUpDao.select(36)));

//        sendMessage(70, member.getChatId(), bot);
        }
        return true;
    }
}
