package com.turlygazhy.dao.impl;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.turlygazhy.entity.Discount;
import com.turlygazhy.entity.Event;
import com.turlygazhy.entity.ListData;
import com.turlygazhy.entity.Message;
import org.h2.jdbc.JdbcSQLException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 12/26/16.
 */
public class ListDao {
    private final Connection connection;
    private final String listName;

    public ListDao(Connection connection, String listName) {
        this.connection = connection;
        this.listName = listName;
    }

    public void insert(String photo, String text) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO PUBLIC." + listName + "(ID, PHOTO, TEXT) VALUES (DEFAULT, ?,? )");
        ps.setString(1, photo);
        ps.setString(2, text);
        ps.execute();
    }

    public void insertIntoLists(String photo, String text, String member_id, String date_post) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO PUBLIC." + listName + "( MEMBER_ID, PHOTO, TEXT, date_post) VALUES ( ?,?,?,? )");
        ps.setString(1, member_id);
        ps.setString(2, photo);
        ps.setString(3, text);
        ps.setString(4, date_post);
        ps.execute();
    }
    public ArrayList<ListData> getAllFromList() throws  SQLException{
        ArrayList<ListData> listDataArrayList = new ArrayList<>();
        ListData listData;
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC." + listName);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        while (resultSet.next()) {
            listData = new ListData();
            listData.setId(resultSet.getLong(     1));
            listData.setMemberId(resultSet.getInt(2));
            listData.setPhoto(resultSet.getString(3));
            listData.setText(resultSet.getString( 4));
            listData.setDate(resultSet.getString( 5));
            listDataArrayList.add(listData);
        }
        return listDataArrayList;

    }

    public ArrayList<ListData> getAllListDataByMemberId(String memberId) throws SQLException{
        ArrayList<ListData> listDataArrayList = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC." + listName + " WHERE MEMBER_ID=?");
        ps.setString(1,memberId);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        while (resultSet.next()){
            ListData listData = new ListData();
            listData.setId(resultSet.getLong(     1));
            listData.setMemberId(resultSet.getInt(2));
            listData.setPhoto(resultSet.getString(3));
            listData.setText(resultSet.getString( 4));
            listData.setDate(resultSet.getString( 5));
            listDataArrayList.add(listData);
        }
        return listDataArrayList;
    }

    public ListData getListDataById(String listDataId) throws SQLException{
        ListData listData = new ListData();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + listName + " where id="+ listDataId );
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        resultSet.next();
        listData.setId(resultSet.getLong(     1));
        listData.setMemberId(resultSet.getInt(2));
        listData.setPhoto(resultSet.getString(3));
        listData.setText(resultSet.getString( 4));
        listData.setDate(resultSet.getString( 5));
    return listData;
    }

    public ArrayList<Event> getAllEvents(boolean ADMIN_ACKNOWLEDGE) throws SQLException{
        ArrayList<Event> events = new ArrayList<>();
        Event event;
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC." + listName + " where ADMIN_ACKNOWLEDGE=?" );
        ps.setBoolean(1,ADMIN_ACKNOWLEDGE);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        while (resultSet.next()) {
            event = new Event();
            event.setId(resultSet.getLong(                   1));
            event.setEVENT_NAME(resultSet.getString(         2));
            event.setPLACE(resultSet.getString(              3));
            event.setWHEN(resultSet.getString(               4));
            event.setRULES(resultSet.getString(              5));
            event.setCONTACT_INFORMATION(resultSet.getString(6));
            event.setPHOTO(resultSet.getString(              7));
            event.setVIDEO(resultSet.getString(              8));
            events.add(event);
        }
        return events;
    }

    public void createNewEvent(String eventName, String where, String date, String rules, String photo, String video, String contactInformatin, boolean ADMIN_ACKNOWLEDGE) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO PUBLIC." + listName + "(EVENT_NAME, PLACE, WHEN, RULES,CONTACT_INFORMATION, PHOTO, VIDEO, ADMIN_ACKNOWLEDGE) VALUES ( ?,?,?,?,?,?,?,?)");
        ps.setString( 1, eventName);
        ps.setString( 2, where);
        ps.setString( 3,   date);
        ps.setString( 4, rules);
        ps.setString( 5, contactInformatin);
        ps.setString( 6, photo);
        ps.setString( 7, video);
        ps.setBoolean(8, ADMIN_ACKNOWLEDGE);
        ps.execute();
    }
    public void voteEvent(String eventId, String userId, String chose) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE " + listName + " set "+ chose +" = CONCAT("+ chose +", '"+ userId +"', '/') WHERE ID= '"+ eventId +"' ");
            ps.execute();
    }

    public List<String> getEvents(String eventsTableName) throws SQLException {
        List<String> stringList = new ArrayList<>();
        String string;
        PreparedStatement ps = connection.prepareStatement("SELECT EVENT_NAME FROM PUBLIC." + eventsTableName);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        while (resultSet.next()){
            string = new String();
            string = resultSet.getString(1);
            stringList.add(string);
        }
        return stringList;
    }

    public long getEventId(String eventName,String where, String date, String rules, boolean adminSaysYes) throws SQLException {
        long eventId = 0;
        PreparedStatement ps = connection.prepareStatement("SELECT id from public."+ listName +" where EVENT_NAME=? and " +
                "PLACE=? and WHEN=? and RULES=? and ADMIN_ACKNOWLEDGE=?"  );
        ps.setString( 1,eventName);
        ps.setString( 2,where);
        ps.setString( 3,date);
        ps.setString( 4,rules);
        ps.setBoolean(5,adminSaysYes);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        if(resultSet.next()){
            eventId = resultSet.getLong(1);
        }
        return eventId;
     }
     public Event getEvent(String eventId)  throws SQLException {
        Event event = new Event();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC." + listName + " where ID="+ eventId);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        try {
            resultSet.next();
            event.setId(resultSet.getLong(                   1));
            event.setEVENT_NAME(resultSet.getString(         2));
            event.setPLACE(resultSet.getString(              3));
            event.setWHEN(resultSet.getString(               4));
            event.setRULES(resultSet.getString(              5));
            event.setCONTACT_INFORMATION(resultSet.getString(6));
            event.setPHOTO(resultSet.getString(              7));
            event.setVIDEO(resultSet.getString(              8));
        } catch (JdbcSQLException e){
            return null;
        }
    return event;
    }

    public void makeEventBe(String eventId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE "+ listName +" SET ADMIN_ACKNOWLEDGE=TRUE WHERE ID="+eventId);
        ps.execute();
    }
    public void declineEvent(String eventId)throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM "+ listName +" WHERE ID="+ eventId);
        ps.execute();
    }
    public String getVotes(String eventId,String voteSelection)throws SQLException {
        String votes;
        PreparedStatement ps = connection.prepareStatement("SELECT " + voteSelection + " FROM "+ listName + " WHERE ID="+ eventId);
        ps.execute();
        ResultSet resultSet  = ps.getResultSet();
        resultSet.next();
        votes                = resultSet.getString(1);

        return votes;
    }

    public List<Message> readAll() throws SQLException {
        List<Message> messages = new ArrayList<>();
        Message message;

        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC." + listName);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        while (resultSet.next()) {
            message = new Message();
            message.setId(resultSet.getLong(1));
            message.setSendMessage(new SendMessage().setText(resultSet.getString(3)));
            message.setSendPhoto(new SendPhoto().setPhoto(resultSet.getString(2)));
            messages.add(message);
        }
        return messages;
    }

    public boolean delete(long id) {
        try {
            read(id);
            PreparedStatement ps = connection.prepareStatement("DELETE FROM PUBLIC." + listName + " WHERE ID=?");
            ps.setLong(1, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public ArrayList<Discount> getDiscounts(String discountType, boolean ADMIN_ACKNOWLEDGE) throws  SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC."+ listName + " WHERE DISCOUNT_TYPE=? AND ADMIN_ACKNOWLEDGE=?");
        ps.setString( 1, discountType);
        ps.setBoolean(2, ADMIN_ACKNOWLEDGE);
        ps.execute();
        ArrayList<Discount> arrayList = new ArrayList<>();
        ResultSet resultSet = ps.getResultSet();
        while(resultSet.next()){
           arrayList.add(new Discount(
                   resultSet.getInt(1),
                   resultSet.getString(2),
                   resultSet.getString(3),
                   resultSet.getString(4),
                   resultSet.getString(5),
                   resultSet.getString(6),
                   resultSet.getString(7),
                   resultSet.getString(8),
                   resultSet.getString(9)
           ));
        }
        return arrayList;
    }

    public void createDiscount(Discount discount, boolean ADMIN_ACKNOWLEDGE) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO PUBLIC." + listName +
                " (DISCOUNT_TYPE, NAME, TEXT_ABOUT, PHOTO, ADDRESS, PAGE, DISCOUNT, MEMBER_ID,ADMIN_ACKNOWLEDGE)  VALUES (?,?,?,?,?,?,?,?,?)");
        ps.setString( 1,discount.getDiscountType());
        ps.setString( 2,discount.getName());
        ps.setString( 3,discount.getTextAbout());
        ps.setString( 4,discount.getPhoto());
        ps.setString( 5,discount.getAddress());
        ps.setString( 6,discount.getPage());
        ps.setString( 7,discount.getDiscount());
        ps.setString( 8,discount.getMemberId());
        ps.setBoolean(9, ADMIN_ACKNOWLEDGE);
        ps.execute();

    }

    public int getDiscountId(Discount discount, boolean ADMIN_ACKNOWLEDGE) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT ID FROM PUBLIC."+ listName + " WHERE DISCOUNT_TYPE=? and NAME =? and TEXT_ABOUT =? and ADDRESS =? and PAGE =?" +
                " and DISCOUNT =? and MEMBER_ID=? and ADMIN_ACKNOWLEDGE=?");
        ps.setString(1,discount.getDiscountType());
        ps.setString(2,discount.getName());
        ps.setString(3,discount.getTextAbout());
        ps.setString(4,discount.getAddress());
        ps.setString(5,discount.getPage());
        ps.setString(6,discount.getDiscount());
        ps.setString(7,discount.getMemberId());
        ps.setBoolean(8,ADMIN_ACKNOWLEDGE);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public void makeDiscountBe(String discountId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE PUBLIC." + listName + " SET ADMIN_ACKNOWLEDGE=TRUE WHERE ID="+discountId);
        ps.execute();
    }

    public Discount getDiscountById(String discountId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC." + listName + " WHERE ID="+discountId);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        resultSet.next();
        return new Discount(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getString(6),
                resultSet.getString(7),
                resultSet.getString(8),
                resultSet.getString(9)
        );
    }


    public void killDiscount(String discountId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM PUBLIC." + listName + " WHERE ID="+discountId);
        ps.execute();
    }

    public void updateDiscountName(String discountId, String newValue) throws  SQLException{
        PreparedStatement ps = connection.prepareStatement("UPDATE PUBLIC."+ listName + " SET NAME=? WHERE ID=?");
        ps.setString(1,newValue);
        ps.setString(2,discountId);
        ps.execute();
    }

    public void updateDiscountTextAbout(String discountId, String newValue) throws  SQLException{
        PreparedStatement ps = connection.prepareStatement("UPDATE PUBLIC."+ listName + " SET TEXT_ABOUT=? WHERE ID=?");
        ps.setString(1,newValue);
        ps.setString(2,discountId);
        ps.execute();
    }

    public void updateDiscountAddress(String discountId, String newValue) throws  SQLException{
        PreparedStatement ps = connection.prepareStatement("UPDATE PUBLIC."+ listName + " SET ADDRESS=? WHERE ID=?");
        ps.setString(1,newValue);
        ps.setString(2,discountId);
        ps.execute();
    }

    public void updateDiscountPage(String discountId, String newValue) throws  SQLException{
        PreparedStatement ps = connection.prepareStatement("UPDATE PUBLIC."+ listName + " SET PAGE=? WHERE ID=?");
        ps.setString(1,newValue);
        ps.setString(2,discountId);
        ps.execute();
    }

    public void updateDiscountDiscount(String discountId, String newValue) throws  SQLException{
        PreparedStatement ps = connection.prepareStatement("UPDATE PUBLIC."+ listName + " SET DISCOUNT=? WHERE ID=?");
        ps.setString(1,newValue);
        ps.setString(2,discountId);
        ps.execute();
    }

    public void updateDiscountPhoto(String discountId, String newValue) throws  SQLException{
        PreparedStatement ps = connection.prepareStatement("UPDATE PUBLIC."+ listName + " SET PHOTO=? WHERE ID=?");
        ps.setString(1,newValue);
        ps.setString(2,discountId);
        ps.execute();
    }

    public void updateDiscountType(String discountId, String newValue) throws  SQLException{
        PreparedStatement ps = connection.prepareStatement("UPDATE PUBLIC."+ listName + " SET DISCOUNT_TYPE=? WHERE ID=?");
        ps.setString(1,newValue);
        ps.setString(2,discountId);
        ps.execute();
    }





    private Message read(long id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC." + listName + " WHERE id=?");
        ps.setLong(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        rs.next();

        Message message = new Message();
        message.setId(rs.getLong(1));
        message.setSendMessage(new SendMessage().setText(rs.getString(3)));
        message.setSendPhoto(new SendPhoto().setPhoto(rs.getString(2)));
        return message;
    }
}
