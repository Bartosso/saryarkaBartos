package com.turlygazhy.service;

import com.turlygazhy.Bot;
import com.turlygazhy.dao.DaoFactory;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;

public class FloodFilterService {
    public void floodFilter(Long chatIdForFloodCount, Update update, Bot bot, ConcurrentHashMap<Long, Integer> floodCount, DaoFactory factory)  {
        try {
            String userStatus =  bot.execute(new GetChatMember().setChatId(bot.getGROUP_FOR_VOTE()).setUserId(Math.toIntExact(chatIdForFloodCount)))
                    .getStatus();
            if (userStatus.equals("member")){
                if (floodCount.containsKey(chatIdForFloodCount)){
                    Integer count = floodCount.get(chatIdForFloodCount);
                    if (count>2){
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.HOUR_OF_DAY, 1);
                        Integer endOfBanInUnix = Math.toIntExact(cal.getTimeInMillis() / 1000);
                        RestrictChatMember restrictChatMember = new RestrictChatMember(bot.getGROUP_FOR_VOTE(), Math.toIntExact(chatIdForFloodCount))
                                .setCanAddWebPagePreviews(false).setCanSendMediaMessages(false).setCanSendMessages(false)
                                .setCanSendOtherMessages(false).setUntilDate(endOfBanInUnix);
                        String username = "@" + update.getMessage().getFrom().getUserName();
                        if (username.equals("@null")){
                            username = update.getMessage().getFrom().getFirstName();
                        }
                        try {
                            bot.execute(restrictChatMember);
                            bot.execute(new SendMessage(chatIdForFloodCount, username + factory.getMessageDao().getMessage(179).getSendMessage().getText()));
                        } catch (SQLException | TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else {
                        count++;
                        floodCount.replace(chatIdForFloodCount,count);
                    }
                } else {
                    floodCount.put(chatIdForFloodCount, 1);
                    bot.createNewTimerForFloodCountByChatId(chatIdForFloodCount);
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void textFilter(Update update, Bot bot) throws TelegramApiException {
        long userChatId = update.getMessage().getFrom().getId();
        String userStatus =  bot.execute(new GetChatMember().setChatId(bot.getGROUP_FOR_VOTE()).setUserId(Math.toIntExact(userChatId)))
                .getStatus();
        if (userStatus.equals("member")||userStatus.equals("restricted")||userStatus.equals("left")
                ||userStatus.equals("kicked")){
                   if (update.getMessage().hasDocument() || update.getMessage().hasPhoto()
                    || update.getMessage().hasLocation()
                    || update.getMessage().getVoice()     != null || update.getMessage().getVideo() != null
                    || update.getMessage().getVideoNote() != null || update.getMessage().getAudio() != null
                    || update.getMessage().getVenue()     != null){
                bot.execute(new DeleteMessage(update.getMessage().getChatId(),update.getMessage().getMessageId()));
            }
        }
    }
}
