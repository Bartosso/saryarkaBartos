package com.turlygazhy.reminder.timer_task;

import com.turlygazhy.Bot;
import com.turlygazhy.dao.impl.Top10Dao;
import com.turlygazhy.entity.Top10;
import com.turlygazhy.reminder.Reminder;
import com.turlygazhy.tool.DateUtil;
import com.turlygazhy.tool.vanderkastTools.Charts.PieChartConstructor;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;


public class EndOfMonthTask extends AbstractTask {
    public EndOfMonthTask(Bot bot, Reminder reminder) {
        super(bot, reminder);
    }
    //todo set dis shit for whatever you want
    private final String pathToTempFolder = "C:/bots-data/";

    @Override
    public void run() {
        try {
            getPieWithOffers();
            getPieWithRequests();
//            reminder.setEndOfMonthTask(DateUtil.getNextMonthEnd());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }




    private void getPieWithOffers() throws SQLException, IOException {
        Top10Dao top10Dao = factory.getTop10Dao("TOP10_OFFERS");
        ArrayList<Top10>   top10ArrayList = top10Dao.getTop10();
        if(top10ArrayList.size()==0){
            try {
                bot.execute(new SendMessage(factory.getUserDao().getAdminChatId(),"К сожалению в этом месяце не было размещено ни одного предложения"));
                return;
            } catch (TelegramApiException e) {
                e.printStackTrace();
                return;
            }
        }
        ArrayList<String>  seriesName     = new ArrayList<>();
        ArrayList<Integer> seriesData     = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            if(i==top10ArrayList.size()){
                break;
            }
        seriesName.add(top10ArrayList.get(i).getText());
        seriesData.add(top10ArrayList.get(i).getCount());
        }
        PieChartConstructor pieChartConstructor = new PieChartConstructor(pathToTempFolder,"Топ 10 предложений за месяц",seriesName,
                seriesData, 800,600);
        new File(pieChartConstructor.getPieChartConstructed());
        String filePath = pieChartConstructor.getPath() + ".jpg";
        File file = new File(filePath);
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            bot.sendPhoto(new SendPhoto()
                    .setChatId(factory.getUserDao().getAdminChatId())
                    .setNewPhoto("photo", fileInputStream)
            );
        } catch (TelegramApiException ignored) {
        }
        fileInputStream.close();
        Path path = Paths.get(filePath);
        Files.delete(path);
        top10Dao.truncateTop();
    }


    private void getPieWithRequests() throws SQLException, IOException {
        Top10Dao top10Dao = factory.getTop10Dao("TOP10_REQUESTS");
        ArrayList<Top10>   top10ArrayList = top10Dao.getTop10();
        if(top10ArrayList.size()==0){
            try {
                bot.execute(new SendMessage(factory.getUserDao().getAdminChatId(),"К сожалению в этом месяце небыло размещено ни одного запроса"));
                return;
            } catch (TelegramApiException e) {
                e.printStackTrace();
                return;
            }
        }
        ArrayList<String>  seriesName     = new ArrayList<>();
        ArrayList<Integer> seriesData     = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            if(i==top10ArrayList.size()){
                break;
            }
            seriesName.add(top10ArrayList.get(i).getText());
            seriesData.add(top10ArrayList.get(i).getCount());
        }
        PieChartConstructor pieChartConstructor = new PieChartConstructor(pathToTempFolder,"Топ 10 запросов за месяц",seriesName,
                seriesData, 800,600);
        new File(pieChartConstructor.getPieChartConstructed());
        String filePath = pieChartConstructor.getPath() + ".jpg";
        File file = new File(filePath);
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            bot.sendPhoto(new SendPhoto()
                    .setChatId(factory.getUserDao().getAdminChatId())
                    .setNewPhoto("photo", fileInputStream)
            );
        } catch (TelegramApiException ignored) {
        }
        fileInputStream.close();
        Path path = Paths.get(filePath);
        Files.delete(path);
        top10Dao.truncateTop();
    }
}
