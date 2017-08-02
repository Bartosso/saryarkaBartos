package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.entity.Button;
import com.turlygazhy.exception.CommandNotFoundException;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 1/2/17.
 * ';' - separator for rows
 * ',' - separator for buttons
 */
public class ButtonDao extends AbstractDao {
    public static final int COMMAND_ID_COLUMN_INDEX = 3;
    private final Connection connection;

    public ButtonDao(Connection connection) {
        this.connection = connection;
    }

    public Button getButton(String text) throws CommandNotFoundException, SQLException {
        try {
            String selectButtonByText = "SELECT * FROM PUBLIC.BUTTON WHERE TEXT=?";
            int textParameterIndex = 1;

            PreparedStatement ps;
            ps = connection.prepareStatement(selectButtonByText);
            ps.setString(textParameterIndex, text);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            rs.next();

            Button button = new Button();
            button.setId(rs.getInt(ID_INDEX));
            button.setText(text);
            button.setCommandId(rs.getInt(3));
            button.setUrl(rs.getString(4));
            button.setRequestContact(rs.getBoolean(5));
            return button;
        } catch (SQLException e) {
            if (e.getMessage().contains("No data is available")) {
                throw new CommandNotFoundException(e);
            }
            throw new SQLException(e);
        }
    }

    public String getButtonText(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT text FROM BUTTON where id=?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        rs.next();
        return rs.getString(1);
    }

    public Button getButton(int id) throws SQLException {
        try {
            return getButton(getButtonText(id));
        } catch (CommandNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateButtonText(int buttonId, String text) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update button set text=? where id=?");
        ps.setString(1, text);
        ps.setInt(2, buttonId);
        ps.execute();
    }

    public void updateButtonUrl(int buttonId, String url) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update BUTTON set URL=? WHERE ID=?");
        ps.setString(1, url);
        ps.setInt(2, buttonId);
        ps.execute();
    }

    public ArrayList<Button> getAllButtons(boolean changeable) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM BUTTON WHERE CHANGEABLE="+ changeable);
        ps.execute();
        ArrayList<Button> buttonsArrayList = new ArrayList<>();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            Button button = new Button();
            button.setId(rs.getInt(ID_INDEX));
            button.setText(rs.getString(2));
            button.setCommandId(rs.getInt(3));
            button.setUrl(rs.getString(4));
            button.setRequestContact(rs.getBoolean(5));
            buttonsArrayList.add(button);
        }
        return buttonsArrayList;
    }
}
