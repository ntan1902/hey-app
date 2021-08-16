import {store} from "../store";
import {loadChatContainer, startNewChatSingle} from "./chatAction";
import {api} from "../api/api";
import {isEmptyString} from "../utils/utils";
import deepcopy from "deepcopy";
import {AuthAPI} from "../api";
import {message} from "antd";
import {ChatAPI} from "../api/chat";

export const ADDRESSBOOK_FETCHED = "addressBook.ADDRESSBOOK_FETCHED";
export const WAITINGFRIEND_FETCHED = "addressBook.WAITINGFRIEND_FETCHED";

export const ADD_FRIEND_FAIL = "addressBook.ADD_FRIEND_FAIL";
export const ADD_FRIEND = "addressBook.ADD_FRIEND";
export const ADD_FRIEND_POPUP_STATE = "addressBook.ADD_FRIEND_POPUP_STATE";
export const EMPTY = "addressBook.EMPTY";
export const TOP_UP = "addressBook.TOPUP";

export function loadAddressBookList() {
    return function (dispatch) {
        return getAddressBookList().then((result) => {
            dispatch(receivedAddressBook(result));
        });
    };
}

export function loadWaitingFriendList() {
    return function (dispatch) {
        return getWaitingFriendList().then((result) => {
            dispatch(receivedWaitingFriend(result));
        });
    };
}

export function receivedAddressBook(addressbook) {
    const fetchedAddressBook = addressbook;
    let fetchedNewAddressBookList = store.getState().addressBookReducer
        .newAddressBookList;
    return {
        type: ADDRESSBOOK_FETCHED,
        fetchedAddressBookList: fetchedAddressBook,
        fetchedNewAddressBookList: fetchedNewAddressBookList,
    };
}

export function receivedWaitingFriend(addressbook) {
    return {
        type: WAITINGFRIEND_FETCHED,
        waitingFriendList: addressbook,
    };
}

export function handleChangeAddressBook(userId) {
    return function (dispatch) {
        api
            .post(
                `/api/protected/sessionidbyuserid`,
                createGetSessionIdRequest(userId)
            )
            .then((result) => {
                dispatch(receivedSessionId(result, userId));
            });
    };
}

export function receivedSessionId(result, userId) {
    if (result.data.payload.sessionId != "-1") {
        store.dispatch(loadChatContainer(result.data.payload.sessionId));
    } else {
        store.dispatch(startNewChatSingle(userId));
    }
    return {type: EMPTY};
}

export function addNewFriend(userId) {
    if (isEmptyString(userId)) {
        let error = "Please input username :(";
        return {type: ADD_FRIEND_FAIL, error: error};
    } else {
        return async function (dispatch) {
            const res = await AuthAPI.getUsername(userId);
            return api
                .post(
                    `/api/protected/addfriend`,
                    createAddFriendRequest(res.data.payload.username)
                )
                .then((result) => {
                    console.log(result);
                    dispatch(rejectWaitingFriend(userId));
                    dispatch(receiveAddFriendResult(result));
                    dispatch(loadWaitingFriendList());
                });
        };
    }
}

export function addNewFriendRequest(username) {
    ChatAPI.addFriendRequest(username)
        .then((res) => {
            console.log(res);
            message.success("Sending friend request to " + username);
        })
        .catch(err => {
            message.error(err.message)
        })
    return {type: EMPTY};
}

export function rejectWaitingFriend(userName) {
    if (isEmptyString(userName)) {
        let error = "Please input username :(";
        return {type: ADD_FRIEND_FAIL, error: error};
    } else {
        return function (dispatch) {
            return api
                .post(
                    `/api/protected/closewaitingfriend`,
                    createGetSessionIdRequest(userName)
                )
                .then((result) => {
                    console.log(result);
                    dispatch(loadWaitingFriendList());
                });
        };
    }
}

export function receiveAddFriendResult(result) {
    if (result.data.error) {
        let error = result.data.error.message;
        return {type: ADD_FRIEND_FAIL, error: error};
    } else {
        let newAddressBookList = deepcopy(
            store.getState().addressBookReducer.newAddressBookList
        );
        let newFriend = {
            name: result.data.payload.item.name,
            userId: result.data.payload.item.userId,
            avatar: processUsernameForAvatar(result.data.payload.item.name),
            status: result.data.payload.item.status,
            isOnline: result.data.payload.item.online,
        };

        newAddressBookList.push(newFriend);
        return {type: ADD_FRIEND, newAddressBookList: newAddressBookList};
    }
}

export function changeStateAddFriendPopup(state) {
    return {type: ADD_FRIEND_POPUP_STATE, popupstate: state};
}

export function changeStateTopup(state) {
    return {type: TOP_UP, topupstate: state};
}

export function changeUserOnlineStatus(userId, status) {
    let fetchedAddressBook = deepcopy(
        store.getState().addressBookReducer.addressBookList
    );
    let fetchedNewAddressBook = deepcopy(
        store.getState().addressBookReducer.newAddressBookList
    );
    for (let i = 0; i < fetchedAddressBook.length; i++) {
        if (fetchedAddressBook[i].userId == userId) {
            fetchedAddressBook[i].isOnline = status;
        }
    }
    for (let i = 0; i < fetchedNewAddressBook.length; i++) {
        if (fetchedNewAddressBook[i].userId == userId) {
            fetchedNewAddressBook[i].isOnline = status;
        }
    }
    let onlineResults = [];
    let offlineResults = [];
    for (let index = 0; index < fetchedAddressBook.length; ++index) {
        if (fetchedAddressBook[index].isOnline) {
            onlineResults.push(fetchedAddressBook[index]);
        } else {
            offlineResults.push(fetchedAddressBook[index]);
        }
    }

    onlineResults.sort(function (a, b) {
        if (a.name < b.name) return -1;
        if (a.name > b.name) return 1;
        return 0;
    });
    offlineResults.sort(function (a, b) {
        if (a.name < b.name) return -1;
        if (a.name > b.name) return 1;
        return 0;
    });

    fetchedAddressBook = onlineResults.concat(offlineResults);

    return {
        type: ADDRESSBOOK_FETCHED,
        fetchedAddressBookList: fetchedAddressBook,
        fetchedNewAddressBookList: fetchedNewAddressBook,
    };
}

function processUsernameForAvatar(username) {
    let x1 = username.charAt(0);
    let x2 = username.charAt(1);
    return x1 + " " + x2;
}

function getAddressBookList() {
    return new Promise(function (resolve, reject) {
        api.get(`/api/protected/addressbook`).then((res) => {
            let items = res.data.payload.items;
            console.log("Friend Result", items);

            let results = [];
            let onlineResults = [];
            let offlineResults = [];
            for (let index = 0; index < items.length; ++index) {
                let addressbookItem = {
                    name: items[index].name,
                    userId: items[index].userId,
                    avatar: processUsernameForAvatar(items[index].name),
                    status: items[index].status,
                    isOnline: items[index].online,
                };
                if (items[index].online) {
                    onlineResults.push(addressbookItem);
                } else {
                    offlineResults.push(addressbookItem);
                }
                onlineResults.sort(function (a, b) {
                    if (a.name < b.name) return -1;
                    if (a.name > b.name) return 1;
                    return 0;
                });
                offlineResults.sort(function (a, b) {
                    if (a.name < b.name) return -1;
                    if (a.name > b.name) return 1;
                    return 0;
                });

                results = onlineResults.concat(offlineResults);
            }
            console.log("Friend Result", results);
            resolve(results);
        });
    });
}

function getWaitingFriendList() {
    return new Promise(function (resolve, reject) {
        api.get(`/api/protected/waitingfriend`).then((res) => {
            let items = res.data.payload.items;
            console.log("Friend Result", items);

            let results = [];
            let onlineResults = [];
            let offlineResults = [];
            for (let index = 0; index < items.length; ++index) {
                let addressbookItem = {
                    name: items[index].name,
                    userId: items[index].userId,
                    avatar: processUsernameForAvatar(items[index].name),
                    status: items[index].status,
                    isOnline: items[index].online,
                };
                if (items[index].online) {
                    onlineResults.push(addressbookItem);
                } else {
                    offlineResults.push(addressbookItem);
                }
                onlineResults.sort(function (a, b) {
                    if (a.name < b.name) return -1;
                    if (a.name > b.name) return 1;
                    return 0;
                });
                offlineResults.sort(function (a, b) {
                    if (a.name < b.name) return -1;
                    if (a.name > b.name) return 1;
                    return 0;
                });

                results = onlineResults.concat(offlineResults);
            }
            console.log("Friend Result", results);
            resolve(results);
        });
    });
}

function createAddFriendRequest(username) {
    return {
        username: username,
    };
}

function createGetSessionIdRequest(userId) {
    return {
        userId: userId,
    };
}
