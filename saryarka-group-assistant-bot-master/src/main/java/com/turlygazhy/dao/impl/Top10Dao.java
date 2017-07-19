package com.turlygazhy.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Top10Dao {
    private final Connection connection;
    private final String listName;

    public Top10Dao(Connection connection, String top10listName) {
        this.connection = connection;
        this.listName = top10listName;
    }

    public void insert(String text) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO "+listName+" (text, count) VALUES(?, count + 1) " +
                " ON DUPLICATE KEY UPDATE    \n" +
                "text=?, count + 1");
        ps.setString(1,text);
        ps.setString(2,text);
        ps.execute();
    }

}
