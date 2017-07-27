package com.turlygazhy.tool.vanderkastTools.Constructors;

import com.turlygazhy.tool.vanderkastTools.patterns.KeyboardPattern;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;



import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vanderkast on 04.07.2017.
 * <p>
 * this class uses to construct reply keyboard
 */
public class ReplyKeyboardConstructor {
    //returns keyboard with one button at row
    public static ReplyKeyboardMarkup getKeyboard(ArrayList<String> buttons) {
        return getKeyboard(buttons, KeyboardPattern.ONE_BUTTON_AT_ROW);
    }

    //returns keyboard with custom count of buttons st row by pattern
    public static ReplyKeyboardMarkup getKeyboard(ArrayList<String> buttons, KeyboardPattern pattern) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<KeyboardRow>();

        while (buttons.size() != 0) {
            KeyboardRow row = new KeyboardRow();
            for (int i = 0; i < ButtonTool.getButtonsAtLine(pattern); i++) {
                if (buttons.size() != 0) {
                    row.add(new KeyboardButton()
                            .setText(buttons.get(0)));
                    buttons.remove(0);
                }
            }
            rows.add(row);
        }
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    //Constructing row
    public static KeyboardRow setButtonsInRow(ArrayList<String> buttons, KeyboardPattern pattern) {
        KeyboardRow row = new KeyboardRow();
        for (int i = 0; i < ButtonTool.getButtonsAtLine(pattern); i++) {
            row.add(new KeyboardButton()
                    .setText(buttons.get(0)));

            buttons.remove(0);
        }
        return row;
    }

    //updating keyboard: sets in Button (row : posInRow) request contact
    public static ReplyKeyboardMarkup addContactRequestInKeyboard(ReplyKeyboardMarkup keyboard, int row, int posInRow) {
        List<KeyboardRow> rows = keyboard.getKeyboard();
        KeyboardRow selectedRow = rows.get(row);
        KeyboardButton button = selectedRow.get(posInRow);

        button.setRequestContact(true);
        selectedRow.set(posInRow, button);
        rows.set(row, selectedRow);

        return keyboard;
    }

    //updating keyboard: sets in Button (row : posInRow) request location
    public static ReplyKeyboardMarkup addLocationRequestInKeyboard(ReplyKeyboardMarkup keyboard, int row, int posInRow) {
        List<KeyboardRow> rows = keyboard.getKeyboard();
        KeyboardRow selectedRow = rows.get(row);
        KeyboardButton button = selectedRow.get(posInRow);

        button.setRequestLocation(true);
        selectedRow.set(posInRow, button);
        rows.set(row, selectedRow);

        return keyboard;
    }

    public static ReplyKeyboardMarkup addContactRequestInKeyboard(ReplyKeyboardMarkup keyboard, String buttonText) {
        List<KeyboardRow> rows = keyboard.getKeyboard();
        List<KeyboardRow> newRows = new ArrayList<KeyboardRow>();

        for (KeyboardRow row : rows) {
            KeyboardRow newRow = new KeyboardRow();
            for (KeyboardButton button : row) {
                if (button.getText().equals(buttonText)) {
                    button.setRequestContact(true);
                }
                newRow.add(button);
            }
            newRows.add(newRow);
        }
        keyboard.setKeyboard(newRows);

        return keyboard;
    }

    public static ReplyKeyboardMarkup addLocationRequestInKeyboard(ReplyKeyboardMarkup keyboard, String buttonText) {
        List<KeyboardRow> rows = keyboard.getKeyboard();
        List<KeyboardRow> newRows = new ArrayList<KeyboardRow>();

        for (KeyboardRow row : rows) {
            KeyboardRow newRow = new KeyboardRow();
            for (KeyboardButton button : row) {
                if (button.getText().equals(buttonText)) {
                    button.setRequestLocation(true);
                }
                newRow.add(button);
            }
            newRows.add(newRow);
        }
        keyboard.setKeyboard(newRows);

        return keyboard;
    }
}
