import * as actionTypes from "./actionTypes";
import { bindActionCreators } from "redux";
import { AuthAPI } from "../api";

const onLogin = (data) => {
  return {
    type: actionTypes.LOGIN,
    data,
  };
};

export const resetToken = async (token) => {
  try {
    console.log(token);
  } catch (error) {
    // Error saving data
  }
};

export const register = (account) => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      await AuthAPI.register(account);
      resolve({ success: true });
    } catch (err) {
      reject({ data: err.response.data.message, success: false });
    }
  });
};

export const authentication = (account) => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      const res = await AuthAPI.login(account);
      const data = {
        isLogin: res.data.success,
        token: res.data.payload.accessToken,
        refreshToken: res.data.payload.refreshToken,
        success: true,
      };
      dispatch(onLogin(data));

      resolve({ data: data, success: true });
    } catch (err) {
      let message = "";
      switch (err.response.status) {
        case 401:
          message = "Username or password is incorrect. Please try again!";
          break;
        default:
          message = "Unknown status error";
      }
      reject({ data: message, success: false });
    }
  });
};

const getProfile = () => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      const { data } = await AuthAPI.getProfile();
      // await dispatch(onGetProfile(data.payload));
      resolve({ data: data.payload, success: true });
    } catch (err) {
      console.log(err.response);
      reject({ data: err.response.data.message, success: false });
    }
  });
};

const updateProfile = (form) => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      await AuthAPI.updateProfile(form);
      await dispatch(getProfile());
      resolve({ success: true });
    } catch (err) {
      console.log(err.response);
      reject({ data: err.response.data.message, success: false });
    }
  });
};

export const changePassword =
  (currentPassword, newPassword) => async (dispatch) => {
    return new Promise(async (resolve, reject) => {
      try {
        console.log("currentPassword: ", currentPassword);
        await AuthAPI.changePassword(currentPassword, newPassword);
        resolve({ success: true });
      } catch (err) {
        console.log(err.response);
        reject({ data: err.response.data.message, success: false });
      }
    });
  };

export const searchUser = (keyword) => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      const response = await AuthAPI.searchUser(keyword);
      resolve({ data: response.data, success: true });
    } catch (err) {
      console.log(err.response);
      reject({ data: err.response.data.message, success: false });
    }
  });
};

export const authActions = {
  register,
  authentication,
  updateProfile,
  changePassword,
  getProfile,
  searchUser,
};

export function bindAuthActions(currentActions, dispatch) {
  return {
    ...currentActions,
    authActions: bindActionCreators(authActions, dispatch),
  };
}
