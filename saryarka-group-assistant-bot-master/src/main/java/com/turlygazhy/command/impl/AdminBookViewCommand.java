package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Book;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eshu on 30.06.2017.
 */
public class AdminBookViewCommand extends Command {
    @SuppressWarnings("FieldCanBeLocal")
    private ArrayList<InlineKeyboardMarkup> keyboards;
    private int page;
    private long chatId;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(update.hasCallbackQuery()){

            if (update.getCallbackQuery().getData().equals("nextPageInAdminBooks")){
                page++;
                bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setChatId(chatId)
                .setMessageId(update.getCallbackQuery().getMessage().getMessageId())
                .setReplyMarkup(keyboards.get(page)));
                return false;
            }
            if (update.getCallbackQuery().getData().equals("previousPageInAdminBooks")){
                page--;
                bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setChatId(chatId)
                .setMessageId(update.getCallbackQuery().getMessage().getMessageId())
                .setReplyMarkup(keyboards.get(page)));
                return false;
            }


        }
        chatId                   = update.getMessage().getChatId();
        ArrayList<Book> bookArrayList = factory.getListDao("BOOKS").getAllBooks();
        if(bookArrayList.isEmpty()){
            sendMessage(131,chatId, bot);
            return true;
        }
        else{
            keyboards = getAdminBookKeyboard(bookArrayList);
            bot.sendMessage(messageDao.getMessage(messageId).getSendMessage().setChatId(chatId)
                    .setReplyMarkup(keyboards.get(page)));
        return false;
        }
    }


    @SuppressWarnings("Duplicates")
    private ArrayList<InlineKeyboardMarkup> getAdminBookKeyboard(ArrayList<Book> bookArrayList){
        ArrayList<InlineKeyboardMarkup>  keyboards = new ArrayList<>();
        List<InlineKeyboardButton>       row;
        List<List<InlineKeyboardButton>> rows;

        int pagesCount = 1 + (bookArrayList.size()/25);
        int counter   = 0;
        for (int b = 0; b < pagesCount; b++) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            rows = new ArrayList<>();
            for (int i = 0; i < 25; i++){
                if (counter<bookArrayList.size()){
                    row                             = new ArrayList<>();
                    InlineKeyboardButton bookButton = new InlineKeyboardButton();
                    bookButton.setText(bookArrayList.get(counter).getBookName());
                    bookButton.setCallbackData("get_book" + ":" + bookArrayList.get(counter).getId());
                    row.add(bookButton);
                    rows.add(row);

                    row                                   = new ArrayList<>();
                    InlineKeyboardButton deleteBookButton = new InlineKeyboardButton();
                    deleteBookButton.setText("Удалить эту книгу ⬆️");
                    deleteBookButton.setCallbackData("delete_book" + ":" + bookArrayList.get(counter).getId());
                    row.add(deleteBookButton);
                    rows.add(row);
                    counter++;
                }}
                row = new ArrayList<>();

                if(keyboards.size()!=0){
                    InlineKeyboardButton buttonToPreviousPage = new InlineKeyboardButton();
                    buttonToPreviousPage.setText("⬅️ Сюда");
                    buttonToPreviousPage.setCallbackData("previousPageInAdminBooks");
                    row.add(buttonToPreviousPage);}


                if(pagesCount>(b+1)){
                    InlineKeyboardButton buttonToNextPage = new InlineKeyboardButton();
                    buttonToNextPage.setText("Туда ➡️");
                    buttonToNextPage.setCallbackData("nextPageInAdminBooks");
                    row.add(buttonToNextPage);
                }
                rows.add(row);
                keyboard.setKeyboard(rows);
                keyboards.add(keyboard);

        }
        return keyboards;

    }
}
