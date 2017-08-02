package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import org.h2.jdbc.JdbcSQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PoorGuysDao extends AbstractDao {
    private final Connection connection;


    public PoorGuysDao(Connection connection) {
        this.connection = connection;
    }

    public void addNewGuy(long chatID) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO POOR_GUYS (CHAT_ID) VALUES (?)");
        ps.setLong(1,chatID);
        ps.execute();
    }

    public long getPoorGuy(long chatID) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT CHAT_ID FROM POOR_GUYS WHERE CHAT_ID=?");
        ps.setLong(1,chatID);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        rs.next();
        try {
        return rs.getLong(1);}
        catch (JdbcSQLException e) {
            return 0;
        }
    }

    public void deletePoorGuy(long chatID) throws  SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM POOR_GUYS WHERE CHAT_ID=?");
        ps.setLong(1,chatID);
        ps.execute();
    }

}
