package com.turlygazhy.command.impl;

import com.sun.org.apache.regexp.internal.RE;
import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Discount;
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
 * Created by Eshu on 29.06.2017.
 */
public class ShowDiscountForAdminAndDeleteMenuCommand extends Command {
    private long chatId;
    private int step = 0;
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if(step == 0){
        chatId = update.getMessage().getChatId();
        bot.sendMessage(new SendMessage(chatId, "Выберите категорию которую хотите просмотреть")
                .setReplyMarkup(keyboardMarkUpDao.select(28)));
        step = 1;
        return false;
        }
        if(step == 1){
            String menuChose;
            if(update.getCallbackQuery()!= null){
                menuChose = update.getCallbackQuery().getData();
            }
            else{
               menuChose = update.getMessage().getText();
            }
            if(menuChose != null){

            if(menuChose.equals(buttonDao.getButtonText(101))){
                getDiscounts(bot,update, "services","Услуги");
                return true;
            }
            if(menuChose.equals(buttonDao.getButtonText(102))){
                getDiscounts(bot,update, "goods", "Товары");
                return true;
            }
//            if(menuChose.equals(buttonDao.getButtonText(103))){
//                getDiscounts(bot,update, "BusinessSchools", "Бизнес-школы");
//                return true;
//            }
        }}
        chatId = 0;
        step   = 0;
        return true;
    }


    private void getDiscounts(Bot bot, Update update, String discountsType, String textWithDiscounts) throws SQLException, TelegramApiException {
        ListDao listDao = factory.getListDao("DISCOUNTS");
        ArrayList<Discount> discountArrayList = listDao.getDiscounts(discountsType);
        if(discountArrayList.isEmpty()){
            SendMessage sendMessage = new SendMessage().setText("К сожалению ничего не найдено")
                    .setChatId(chatId);
            bot.sendMessage(sendMessage);
        }
        else{
            ReplyKeyboard keyboard                = getDiscountWithDeleteViaButtons(discountArrayList);
            SendMessage sendMessage               = new SendMessage().setChatId(chatId)
                    .setText(textWithDiscounts).setReplyMarkup(keyboard);
            bot.sendMessage(sendMessage);}
    }


    private ReplyKeyboard getDiscountWithDeleteViaButtons(ArrayList<Discount> discountArrayList){
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row;

        for (Discount discount : discountArrayList) {
            row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            String buttonText = discount.getName() + " (-" + discount.getDiscount() +")";
            button.setText(buttonText);
            button.setCallbackData("get_discount:"+ discount.getId());
            row.add(button);
            rows.add(row);

            row = new ArrayList<>();
            InlineKeyboardButton buttonDelete = new InlineKeyboardButton();
            buttonDelete.setText("Удалить дисконт ⬆️");
            buttonDelete.setCallbackData("deleteDiscount:" + discount.getId());
            row.add(buttonDelete);
            rows.add(row);
        }
        keyboard.setKeyboard(rows);
        return keyboard;

    }

//    private ReplyKeyboard getKeyboardAdminCat() throws SQLException {
//        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
//        List<InlineKeyboardButton> row;
//
//        row = new ArrayList<>();
//        InlineKeyboardButton restaurant = new InlineKeyboardButton();
//        String buttonText = buttonDao.getButtonText(97);
//        restaurant.setText(buttonText);
//        restaurant.setCallbackData(buttonText);
//        row.add(restaurant);
//        rows.add(row);
//
//        row = new ArrayList<>();
//        InlineKeyboardButton hotels = new InlineKeyboardButton();
//        String buttonTextHotels = buttonDao.getButtonText(98);
//        hotels.setText(buttonTextHotels);
//        hotels.setCallbackData(buttonTextHotels);
//        row.add(hotels);
//        rows.add(row);
//
//        row = new ArrayList<>();
//        InlineKeyboardButton businessSchools = new InlineKeyboardButton();
//        String buttonTextBusinessSchools = buttonDao.getButtonText(99);
//        hotels.setText(buttonTextBusinessSchools);
//        hotels.setCallbackData(buttonTextBusinessSchools);
//        row.add(businessSchools);
//        rows.add(row);
//
//        keyboard.setKeyboard(rows);
//
//
//
//       return keyboard;
//    }
}
