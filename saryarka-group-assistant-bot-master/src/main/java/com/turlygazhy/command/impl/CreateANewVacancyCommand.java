package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.MessageElement;

import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eshu on 20.06.2017.
 */
public class CreateANewVacancyCommand extends Command {
    private String             companyName;
    private String             sfera;
    private String             experience;
    private String             place;
    private String             workingConditions;
    private String             salary;
    private String             contact;
//    private String             photo;
    private MessageElement expectedMessageElement;
//    private boolean            needPhoto     = true;
    private ListDao            listDao       = factory.getListDao("VACANCIES");
    private int                step          = 1;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
        if (updateMessage == null) {
            updateMessage = update.getCallbackQuery().getMessage();
        }
        Long chatId = updateMessage.getChatId();
        if (expectedMessageElement != null) {
            switch (step){
                case 1:
                    companyName       = updateMessage.getText();
                    step = 2;
                    break;
                case 2:
                    sfera             = updateMessage.getText();
                    step = 3;
                    break;
                case 3:
                    experience        = updateMessage.getText();
                    step = 4;
                    break;
                case 4:
                    place             = updateMessage.getText();
                    step = 5;
                    break;
                case 5:
                    workingConditions = updateMessage.getText();
                    step = 6;
                    break;
                case 6:
                    salary            = updateMessage.getText();
                    step = 7;
                    break;
                case 7:
                    contact           = updateMessage.getText();
                    step = 8;
                    break;
            }
        }

        if (step == 1 && companyName == null) {
            Message message = messageDao.getMessage(112);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if (step == 2 && sfera == null) {
            Message message = messageDao.getMessage(113);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if (step == 3 && experience == null) {
            Message message = messageDao.getMessage(114);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if ( step == 4 && place == null) {
            Message message = messageDao.getMessage(115);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.TEXT;
            return false;
        }
        if (step == 5 && workingConditions == null) {
            Message message = messageDao.getMessage(116);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);

            bot.sendMessage(sendMessage);
            expectedMessageElement = MessageElement.PHOTO;
            return false;
        }
        if (step == 6 && salary == null) {
            Message message = messageDao.getMessage(117);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);
            bot.sendMessage(sendMessage);
            return false;
        }
        if (step == 7 & contact == null){
            Message message = messageDao.getMessage(118);
            SendMessage sendMessage = message.getSendMessage()
                    .setChatId(chatId).setParseMode(ParseMode.HTML);
            bot.sendMessage(sendMessage);
            return false;
        }
        if(step == 8){
            int vacancyId = listDao.addNewVacancy(companyName,sfera,experience,place,workingConditions, salary,
                    contact,memberDao.getMemberId(chatId), false);
            SendMessage sendVacancyToAdmin = getTextToAdmin(vacancyId).setChatId(getAdminChatId());
            SendMessage sendMessage = new SendMessage().setText(messageDao.getMessage(141).getSendMessage()
                    .getText()).setChatId(chatId);
            bot.sendMessage(sendVacancyToAdmin);
            bot.sendMessage(sendMessage);
            return true;
        }


        return true;
    }

    private SendMessage getTextToAdmin(int vacancyId) throws SQLException {
        String text = messageDao.getMessage(119).getSendMessage().getText()
                .replaceAll("company_name"       , companyName)
                .replaceAll("sfera"              , sfera)
                .replaceAll("experience"         , experience)
                .replaceAll("place"              , place)
                .replaceAll("working_coniditions", workingConditions)
                .replaceAll("salary"             , salary)
                .replaceAll("contact_info"       , contact);
        return new SendMessage().setText("Запрос на публикацию вакансии\n" + text)
                .setParseMode(ParseMode.HTML).setReplyMarkup(keyBoardForAdmin(vacancyId));
    }
    private ReplyKeyboard keyBoardForAdmin(int vacancyId) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton acceptVacancy = new InlineKeyboardButton();
        acceptVacancy.setText(buttonDao.getButtonText(138));
        acceptVacancy.setCallbackData(buttonDao.getButtonText(138) + ":" + vacancyId);
        row.add(acceptVacancy);

        InlineKeyboardButton deleteVacancy = new InlineKeyboardButton();
        deleteVacancy.setText(buttonDao.getButtonText(137));
        deleteVacancy.setCallbackData("delete_vacancy:" + vacancyId);
        row.add(deleteVacancy);

        InlineKeyboardButton editVacancy = new InlineKeyboardButton();
        editVacancy.setText(buttonDao.getButtonText(187));
        editVacancy.setCallbackData("edit_vacancy:" + vacancyId);
        row.add(editVacancy);


        rows.add(row);
        keyboard.setKeyboard(rows);

        return keyboard;
    }
}
