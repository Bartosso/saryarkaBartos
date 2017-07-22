package com.turlygazhy.dao.impl;

import com.turlygazhy.entity.Top10;

import java.sql.*;
import java.util.ArrayList;

public class Top10Dao {
    private final Connection connection;
    private final String listName;

    public Top10Dao(Connection connection, String top10listName) {
        this.connection = connection;
        this.listName = top10listName;
    }

    public void insert(String text) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("MERGE INTO "+listName+" KEY(text) " +
                " VALUES(default, ?, (ifnull(select count from "+listName+" where text =?,0)) +1)");
        ps.setString(1,text);
        ps.setString(2,text);
        ps.execute();
    }

    public ArrayList<Top10> getTop10() throws SQLException {
        ArrayList<Top10> top10ArrayList = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM "+listName+" order by count desc");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            top10ArrayList.add(new Top10(rs.getString(2),
                    rs.getInt(3)));
        }
        return top10ArrayList;
    }

    public void truncateTop() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("TRUNCATE TABLE "+ listName + " ;" +
                "ALTER TABLE "+listName+" ALTER COLUMN id RESTART WITH 1");
        ps.execute();
    }

}
