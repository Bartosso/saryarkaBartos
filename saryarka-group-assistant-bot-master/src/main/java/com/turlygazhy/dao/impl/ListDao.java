package com.turlygazhy.dao.impl;

import com.turlygazhy.entity.*;
import org.h2.jdbc.JdbcSQLException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;

import java.sql.*;
import java.util.ArrayList;
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

    public int insertIntoLists( String text, String member_id, String date_post, boolean ADMIN_ACKNOWLEDGE) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO PUBLIC." + listName + "( MEMBER_ID, TEXT, date_post, ADMIN_ACKNOWLEDGE) VALUES ( ?,?,?,? )");
        ps.setString( 1, member_id);
//        ps.setString(2, photo);
        ps.setString( 2, text);
        ps.setString( 3, date_post);
        ps.setBoolean(4, ADMIN_ACKNOWLEDGE);
        ps.execute();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        return rs.getInt(1);
    }

    public ArrayList<ListData> getAllFromList(boolean ADMIN_ACKNOWLEDGE) throws  SQLException{
        ArrayList<ListData> listDataArrayList = new ArrayList<>();
        ListData listData;
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC." + listName +
                " WHERE ADMIN_ACKNOWLEDGE="+ADMIN_ACKNOWLEDGE);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        while (resultSet.next()) {
            listData = new ListData();
            listData.setId(resultSet.getLong(     1));
            listData.setMemberId(resultSet.getInt(2));
//            listData.setPhoto(resultSet.getString(3));
            listData.setText(resultSet.getString( 3));
            listData.setDate(resultSet.getString( 4));
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
//            listData.setPhoto(resultSet.getString(3));
            listData.setText(resultSet.getString( 3));
            listData.setDate(resultSet.getString( 4));
            listDataArrayList.add(listData);
        }
        return listDataArrayList;
    }

    public ListData getListDataById(String listDataId) throws SQLException{
        ListData listData = new ListData();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM public." + listName + " where id="+ listDataId );
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        resultSet.next();
        listData.setId(resultSet.getLong(     1));
        listData.setMemberId(resultSet.getInt(2));
//        listData.setPhoto(resultSet.getString(3));
        listData.setText(resultSet.getString( 3));
        listData.setDate(resultSet.getString( 4));
    return listData;
    }

    public void updateTenderText(String listDataID, String newValue) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("UPDATE PUBLIC."+ listName + " SET TEXT=? WHERE ID=?");
        ps.setString(1, newValue);
        ps.setString(2, listDataID);
        ps.execute();
    }

    public String moveTenderInAnotherType(String listDataId, String anotherListName) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("INSERT INTO "+ anotherListName +
                "(MEMBER_ID, TEXT, DATE_POST, ADMIN_ACKNOWLEDGE) select MEMBER_ID, TEXT, DATE_POST, ADMIN_ACKNOWLEDGE from "
                + listName +" where id ="+ listDataId , Statement.RETURN_GENERATED_KEYS);
        ps.execute();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        try{
        return rs.getString(1);}
        catch (JdbcSQLException e){
            return null;
        }
    }

    public void deleteListById(long listDataId) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("DELETE FROM PUBLIC."+ listName + " WHERE ID="+ listDataId);
        ps.execute();
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
            event.setCONTACT_INFORMATION(resultSet.getString(5));
            event.setPHOTO(resultSet.getString(              6));
            event.setRULES(resultSet.getString(              11));
            event.setDRESS_CODE(resultSet.getString(         12));
            event.setPROGRAM(resultSet.getString(            13));
            event.setPAGE(resultSet.getString(               14));
            event.setBY_ADMIN(resultSet.getBoolean(          15));
            event.setDOCUMENT(resultSet.getString(           16));
            events.add(event);
        }
        return events;
    }

    public long createNewEvent(String eventName, String where, String date, String photo,String contactInformation,String rules, String dressCode, String program, String page, String DOCUMENT , boolean ADMIN_ACKNOWLEDGE, boolean BY_ADMIN) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO PUBLIC." + listName + "(EVENT_NAME, PLACE, WHEN, ADMIN_ACKNOWLEDGE) VALUES ( ?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        ps.setString(  1, eventName);
        ps.setString(  2, where);
        ps.setString(  3, date);
        ps.setBoolean(4, ADMIN_ACKNOWLEDGE);
//        ps.setString(  4, contactInformation);
//        ps.setString(  5, photo);
//        ps.setBoolean( 6, ADMIN_ACKNOWLEDGE);
//        ps.setString(  7, rules);
//        ps.setString(  8, dressCode);
//        ps.setString(  9, program);
//        ps.setString( 10, page);
//        ps.setBoolean(11, BY_ADMIN);
//        ps.setString( 12, DOCUMENT);
        ps.execute();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();

        return rs.getInt(1);
    }


    public void voteEvent(String eventId, String userId, String chose) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE public." + listName + " set "+ chose +" = CONCAT("+ chose +", '"+ userId +"', '/') WHERE ID= '"+ eventId +"' ");
            ps.execute();
    }

    public boolean checkEventStatus(String eventId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT ADMIN_ACKNOWLEDGE FROM PUBLIC."+listName + " WHERE ID="+eventId);
        ps.execute();
        ResultSet resultSet  = ps.getResultSet();
        resultSet.next();
        try{
        return resultSet.getBoolean(1);}
        catch (Exception e){
            return false;
        }
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

    public long getEventId(String eventName,String where, String date, boolean adminSaysYes) throws SQLException {
        long eventId = 0;
        PreparedStatement ps = connection.prepareStatement("SELECT id from public."+ listName +" where EVENT_NAME=? and " +
                "PLACE=? and WHEN=? and ADMIN_ACKNOWLEDGE=?"  );
        ps.setString( 1,eventName);
        ps.setString( 2,where);
        ps.setString( 3,date);
        ps.setBoolean(4,adminSaysYes);
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
            event.setCONTACT_INFORMATION(resultSet.getString(5));
            event.setPHOTO(resultSet.getString(              6));
            event.setRULES(resultSet.getString(              11));
            event.setDRESS_CODE(resultSet.getString(         12));
            event.setPROGRAM(resultSet.getString(            13));
            event.setPAGE(resultSet.getString(               14));
            event.setBY_ADMIN(resultSet.getBoolean(          15));
            event.setDOCUMENT(resultSet.getString(           16));
        } catch (JdbcSQLException e){
            return null;
        }
    return event;
    }

    public void makeStuffBe(String stuffId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE public."+ listName +" SET ADMIN_ACKNOWLEDGE=TRUE WHERE ID="+stuffId);
        ps.execute();
    }
    public void declineEvent(String eventId)throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM public."+ listName +" WHERE ID="+ eventId);
        ps.execute();
    }

    public void addEndedEvent(Event event) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO PUBLIC."+ listName + " (EVENT_NAME, PLACE, WHEN, CONTACT_INFORMATION, PHOTO, RULES, DRESS_CODE, PROGRAM, PAGE, ADMIN_ACKNOWLEDGE, BY_ADMIN, DOCUMENT) " +
                "VALUES (?,?,?,?,?,?,?,?,?, true,?,?)");
        ps.setString(  1, event.getEVENT_NAME());
        ps.setString(  2, event.getPLACE());
        ps.setString(  3, event.getWHEN());
        ps.setString(  4, event.getCONTACT_INFORMATION());
        ps.setString(  5, event.getPHOTO());
        ps.setString(  6, event.getRULES());
        ps.setString(  7, event.getDRESS_CODE());
        ps.setString(  8, event.getPROGRAM());
        ps.setString(  9, event.getPAGE());
        ps.setBoolean(10, event.isBY_ADMIN());
        ps.setString( 11, event.getDOCUMENT());
        ps.execute();
    }

    public String getVotes(String eventId,String voteSelection)throws SQLException {
        String votes;
        PreparedStatement ps = connection.prepareStatement("SELECT " + voteSelection + " FROM public."+ listName + " WHERE ID="+ eventId);
        ps.execute();
        ResultSet resultSet  = ps.getResultSet();
        resultSet.next();
        votes                = resultSet.getString(1);

        return votes;
    }

    public String getMembersWhoNeedReminder(String eventId) throws  SQLException {
        String   result;
        PreparedStatement ps = connection.prepareStatement("SELECT MEMBERS_WHO_NEED_REMINDER FROM PUBLIC."+ listName + " WHERE ID="+ eventId);
        ps.execute();
        ResultSet resultSet  = ps.getResultSet();
        resultSet.next();
        try{
        return resultSet.getString(1);
        }catch (JdbcSQLException e){
            return null;
        }
    }

    public void addMemberWhoNeedReminder(String eventId, String memberId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE PUBLIC."+ listName
                + " SET MEMBERS_WHO_NEED_REMINDER = CONCAT (MEMBERS_WHO_NEED_REMINDER, ?, '`') WHERE ID ="+eventId);
        ps.setString(1, memberId);
        ps.execute();
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

    public boolean delete(String id) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM PUBLIC." + listName + " WHERE ID=?");
            ps.setString(1, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }



//    public ArrayList<Discount> getDiscounts(String discountType) throws  SQLException {
//        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC."+ listName + " WHERE TYPE=?");
//        ps.setString( 1, discountType);
//        ps.execute();
//        ArrayList<Discount> arrayList = new ArrayList<>();
//        ResultSet resultSet = ps.getResultSet();
//        while(resultSet.next()){
//            arrayList.add(new Discount(
//                    resultSet.getInt(1),
//                    resultSet.getString(2),
//                    resultSet.getString(3),
//                    resultSet.getString(4),
//                    resultSet.getString(5)
//            ));
//        }
//        return arrayList;
//    }

    public ArrayList<Discount> getDiscounts(String discountType) throws  SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC."+ listName + " WHERE DISCOUNT_TYPE=?");
        ps.setString( 1, discountType);
        ps.execute();
        ArrayList<Discount> arrayList = new ArrayList<>();
        ResultSet resultSet = ps.getResultSet();
        while(resultSet.next()){
            arrayList.add(new Discount(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(8),
                    resultSet.getString(5)
            ));
        }
        return arrayList;
    }

//    public ArrayList<Discount> getDiscounts(String discountType, boolean ADMIN_ACKNOWLEDGE) throws  SQLException {
//        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC."+ listName + " WHERE DISCOUNT_TYPE=? AND ADMIN_ACKNOWLEDGE=?");
//        ps.setString( 1, discountType);
//        ps.setBoolean(2, ADMIN_ACKNOWLEDGE);
//        ps.execute();
//        ArrayList<Discount> arrayList = new ArrayList<>();
//        ResultSet resultSet = ps.getResultSet();
//        while(resultSet.next()){
//           arrayList.add(new Discount(
//                   resultSet.getInt(1),
//                   resultSet.getString(2),
//                   resultSet.getString(3),
//                   resultSet.getString(4),
//                   resultSet.getString(5),
//                   resultSet.getString(6),
//                   resultSet.getString(7),
//                   resultSet.getString(8),
//                   resultSet.getString(9)
//           ));
//        }
//        return arrayList;
//    }

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
        ps.setString(1, newValue);
        ps.setString(2, discountId);
        ps.execute();
    }

    public void createDiscountVersion2(String discountType, String discountName, String discountAmount, String discountPage) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO PUBLIC."+ listName + " (TYPE, TEXT, AMOUNT, URL) VALUES (?,?,?,?)");
        ps.setString(1, discountType);
        ps.setString(2, discountName);
        ps.setString(3, discountAmount);
        ps.setString(4, discountPage);
        ps.execute();
    }

    public void createDiscountVersion3(String discountType,String discountDescription, String discountName, String discountAmount,String discountAddress,
                                       String discountPhone, String discountPhoto) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("INSERT INTO PUBLIC."+ listName + " (DISCOUNT_TYPE, NAME, TEXT_ABOUT, PHOTO, ADDRESS," +
                " PHONE, DISCOUNT ) VALUES (?,?,?,?,?,?,?)");
        ps.setString(1, discountType);
        ps.setString(2, discountName);
        ps.setString(3, discountDescription);
        ps.setString(4, discountPhoto);
        ps.setString(5, discountAddress);
        ps.setString(6, discountPhone);
        ps.setString(7, discountAmount);
        ps.execute();
    }

    public int addNewVacancy(String companyName, String sfera, String experience,
                              String place, String workingConditions, String salary, String contact,
                              String memberId, boolean ADMIN_ACKNOWLEDGE) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO PUBLIC."+ listName + " (COMPANY_NAME, SFERA, EXPERIENCE, PLACE" +
                ", WORKING_CONDITIONS, SALARY, CONTACT, MEMBER_ID, ADMIN_ACKNOWLEDGE) VALUES (?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        ps.setString( 1, companyName);
        ps.setString( 2, sfera);
        ps.setString( 3, experience);
        ps.setString( 4, place);
        ps.setString( 5, workingConditions);
        ps.setString( 6, salary);
        ps.setString( 7, contact);
        ps.setString( 8, memberId);
        ps.setBoolean(9, ADMIN_ACKNOWLEDGE);
        ps.execute();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();

       return rs.getInt(1);
    }

    public boolean isStuffActive(String stuffId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT ADMIN_ACKNOWLEDGE FROM PUBLIC."+ listName + " WHERE ID="+ stuffId);
        ps.execute();
        ResultSet resultSet  = ps.getResultSet();
        resultSet.next();
        try{
        return resultSet.getBoolean(1);}
        catch (JdbcSQLException e){
            return true;
        }
    }

    public ArrayList<Vacancy> getAllVacancyById(String memberId, boolean ADMIN_ACKNOWLEDGE) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC."+ listName + " WHERE MEMBER_ID="+ memberId +
                " AND ADMIN_ACKNOWLEDGE="+ ADMIN_ACKNOWLEDGE);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        ArrayList<Vacancy> vacancyArrayList = new ArrayList<>();
        while (resultSet.next()){
            vacancyArrayList.add(new Vacancy(
                    resultSet.getLong(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(5),
                    resultSet.getString(6),
                    resultSet.getString(7),
                    resultSet.getString(8),
                    resultSet.getString(9),
                    resultSet.getString(10)));
        }
        return vacancyArrayList;
    }

    public ArrayList<Vacancy> getAllVacancyBySfera(String sfera, boolean ADMIN_ACKNOWLEDGE) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC."+ listName +" WHERE SFERA='"+sfera+"'"
                +" AND ADMIN_ACKNOWLEDGE=" + ADMIN_ACKNOWLEDGE);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        ArrayList<Vacancy> vacancyArrayList = new ArrayList<>();
        while (resultSet.next()){
            vacancyArrayList.add(new Vacancy(
                    resultSet.getLong(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(5),
                    resultSet.getString(6),
                    resultSet.getString(7),
                    resultSet.getString(8),
                    resultSet.getString(9),
                    resultSet.getString(10)));
        }
        return vacancyArrayList;
    }

    public ArrayList<Vacancy> getAllVacancy(boolean ADMIN_ACKNOWLEDGE) throws SQLException   {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC."+ listName + " WHERE ADMIN_ACKNOWLEDGE="+ ADMIN_ACKNOWLEDGE);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        ArrayList<Vacancy> vacancyArrayList = new ArrayList<>();
        while (resultSet.next()){
            vacancyArrayList.add(new Vacancy(
                    resultSet.getLong(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(5),
                    resultSet.getString(6),
                    resultSet.getString(7),
                    resultSet.getString(8),
                    resultSet.getString(9),
                    resultSet.getString(10)));
        }
        return vacancyArrayList;
    }

    public Vacancy getVacancy(String vacancyId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC."+ listName + " WHERE ID="+ vacancyId);
        ps.execute();
        ResultSet resultSet  = ps.getResultSet();
        resultSet.next();
        return new Vacancy(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getString(6),
                resultSet.getString(7),
                resultSet.getString(8),
                resultSet.getString(9),
                resultSet.getString(10));
    }

    public void deleteVacancy(String vacancyId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM PUBLIC."+ listName + " WHERE ID="+ vacancyId);
        ps.execute();
    }



    public void addNewBook(String bookName, String book, String category) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("INSERT INTO PUBLIC."+ listName + " (NAME, FILE,CATEGORY) values (?,?,?)");
        ps.setString(1,bookName);
        ps.setString(2,book);
        ps.setString(3,category);
        ps.execute();
    }

    public ArrayList<Book> getAllBooks() throws SQLException{
        ArrayList<Book> bookArrayList = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC."+ listName);
        ps.execute();
        ResultSet resultSet  = ps.getResultSet();
        while (resultSet.next()){
            bookArrayList.add(new Book(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4)));
        }
        return bookArrayList;
    }

    public ArrayList<String> getBooksCategories() throws SQLException{
        PreparedStatement ps = connection.prepareStatement("select distinct category from "+listName);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        ArrayList<String> booksCategories = new ArrayList<>();
        while (rs.next()){
            booksCategories.add(rs.getString(1));
        }
        return booksCategories;
    }

    public ArrayList<Book> getAllBooksInDistinctCategories(String category)throws SQLException{
        ArrayList<Book> bookArrayList = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + listName+ " where category =?");
        ps.setString(1,category);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            bookArrayList.add(new Book(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4)));
        }

        return bookArrayList;
    }

    public Book getBookById(String bookId) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC."+ listName + " WHERE ID=?");
        ps.setString(1, bookId);
        ps.execute();
        ResultSet resultSet  = ps.getResultSet();
        resultSet.next();
        return new Book(resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3));
    }

    public void deleteBook(String bookId) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("DELETE FROM PUBLIC." + listName + " WHERE ID=?");
        ps.setString(1, bookId);
        ps.execute();
    }

    public void fakeVote(String section) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("UPDATE PUBLIC."+ listName + " SET " + section +"= "+ section +"+1");
        ps.execute();
    }
    public int[] getFakeVote() throws SQLException{
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC."+ listName);
        ps.execute();
        int result[] = new int[2];
        ResultSet resultSet  = ps.getResultSet();
        resultSet.next();
        try{
        result[0] = resultSet.getInt(1);
        result[1] = resultSet.getInt(2);}
        catch (JdbcSQLException e){
            result[0] = 0;
            result[1] = 0;
            return result;
        }
        return result;
    }

    public String daiKostil() throws SQLException{
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PUBLIC."+ listName);
        ps.execute();
        ResultSet rs         = ps.getResultSet();
        rs.next();
        return rs.getString(1);
    }

    //Секция редактирования ивентов перед публикацией

    public void changeStuff(String newValue, String stuffId, String whatToChange) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("UPDATE PUBLIC."+ listName +" SET "+whatToChange+"='"+newValue+"' WHERE ID="+stuffId);
        ps.execute();
    }

    public void deleteStuff(String stuffId, String whatToDelete) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE PUBLIC."+listName+" SET "+whatToDelete+" = NULL WHERE ID="+stuffId);
        ps.execute();
    }

    public void changeBooksCategory(String oldCategory, String newCategory) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE PUBLIC."+ listName +" SET CATEGORY=? WHERE CATEGORY=?");
        ps.setString(1, newCategory);
        ps.setString(2, oldCategory);
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
