package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Member;
import com.turlygazhy.google_sheets.SheetsAdapter;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
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
    private boolean toMainSheet;
//    private static final String KEY = "src/main/resources/members-36a5849089da.json";
    private static final String KEY = "C:\\bots-data\\members-36a5849089da.json";
    //todo don't forget about json

    private static final String SPREAD_SHEET_ID = "1YiXnofbXojLCpPLxlAednPxRYN1c-oxZVdUddg0luLM";
//            "1HyLocKj3xc-auD2zCbk5zpXNioHveMJEYYvpHHVvCEM";

    public AddToGoogleSheetsCommand(String userId, boolean toMainSheet) {
        super();
        this.userId      = userId;
        this.toMainSheet = toMainSheet;
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Integer userId = Integer.valueOf(this.userId);
        Member member = memberDao.selectByUserId(userId);
        SheetsAdapter sheets = new SheetsAdapter();

        ArrayList<Member> list = new ArrayList<>();
        list.add(member);
        if (!toMainSheet){
            try {
                sheets.authorize(KEY);
                sheets.appendData(SPREAD_SHEET_ID, "Лист2", 'A', 1, list);
                bot.execute(new SendMessage(String.valueOf(userId),messageDao
                .getMessage(180).getSendMessage().getText()).setParseMode(ParseMode.HTML));
                bot.execute(new DeleteMessage(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery()
                        .getMessage().getMessageId()));
                bot.execute(new SendMessage(update.getCallbackQuery().getMessage().getChatId(), messageDao.getMessage(174)
                        .getSendMessage().getText()+"\nПользователь добавлен в архив."));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        int lastRow = Integer.parseInt(constDao.select(LAST_ROW_DATA_ID));
        int puttedRow = lastRow + 1;
        try {
            sheets.authorize(KEY);
            sheets.writeData(SPREAD_SHEET_ID, "Лист1", 'B', puttedRow, list);
            constDao.update(LAST_ROW_DATA_ID, String.valueOf(puttedRow));
            memberDao.setAddedToGroup(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        bot.execute(new DeleteMessage(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery()
        .getMessage().getMessageId()));
        sendMessage(60, update.getCallbackQuery().getMessage().getChatId(), bot);
        sendMessage(72, member.getChatId(), bot);
        if (!memberDao.checkMemberAgreeToRules(String.valueOf(member.getId()))) {
            bot.execute(messageDao.getMessage(70).getSendMessage()
                    .setChatId(member.getChatId()).setReplyMarkup(keyboardMarkUpDao.select(36)));

        }
        return true;
    }
}
