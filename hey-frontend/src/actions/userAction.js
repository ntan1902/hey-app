import {api} from "../api/api";

export const CHANGE_TAB = "portal.CHANGE_TAB";
export const REGISTER_SUCCEEDED = "user.REGISTER_SUCCEEDED";
export const REGISTER_FAILED = "user.REGISTER_FAILED";
export const USER_PROFILE = "user.USER_PROFILE";
export const CHANGE_STATUS = "user.CHANGE_STATUS";

export function changeTab(activeTabKey) {
  return { type: CHANGE_TAB, activeTabKey: activeTabKey };
}

export function logout() {
  return { type: "USER_LOGOUT" };
}

export function receivedUserProfile(result) {
  let status = "You are online";
  if (result.data.payload.status !== "") {
    status = result.data.payload.status;
  }
  return {
    type: USER_PROFILE,
    userName: result.data.payload.userName,
    userFullName: result.data.payload.userFullName,
    userStatus: status,
  };
}

export function getProfile() {
  return function (dispatch) {
    api.get(`/api/protected/user`).then((res) => {
      dispatch(receivedUserProfile(res));
    });
  };
}

export function changeUserStatus(status) {
  let userStatus = "You are online";
  if (status !== "") {
    userStatus = status;
  }
  api.post(`/api/protected/status`, createChangeStatusRequest(status));
  return { type: CHANGE_STATUS, userStatus: userStatus };
}

function createChangeStatusRequest(status) {
  return {
    status: status,
  };
}
