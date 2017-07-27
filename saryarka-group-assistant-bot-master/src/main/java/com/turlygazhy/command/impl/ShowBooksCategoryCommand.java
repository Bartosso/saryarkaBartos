package com.turlygazhy.command.impl;


import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Book;
import com.turlygazhy.tool.vanderkastTools.Constructors.InlineKeyboardConstructor;
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

public class ShowBooksCategoryCommand extends Command {
    private long chatId;
    private ArrayList<String> categories;
    private int step = 0;
    private ListDao listDao;
    private int page = 0;
    private ArrayList<InlineKeyboardMarkup> pages;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(step == 0){
        chatId = update.getMessage().getChatId();
        InlineKeyboardMarkup keyboard;
        try{
            keyboard = getCategories();

        }catch (Exception e){
            bot.sendMessage(new SendMessage(chatId, messageDao.getMessage(131).getSendMessage().getText()));
            return true;
        }
        bot.sendMessage(messageDao.getMessage(165).getSendMessage().setChatId(chatId)
        .setParseMode(ParseMode.HTML).setReplyMarkup(keyboard));
        step = 1;
        return false;
        }
        else {
            if(update.hasCallbackQuery()){
                if (update.getCallbackQuery().getData().equals("nextPageInBooks")){
                    page++;
                    bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(
                            update.getCallbackQuery().getMessage().getMessageId()
                    ).setReplyMarkup(pages.get(page)));
                    return false;

                }
                if (update.getCallbackQuery().getData().equals("previousPageInBooks")){
                    page--;
                    bot.editMessageReplyMarkup(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(
                            update.getCallbackQuery().getMessage().getMessageId()
                    ).setReplyMarkup(pages.get(page)));
                    return false;
                }





            }



            bot.deleteMessage(new DeleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId()));
            String categoryId = update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1);

            ArrayList<Book> chosenBooks = listDao.getAllBooksInDistinctCategories(categories.get(Integer.parseInt(categoryId)));

            pages = getBooksViaButtons(chosenBooks);
            bot.sendMessage(new SendMessage(chatId, chosenBooks.get(0).getCategory())
            .setReplyMarkup(pages.get(page)));
            return false;
        }
    }
    private InlineKeyboardMarkup getCategories() throws SQLException {
        listDao = factory.getListDao("BOOKS");
        categories = listDao.getBooksCategories();
        ArrayList<String> buttonText  = new ArrayList<>();
        ArrayList<String> buttonsData = new ArrayList<>();
        for(int i = 0; i<categories.size(); i++){
//            if(categories.get(i)==null){
//                buttonText.add(buttonDao.getButtonText(217));
//            }
//            else {
                buttonText.add(categories.get(i));
//            }
            buttonsData.add("getBookCat:" + i);
        }
        return InlineKeyboardConstructor.getKeyboard(buttonText, buttonsData);
    }

    @SuppressWarnings("Duplicates")
    private ArrayList<InlineKeyboardMarkup> getBooksViaButtons(ArrayList<Book> bookArrayList){
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
                counter++;
                }
        }
            row = new ArrayList<>();

            if(keyboards.size()!=0){
                InlineKeyboardButton buttonToPreviousPage = new InlineKeyboardButton();
                buttonToPreviousPage.setText("⬅️ Сюда");
                buttonToPreviousPage.setCallbackData("previousPageInBooks");
                row.add(buttonToPreviousPage);}


            if(pagesCount>(b+1)){
                InlineKeyboardButton buttonToNextPage = new InlineKeyboardButton();
                buttonToNextPage.setText("Туда ➡️");
                buttonToNextPage.setCallbackData("nextPageInBooks");
                row.add(buttonToNextPage);
        }
            rows.add(row);
            keyboard.setKeyboard(rows);
            keyboards.add(keyboard);
    }
    return keyboards;
    }
}
