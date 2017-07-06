package com.turlygazhy;

//import com.turlygazhy.reminder.Reminder;
import com.turlygazhy.reminder.Reminder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by Yerassyl_Turlygazhy on 11/24/2016.
 */
public class Main {
    private static Reminder reminder;
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args)  {

        logger.info("ApiContextInitializer.init()");
        ApiContextInitializer.init();

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            Bot bot           = new Bot();
            reminder = new Reminder(bot);
            telegramBotsApi.registerBot(bot);
            logger.info("Bot was registered");

        } catch (TelegramApiRequestException e) {
            throw new RuntimeException(e);

        }
    }

    public static Reminder getReminder() {
        return reminder;
    }
}
