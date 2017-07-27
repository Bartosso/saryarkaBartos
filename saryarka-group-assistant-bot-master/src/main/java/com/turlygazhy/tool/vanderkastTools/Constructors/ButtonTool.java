package com.turlygazhy.tool.vanderkastTools.Constructors;



import com.turlygazhy.tool.vanderkastTools.patterns.KeyboardPattern;

import java.util.ArrayList;

/**
 * Created by Vanderkast on 05.07.2017.
 *
 * this class uses to arrangement buttons
 */
public class ButtonTool {
    //returns list of buttons without first line by pattern
    public static ArrayList<String> removeSetButtons(ArrayList<String> buttons, KeyboardPattern pattern) {
        for (int i = 0; i < getButtonsAtLine(pattern); i++) {
            buttons.remove(0);
        }
        return buttons;
    }

    //returns count of lines, which full by buttons
    public static int completeLines(int buttons, KeyboardPattern pattern) {
        return buttons / getButtonsAtLine(pattern);
    }

    //returns buttons in last uncomplete line;
    public static int buttonsInUncompleteLine(int buttons, KeyboardPattern pattern) {
        return buttons % getButtonsAtLine(pattern);
    }

    //returns count of buttons in one line by pattern
    public static int getButtonsAtLine(KeyboardPattern pattern) {
        switch (pattern) {
            case ONE_BUTTON_AT_ROW:
                return 1;
            case TWO_BUTTON_AT_ROW:
                return 2;
            case THREE_BUTTON_AT_ROW:
                return 3;
            default:
                return -1;
        }
    }

}
