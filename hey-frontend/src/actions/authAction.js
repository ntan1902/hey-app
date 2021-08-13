import * as actionTypes from "./actionTypes";
import { bindActionCreators } from "redux";
import { AuthAPI } from "../api";

const onLogin = (data) => {
  return {
    type: actionTypes.LOGIN,
    data,
  };
};

const onLogout = () => {
  return {
    type: actionTypes.LOGOUT,
  };
};

const onGetProfile = (data) => {
  return {
    type: actionTypes.GET_PROFILE,
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
      console.log("Current Account: ", account);
      await AuthAPI.register(account);
      resolve({ success: true });
    } catch (err) {
      console.log(err.response);
      reject({ data: err.response.data.message, success: false });
    }
  });
};

export const authentication = (account) => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      console.log(account);
      console.log(AuthAPI);
      const res = await AuthAPI.login(account);
      console.log(res);
      const data = {
        isLogin: res.data.success,
        token: res.data.payload.accessToken,
        refreshToken: res.data.payload.refreshToken,
        success: true,
      };
      await dispatch(onLogin(data));
      // await dispatch(getProfile());
      // await dispatch(setToken(res.data.payload.accessToken));

      resolve({ data: data, success: true });
    } catch (err) {
      let message = "";
      console.log("error in action", err);
      switch (err.response) {
        case 401:
          message = "Username or password incorrect";
          break;
        default:
          message = "Unknown status error";
      }
      reject({ data: err, success: false });
    }
  });
};

export const loadLocal = () => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      // const token = await AsyncStorage.getItem("TOKEN");
      // console.log(token);
      // if (token) {
      //   const data = {
      //     isLogin: true,
      //     token: JSON.parse(token),
      //     success: true,
      //   };
      //   await dispatch(onLogin(data));
      //   await dispatch(getProfile());
      //   resolve({ success: true });
      // }
    } catch (err) {
      reject(err);
    }
  });
};

export const logOut = () => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      await dispatch(onLogout());
      await dispatch(resetToken());

      resolve({ success: true });
    } catch (err) {
      reject(err);
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

export const changePassword = (currentPassword, newPassword) => async (dispatch) => {
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
  logOut,
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
