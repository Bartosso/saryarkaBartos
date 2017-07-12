package com.turlygazhy.command;

import com.turlygazhy.command.impl.*;
import com.turlygazhy.exception.NotRealizedMethodException;

/**
 * Created by user on 1/2/17.
 */
public class CommandFactory {
    public static Command getCommand(long id) {
        CommandType type = CommandType.getType(id);
        switch (type) {
            case CHANGE_PASSWORD:
                return new ChangePasswordCommand();
            case TRY_AGAIN_ADD_TO_GOOGLE:
                return new TryAgainAddToGoogleCommand();
            case MAKE_ME_ADMIN:
                return new MakeMeAdminCommand();
            case CHANGE_PHONE_NUMBER:
                return new ChangePhoneNumberCommand();
            case CHANGE_CONTACT:
                return new ChangeContactCommand();
            case CHANGE_COMPANY:
                return new ChangeCompanyCommand();
            case CHANGE_FIO:
                return new ChangeFioCommand();
            case CHANGE_MESSAGE_TEXT:
                return new ChangeMessageText();
            case WANT_TO_GROUP:
                return new WantToGroupCommand();
            case SHOW_INFO:
                return new ShowInfoCommand();
            case CHANGE_INFO:
                return new ChangeInfoCommand();
            case ADD_TO_LIST:
                return new AddToListCommand();
            case SHOW_ALL_LIST:
                return new ShowAllListCommand();
            case DELETE_FROM_LIST:
                return new DeleteFromListCommand();
            case INFORM_ADMIN:
                return new InformAdminCommand();
            case REQUEST_CALL:
                return new RequestCallCommand();
            case PUT_TEXT_INSTEAD_BUTTON:
                return new PutTextInsteadButtonCommand();
            case COLLECT_INFO_COMMAND:
                return new CollectInfoCommand();
            case SHOW_INFO_ABOUT_MEMBER:
                return new ShowInfoAboutMemberCommand();
            case CHANGE_NISHA:
                return new ChangeNishaCommand();
            case CHANGE_NAVIKI:
                return new ChangeNavikiCommand();
            case KEY_WORDS:
                return new KeyWordsCommand();
            case SEARCH:
                return new SearchCommand();
            case CREATE_VOTE:
                return new VotingCommand();
            default:
                throw new NotRealizedMethodException("Not realized for type: " + type);
        }
    }
}
