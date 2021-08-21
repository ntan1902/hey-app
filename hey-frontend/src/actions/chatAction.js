import React from "react";
import { store } from "../store";
import { API_WS } from "../config/setting";
import Sockette from "sockette";
import {
  getJwtFromStorage,
  getUserIdFromStorage,
  isEmptyString,
} from "../utils/utils";
import deepcopy from "deepcopy";
import { Button, message, notification } from "antd/lib/index";
import {
  changeUserOnlineStatus,
  loadAddressBookList,
  loadWaitingFriendList,
} from "./addressBookAction";
import { changeOffset, newTransferStatement } from "./paymentAction";

import { statusNotification } from "../components/status-notification";
import { AuthAPI, PaymentAPI } from "../api";
import { ChatAPI } from "../api/chat";
import { bindActionCreators } from "redux";
import * as actionTypes from "./actionTypes";
import videoCallUtils from "../utils/videoCallUtils";

export const EMPTY = "chatList.EMPTY";
export const CHATLIST_FETCHED = "chatList.CHATLIST_FETCHED";
export const CHATLIST_REFETCHED = "chatList.CHATLIST_REFETCHED";
export const MESSAGE_HEADER_FETCHED = "chatList.MESSAGE_HEADER_FETCHED";
export const MESSAGE_PANEL_FETCHED = "chatList.MESSAGE_PANEL_FETCHED";
export const NEW_MESSAGE_IN_PANEL_FETCHED =
  "chatList.NEW_MESSAGE_IN_PANEL_FETCHED";
export const ADD_NEW_START_CHAT_GROUP = "chatList.ADD_NEW_START_CHAT_GROUP";
export const REMOVE_START_CHAT_GROUP = "chatList.REMOVE_START_CHAT_GROUP";
export const START_CHAT_GROUP = "chatList.START_CHAT_GROUP";
export const START_CHAT_SINGLE = "chatList.START_CHAT_SINGLE";
export const ADD_NEW_START_CHAT_GROUP_FAIL =
  "chatList.ADD_NEW_START_CHAT_GROUP_FAIL";
export const USER_SELECTED = "chatList.USER_SELECTED";
export const WEBSOCKET_FETCHED = "chatList.WEBSOCKET_FETCHED";

export function initialWebSocket() {
  const jwt = getJwtFromStorage();
  const webSocket = new Sockette(API_WS + "?jwt=" + jwt, {
    timeout: 5e3,
    maxAttempts: 100,
    onopen: (e) => {},
    onmessage: (e) => {
      var data = JSON.parse(e.data);
      switch (data.type) {
        case "CHAT_ITEMS_RESPONSE":
          store.dispatch(changeMessageItems(data.chatItems, data.sessionId));
          break;
        case "CHAT_MESSAGE_RESPONSE":
          store.dispatch(receivedNewMessage(data));
          break;
        case "CHAT_NEW_SESSION_RESPONSE":
          store.dispatch(receivedNewChatSession(data));
          break;
        case "USER_ONLINE_RESPONSE":
          store.dispatch(receivedUserOnline(data));
          break;
        case "USER_OFFLINE_RESPONSE":
          store.dispatch(receivedUserOffline(data));
          break;
        case "ADD_FRIEND_RESPONSE":
          message.success("New Friend Request !!!");
          store.dispatch(loadWaitingFriendList());
          break;
        case "ACCEPT_FRIEND_RESPONSE":
          message.success("Accept Friend Request !!!");
          store.dispatch(loadAddressBookList());
          break;
        case "HAVE_CALL":
          let type = data.videoCall ? "video call" : "all";
          let description = `You have a ${type} from ${data.groupName}`;
          const key = `open${Date.now()}`;
          let btn = (
            <div style={{ display: "flex" }}>
              <Button
                type="danger"
                style={{ marginRight: 10 }}
                onClick={() => {
                  notification.close(key);
                  videoCallUtils.rejectCall(data.sessionId);
                }}
              >
                Reject
              </Button>
              <Button
                type="primary"
                onClick={() => {
                  notification.close(key);
                  videoCallUtils.acceptCall(data.sessionId, data.videoCall);
                }}
              >
                Join
              </Button>
            </div>
          );
          notification.open({
            message: data.videoCall ? "Video Call" : "Call",
            description,
            btn,
            key,
            duration: 100,
          });
      }
    },
    onreconnect: (e) => console.log("Reconnecting...", e),
    onmaximum: (e) => console.log("Stop Attempting!", e),
    onclose: (e) => console.log("Closed!", e),
    onerror: (e) => console.log("Error:", e),
  });
  return { type: WEBSOCKET_FETCHED, webSocket: webSocket };
}

export function closeWebSocket() {
  store.getState().chatReducer.webSocket.close();
  return { type: EMPTY };
}
function createLoadNewAddFriendRequest(sessionId) {
  const req = {
    type: "ADD_FRIEND_REQUEST",
    sessionId: sessionId,
  };
  return req;
}

export function loadChatList() {
  return function (dispatch) {
    return getChatList().then((result) => {
      dispatch(receivedChatList(result));
    });
  };
}

export function loadNewAddFriend(sessionId) {
  store
    .getState()
    .chatReducer.webSocket.json(createLoadNewAddFriendRequest(sessionId));
  message.success("Sending friend request to " + sessionId);
  return { type: EMPTY };
}

export function reloadChatList() {
  return function (dispatch) {
    return getChatList().then((result) => {
      dispatch(receivedReloadChatList(result));
    });
  };
}

export function loadChatContainer(sessionId) {
  store
    .getState()
    .chatReducer.webSocket.json(createLoadChatContainerRequest(sessionId));
  return { type: EMPTY };
}

export function addFriendToSession(sessionId, userId) {
  ChatAPI.addFriendToSession(sessionId, userId)
    .then((res) => {
      console.log(res);
      message.success("Add friend to session id");
    })
    .catch((err) => {
      message.error(err.message);
    });
  return { type: EMPTY };
}

export function specialLoadChatContainer(sessionId) {
  let chatList = [];
  let timeout = function () {
    try {
      store
        .getState()
        .chatReducer.webSocket.json(createLoadChatContainerRequest(sessionId));
      if (store.getState().chatReducer.chatList.length > 0) {
        chatList = deepcopy(store.getState().chatReducer.chatList);
        for (var i = 0; i < chatList.length; i++) {
          if (chatList[i].sessionId == sessionId) {
            chatList[i].unread = 0;
          }
        }
      }
    } catch (e) {
      setTimeout(timeout, 500);
    }
  };
  timeout();

  return { type: EMPTY };
}

export function submitChatMessage(message) {
  let sessionId = store.getState().chatReducer.currentSessionId;
  let waitingGroupUsernames =
    store.getState().chatReducer.waitingGroupUsernames;

  let groupName = store.getState().chatReducer.messageHeader.title;
  store
    .getState()
    .chatReducer.webSocket.json(
      createChatMessageRequest(
        sessionId,
        message,
        waitingGroupUsernames,
        groupName
      )
    );
  return { type: EMPTY };
}

export function receivedChatList(chatList) {
  const fetchedChatList = chatList;
  let header = {};
  if (fetchedChatList.length > 0) {
    // header = {
    //   title:
    //     fetchedChatList[0].groupName === ""
    //       ? fetchedChatList[0].name
    //       : fetchedChatList[0].groupName,
    //   avatar: fetchedChatList[0].avatar,
    //   group: fetchedChatList[0].group,
    // };
    // store.dispatch(specialLoadChatContainer(fetchedChatList[0].sessionId));
  }

  return {
    type: CHATLIST_FETCHED,
    fetchedChatList: fetchedChatList,
    messageHeader: header,
    currentSessionId:
      fetchedChatList.length > 0 ? fetchedChatList[0].sessionId : null,
  };
}

export function receivedReloadChatList(chatList) {
  const fetchedChatList = chatList;
  return { type: CHATLIST_REFETCHED, fetchedChatList: fetchedChatList };
}

export function receivedNewMessage(message) {
  console.log("New Message", message);

  let currentSessionId = store.getState().chatReducer.currentSessionId;
  let userId = getUserIdFromStorage();
  let userSelected = store.getState().chatReducer.userSelected;
  let messageItems = [];
  if (store.getState().chatReducer.messageItems.length > 0) {
    messageItems = deepcopy(store.getState().chatReducer.messageItems);
  }

  let chatList = [];
  if (store.getState().chatReducer.chatList.length > 0) {
    chatList = deepcopy(store.getState().chatReducer.chatList);
  }

  if (currentSessionId == message.sessionId) {
    let type = 1;
    if (message.userId != userId) {
      type = 2;
    }
    let showAvatar = true;
    if (
      messageItems.length > 0 &&
      messageItems[0].type == type &&
      messageItems[0].userId == message.userId
    ) {
      showAvatar = false;
    }
    let messageItem = {
      message: message.message,
      type: type,
      showavatar: showAvatar,
      avatar: processUsernameForAvatar(message.name),
      userId: message.userId,
      createdDate: new Date(message.createdDate).toLocaleString(),
    };
    messageItems.unshift(messageItem);

    //re-arrange chat list
    for (var i = 0; i < chatList.length; i++) {
      if (chatList[i].sessionId == message.sessionId) {
        userSelected = message.sessionId;
        chatList[i].lastMessage = message.message;
        var temp = chatList[i];
        chatList.splice(i, 1);
        chatList.unshift(temp);
        break;
      }
    }
  } else {
    //re-arrange chat list
    for (var i = 0; i < chatList.length; i++) {
      if (chatList[i].sessionId == message.sessionId) {
        chatList[i].lastMessage = message.message;
        chatList[i].unread = chatList[i].unread + 1;
        var temp = chatList[i];
        chatList.splice(i, 1);
        chatList.unshift(temp);
        break;
      }
    }
  }

  // Realtime get transfer statement
  if (message.transferStatement) {
    PaymentAPI.getTransferStatements(0, 1).then((res) => {
      store.dispatch(newTransferStatement(res.data.payload[0]));
      store.dispatch(changeOffset(store.getState().paymentReducer.offset + 1));
    });
  }

  return {
    type: NEW_MESSAGE_IN_PANEL_FETCHED,
    messageItems: messageItems,
    chatList: chatList,
    userSelected: userSelected,
  };
}

export function receivedNewChatSession(message) {
  if (store.getState().chatReducer.currentSessionId === "-1") {
    store.dispatch(loadChatContainer(message.sessionId));
  }
  store.dispatch(reloadChatList());
  store.dispatch(userSelected(message.sessionId));

  // Realtime get transfer statement
  if (message.transferStatement) {
    PaymentAPI.getTransferStatements(0, 1).then((res) => {
      store.dispatch(newTransferStatement(res.data.payload[0]));
      store.dispatch(changeOffset(store.getState().paymentReducer.offset + 1));
    });
  }
  return { type: EMPTY };
}

export function changeMessageItems(chatItems, sessionId) {
  const messageItems = getMessageItems(chatItems);
  let chatList = [];
  if (store.getState().chatReducer.chatList.length > 0) {
    chatList = deepcopy(store.getState().chatReducer.chatList);
    for (var i = 0; i < chatList.length; i++) {
      if (chatList[i].sessionId == sessionId) {
        chatList[i].unread = 0;
      }
    }
    store.dispatch(userSelected(sessionId));
  }
  return {
    type: MESSAGE_PANEL_FETCHED,
    messageItems: messageItems,
    currentSessionId: sessionId,
    chatList: chatList,
  };
}

export function changeMessageHeader(title, avatar, group) {
  const header = {
    title: title,
    avatar: avatar,
    group: group,
  };
  return { type: MESSAGE_HEADER_FETCHED, messageHeader: header };
}

export function addNewUserChatGroup(userId) {
  if (isEmptyString(userId)) {
    let error = "Please input username :(";
    return { type: ADD_NEW_START_CHAT_GROUP_FAIL, error: error };
  } else {
    return async function (dispatch) {
      const res = await AuthAPI.getUsername(userId);
      const userName = res.data.payload.username;
      return ChatAPI.usernameExisted(
        createCheckUsernameExistedRequest(userName)
      )
        .then((result) => {
          dispatch(receiveNewUserChatGroup(result));
        })
        .catch((err) => console.log(err.response));
    };
  }
}

export function removeUserChatGroup(userName) {
  let startChatGroupList = deepcopy(
    store.getState().chatReducer.startChatGroupList
  );
  let index = startChatGroupList.indexOf(userName);
  if (index > -1) {
    startChatGroupList.splice(index, 1);
  }

  return {
    type: REMOVE_START_CHAT_GROUP,
    startChatGroupList: startChatGroupList,
  };
}

export function receiveNewUserChatGroup(result) {
  if (!result.data.payload.existed) {
    let error = "Username is not existed :(";
    return { type: ADD_NEW_START_CHAT_GROUP_FAIL, error: error };
  } else {
    let startChatGroupList = deepcopy(
      store.getState().chatReducer.startChatGroupList
    );
    startChatGroupList.push(result.data.payload.username);
    return {
      type: ADD_NEW_START_CHAT_GROUP,
      startChatGroupList: startChatGroupList,
    };
  }
}

export function startNewChatGroup(groupName) {
  if (store.getState().chatReducer.startChatGroupList.length > 1) {
    let messageItems = [];
    let waitingGroupUsernames = store.getState().chatReducer.startChatGroupList;
    let currentSessionId = "-1";
    ChatAPI.waitingChatHeader(
      createWaitingChatHeaderRequest(waitingGroupUsernames, groupName)
    )
      .then((res) => {
        store.dispatch(changeMessageHeader(res.data.payload.title, "", true));
      })
      .catch((err) => {
        console.log("ERR", err.response);
      });
    return {
      type: START_CHAT_GROUP,
      messageItems: messageItems,
      waitingGroupUsernames: waitingGroupUsernames,
      currentSessionId: currentSessionId,
    };
  } else {
    message.error(
      "Sorry, but a group chat must contains more than 2 people :("
    );
    return {
      type: EMPTY,
    };
  }
}

export function startNewChatSingle(userId) {
  let messageItems = [];
  let waitingGroupUsernames = [userId];
  let currentSessionId = "-1";
  return {
    type: START_CHAT_SINGLE,
    messageItems: messageItems,
    waitingGroupUsernames: waitingGroupUsernames,
    currentSessionId: currentSessionId,
  };
}

export function receivedUserOnline(res) {
  var userId = res.userId;
  var userFullname = res.fullName;
  statusNotification.onlineNotification(userFullname);
  store.dispatch(changeUserOnlineStatus(userId, true));
  return {
    type: EMPTY,
  };
}

export function receivedUserOffline(res) {
  var userId = res.userId;
  var userFullname = res.fullName;
  statusNotification.offlineNotification(userFullname);
  store.dispatch(changeUserOnlineStatus(userId, false));
  return {
    type: EMPTY,
  };
}

export function userSelected(sessionId) {
  var userSelectedKeys = [sessionId];
  return {
    type: USER_SELECTED,
    userSelectedKeys: userSelectedKeys,
  };
}

function getMessageItems(chatItems) {
  var userId = getUserIdFromStorage();
  var results = [];
  for (var i = 0; i < chatItems.length; i++) {
    var type = 1;
    if (chatItems[i].userId != userId) {
      type = 2;
    }
    var showAvatar = true;
    if (
      i > 0 &&
      results[i - 1].type == type &&
      results[i - 1].userId == chatItems[i].userId
    ) {
      showAvatar = false;
    }
    var messageItem = {
      message: chatItems[i].message,
      type: type,
      showavatar: showAvatar,
      avatar: processUsernameForAvatar(chatItems[i].name),
      userId: chatItems[i].userId,
      createdDate: new Date(chatItems[i].createdDate).toLocaleString(),
    };
    results.push(messageItem);
  }
  results.reverse();
  return results;
}

function getChatList() {
  return new Promise(function (resolve, reject) {
    ChatAPI.getChatList()
      .then((res) => {
        const items = res.data.payload.items;
        const results = [];
        for (var index = 0; index < items.length; ++index) {
          const chatItem = {
            name: items[index].name,
            sessionId: items[index].sessionId,
            avatar: processUsernameForAvatar(items[index].name),
            lastMessage: items[index].lastMessage,
            unread: items[index].unread,
            groupName: items[index].groupName,
            group: items[index].group,
            updatedDate: items[index].updatedDate,
          };
          results.push(chatItem);
        }
        results.sort(function (a, b) {
          if (a.updatedDate < b.updatedDate) return 1;
          if (a.updatedDate > b.updatedDate) return -1;
          return 0;
        });
        resolve(results);
      })
      .catch((err) => {
        console.log(err.response);
      });
  });
}

function processUsernameForAvatar(username) {
  const x1 = username.charAt(0);
  const x2 = username.charAt(1);
  return x1 + " " + x2;
}

function createLoadChatContainerRequest(sessionId) {
  return {
    type: "CHAT_ITEMS_REQUEST",
    sessionId: sessionId,
  };
}

function createChatMessageRequest(
  sessionId,
  message,
  waitingGroupUsernames,
  groupName
) {
  return {
    type: "CHAT_MESSAGE_REQUEST",
    sessionId: sessionId,
    message: message,
    usernames: waitingGroupUsernames,
    groupName: groupName,
    groupChat: sessionId == "-1",
  };
}

function createCheckUsernameExistedRequest(username) {
  return {
    username: username,
  };
}

function createWaitingChatHeaderRequest(usernames, groupName) {
  return {
    usernames: usernames,
    groupName: groupName,
  };
}

const kickMembers = (sessionId, userId) => async (dispatch) => {
  try {
    let kickMemberRes = await ChatAPI.kickMember(sessionId, userId);
    return Promise.resolve(kickMemberRes);
  } catch (error) {
    return Promise.reject({ error, success: false });
  }
};

const changeGroupTitle = (data) => async (dispatch) => {
  try {
    let changeGroupName = await ChatAPI.changeGroupName(data);
    return Promise.resolve(changeGroupName);
  } catch (error) {
    return Promise.reject({ error, success: false });
  }
};

const fetchMember = (sessionId) => async (dispatch) => {
  try {
    let fetchMemberRes = await ChatAPI.getMembersOfSessionChat(sessionId);
    dispatch({
      type: actionTypes.FETCH_MEMBERS,
      members: fetchMemberRes.data.payload.members,
      isOwner: fetchMemberRes.data.payload.isOwner,
    });
  } catch (err) {
    message.error(err.error.response.data.message);
  }
};

export const chatActions = {
  kickMembers,
  fetchMember,
  changeGroupTitle,
};

export function bindChatActions(currentActions, dispatch) {
  return {
    ...currentActions,
    chatActions: bindActionCreators(chatActions, dispatch),
  };
}
