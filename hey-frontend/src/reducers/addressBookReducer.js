import {
  ADD_FRIEND,
  ADD_FRIEND_FAIL,
  ADD_FRIEND_POPUP_STATE,
  ADDRESSBOOK_FETCHED,
  CHANGE_STATUS,
  TOP_UP,
  WAITINGFRIEND_FETCHED,
} from "../actions/addressBookAction";

const initialState = {
  addressBookList: [],
  waitingFriendList: [],
  newAddressBookList: [],
  addFriendError: false,
  addFriendErrorMessage: "",
  addFriendPopup: false,
  topup: false,
};

export default function reduce(state = initialState, action) {
  switch (action.type) {
    case ADDRESSBOOK_FETCHED:
      return {
        ...state,
        addressBookList: action.fetchedAddressBookList,
        newAddressBookList: action.fetchedNewAddressBookList,
      };
    case WAITINGFRIEND_FETCHED:
      return {
        ...state,
        waitingFriendList: action.waitingFriendList,
      };
    case ADD_FRIEND:
      return {
        ...state,
        addFriendError: false,
        addFriendErrorMessage: "",
        newAddressBookList: action.newAddressBookList,
        addFriendPopup: false,
      };
    case ADD_FRIEND_FAIL:
      return {
        ...state,
        addFriendError: true,
        addFriendErrorMessage: action.error,
      };
    case ADD_FRIEND_POPUP_STATE:
      return {
        ...state,
        addFriendPopup: action.popupstate,
      };
    case TOP_UP:
      return {
        ...state,
        topup: action.topupstate,
      };
    default:
      return state;
  }
}
