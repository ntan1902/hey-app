import {message} from "antd";
import {AuthAPI} from "../api";
import {ChatAPI} from "../api/chat";

export const CHANGE_TAB = "portal.CHANGE_TAB";
export const REGISTER_SUCCEEDED = "user.REGISTER_SUCCEEDED";
export const REGISTER_FAILED = "user.REGISTER_FAILED";
export const USER_PROFILE = "user.USER_PROFILE";
export const CHANGE_STATUS = "user.CHANGE_STATUS";
export const SET_PROFILE = "user.SET_PROFILE";
export const UPDATE_AVATAR = "user.UPDATE_AVATAR";
export const HAS_PIN = "user.HAS_PIN";

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
    ChatAPI.getUser().then((res) => {
      dispatch(receivedUserProfile(res));
    });
  };
}

export function changeUserStatus(status) {
  let userStatus = "You are online";
  if (status !== "") {
    userStatus = status;
  }
  ChatAPI.updateStatus(createChangeStatusRequest(status));
  return { type: CHANGE_STATUS, userStatus: userStatus };
}

function createChangeStatusRequest(status) {
  const req = {
    status: status,
  };
  return req;
}

const setProfile = (profile) => {
  return {
    type: SET_PROFILE,
    profile,
  };
};

const setHasPin = (hasPin) => {
  return {
    type: HAS_PIN,
    hasPin,
  };
};

const getHasPin = () => async (dispatch) => {
  let res = await AuthAPI.hasPin();
  dispatch(setHasPin(res.data.payload.hasPin));
};

const updateAvatar = (fileAvatar) => async (dispatch) => {
  let formdata = new FormData();
  formdata.append("file", fileAvatar);
  try {
    let uploadImageRes = await AuthAPI.uploadImage(formdata);
    let updateAvatarRes = await AuthAPI.updateAvatar(
      uploadImageRes.data.payload.uri,
      uploadImageRes.data.payload.miniUri
    );
    dispatch({
      type: UPDATE_AVATAR,
      avatar: uploadImageRes.data.payload.uri,
      miniAvatar: uploadImageRes.data.payload.miniUri,
    });
  } catch (error) {
    message.error(error.response.data.payload);
  }
};

export { setProfile, updateAvatar, getHasPin };
