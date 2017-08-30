package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Vacancy;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
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
 * Created by Eshu on 06.07.2017.
 */
public class VacancyPreEditMenuCommand extends Command {
    private boolean expectData;
    private String  chose;
    private String  vacancyId;
    private long    chatId;
    private int     messageId;
    private int     secondMessageId;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(expectData){
            org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
            if (updateMessage == null) {
                updateMessage = update.getCallbackQuery().getMessage();
            }
            bot.execute(new DeleteMessage(chatId, secondMessageId));
            bot.execute(new EditMessageReplyMarkup().setMessageId(messageId).setReplyMarkup(null).setChatId(chatId));
            ListDao listDao = factory.getListDao("VACANCIES");
            switch (chose){
                case "editVacancyName":
                    listDao.changeStuff(updateMessage.getText(),vacancyId, "COMPANY_NAME");
                    bot.execute(new SendMessage(chatId,"изменения сохранены"));
                    sendEditMessageToAdmin(bot,vacancyId);
                    return true;
                case "editVacancySfera":
                    listDao.changeStuff(updateMessage.getText(),vacancyId, "SFERA");
                    bot.execute(new SendMessage(chatId,"изменения сохранены"));
                    sendEditMessageToAdmin(bot,vacancyId);
                    return true;
                case "editVacancyExp":
                    listDao.changeStuff(updateMessage.getText(),vacancyId, "EXPERIENCE");
                    bot.execute(new SendMessage(chatId,"изменения сохранены"));
                    sendEditMessageToAdmin(bot,vacancyId);
                    return true;
                case "editVacancyPlc":
                    listDao.changeStuff(updateMessage.getText(),vacancyId, "PLACE");
                    bot.execute(new SendMessage(chatId,"изменения сохранены"));
                    sendEditMessageToAdmin(bot,vacancyId);
                    return true;
                case "editVacancyWC":
                    listDao.changeStuff(updateMessage.getText(),vacancyId, "WORKING_CONDITIONS");
                    bot.execute(new SendMessage(chatId,"изменения сохранены"));
                    sendEditMessageToAdmin(bot,vacancyId);
                    return true;
                case "editVacancySlr":
                    listDao.changeStuff(updateMessage.getText(),vacancyId, "SALARY");
                    bot.execute(new SendMessage(chatId,"изменения сохранены"));
                    sendEditMessageToAdmin(bot,vacancyId);
                    return true;
                case "editVacancyCnt":
                    listDao.changeStuff(updateMessage.getText(),vacancyId, "CONTACT");
                    bot.execute(new SendMessage(chatId,"изменения сохранены"));
                    sendEditMessageToAdmin(bot,vacancyId);
                    return true;
            }
        }
        else {
        vacancyId = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1);
        chose     = update.getCallbackQuery().getData().substring(0,update.getCallbackQuery().getData().indexOf(":"));
        chatId    = update.getCallbackQuery().getFrom().getId();
        messageId = update.getCallbackQuery().getMessage().getMessageId();
        if(chose.equals("edit_vacancy")){
           secondMessageId = sendEditMessageToAdmin(bot, vacancyId);
            return false;
        }
        expectData      = true;
        secondMessageId = bot.execute(new SendMessage(chatId,"Введите новое значение")).getMessageId();
        }
        return false;
    }

    private int sendEditMessageToAdmin(Bot bot, String vacancyId) throws SQLException, TelegramApiException {
        Vacancy vacancy = factory.getListDao("VACANCIES").getVacancy(vacancyId);
        String text = messageDao.getMessage(119).getSendMessage().getText()
                .replaceAll("company_name"       , vacancy.getCompanyName())
                .replaceAll("sfera"              , vacancy.getSfera())
                .replaceAll("experience"         , vacancy.getExperience())
                .replaceAll("place"              , vacancy.getPlace())
                .replaceAll("working_coniditions", vacancy.getWorkingConditions())
                .replaceAll("salary"             , vacancy.getSalary())
                .replaceAll("contact_info"       , vacancy.getContact());
       return bot.execute(new SendMessage().setText("Редактирование вакансии перед публикацией\n" + text)
                .setParseMode(ParseMode.HTML).setReplyMarkup(getKeyboardForEdit(vacancyId)).setChatId(chatId)).getMessageId();
    }

    private ReplyKeyboard getKeyboardForEdit(String vacancyId) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton editVacancyName       = new InlineKeyboardButton(buttonDao.getButtonText(189));
        editVacancyName.setCallbackData("editVacancyName"+":" + vacancyId);
        row.add(editVacancyName);
        rows.add(row);

        row = new ArrayList<>();
        InlineKeyboardButton editVacancySfera      = new InlineKeyboardButton(buttonDao.getButtonText(191));
        editVacancySfera.setCallbackData("editVacancySfera"+":" + vacancyId);
        row.add(editVacancySfera);
        rows.add(row);

        row = new ArrayList<>();
        InlineKeyboardButton editVacancyExperience = new InlineKeyboardButton(buttonDao.getButtonText(193));
        editVacancyExperience.setCallbackData("editVacancyExp" + ":" + vacancyId);
        row.add(editVacancyExperience);
        rows.add(row);

        row = new ArrayList<>();
        InlineKeyboardButton editVacancyPlace      = new InlineKeyboardButton(buttonDao.getButtonText(195));
        editVacancyPlace.setCallbackData("editVacancyPlc" + ":" + vacancyId);
        row.add(editVacancyPlace);
        rows.add(row);

        row = new ArrayList<>();
        InlineKeyboardButton editVacancyWC         = new InlineKeyboardButton(buttonDao.getButtonText(197));
        editVacancyWC.setCallbackData("editVacancyWC" + ":" + vacancyId);
        row.add(editVacancyWC);
        rows.add(row);

        row = new ArrayList<>();
        InlineKeyboardButton editVacancySalary     = new InlineKeyboardButton(buttonDao.getButtonText(199));
        editVacancySalary.setCallbackData("editVacancySlr" + ":" + vacancyId);
        row.add(editVacancySalary);
        rows.add(row);

        row = new ArrayList<>();
        InlineKeyboardButton editVacancyContact    = new InlineKeyboardButton(buttonDao.getButtonText(201));
        editVacancyContact.setCallbackData("editVacancyCnt" + ":" + vacancyId);
        row.add(editVacancyContact);
        rows.add(row);

        row = new ArrayList<>();
        InlineKeyboardButton acceptVacancy = new InlineKeyboardButton();
        acceptVacancy.setText(buttonDao.getButtonText(138));
        acceptVacancy.setCallbackData(buttonDao.getButtonText(138) + ":" + vacancyId);
        row.add(acceptVacancy);

        InlineKeyboardButton deleteVacancy = new InlineKeyboardButton();
        deleteVacancy.setText(buttonDao.getButtonText(137));
        deleteVacancy.setCallbackData("delete_vacancy:" + vacancyId);
        row.add(deleteVacancy);

        rows.add(row);
        return keyboard.setKeyboard(rows);
    }
}
