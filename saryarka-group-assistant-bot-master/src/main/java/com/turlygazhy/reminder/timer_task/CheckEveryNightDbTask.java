package com.turlygazhy.reminder.timer_task;

import com.turlygazhy.Bot;
import com.turlygazhy.dao.impl.ListDao;
import com.turlygazhy.entity.Event;
import com.turlygazhy.entity.ListData;
import com.turlygazhy.reminder.Reminder;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Eshu on 23.06.2017.
 */
public class CheckEveryNightDbTask extends AbstractTask {
    public CheckEveryNightDbTask(Bot bot, Reminder reminder) {
        super(bot, reminder);
    }

    @Override
    public void run() {
        try {
            checkEvents();
            checkTenders();
            reminder.setCheckEveryNightDb(0);
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }

    }

    private void checkTenders() throws SQLException, ParseException{
        ListDao requestsList            = factory.getListDao("REQUESTS_LIST");
        ListDao offerList               = factory.getListDao("OFFER_LIST");
        ArrayList<ListData> requests    = requestsList.getAllFromList(true);
        ArrayList<ListData> offers      = offerList.getAllFromList(true);
        if(!requests.isEmpty()){
            Date now                    = new Date();
            Date dateIn;
            Date dateOut;
            SimpleDateFormat format     = new SimpleDateFormat();
            format.applyPattern("dd/MM/yyyy");
            for(ListData request: requests){
                dateIn = format.parse(request.getDate());
                LocalDateTime localDateTime = LocalDateTime.ofInstant(dateIn.toInstant(), ZoneId.systemDefault());
                localDateTime               = localDateTime.plusDays(7);
                dateOut                     = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                if(dateOut.before(now)){
                    requestsList.deleteListById(request.getId());
                }
            }
        }
        if(!offers.isEmpty()){
            Date now                    = new Date();
            Date dateIn;
            Date dateOut;
            SimpleDateFormat format     = new SimpleDateFormat();
            format.applyPattern("dd/MM/yyyy");
            for(ListData offer: offers){
                dateIn = format.parse(offer.getDate());
                LocalDateTime localDateTime = LocalDateTime.ofInstant(dateIn.toInstant(), ZoneId.systemDefault());
                localDateTime               = localDateTime.plusDays(7);
                dateOut                     = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                if(dateOut.before(now)){
                    offerList.deleteListById(offer.getId());
                }
            }
        }
    }

    private void checkEvents() throws SQLException, ParseException {
        ListDao notEndedEvents          = factory.getListDao("EVENTS_LIST");
        ListDao endedEvents             = factory.getListDao("ENDED_EVENTS_LIST");
        ArrayList<Event> eventArrayList = notEndedEvents.getAllEvents(true);
        if(!eventArrayList.isEmpty()){
            Date now                    = new Date();
            Date eventDate;
            SimpleDateFormat format     = new SimpleDateFormat();
            format.applyPattern("dd.MM.yy");
            for(Event event : eventArrayList){
                eventDate = format.parse(event.getWHEN());
                if(eventDate.before(now)){
                    notEndedEvents.delete(String.valueOf(event.getId()));
//                    notEndedEvents.declineEvent(String.valueOf(event.getId()));
//                    endedEvents.addEndedEvent(event);
                }
            }
        }

    }
}
