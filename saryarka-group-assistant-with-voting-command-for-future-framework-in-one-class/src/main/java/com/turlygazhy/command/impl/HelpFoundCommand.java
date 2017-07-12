package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by user on 2/19/17.
 */
public class HelpFoundCommand extends Command {
    private Integer messageId;
    private Long groupId;

    public HelpFoundCommand(String s) {
        super();
        String[] split = s.split("/");
        messageId = Integer.valueOf(split[0]);
        groupId = Long.valueOf(split[1]);
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        bot.sendMessage(new SendMessage()
                .setChatId(groupId)
                .setReplyToMessageId(messageId)
                .setText(messageDao.getMessage(65).getSendMessage().getText())
        );
        return true;
    }
}
