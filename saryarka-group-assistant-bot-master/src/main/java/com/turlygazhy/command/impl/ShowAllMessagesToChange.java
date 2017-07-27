package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShowAllMessagesToChange extends Command {
    private int                page;
    private long               chatId;
    private final int          messagesPerPage = 10;
    private ArrayList<com.turlygazhy.entity.Message> messageArrayList;
    private ArrayList<ArrayList<SendMessage>> pages;
    private ArrayList<Integer> messagesToDeleteIds;
    private long               messageEditId;
    private boolean editingMessage;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (editingMessage){
            if(update.hasMessage()){
                if(update.getMessage().hasText()){
                    messageDao.updateText(update.getMessage().getText(),messageEditId);
                    bot.sendMessage(messageDao.getMessage(53).getSendMessage().setChatId(chatId));
                    return true;
                }
            }

        }
        if(update.hasCallbackQuery()){
            if (update.getCallbackQuery().getData().equals("nextPageInMessages")){
                page++;
                messagesToDeleteIds = showPage(bot,messagesToDeleteIds,pages, page, chatId);
                return false;
            }
            if(update.getCallbackQuery().getData().equals("previousPageInMessages")){
                page--;
                messagesToDeleteIds = showPage(bot,messagesToDeleteIds,pages, page, chatId);

                return false;
            }
            if(update.getCallbackQuery().getData().contains("editMessageAdmin")){
                deletePages(bot,messagesToDeleteIds, chatId);
                messageEditId  = Long.parseLong(update.getCallbackQuery().getData().substring(update.getCallbackQuery().getData().indexOf(":")+1));
                bot.sendMessage(messageDao.getMessage(29).getSendMessage().setChatId(chatId));
                editingMessage = true;
                return false;
            }

        }
        if(update.getMessage().getText().equals(buttonDao.getButtonText(226))){
            messageArrayList = messageDao.getAllMessages();
            chatId =  update.getMessage().getChatId();
            pages  = getPages(messageArrayList);
            page   = 0;
            messagesToDeleteIds = showPage(bot,messagesToDeleteIds,pages, page, chatId);

        }
        return false;
    }
    private ArrayList<ArrayList<SendMessage>> getPages(ArrayList<com.turlygazhy.entity.Message> arrayListMessage){
        ArrayList<ArrayList<SendMessage>> pages = new ArrayList<>();
        ArrayList<SendMessage>            page;
        int pagesCount =  1+(arrayListMessage.size() / messagesPerPage);
        int counter    = 0;
        for (int b = 0; b < pagesCount; b++){
            page = new ArrayList<>();
            boolean isLastPageInMessage = false;
        for (int i = 0; i < messagesPerPage; i++){
            if (counter<arrayListMessage.size()){
            if (i == (messagesPerPage-1)||counter==(arrayListMessage.size()-1)){
                isLastPageInMessage = true;
            }
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(arrayListMessage.get(counter).getSendMessage().getText());
            sendMessage.setReplyMarkup(getButtonForEdit(arrayListMessage.get(counter).getId(), isLastPageInMessage, pagesCount, b));
            page.add(sendMessage);
            counter++;
            }

        }
            pages.add(page);
        }
        return pages;

    }

    private InlineKeyboardMarkup getButtonForEdit(long messageId, boolean isLastPageInMessage, int pageCount, int pageNow){
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        ArrayList<List<InlineKeyboardButton>> rows = new ArrayList<>();
        ArrayList<InlineKeyboardButton>       row  = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton("Изменить сообщение ⬆️");
        button.setCallbackData("editMessageAdmin:" + messageId);
        row.add(button);
        rows.add(row);

        if(isLastPageInMessage){
            row = new ArrayList<>();
            if(pageNow!=0){
            InlineKeyboardButton buttonNextPage = new InlineKeyboardButton("⬅️ Сюда");
            buttonNextPage.setCallbackData("previousPageInMessages");
            row.add(buttonNextPage);

            }
            if(pageCount!=(pageNow+1)){
                InlineKeyboardButton buttonPevPage = new InlineKeyboardButton("Туда ➡️");
                buttonPevPage.setCallbackData("nextPageInMessages");
                row.add(buttonPevPage);
            }
        rows.add(row);
        }
        return keyboard.setKeyboard(rows);
    }

    private ArrayList<Integer> showPage(Bot bot,ArrayList<Integer> messagesToDeleteIds, ArrayList<ArrayList<SendMessage>> pages, int page, long chatId) throws TelegramApiException {
        if(messagesToDeleteIds!=null){
        if(messagesToDeleteIds.size()!=0){
            for (int i : messagesToDeleteIds){
                bot.deleteMessage(new DeleteMessage(chatId, i));
            }

        }}
        messagesToDeleteIds = new ArrayList<>();
        for (SendMessage sendMessage : pages.get(page)){
            messagesToDeleteIds.add(bot.sendMessage(sendMessage).getMessageId());
        }

        return messagesToDeleteIds;
    }

    private void deletePages(Bot bot, ArrayList<Integer> messagesToDeleteIds, long chatId) throws TelegramApiException {
        if(messagesToDeleteIds!=null){
            if(messagesToDeleteIds.size()!=0){
                for (int i : messagesToDeleteIds){
                    bot.deleteMessage(new DeleteMessage(chatId, i));
                }

            }}
    }
}
