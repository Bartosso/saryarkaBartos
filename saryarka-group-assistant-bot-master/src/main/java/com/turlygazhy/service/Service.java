package com.turlygazhy.service;

import com.turlygazhy.dao.*;
import com.turlygazhy.dao.impl.*;

/**
 * Created by user on 1/2/17.
 */
public class Service {
    DaoFactory factory = DaoFactory.getFactory();
    MessageDao messageDao = factory.getMessageDao();
    KeyboardMarkUpDao keyboardMarkUpDao = factory.getKeyboardMarkUpDao();
    ButtonDao buttonDao = factory.getButtonDao();
    CommandDao commandDao = factory.getCommandDao();
    ConstDao constDao = factory.getConstDao();


}
