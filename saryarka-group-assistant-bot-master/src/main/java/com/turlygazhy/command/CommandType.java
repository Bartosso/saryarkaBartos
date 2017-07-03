package com.turlygazhy.command;

import com.turlygazhy.exception.NotRealizedMethodException;

/**
 * Created by user on 1/1/17.
 */
public enum CommandType {
    SHOW_INFO(1),
    CHANGE_INFO(2),
    ADD_TO_LIST(3),
    DELETE_FROM_LIST(4),
    SHOW_ALL_LIST(5),
    INFORM_ADMIN(6),
    REQUEST_CALL(7),
    PUT_TEXT_INSTEAD_BUTTON(8),
    COLLECT_INFO_COMMAND(9),
    SHOW_INFO_ABOUT_MEMBER(10),
    CHANGE_NISHA(11),
    CHANGE_NAVIKI(12),
    KEY_WORDS(13),
    SEARCH(14),
    WANT_TO_GROUP(15),
    CHANGE_MESSAGE_TEXT(16),
    CHANGE_FIO(17),
    CHANGE_COMPANY(18),
    CHANGE_CONTACT(19),
    CHANGE_PHONE_NUMBER(20),
    ADD_TO_GOOGLE_SHEETS(21),
    DECLINE_REQUEST_TO_GOOGLE_SHEETS(22),
    MAKE_ME_ADMIN(23),
    TRY_AGAIN_ADD_TO_GOOGLE(24),
    CHANGE_PASSWORD(25),
    SHOW_REQUESTS(26),
    CHANGE_REQUEST_TEXT(27),
    ADD_TO_REQUESTS_LIST(28),
    ADD_TO_OFFER_LIST(29),
    CREATE_EVENT_VOTE(30),
    EVENT_VOTE(31),
    GET_ALL_FROM_LIST(32),
    MAKE_BID(33),
    REQUEST_TO_ADD_PARTNER(34),
    SEARCH_MEMBERS(35),
    SHOW_EVENTS_MENU(36),
    CABINET(37),
    SHOW_WILL_BE_EVENTS(38),
    EVENT_CREATE_FROM_MEMBER(39),
    SOLUTION_FOR_EVENT_FROM_ADMIN(40),
    CREATE_A_NEW_VACANCY(41),
    GET_EVENT(42),
    SEARCH_MEMBER_ITERATION(43),
    SOLUTION_FOR_PARTNER(44),
//    SHOW_MY_PARTNERS(45),
    GET_PARTNER(46),
    GET_TENDER(47),
    GET_MY_TENDERS(48),
    DISCOUNT_MENU(49),
    CREATE_DISCOUNT_FROM_MEMBER(50),
    SOLUTION_FOR_DISCOUNT_FROM_ADMIN(51),
    EDIT_DISCOUNT(52),
    CHANGE_DISCOUNT_TYPE(53),
    GET_DISCOUNT(54),
    COMMUNITY_MENU(55),
    HR_MENU(56),
    SHOW_EVENTS_WITH_ITERATOR(57),
    GET_MY_VACANCIES(58),
    GET_VACANCY(59),
    DELETE_VACANCY(60),
    SHOW_ALL_VACANCIES(61),
    SHOW_VACANCIES_BY_SFERA(62),
    CREATE_DISCOUNT_VERSION2(63),
    SHOW_DISCOUNT_FOR_ADMIN(64),
    DELETE_DISCOUNT(65),
    LIBRARY_MENU(66),
    ADD_NEW_BOOK(67),
    ADMIN_BOOK_VIEW(68),
    GET_BOOK(69),
    DELETE_BOOK(70),
    SHOW_BOOKS_TO_MEMBER(71),
    SHOW_DOWNLOADED_BOOKS(72),
    DONATE_BOOK(73),
    INFO_VOTE(74),
    SHOW_INFO_WITH_VOTE(75),
    GET_LINK_TO_GROUP(76),
    MAKE_VACANCY_BE(77),
    SOLUTION_FOR_REQUEST_TENDER(78),
    SOLUTION_FOR_OFFER_TENDER(79);


    private final int id;

    CommandType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CommandType getType(long id) {
        for (CommandType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new NotRealizedMethodException("There are no type for id: " + id);
    }
}
