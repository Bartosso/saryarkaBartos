package com.turlygazhy;

import com.turlygazhy.command.Command;
import com.turlygazhy.command.impl.SearchCommand;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.ButtonDao;
import com.turlygazhy.dao.impl.KeyboardMarkUpDao;
import com.turlygazhy.dao.impl.MessageDao;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.exception.CommandNotFoundException;
import com.turlygazhy.service.CommandService;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yerassyl_Turlygazhy on 11/27/2016.
 */
public class Conversation {
    private CommandService commandService = new CommandService();
    private Command command;
    private DaoFactory factory = DaoFactory.getFactory();
    private MessageDao messageDao = factory.getMessageDao();
    private ButtonDao buttonDao = factory.getButtonDao();
    private KeyboardMarkUpDao keyboardMarkUpDao = factory.getKeyboardMarkUpDao();
    private List<Command> commandList = new ArrayList<Command>();

    private WaitingType waitingType;
    private String nisha;
    private String naviki;

    public void handleUpdate(Update update, Bot bot) throws SQLException, TelegramApiException {
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
        String inputtedText;
        //НЕ использавать в кнопках знак ":", обрезается для кнопок - принять ивент и не принять ивент и т.д.
        if (updateMessage == null) {
            inputtedText = update.getCallbackQuery().getData();
            updateMessage = update.getCallbackQuery().getMessage();
            if(inputtedText.contains(":")){
                inputtedText = update.getCallbackQuery().getData().substring(0,
                        inputtedText.indexOf(":"));
            }
        } else {
            inputtedText = updateMessage.getText();
        }

        try {
            command = commandService.getCommand(inputtedText);
        } catch (CommandNotFoundException e) {
            if (command == null) {
                Message message = messageDao.getMessage(10);
                SendMessage sendMessage = message.getSendMessage();
                sendMessage.setChatId(update.getMessage().getChatId());
                sendMessage.setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));
                bot.sendMessage(sendMessage);
                return;
            }
        }
        boolean commandFinished = command.execute(update, bot);
        if (commandFinished) {
            command = null;
        }
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
