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
//            case TRY_AGAIN_ADD_TO_GOOGLE:
//                return new TryAgainAddToGoogleCommand();
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
//            case CHANGE_NAVIKI:
//                return new ChangeNavikiCommand();
            case KEY_WORDS:
                return new KeyWordsCommand();
            case SEARCH:
                return new SearchCommand();
            case SHOW_REQUESTS:
                return new ShowRequestCommand();
            case CHANGE_REQUEST_TEXT:
                return new ChangeRequestTextCommand();
            case ADD_TO_REQUESTS_LIST:
                return new AddToRequestsListCommand();
            case ADD_TO_OFFER_LIST:
                return new AddToOfferListCommand();
            case CREATE_EVENT_VOTE:
                return new CreateEventVoteCommand();
            case EVENT_VOTE:
                return new EventVoteCommand();
            case GET_ALL_FROM_LIST:
                return new GetAllFromListCommand();
            case MAKE_BID:
                return new MakeBIdCommand();
            case REQUEST_TO_ADD_PARTNER:
                return new RequestToAddPartnerCommand();
            case SEARCH_MEMBERS:
                return new SearchMembersCommand();
            case SHOW_EVENTS_MENU:
                return new ShowEventsMenuCommand();
            case CABINET:
                return new ShowCabinetCommand();
            case SHOW_WILL_BE_EVENTS:
                return new ShowEventsCommand();
            case EVENT_CREATE_FROM_MEMBER:
                return new EventCreateFromMemberCommand();
            case SOLUTION_FOR_EVENT_FROM_ADMIN:
                return new SolutionForEventFromAdminCommand();
            case CREATE_A_NEW_VACANCY:
                return new CreateANewVacancyCommand();
            case GET_EVENT:
                return new GetEventCommand();
            case SEARCH_MEMBER_ITERATION:
                return new SearchMemberIterationCommand();
            case SOLUTION_FOR_PARTNER:
                return new SolutionForPartnerCommand();
//            case SHOW_MY_PARTNERS:
//                return new ShowMyPartnersCommand();
            case GET_PARTNER:
                return new GetPartnerCommand();
            case GET_TENDER:
                return new GetTenderCommand();
            case GET_MY_TENDERS:
                return new GetMyTendersCommand();
            case DISCOUNT_MENU:
                return new DiscountMenuCommand();
            case CREATE_DISCOUNT_FROM_MEMBER:
                return new CreateDiscountFromMemberCommand();
            case SOLUTION_FOR_DISCOUNT_FROM_ADMIN:
                return new SolutionForDiscountFromAdminCommand();
            case EDIT_DISCOUNT:
                return new EditDiscountCommand();
            case CHANGE_DISCOUNT_TYPE:
                return new ChangeDiscountCategoryCommand();
            case GET_DISCOUNT:
                return new GetDiscountCommand();
            case COMMUNITY_MENU:
                return new CommunityMenuCommand();
            case HR_MENU:
                return new HrMenuCommand();
            case SHOW_EVENTS_WITH_ITERATOR:
                return new ShowEventsLikeIteratorCommand();
            case GET_MY_VACANCIES:
                return new GetMyVacanciesCommand();
            case GET_VACANCY:
                return new GetVacancyCommand();
            case DELETE_VACANCY:
                return new DeleteVacancyCommand();
            case SHOW_ALL_VACANCIES:
                return new ShowAllVacanciesCommand();
            case SHOW_VACANCIES_BY_SFERA:
                return new ShowVacanciesBySferaCommand();
            case CREATE_DISCOUNT_VERSION2:
                return new CreateDiscountViaLinkCommand();
            case SHOW_DISCOUNT_FOR_ADMIN:
                return new ShowDiscountForAdminAndDeleteMenuCommand();
            case DELETE_DISCOUNT:
                return new DeleteDiscountCommand();
            case LIBRARY_MENU:
                return new LibraryMenuCommand();
            case ADD_NEW_BOOK:
                return new AddBookAdminCommand();
            case ADMIN_BOOK_VIEW:
                return new AdminBookViewCommand();
            case GET_BOOK:
                return new GetBookCommand();
            case DELETE_BOOK:
                return new DeleteBookCommand();
            case SHOW_BOOKS_TO_MEMBER:
                return new ShowBooksToMemberCommand();
            case SHOW_DOWNLOADED_BOOKS:
                return new ShowDownloadedBooksCommand();
            case DONATE_BOOK:
                return new DonateAbookCommand();
            case INFO_VOTE:
                return new InfoVoteCommand();
            case SHOW_INFO_WITH_VOTE:
                return new ShowInfoWithVoteCommand();
            case GET_LINK_TO_GROUP:
                return new GetLinkToGroupCommand();
            case MAKE_VACANCY_BE:
                return new MakeVacancyBe();
            case SOLUTION_FOR_REQUEST_TENDER:
                return new SolutionForRequestTenderCommand();
            case SOLUTION_FOR_OFFER_TENDER:
                return new SolutionForOfferTenderCommand();
            case CREATE_REMINDER_WITH_VOTE_IN_GROUP:
                return new CreateReminderWithVoteInGroupCommand();
            case EDIT_OFFER_TENDER_BY_ADMIN:
                return new EditOfferTenderByAdminCommand();
            case EDIT_REQUEST_TENDER_BY_ADMIN:
                return new EditRequestTenderByAdminCommand();
            case EDIT_EVENT_COMMAND:
                return new EditEventCommand();
            case VACANCY_EDIT_COMMAND:
                return new VacancyPreEditMenuCommand();
            case CHANGE_CITY:
                return new ChangeCityCommand();
            default:
                throw new NotRealizedMethodException("Not realized for type: " + type);
        }
    }
}
