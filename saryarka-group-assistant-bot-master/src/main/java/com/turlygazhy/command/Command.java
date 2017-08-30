package com.turlygazhy.command;

import com.turlygazhy.Bot;
import com.turlygazhy.Main;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.*;
import com.turlygazhy.entity.*;
import com.turlygazhy.tool.EventAnonceUtil;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendContact;
import org.telegram.telegrambots.api.methods.send.SendDocument;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Yerassyl_Turlygazhy on 11/27/2016.
 */
@SuppressWarnings("unused")
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
        bot.execute(sendMessage);
        if (contact != null) {
            bot.execute(new SendContact()
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

    @SuppressWarnings("SameParameterValue")
    protected void sendMessageToAdmin(long messageId, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
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

    protected void sendMessageToAdmin(String text, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
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

    protected void sendPhotoToAdmin(String photo, Bot bot) throws TelegramApiException {
        long adminChatId = getAdminChatId();
        bot.sendPhoto(new SendPhoto()
                .setChatId(adminChatId)
                .setPhoto(photo)
        );
    }

    protected ReplyKeyboard getAddToSheetKeyboard(Integer id, Long chatId, String userName) throws SQLException {
        InlineKeyboardMarkup keyboard         = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row               = new ArrayList<>();
        InlineKeyboardButton addToGoogleSheetsButton = new InlineKeyboardButton();
        InlineKeyboardButton declineButton           = new InlineKeyboardButton();
        InlineKeyboardButton editBidButton           = new InlineKeyboardButton();
        InlineKeyboardButton openChatWithMember      = new InlineKeyboardButton();
        InlineKeyboardButton addToArchiveButton      = new InlineKeyboardButton();

        String buttonText = buttonDao.getButtonText(52);
        addToGoogleSheetsButton.setText(buttonText);
        addToGoogleSheetsButton.setCallbackData(buttonText + "/" + id);
        row.add(addToGoogleSheetsButton);

        String declineButtonText = buttonDao.getButtonText(53);
        declineButton.setText(declineButtonText);
        declineButton.setCallbackData(declineButtonText + "/" + chatId);
        row.add(declineButton);
        rows.add(row);

        row = new ArrayList<>();

        String editCollectedInfoText = buttonDao.getButtonText(218);
        editBidButton.setText(editCollectedInfoText);
        editBidButton.setCallbackData("editBid:" + chatId);
        row.add(editBidButton);
        rows.add(row);

        row = new ArrayList<>();

        String openChatWithMemberText = buttonDao.getButtonText(238);
        openChatWithMember.setText(openChatWithMemberText);
        openChatWithMember.setUrl("t.me/"+ userName);
        row.add(openChatWithMember);
        rows.add(row);

        row = new ArrayList<>();

        String addToArchiveText = buttonDao.getButtonText(244);
        addToArchiveButton.setText(addToArchiveText);
        addToArchiveButton.setCallbackData(addToArchiveText+"/" + id);
        row.add(addToArchiveButton);
        rows.add(row);



        keyboard.setKeyboard(rows);
        return keyboard;
    }

    protected ReplyKeyboard getAdminKeyboardForEvent(String eventId) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton acceptEventButton = new InlineKeyboardButton();
        InlineKeyboardButton rejectEventButton = new InlineKeyboardButton();
        InlineKeyboardButton editEventButton   = new InlineKeyboardButton();

        acceptEventButton.setText(buttonDao.getButtonText(90));
        acceptEventButton.setCallbackData("acceptEvent" + ":" + eventId);
        row.add(acceptEventButton);

        rejectEventButton.setText(buttonDao.getButtonText(91));
        rejectEventButton.setCallbackData("declineEvent" + ":" + eventId);
        row.add(rejectEventButton);
        rows.add(row);

        row = new ArrayList<>();
        editEventButton.setText(buttonDao.getButtonText(163));
        editEventButton.setCallbackData("editEvent"+ ":" + eventId);
        row.add(editEventButton);



        rows.add(row);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    //Клавиатура для голосований по участию в ивентах с счетчиком
    protected ReplyKeyboard getKeyBoardForVote(long eventId, String eventTypeToVote, ListDao listDao) throws SQLException {
        InlineKeyboardMarkup keyboard         = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row        = new ArrayList<>();

        switch (eventTypeToVote) {

            case "было":

                InlineKeyboardButton like = new InlineKeyboardButton();
                String likeVotes = listDao.getVotes(String.valueOf(eventId), "WILL_GO_USERS_ID");
                like.setText("Лайк \uD83D\uDC4D " + getVoteCount(likeVotes));
                like.setCallbackData("Пойду" + ":" + eventId + "/" + eventTypeToVote);
                row.add(like);
                rows.add(row);

                row = new ArrayList<>();
                String supirVotes = listDao.getVotes(String.valueOf(eventId), "MAYBE_USERS_ID");
                InlineKeyboardButton supir = new InlineKeyboardButton();
                supir.setText("Супер! \uD83D\uDE03 " + getVoteCount(supirVotes));
                supir.setCallbackData("Планирую" + ":" + eventId + "/" + eventTypeToVote);
                row.add(supir);
                rows.add(row);


                break;

            case "будет":
                InlineKeyboardButton will_go = new InlineKeyboardButton();
                String will_go_votes = listDao.getVotes(String.valueOf(eventId), "WILL_GO_USERS_ID");
                will_go.setText("Пойду " + getVoteCount(will_go_votes));
                will_go.setCallbackData("Пойду" + ":" + eventId + "/" + eventTypeToVote);
                row.add(will_go);
                rows.add(row);

                row = new ArrayList<>();
                String maybe_go_votes = listDao.getVotes(String.valueOf(eventId), "MAYBE_USERS_ID");
                InlineKeyboardButton maybe_go = new InlineKeyboardButton();
                maybe_go.setText("Планирую " + getVoteCount(maybe_go_votes));
                maybe_go.setCallbackData("Планирую" + ":" + eventId + "/" + eventTypeToVote);
                row.add(maybe_go);
                rows.add(row);





        }
        keyboard.setKeyboard(rows);
        return keyboard;

    }

    @SuppressWarnings("Duplicates")
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


    protected ReplyKeyboard getKeyBoardForSearch(String forNextButton, String forButtonWrite) {
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

    private ReplyKeyboard getEditDiscountKeys(String discountId) throws SQLException {
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

    protected void makeNewMessageDiscountForAdminEdit(Bot bot, String discountId, ListDao listDao, long chatId) throws SQLException, TelegramApiException {
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

    protected ReplyKeyboard getVacancyViaButtons(ArrayList<Vacancy> arrayList){
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

    protected void createRemind(Bot bot, long eventId, String when, long chatId) throws ParseException, SQLException, TelegramApiException {
        Date now                 = new Date();
        SimpleDateFormat format  = new SimpleDateFormat();
        format.applyPattern("dd.MM.yy");
        Date eventDate           = format.parse(when);
        LocalDateTime eventLocal = LocalDateTime.ofInstant(eventDate.toInstant(), ZoneId.systemDefault());
        Date dateEventMinusDay   = Date.from(eventLocal.minusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        Date dateEventMinusHour  = Date.from(eventLocal.minusHours(1).atZone(ZoneId.systemDefault()).toInstant());
//        if (now.before(dateEventMinusDay)) {
//            Main.getReminder().setRemindEventStartOneDay(dateEventMinusDay, eventId);
//            Main.getReminder().setRemindEventsStartOneHour(dateEventMinusHour, eventId);
//        } else {
            if (now.before(dateEventMinusHour)){
//                Main.getReminder().setRemindEventsStartOneHour(dateEventMinusHour, eventId);
                Main.getReminder().setRemindEventStartOneDay(dateEventMinusDay, eventId);
            }
            else {
                bot.sendMessage(messageDao.getMessage(149).getSendMessage().setChatId(chatId));
            }

//        }
    }

    protected void sendNewMessageForEditOfferTender(Bot bot, String tenderId, ListDao listDao, long chatId) throws TelegramApiException, SQLException {
        ListData offer = listDao.getListDataById(tenderId);
        ReplyKeyboard keyboard = getKeyboardForEditOfferTender(Integer.parseInt(tenderId));
        SendMessage sendMessageToAdmin = new SendMessage().setText(messageDao.getMessage(154).getSendMessage()
                .getText()
                .replaceAll("tender_type_name", "Список предложений")
                .replaceAll("tender_type_word", "предлагает")
                .replaceAll("tender_text"     , offer.getText())
                .replaceAll("tender_member", "@"+memberDao.getMemberById(offer.getMemberId()).getUserName()))
                .setReplyMarkup(keyboard)
                .setChatId(chatId);
        SendMessage sendMessageSuccess = new SendMessage().setChatId(chatId).setText("Изменения сохранены");
        bot.sendMessage(sendMessageToAdmin);
        bot.sendMessage(sendMessageSuccess);
    }

    protected ReplyKeyboard getKeyboardForEditOfferTender(int tenderId) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton changeTenderType = new InlineKeyboardButton();
        changeTenderType.setText(buttonDao.getButtonText(150));
        changeTenderType.setCallbackData("moveOfferToRequest" + ":" + tenderId);
        row.add(changeTenderType);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton changeTenderText = new InlineKeyboardButton();
        changeTenderText.setText(buttonDao.getButtonText(149));
        changeTenderText.setCallbackData("changeOfferText" + ":" + tenderId);
        row.add(changeTenderText);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton makeOfferTenderBe = new InlineKeyboardButton();
        makeOfferTenderBe.setText(buttonDao.getButtonText(141));
        makeOfferTenderBe.setCallbackData("acceptOfferTender" + ":" + tenderId);
        row.add(makeOfferTenderBe);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton rejectOfferTender = new InlineKeyboardButton();
        rejectOfferTender.setText(buttonDao.getButtonText(142));
        rejectOfferTender.setCallbackData("rejectOfferTender" + ":" + tenderId);
        row.add(rejectOfferTender);

        rows.add(row);
        return keyboard.setKeyboard(rows);

    }

    protected void sendNewMessageForEditRequestTender(Bot bot, String tenderId, ListDao listDao, long chatId) throws TelegramApiException, SQLException {
        ListData offer = listDao.getListDataById(tenderId);
        ReplyKeyboard keyboard = getKeyboardForEditRequestTender(Integer.parseInt(tenderId));
        SendMessage sendMessageToAdmin = new SendMessage().setText(messageDao.getMessage(154).getSendMessage()
                .getText()
                .replaceAll("tender_type_name", "Список запросов")
                .replaceAll("tender_type_word", "ищет")
                .replaceAll("tender_text"     , offer.getText())
                .replaceAll("tender_member", "@"+memberDao.getMemberById(offer.getMemberId()).getUserName()))
                .setReplyMarkup(keyboard)
                .setChatId(chatId);
        SendMessage sendMessageSuccess = new SendMessage().setChatId(chatId).setText("Изменения сохранены");
        bot.sendMessage(sendMessageToAdmin);
        bot.sendMessage(sendMessageSuccess);
    }

    protected ReplyKeyboard getKeyboardForEditRequestTender(int tenderId) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton changeTenderType = new InlineKeyboardButton();
        changeTenderType.setText(buttonDao.getButtonText(152));
        changeTenderType.setCallbackData("moveRequestToOffer" + ":" + tenderId);
        row.add(changeTenderType);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton changeTenderText = new InlineKeyboardButton();
        changeTenderText.setText(buttonDao.getButtonText(151));
        changeTenderText.setCallbackData("changeRequestText" + ":" + tenderId);
        row.add(changeTenderText);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton makRequestTenderBe = new InlineKeyboardButton();
        makRequestTenderBe.setText(buttonDao.getButtonText(139));
        makRequestTenderBe.setCallbackData("acceptRequestTender" + ":" + tenderId);
        row.add(makRequestTenderBe);
        rows.add(row);
        row = new ArrayList<>();

        InlineKeyboardButton rejectRequestTender = new InlineKeyboardButton();
        rejectRequestTender.setText(buttonDao.getButtonText(140));
        rejectRequestTender.setCallbackData("rejectRequestTender" + ":" + tenderId);
        row.add(rejectRequestTender);

        rows.add(row);
        return keyboard.setKeyboard(rows);

    }

    private ReplyKeyboard getKeyboardForEditEvent(String eventId, boolean isEnded) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        String editEventNameCallback  = "editEventName";
        String editEventPlaceCallback = "editEventPlace";
        String editEventWhenCallback  = "editEventWhen";
        String acceptEventCallback    = "acceptEvent";
        String rejectEventCallback    = "declineEvent";
        if(isEnded){
            editEventNameCallback  = "editEndedEventName";
            editEventPlaceCallback = "editEndedEventPlace";
            editEventWhenCallback  = "editEndedEventWhen";
            acceptEventCallback    = "acceptEndedEvent";
            rejectEventCallback    = "declineEndedEvent";
        }

        InlineKeyboardButton editEventName     = new InlineKeyboardButton(buttonDao.getButtonText(167));
        editEventName.setCallbackData(editEventNameCallback+":" + eventId);
        row.add(editEventName);
        rows.add(row);

        row = new ArrayList<>();
        InlineKeyboardButton editEventPlace     = new InlineKeyboardButton(buttonDao.getButtonText(169));
        editEventPlace.setCallbackData(editEventPlaceCallback+ ":" + eventId);
        row.add(editEventPlace);
        rows.add(row);

        row = new ArrayList<>();
        InlineKeyboardButton editEventWhen      = new InlineKeyboardButton(buttonDao.getButtonText(171));
        editEventWhen.setCallbackData(editEventWhenCallback+ ":" + eventId);
        row.add(editEventWhen);
        rows.add(row);

//        row = new ArrayList<>();
//        InlineKeyboardButton editEventContact   = new InlineKeyboardButton(buttonDao.getButtonText(173));
//        editEventContact.setCallbackData("editEventCont" + ":" + eventId);
//        row.add(editEventContact);
//        rows.add(row);
//
//        row = new ArrayList<>();
//        InlineKeyboardButton editEventPhoto     = new InlineKeyboardButton(buttonDao.getButtonText(175));
//        editEventPhoto.setCallbackData("editEventPhoto" + ":" + eventId);
//        row.add(editEventPhoto);
//        rows.add(row);
//
//        row = new ArrayList<>();
//        InlineKeyboardButton editEventRules     = new InlineKeyboardButton(buttonDao.getButtonText(177));
//        editEventRules.setCallbackData("editEventRules" + ":" + eventId);
//        row.add(editEventRules);
//        rows.add(row);
//
//        row = new ArrayList<>();
//        InlineKeyboardButton editEventDressCode = new InlineKeyboardButton(buttonDao.getButtonText(179));
//        editEventDressCode.setCallbackData("editEventDcode" + ":" + eventId);
//        row.add(editEventDressCode);
//        rows.add(row);
//
//        row = new ArrayList<>();
//        InlineKeyboardButton editEventProgram   = new InlineKeyboardButton(buttonDao.getButtonText(181));
//        editEventProgram.setCallbackData("editEventPrgm" + ":" + eventId);
//        row.add(editEventProgram);
//        rows.add(row);
//
//        row = new ArrayList<>();
//        InlineKeyboardButton editEventPage       = new InlineKeyboardButton(buttonDao.getButtonText(183));
//        editEventPage.setCallbackData("editEventPage" + ":" + eventId);
//        row.add(editEventPage);
//        rows.add(row);
//
//        row = new ArrayList<>();
//        InlineKeyboardButton editEventDocument   = new InlineKeyboardButton(buttonDao.getButtonText(185));
//        editEventDocument.setCallbackData("editEventDoc" + ":" + eventId);
//        row.add(editEventDocument);
//        rows.add(row);
//
        row = new ArrayList<>();
        InlineKeyboardButton acceptEventButton   = new InlineKeyboardButton(buttonDao.getButtonText(90));
        acceptEventButton.setCallbackData(acceptEventCallback + ":" + eventId);
        row.add(acceptEventButton);

        InlineKeyboardButton rejectEventButton   = new InlineKeyboardButton(buttonDao.getButtonText(91));
        rejectEventButton.setCallbackData(rejectEventCallback + ":" + eventId);
        row.add(rejectEventButton);
        rows.add(row);



        keyboard.setKeyboard(rows);
return keyboard;
    }


    protected void sendMessageForEditEventToAdmin(Bot bot, String eventId, ListDao listDao, long chatId) throws SQLException, TelegramApiException {
        Event event = listDao.getEvent(eventId);
        String photo = event.getPHOTO();
        SendMessage sendEditMessage = new SendMessage(chatId, EventAnonceUtil.getEventWithPatternNoByAdmin(event,messageDao)).setReplyMarkup(getKeyboardForEditEvent(eventId,false))
                .setParseMode(ParseMode.HTML);

        if (event.getPHOTO()!= null) {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setPhoto(photo);
            bot.sendPhoto(sendPhoto.setChatId(chatId));
        }
        bot.sendMessage(sendEditMessage);
        if(event.getDOCUMENT() != null){
            SendDocument sendDocument = new SendDocument();
            sendDocument.setDocument(event.getDOCUMENT());
            bot.sendDocument(sendDocument.setChatId(chatId));
        }

    }


    protected void sendMessageForEditEndedEventToAdmin(Bot bot, String eventId, ListDao listDao, long chatId) throws SQLException, TelegramApiException {
        Event event = listDao.getEvent(eventId);
        String photo = event.getPHOTO();
        SendMessage sendEditMessage = new SendMessage(chatId, EventAnonceUtil.getEventWithPatternNoByAdmin(event,messageDao)).setReplyMarkup(getKeyboardForEditEvent(eventId,true))
                .setParseMode(ParseMode.HTML);

        if (event.getPHOTO()!= null) {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setPhoto(photo);
            bot.sendPhoto(sendPhoto.setChatId(chatId));
        }
        bot.sendMessage(sendEditMessage);
        if(event.getDOCUMENT() != null){
            SendDocument sendDocument = new SendDocument();
            sendDocument.setDocument(event.getDOCUMENT());
            bot.sendDocument(sendDocument.setChatId(chatId));
        }

    }


}