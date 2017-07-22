package com.turlygazhy.dao.impl;

import com.turlygazhy.entity.Member;
import org.h2.jdbc.JdbcSQLException;
import org.telegram.telegrambots.api.objects.Contact;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by user on 1/21/17.
 */
public class MemberDao {
    private static final int ID_COLUMN_INDEX           = 1;
    private static final int CHAT_ID_COLUMN_INDEX      = 3;
    private static final int NISHA_COLUMN_INDEX        = 4;
    private static final int USERNAME_COLUMN_INDEX     = 5;
    private static final int COMPANY_NAME_COLUMN_INDEX = 6;
    private static final int CONTACT_COLUMN_INDEX      = 7;
    private static final int FIO_COLUMN_INDEX          = 8;
    private static final int USER_ID_COLUMN_INDEX      = 2;
    private static final int FIRST_NAME_COLUMN_INDEX   = 9;
    private static final int LAST_NAME_COLUMN_INDEX    = 10;
    private static final int PHONE_NUMBER_COLUMN_INDEX = 11;
    private static final int CITY_COLUMN_INDEX         = 18;
    private final Connection connection;

    public MemberDao(Connection connection) {
        this.connection = connection;
    }

    public void insert(String nisha,Long chatId, String userName, Integer userId, String companyName, String contact, String fio, Contact phoneNumber, String city) throws SQLException {
        boolean userRegistered = isUserRegistered(userId);
        if (userRegistered) {
            PreparedStatement updatePS = connection.prepareStatement(
                    "UPDATE member SET CHAT_ID=?, NISHA=?, USER_NAME=?, COMPANY_NAME=?, CONTACT=?, FIO=?, FIRST_NAME=?, LAST_NAME=?, PHONE_NUMBER=?, CITY=? WHERE user_ID=?");
            updatePS.setLong(1, chatId);
            updatePS.setString( 2, nisha);
            updatePS.setString( 3, userName);
            updatePS.setString( 4, companyName);
            updatePS.setString( 5, contact);
            updatePS.setString( 6, fio);
            updatePS.setString( 7, phoneNumber.getFirstName());
            updatePS.setString( 8, phoneNumber.getLastName());
            updatePS.setString (9, phoneNumber.getPhoneNumber());
            updatePS.setString(10, city);
            updatePS.setLong(  11, userId);

            updatePS.execute();
            return;
        }

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO member(ID, USER_ID, CHAT_ID,  NISHA, USER_NAME, COMPANY_NAME, CONTACT, FIO, FIRST_NAME, LAST_NAME, PHONE_NUMBER, CITY) VALUES (DEFAULT, ?,?,?,?, ?,?, ?, ?, ?, ?, ?)");
        ps.setLong(   1, userId);
        ps.setLong(   2, chatId);
        ps.setString( 3, nisha);
        ps.setString( 4, userName);
        ps.setString( 5, companyName);
        ps.setString( 6, contact);
        ps.setString( 7, fio);
        ps.setString( 8, phoneNumber.getFirstName());
        ps.setString( 9, phoneNumber.getLastName());
        ps.setString(10, phoneNumber.getPhoneNumber());
        ps.setString(11, city);
        ps.execute();
    }

    private boolean isUserRegistered(Integer userID) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM MEMBER where user_id=?");
            ps.setInt(1, userID);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMemberId(long userID) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM MEMBER where user_id=?");
            ps.setLong(1, userID);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            rs.next();
            return rs.getString(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long getMemberChatId(String memberId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT USER_ID FROM MEMBER WHERE ID=?");
        ps.setString(1, memberId);
        ps.execute();
        ResultSet resultSet  = ps.getResultSet();
        resultSet.next();
        return resultSet.getLong(1);
    }

    public Member selectByUserId(Integer userId) throws SQLException {
        Member member = new Member();

        PreparedStatement ps = connection.prepareStatement("select * from member where user_id=?");
        ps.setInt(1, userId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        try{
        rs.next();
        member.setId(rs.getInt(ID_COLUMN_INDEX));
        member.setUserId(userId);
        member.setChatId(rs.getLong(CHAT_ID_COLUMN_INDEX));
        member.setNisha(rs.getString(NISHA_COLUMN_INDEX));
        member.setUserName(rs.getString(USERNAME_COLUMN_INDEX));
        member.setCompanyName(rs.getString(COMPANY_NAME_COLUMN_INDEX));
        member.setContact(rs.getString(CONTACT_COLUMN_INDEX));
        member.setFIO(rs.getString(FIO_COLUMN_INDEX));
        member.setFirstName(rs.getString(FIRST_NAME_COLUMN_INDEX));
        member.setLastName(rs.getString(LAST_NAME_COLUMN_INDEX));
        member.setPhoneNumber(rs.getString(PHONE_NUMBER_COLUMN_INDEX));
        member.setCity(rs.getString(CITY_COLUMN_INDEX));}
        catch (JdbcSQLException e){
            return null;
        }
        return member;
    }

    public void updateNishaByUserId(Integer userId, String nisha) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update member set nisha = ? where user_id=?");
        ps.setString(1, nisha);
        ps.setInt(2, userId);
        ps.execute();
    }

    public String getMemberCityByChatId(long chatId) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("SELECT CITY FROM MEMBER WHERE CHAT_ID=?");
        ps.setLong(1,chatId);
        ps.execute();
        ResultSet  rs = ps.getResultSet();
        rs.next();
        try {
            return rs.getString(1);
        } catch (JdbcSQLException e){
            return null;
        }
    }

    public String getEventsWhereVoted(String memberId, String EVENT_TYPE) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT "+ EVENT_TYPE +" FROM MEMBER WHERE id=?");
        ps.setString(1,memberId);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        resultSet.next();
        return resultSet.getString(1);
    }

    public void addEventsWhereVoted(String memberId, String eventId, String EVENT_TYPE) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE MEMBER SET "+ EVENT_TYPE +" = CONCAT("+ EVENT_TYPE +", '"+eventId+"', '/' ) WHERE ID="+memberId);
        ps.execute();
    }


    public Member getMemberById(long member_id) throws SQLException {
        Member member = new Member();
        PreparedStatement ps = connection.prepareStatement("select * from member where id=?");
        ps.setLong(1,member_id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            member.setId(rs.getInt(ID_COLUMN_INDEX));
            member.setUserId(rs.getInt(USER_ID_COLUMN_INDEX));
            member.setChatId(rs.getLong(CHAT_ID_COLUMN_INDEX));
            member.setNisha(rs.getString(NISHA_COLUMN_INDEX));
            member.setUserName(rs.getString(USERNAME_COLUMN_INDEX));
            member.setCompanyName(rs.getString(COMPANY_NAME_COLUMN_INDEX));
            member.setContact(rs.getString(CONTACT_COLUMN_INDEX));
            member.setFIO(rs.getString(FIO_COLUMN_INDEX));
            member.setPhoneNumber(rs.getString(PHONE_NUMBER_COLUMN_INDEX));
            member.setFirstName(rs.getString(FIRST_NAME_COLUMN_INDEX));
            member.setLastName(rs.getString(LAST_NAME_COLUMN_INDEX));
            member.setCity(rs.getString(CITY_COLUMN_INDEX));
        }
        return member;
    }

    public ArrayList<Member> search(String searchString) throws SQLException {
        Set<Member> result = new HashSet<>();
        List<Member> allMembers = selectAll();
        for (Member member : allMembers) {
            String nisha = member.getNisha();
            for (String s : nisha.split(",")) {
                if (searchString.toLowerCase().contains(s.toLowerCase().trim())) {
                    result.add(member);
                    break;
                }
            }
        }
        ArrayList<Member> memberList = new ArrayList<>();
        memberList.addAll(result);
        return memberList;
    }

    private ArrayList<Member> selectAll() throws SQLException {
        ArrayList<Member> result = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("select * from member");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            Member member = new Member();

            member.setId(rs.getInt(ID_COLUMN_INDEX));
            member.setUserId(rs.getInt(USER_ID_COLUMN_INDEX));
            member.setChatId(rs.getLong(CHAT_ID_COLUMN_INDEX));
            member.setNisha(rs.getString(NISHA_COLUMN_INDEX));
            member.setUserName(rs.getString(USERNAME_COLUMN_INDEX));
            member.setCompanyName(rs.getString(COMPANY_NAME_COLUMN_INDEX));
            member.setContact(rs.getString(CONTACT_COLUMN_INDEX));
            member.setFIO(rs.getString(FIO_COLUMN_INDEX));
            member.setCity(rs.getString(CITY_COLUMN_INDEX));

            result.add(member);
        }
        return result;
    }

    public void updateFio(Integer userId, String fio) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update member set fio = ? where user_id=?");
        ps.setString(1, fio);
        ps.setInt(2, userId);
        ps.execute();
    }

    public void updateCompany(Integer userId, String company) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update member set COMPANY_NAME = ? where user_id=?");
        ps.setString(1, company);
        ps.setInt(2, userId);
        ps.execute();
    }

    public void updateContact(Integer userId, String contact) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update member set CONTACT = ? where user_id=?");
        ps.setString(1, contact);
        ps.setInt(2, userId);
        ps.execute();
    }

    public void updatePhoneNumber(Integer userId, Contact contact) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update member set first_name = ?, last_name=?, phone_number=? where user_id=?");
        ps.setString(1, contact.getFirstName());
        ps.setString(2, contact.getLastName());
        ps.setString(3, contact.getPhoneNumber());
        ps.setLong(4, userId);
        ps.execute();
    }

    public void updatePhoneNumber(Integer userId, String contact) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update member set phone_number=? where user_id=?");
        ps.setString(1, contact);
        ps.setLong(2, userId);
        ps.execute();
    }

    public void updateCity(Integer userId, String city) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("update member set city=? where user_id=?");
        ps.setString(1, city);
        ps.setLong(2, userId);
        ps.execute();
    }

    public boolean isMemberAdded(Integer userId) throws SQLException {
        try {
            PreparedStatement ps = connection.prepareStatement("select * from member where user_id=?");
            ps.setInt(1, userId);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            rs.next();
            return rs.getBoolean(12);
        } catch (JdbcSQLException e) {
            return false;
        }
    }

    public void setAddedToGroup(Integer userId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update member set ADDED_TO_GROUP=? where user_id=?");
        ps.setBoolean(1, true);
        ps.setInt(2, userId);
        ps.execute();
    }

    public Member getMember(String chatId) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM MEMBER WHERE CHAT_ID=?");
        ps.setString(1,chatId);
        ps.execute();
        ResultSet resultSet  = ps.getResultSet();
        resultSet.next();

        return new Member(
                resultSet.getInt(1),
                resultSet.getInt(2),
                resultSet.getLong(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getString(6),
                resultSet.getString(7),
                resultSet.getString(8),
                resultSet.getString(9),
                resultSet.getString(10),
                resultSet.getString(11),
                resultSet.getString(18));
    }

    public void addDownloadedBook(String bookId, int memberId) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("UPDATE MEMBER SET DOWNLOADED_BOOKS= CONCAT(DOWNLOADED_BOOKS, '"+bookId+"', '/' ) WHERE ID="+memberId);
        ps.execute();
    }

    public String[] getMemberBooks(String memberId) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("SELECT DOWNLOADED_BOOKS FROM MEMBER WHERE ID="+ memberId);
        ps.execute();
        ResultSet resultSet  = ps.getResultSet();
        resultSet.next();

            String result = resultSet.getString(1);
            if(result==null){
                return null;
            }
            return result.split("/");

    }

    public void makeMemberAgreeRules(String memberId) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("UPDATE MEMBER SET ACCEPT_RULES=? WHERE ID=?");
        ps.setBoolean(1,true);
        ps.setString( 2, memberId);
        ps.execute();
    }

    public boolean checkMemberAgreeToRules(String memberId) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("SELECT ACCEPT_RULES FROM MEMBER WHERE ID=?");
        ps.setString(1,memberId);
        ps.execute();
        ResultSet resultSet  = ps.getResultSet();
        resultSet.next();
        return resultSet.getBoolean(1);
    }

}

