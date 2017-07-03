package com.turlygazhy.command;

import com.turlygazhy.Bot;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.*;
import com.turlygazhy.entity.Discount;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.Vacancy;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendContact;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yerassyl_Turlygazhy on 11/27/2016.
 */
public abstract class Command {
    protected long id;
    protected long messageId;

    protected DaoFactory factory = DaoFactory.getFactory();
    protected UserDao userDao = factory.getUserDao();
    protected MessageDao messageDao = factory.getMessageDao();
    protected KeyboardMarkUpDao keyboardMarkUpDao = factory.getKeyboardMarkUpDao();
    protected ButtonDao buttonDao = factory.getButtonDao();
    protected CommandDao commandDao = factory.getCommandDao();
    protected ConstDao constDao = factory.getConstDao();
    protected MemberDao memberDao = factory.getMemberDao();
    protected KeyWordDao keyWordDao = factory.getKeyWordDao();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return is command finished
     */
    public abstract boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException;

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void sendMessage(long messageId, long chatId, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
        sendMessage(messageId, chatId, bot, null);
    }

    public void sendMessage(String text, long chatId, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
        sendMessage(text, chatId, bot, null);
    }

    public void sendMessage(long messageId, long chatId, TelegramLongPollingBot bot, Contact contact) throws SQLException, TelegramApiException {
        Message message = messageDao.getMessage(messageId);
        SendMessage sendMessage = message.getSendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));
        bot.sendMessage(sendMessage);
        if (contact != null) {
            bot.sendContact(new SendContact()
                    .setChatId(chatId)
                    .setFirstName(contact.getFirstName())
                    .setLastName(contact.getLastName())
                    .setPhoneNumber(contact.getPhoneNumber())
            );
        }
    }

    public void sendMessage(String text, long chatId, TelegramLongPollingBot bot, Contact contact) throws SQLException, TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        bot.sendMessage(sendMessage);
        if (contact != null) {
            bot.sendContact(new SendContact()
                    .setChatId(chatId)
                    .setFirstName(contact.getFirstName())
                    .setLastName(contact.getLastName())
                    .setPhoneNumber(contact.getPhoneNumber())
            );
        }
    }

    public void sendMessageToAdmin(long messageId, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
        long adminChatId = getAdminChatId();
        sendMessage(messageId, adminChatId, bot);
    }

    public long getAdminChatId() {
        return userDao.getAdminChatId();
    }

    public void sendMessageToAdmin(long messageId, Bot bot, Contact contact) throws SQLException, TelegramApiException {
        long adminChatId = getAdminChatId();
        sendMessage(messageId, adminChatId, bot, contact);
    }

    public void sendMessageToAdmin(String text, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
        long adminChatId = getAdminChatId();
        sendMessage(text, adminChatId, bot);
    }

    public void sendContactToAdmin(Contact contact, Bot bot) throws TelegramApiException {
        long adminChatId = getAdminChatId();
        bot.sendContact(new SendContact()
                .setChatId(adminChatId)
                .setFirstName(contact.getFirstName())
                .setLastName(contact.getLastName())
                .setPhoneNumber(contact.getPhoneNumber())
        );
    }

    public void sendPhotoToAdmin(String photo, Bot bot) throws TelegramApiException {
        long adminChatId = getAdminChatId();
        bot.sendPhoto(new SendPhoto()
                .setChatId(adminChatId)
                .setPhoto(photo)
        );
    }

    public ReplyKeyboard getAddToSheetKeyboard(Integer id, Long chatId) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton addToGoogleSheetsButton = new InlineKeyboardButton();
        InlineKeyboardButton declineButton = new InlineKeyboardButton();

        String buttonText = buttonDao.getButtonText(52);
        addToGoogleSheetsButton.setText(buttonText);
        addToGoogleSheetsButton.setCallbackData(buttonText + "/" + id);
        row.add(addToGoogleSheetsButton);

        String declineButtonText = buttonDao.getButtonText(53);
        declineButton.setText(declineButtonText);
        declineButton.setCallbackData(declineButtonText + "/" + chatId);
        row.add(declineButton);

        rows.add(row);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    public ReplyKeyboard getAdditionalInfoToKeyboard(Integer memberId, String info1, String info2, int button1, int button2) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton firstButton = new InlineKeyboardButton();
        InlineKeyboardButton secondButton = new InlineKeyboardButton();

        String buttonText = buttonDao.getButtonText(button1);
        firstButton.setText(buttonText);
        firstButton.setCallbackData(buttonText + ":" + info1);
        row.add(firstButton);

        String declineButtonText = buttonDao.getButtonText(button2);
        secondButton.setText(declineButtonText);
        secondButton.setCallbackData(declineButtonText + ":" + info1);
        row.add(secondButton);

        rows.add(row);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    //Клавиатура для голосований по участию в ивентах с счетчиком
    public ReplyKeyboard getKeyBoardForVote(String eventId, String eventTypeToVote, ListDao listDao) throws SQLException {
        InlineKeyboardMarkup keyboard         = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row        = new ArrayList<>();

        switch (eventTypeToVote) {

            case "было":

                InlineKeyboardButton like = new InlineKeyboardButton();
                String likeVotes = listDao.getVotes(eventId, "WILL_GO_USERS_ID");
                like.setText("Лайк \uD83D\uDC4D " + getVoteCount(likeVotes));
                like.setCallbackData("Пойду" + ":" + eventId + "/" + eventTypeToVote);
                row.add(like);
                rows.add(row);

                row = new ArrayList<>();
                String supirVotes = listDao.getVotes(eventId, "MAYBE_USERS_ID");
                InlineKeyboardButton supir = new InlineKeyboardButton();
                supir.setText("Супер! \uD83D\uDE03 " + getVoteCount(supirVotes));
                supir.setCallbackData("Планирую" + ":" + eventId + "/" + eventTypeToVote);
                row.add(supir);
                rows.add(row);

//                row = new ArrayList<>();
//                String hahaVotes = listDao.getVotes(eventId, "NOT_GO_USERS_ID");
//                InlineKeyboardButton haha = new InlineKeyboardButton();
//                haha.setText("Ха ха! \uD83D\uDE02 " + getVoteCount(hahaVotes));
//                haha.setCallbackData("Пропустить" + ":" + eventId + "/" + eventTypeToVote);
//                row.add(haha);
//                rows.add(row);
                break;

            case "будет":
                InlineKeyboardButton will_go = new InlineKeyboardButton();
                String will_go_votes = listDao.getVotes(eventId, "WILL_GO_USERS_ID");
                will_go.setText("Пойду " + getVoteCount(will_go_votes));
                will_go.setCallbackData("Пойду" + ":" + eventId + "/" + eventTypeToVote);
                row.add(will_go);
                rows.add(row);

                row = new ArrayList<>();
                String maybe_go_votes = listDao.getVotes(eventId, "MAYBE_USERS_ID");
                InlineKeyboardButton maybe_go = new InlineKeyboardButton();
                maybe_go.setText("Планирую " + getVoteCount(maybe_go_votes));
                maybe_go.setCallbackData("Планирую" + ":" + eventId + "/" + eventTypeToVote);
                row.add(maybe_go);
                rows.add(row);

//                row = new ArrayList<>();
//                String not_go_votes = listDao.getVotes(eventId, "NOT_GO_USERS_ID");
//                InlineKeyboardButton not_go = new InlineKeyboardButton();
//                not_go.setText("Пропустить " + getVoteCount(not_go_votes));
//                not_go.setCallbackData("Пропустить" + ":" + eventId + "/" + eventTypeToVote);
//                row.add(not_go);
//                rows.add(row);


        }
        keyboard.setKeyboard(rows);
        return keyboard;

    }

    private long getVoteCount(String votes) {
        long count = 0;
        if (votes == null) {
            return count;
        } else {
            for (char element : votes.toCharArray()) {
                if (element == '/') count++;
            }
        }
        return count;
    }

//    public ReplyKeyboard getKeyBoardForVoteWithSomeThings(String eventId, String eventTypeToVote, ListDao listDao) throws SQLException {
//        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
//        List<InlineKeyboardButton> row = new ArrayList<>();
//
//        switch (eventTypeToVote) {
//
//            case "было":
//
//                InlineKeyboardButton like = new InlineKeyboardButton();
//                String likeVotes = listDao.getVotes(eventId, "WILL_GO_USERS_ID");
//                like.setText("Лайк \uD83D\uDC4D \n" + getVoteCount(likeVotes));
//                like.setCallbackData("Пойду" + ":" + eventId + "/" + eventTypeToVote);
//                row.add(like);
//                rows.add(row);
//
//                row = new ArrayList<>();
//                String supirVotes = listDao.getVotes(eventId, "MAYBE_USERS_ID");
//                InlineKeyboardButton supir = new InlineKeyboardButton();
//                supir.setText("Супер! \uD83D\uDE03 \n" + getVoteCount(supirVotes));
//                supir.setCallbackData("Планирую" + ":" + eventId + "/" + eventTypeToVote);
//                row.add(supir);
//                rows.add(row);
//
//                row = new ArrayList<>();
//                String hahaVotes = listDao.getVotes(eventId, "NOT_GO_USERS_ID");
//                InlineKeyboardButton haha = new InlineKeyboardButton();
//                haha.setText("Ха ха! \uD83D\uDE02 \n" + getVoteCount(hahaVotes));
//                haha.setCallbackData("Пропустить" + ":" + eventId + "/" + eventTypeToVote);
//                row.add(haha);
//                rows.add(row);
//
//                row = new ArrayList<>();
//                InlineKeyboardButton next = new InlineKeyboardButton();
//                next.setText(buttonDao.getButtonText(116));
//                next.setCallbackData(buttonDao.getButtonText(116));
//                row.add(next);
//                rows.add(row);
//                break;
////                break;
//
//            case "будет":
//                InlineKeyboardButton will_go = new InlineKeyboardButton();
//                String will_go_votes = listDao.getVotes(eventId, "WILL_GO_USERS_ID");
//                will_go.setText("Пойду \n" + getVoteCount(will_go_votes));
//                will_go.setCallbackData("Пойду" + ":" + eventId + "/" + eventTypeToVote);
//                row.add(will_go);
//                rows.add(row);
//
//                row = new ArrayList<>();
//                String maybe_go_votes = listDao.getVotes(eventId, "MAYBE_USERS_ID");
//                InlineKeyboardButton maybe_go = new InlineKeyboardButton();
//                maybe_go.setText("Планирую \n" + getVoteCount(maybe_go_votes));
//                maybe_go.setCallbackData("Планирую" + ":" + eventId + "/" + eventTypeToVote);
//                row.add(maybe_go);
//                rows.add(row);
//
//                row = new ArrayList<>();
//                String not_go_votes = listDao.getVotes(eventId, "NOT_GO_USERS_ID");
//                InlineKeyboardButton not_go = new InlineKeyboardButton();
//                not_go.setText("Пропустить \n" + getVoteCount(not_go_votes));
//                not_go.setCallbackData("Пропустить" + ":" + eventId + "/" + eventTypeToVote);
//                row.add(not_go);
//                rows.add(row);
//
//                row = new ArrayList<>();
//                InlineKeyboardButton nextInFuture = new InlineKeyboardButton();
//                nextInFuture.setText(buttonDao.getButtonText(116));
//                nextInFuture.setCallbackData(buttonDao.getButtonText(116));
//                row.add(nextInFuture);
//                rows.add(row);
//        }
//        keyboard.setKeyboard(rows);
//        return keyboard;
//
//    }
//
//    private long getVoteCount(String votes) {
//        long count = 0;
//        if (votes == null) {
//            return count;
//        } else {
//            for (char element : votes.toCharArray()) {
//                if (element == '/') count++;
//            }
//        }
//        return count;
//    }

    public ReplyKeyboard getKeyBoardForSearch(String forNextButton, String forButtonWrite) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton buttonNext = new InlineKeyboardButton();
        buttonNext.setText("Следующий");
        buttonNext.setCallbackData("Следующий:" + forNextButton);
        row.add(buttonNext);

        InlineKeyboardButton buttonWrite = new InlineKeyboardButton();
        buttonWrite.setText("Написать");
        buttonWrite.setCallbackData("Написать:" + forButtonWrite);
        row.add(buttonWrite);

        rows.add(row);
        keyboard.setKeyboard(rows);

        return keyboard;
    }

    public ReplyKeyboard getEditDiscountKeys(String discountId) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton changeCar = new InlineKeyboardButton();
        changeCar.setText("Категорию");
        changeCar.setCallbackData("edit_discount_type" + ":" + discountId);
        row.add(changeCar);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton changeName = new InlineKeyboardButton();
        changeName.setText("Изменить название организации");
        changeName.setCallbackData("edit_discount" + ":" + discountId + "/" + "changeName");
        row.add(changeName);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton changeAbout = new InlineKeyboardButton();
        changeAbout.setText("Изменить описание");
        changeAbout.setCallbackData("edit_discount" + ":" + discountId + "/" + "changeAbout");
        row.add(changeAbout);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton changePhoto = new InlineKeyboardButton();
        changePhoto.setText("Изменить фото");
        changePhoto.setCallbackData("edit_discount" + ":" + discountId + "/" + "changePhoto");
        row.add(changePhoto);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton changeAddress = new InlineKeyboardButton();
        changeAddress.setText("Изменить адресс");
        changeAddress.setCallbackData("edit_discount" + ":" + discountId + "/" + "changeAddress");
        row.add(changeAddress);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton changePage = new InlineKeyboardButton();
        changePage.setText("Изменить ссылку на страницу");
        changePage.setCallbackData("edit_discount" + ":" + discountId + "/" + "changePage");
        row.add(changePage);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton changeDiscount = new InlineKeyboardButton();
        changeDiscount.setText("Изменить discount");
        changeDiscount.setCallbackData("edit_discount" + ":" + discountId + "/" + "changeDiscount");
        row.add(changeDiscount);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton accept = new InlineKeyboardButton();
        accept.setText("Опубликовать");
        accept.setCallbackData("solution_for_discount_from_admin" + ":" + discountId + "/" + "accept");
        row.add(accept);
        rows.add(row);

        return keyboard.setKeyboard(rows);
    }

    public void makeNewMessageDiscountForAdminEdit(Bot bot, String discountId, ListDao listDao, long chatId) throws SQLException, TelegramApiException {
        Discount discount = listDao.getDiscountById(discountId);
        ReplyKeyboard keyboard = getEditDiscountKeys(discountId);
        SendMessage sendMessageToAdmin = new SendMessage().setText(messageDao.getMessage(106).getSendMessage()
                .getText().replaceAll("discount_type", discount.getDiscountType())
                .replaceAll("company_name", discount.getName())
                .replaceAll("text_about", discount.getTextAbout())
                .replaceAll("address", discount.getAddress())
                .replaceAll("page", discount.getPage())
                .replaceAll("discount", discount.getDiscount())).setChatId(chatId).setParseMode(ParseMode.HTML)
                .setReplyMarkup(keyboard);
        SendMessage sendMessageSuccess = new SendMessage().setChatId(chatId).setText("Изменения сохранены");
        String photo = discount.getPhoto();
        if (photo != null) {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setPhoto(discount.getPhoto());
            bot.sendPhoto(sendPhoto.setChatId(getAdminChatId()));
        }
        bot.sendMessage(sendMessageToAdmin);
        bot.sendMessage(sendMessageSuccess);
    }

    public String getMonthInRussian(int month) {
        String string = null;

        switch (month){
            case 1:
                string = "января";
                break;
            case 2:
                string = "февраля";
                break;
            case 3:
                string = "марта";
                break;
            case 4:
                string = "апреля";
                break;
            case 5:
                string = "мая";
                break;
            case 6:
                string = "июня";
                break;
            case 7:
                string = "июля";
                break;
            case 8:
                string = "августа";
                break;
            case 9:
                string = "сентября";
                break;
            case 10:
                string = "октября";
                break;
            case 11:
                string = "ноября";
                break;
            case 12:
                string = "декабря";
                break;
        }

        return string;
    }

    public ReplyKeyboard getVacancyViaButtons(ArrayList<Vacancy> arrayList){
        InlineKeyboardMarkup keyboard         = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row;


        for(Vacancy vacancy: arrayList) {
            row        = new ArrayList<>();
            InlineKeyboardButton vacancyButton = new InlineKeyboardButton();
            vacancyButton.setText(vacancy.getCompanyName()+ ". Кто нужен: "+ vacancy.getSfera() + " ("+ vacancy.getSalary() +")");
            vacancyButton.setCallbackData("get_vacancy" + ":" + vacancy.getId());
            row.add(vacancyButton);
            rows.add(row);

        }
        keyboard.setKeyboard(rows);
        return keyboard;
    }
}