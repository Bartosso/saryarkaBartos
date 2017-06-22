package com.turlygazhy;

//import com.turlygazhy.reminder.Reminder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

/**
 * Created by Yerassyl_Turlygazhy on 11/24/2016.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("ApiContextInitializer.init()");
        ApiContextInitializer.init();

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            Bot bot = new Bot();
            telegramBotsApi.registerBot(bot);
            logger.info("Bot was registered");

        } catch (TelegramApiRequestException e) {
            throw new RuntimeException(e);
        }

    }
}
