import {
  ADD_NEW_START_CHAT_GROUP,
  ADD_NEW_START_CHAT_GROUP_FAIL,
  CHATLIST_FETCHED,
  CHATLIST_REFETCHED,
  MESSAGE_HEADER_FETCHED,
  MESSAGE_PANEL_FETCHED,
  NEW_MESSAGE_IN_PANEL_FETCHED,
  REMOVE_START_CHAT_GROUP,
  START_CHAT_GROUP,
  START_CHAT_SINGLE,
  USER_SELECTED,
  WEBSOCKET_FETCHED,
} from "../actions/chatAction";
import * as actionTypes from "../actions/actionTypes";

const initialState = {
  chatList: [],
  messageItems: [],
  messageHeader: {},
  webSocket: null,
  currentSessionId: null,
  startChatGroupList: [],
  startChatGroupError: false,
  startChatGroupErrorMessage: "",
  waitingGroupUsernames: [],
  userSelectedKeys: [],
  members: [],
  isOwner: false,
};

export default function reduce(state = initialState, action) {
  switch (action.type) {
    case CHATLIST_FETCHED:
      return {
        ...state,
        chatList: action.fetchedChatList,
        messageHeader: action.messageHeader,
        currentSessionId: action.currentSessionId,
      };
    case CHATLIST_REFETCHED:
      return {
        ...state,
        chatList: action.fetchedChatList,
      };
    case USER_SELECTED:
      return {
        ...state,
        userSelectedKeys: action.userSelectedKeys,
      };
    case ADD_NEW_START_CHAT_GROUP_FAIL:
      return {
        ...state,
        startChatGroupError: true,
        startChatGroupErrorMessage: action.error,
      };
    case ADD_NEW_START_CHAT_GROUP:
      return {
        ...state,
        startChatGroupError: false,
        startChatGroupErrorMessage: "",
        startChatGroupList: action.startChatGroupList,
      };
    case REMOVE_START_CHAT_GROUP:
      return {
        ...state,
        startChatGroupList: action.startChatGroupList,
      };
    case START_CHAT_GROUP:
      return {
        ...state,
        messageItems: action.messageItems,
        waitingGroupUsernames: action.waitingGroupUsernames,
        currentSessionId: action.currentSessionId,
        startChatGroupList: [],
      };
    case START_CHAT_SINGLE:
      return {
        ...state,
        messageItems: action.messageItems,
        waitingGroupUsernames: action.waitingGroupUsernames,
        currentSessionId: action.currentSessionId,
      };
    case MESSAGE_PANEL_FETCHED:
      return {
        ...state,
        messageItems: action.messageItems,
        waitingGroupUsernames: [],
        currentSessionId: action.currentSessionId,
        chatList: action.chatList,
        userSelected: action.userSelected,
      };
    case NEW_MESSAGE_IN_PANEL_FETCHED:
      return {
        ...state,
        messageItems: action.messageItems,
        chatList: action.chatList,
      };
    case MESSAGE_HEADER_FETCHED:
      return {
        ...state,
        messageHeader: action.messageHeader,
      };
    case WEBSOCKET_FETCHED:
      return {
        ...state,
        webSocket: action.webSocket,
      };
    case actionTypes.FETCH_MEMBERS:
      return {
        ...state,
        members: action.members,
        isOwner: action.isOwner,
      };
    default:
      return state;
  }
}
