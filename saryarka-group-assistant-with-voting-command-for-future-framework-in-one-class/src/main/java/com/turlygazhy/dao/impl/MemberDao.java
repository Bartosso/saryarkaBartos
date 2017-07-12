package com.turlygazhy.dao.impl;

import com.turlygazhy.entity.Member;
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
    public static final int ID_COLUMN_INDEX = 1;
    public static final int CHAT_ID_COLUMN_INDEX = 3;
    public static final int NAVIKI_COLUMN_INDEX = 4;
    public static final int NISHA_COLUMN_INDEX = 5;
    public static final int USERNAME_COLUMN_INDEX = 6;
    public static final int COMPANY_NAME_COLUMN_INDEX = 7;
    public static final int CONTACT_COLUMN_INDEX = 8;
    public static final int FIO_COLUMN_INDEX = 9;
    public static final int USER_ID_COLUMN_INDEX = 2;
    public static final int FIRST_NAME_COLUMN_INDEX = 10;
    public static final int LAST_NAME_COLUMN_INDEX = 11;
    public static final int PHONE_NUMBER_COLUMN_INDEX = 12;
    private final Connection connection;

    public MemberDao(Connection connection) {
        this.connection = connection;
    }

    public void insert(String nisha, String naviki, Long chatId, String userName, Integer userId, String companyName, String contact, String fio, Contact phoneNumber) throws SQLException {
        boolean userRegistered = isUserRegistered(userId);
        if (userRegistered) {
            PreparedStatement updatePS = connection.prepareStatement(
                    "UPDATE member SET CHAT_ID=?, NAVIKI=?, NISHA=?, USER_NAME=?, COMPANY_NAME=?, CONTACT=?, FIO=?, FIRST_NAME=?, LAST_NAME=?, PHONE_NUMBER=? WHERE user_ID=?");
            updatePS.setLong(1, chatId);
            updatePS.setString(2, naviki);
            updatePS.setString(3, nisha);
            updatePS.setString(4, userName);
            updatePS.setString(5, companyName);
            updatePS.setString(6, contact);
            updatePS.setString(7, fio);
            updatePS.setString(8, phoneNumber.getFirstName());
            updatePS.setString(9, phoneNumber.getLastName());
            updatePS.setString(10, phoneNumber.getPhoneNumber());
            updatePS.setLong(11, userId);

            updatePS.execute();
            return;
        }

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO member(ID, USER_ID, CHAT_ID, NAVIKI, NISHA, USER_NAME, COMPANY_NAME, CONTACT, FIO, FIRST_NAME, LAST_NAME, PHONE_NUMBER) VALUES (DEFAULT, ?,?,?,?, ?, ?, ?, ?, ?, ?, ?)");
        ps.setLong(1, userId);
        ps.setLong(2, chatId);
        ps.setString(3, naviki);
        ps.setString(4, nisha);
        ps.setString(5, userName);
        ps.setString(6, companyName);
        ps.setString(7, contact);
        ps.setString(8, fio);
        ps.setString(9, phoneNumber.getFirstName());
        ps.setString(10, phoneNumber.getLastName());
        ps.setString(11, phoneNumber.getPhoneNumber());
        ps.execute();
    }

    public boolean isUserRegistered(Integer userID) {
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

    public Member selectByUserId(Integer userId) throws SQLException {
        Member member = new Member();

        PreparedStatement ps = connection.prepareStatement("select * from member where user_id=?");
        ps.setInt(1, userId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        rs.next();
        member.setId(rs.getInt(ID_COLUMN_INDEX));
        member.setUserId(userId);
        member.setChatId(rs.getLong(CHAT_ID_COLUMN_INDEX));
        member.setNaviki(rs.getString(NAVIKI_COLUMN_INDEX));
        member.setNisha(rs.getString(NISHA_COLUMN_INDEX));
        member.setUserName(rs.getString(USERNAME_COLUMN_INDEX));
        member.setCompanyName(rs.getString(COMPANY_NAME_COLUMN_INDEX));
        member.setContact(rs.getString(CONTACT_COLUMN_INDEX));
        member.setFIO(rs.getString(FIO_COLUMN_INDEX));
        member.setFirstName(rs.getString(FIRST_NAME_COLUMN_INDEX));
        member.setLastName(rs.getString(LAST_NAME_COLUMN_INDEX));
        member.setPhoneNumber(rs.getString(PHONE_NUMBER_COLUMN_INDEX));
        return member;
    }

    public void updateNishaByUserId(Integer userId, String nisha) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update member set nisha = ? where user_id=?");
        ps.setString(1, nisha);
        ps.setInt(2, userId);
        ps.execute();
    }

    public void updateNavikiByUserId(Integer userId, String naviki) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update member set naviki = ? where user_id=?");
        ps.setString(1, naviki);
        ps.setInt(2, userId);
        ps.execute();
    }

    public List<Member> search(String searchString) throws SQLException {
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
        List<Member> memberList = new ArrayList<>();
        for (Member member : result) {
            memberList.add(member);
        }
        return memberList;
    }

    public List<Member> selectAll() throws SQLException {
        List<Member> result = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("select * from member");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            Member member = new Member();

            member.setId(rs.getInt(ID_COLUMN_INDEX));
            member.setUserId(rs.getInt(USER_ID_COLUMN_INDEX));
            member.setChatId(rs.getLong(CHAT_ID_COLUMN_INDEX));
            member.setNaviki(rs.getString(NAVIKI_COLUMN_INDEX));
            member.setNisha(rs.getString(NISHA_COLUMN_INDEX));
            member.setUserName(rs.getString(USERNAME_COLUMN_INDEX));
            member.setCompanyName(rs.getString(COMPANY_NAME_COLUMN_INDEX));
            member.setContact(rs.getString(CONTACT_COLUMN_INDEX));
            member.setFIO(rs.getString(FIO_COLUMN_INDEX));

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

    public boolean isMemberAdded(Integer userId) throws SQLException {
        try {
            PreparedStatement ps = connection.prepareStatement("select * from member where user_id=?");
            ps.setInt(1, userId);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            rs.next();
            return rs.getBoolean(13);
        } catch (Exception e) {
            return false;
        }
    }

    public void setAddedToGroup(Integer userId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update member set ADDED_TO_GROUP=? where user_id=?");
        ps.setBoolean(1, true);
        ps.setInt(2, userId);
        ps.execute();
    }
}
