package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.ListData;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Eshu on 02.07.2017.
 */
public class SolutionForRequestTenderCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        long chatID = update.getCallbackQuery().getFrom().getId();
        String tenderId = update.getCallbackQuery().getData().substring(update.getCallbackQuery()
                .getData().indexOf(":") + 1);
        String chose = update.getCallbackQuery().getData().substring(0, update.getCallbackQuery()
                .getData().indexOf(":"));
        ListDao listDao = factory.getListDao("REQUESTS_LIST");

        if (chose.equals(buttonDao.getButtonText(159))) {
            ListData offer = listDao.getListDataById(tenderId);
            ReplyKeyboard keyboard  = getKeyboardForEditRequestTender(Integer.parseInt(tenderId));
            bot.deleteMessage(new DeleteMessage().setChatId(String.valueOf(chatID))
                    .setMessageId(update.getCallbackQuery().getMessage().getMessageId()));
            bot.sendMessage(new SendMessage().setChatId(chatID).setText(
                    messageDao.getMessage(154).getSendMessage().getText()
                            .replaceAll("tender_type_name", "Список запросов")
                            .replaceAll("tender_text"     , offer.getText())
                            .replaceAll("tender_type_word", "ищет")
                            .replaceAll("tender_member", "@"+memberDao.getMemberById(offer.getMemberId()).getUserName()))
                    .setReplyMarkup(keyboard));
        }

        if (listDao.isStuffActive(tenderId)) {
            sendMessage(143, chatID, bot);
            return true;
        } else {
            if (chose.equals(buttonDao.getButtonText(157))) {
            listDao.makeStuffBe(tenderId);
            factory.getTop10Dao("TOP10_REQUESTS").insert(listDao.getListDataById(tenderId).getText().toLowerCase());
            sendMessage(144, chatID, bot);
            }
            if (chose.equals(buttonDao.getButtonText(158))) {
            listDao.delete(tenderId);
            sendMessage(146, chatID, bot);
            }
            return true;
        }
    }
}
