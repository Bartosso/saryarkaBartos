package com.turlygazhy.service;

import com.turlygazhy.command.Command;
//import com.turlygazhy.command.impl.AddToGoogleSheetsCommand;
import com.turlygazhy.command.impl.AddToGoogleSheetsCommand;
import com.turlygazhy.command.impl.DeclineRequestToGoogleSheetsCommand;
import com.turlygazhy.command.impl.HelpFoundCommand;
import com.turlygazhy.command.impl.WaitHelpInGroupCommand;
import com.turlygazhy.entity.Button;
import com.turlygazhy.exception.CommandNotFoundException;

import java.sql.SQLException;

/**
 * Created by user on 1/2/17.
 */
public class CommandService extends Service {

    public Command getCommand(String text) throws SQLException, CommandNotFoundException {
        if (text != null) {
            String[] split = text.split("/");
            if (split[0].equals(buttonDao.getButtonText(52))) {
                return new AddToGoogleSheetsCommand(split[1],true);
            }
            if (split[0].equals(buttonDao.getButtonText(244))) {
                return new AddToGoogleSheetsCommand(split[1],false);
            }
            if (split[0].equals(buttonDao.getButtonText(53))) {
                return new DeclineRequestToGoogleSheetsCommand(split[1]);
            }
            if (split[0].equals(constDao.select(4))) {
                return new HelpFoundCommand(split[1]);
            }
            if (split[0].equals(constDao.select(5))) {
                return new WaitHelpInGroupCommand();
            }
        }
        Button button = buttonDao.getButton(text);
        return commandDao.getCommand(button.getCommandId());
    }
}
