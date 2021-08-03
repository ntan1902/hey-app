import axios from "axios";
import _ from "lodash";
import { store } from "../store";

import { isAuthenticated, getJwtFromStorage } from "./utils";

let callback401 = null;

export function set401Callback(cb) {
  callback401 = cb;
}

const axiosInstance = axios.create();

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    const { response } = error;
    if (
      !_.isEmpty(response) &&
      response.status === 401 &&
      !_.isNull(callback401)
    ) {
      callback401(response.data.error);
    }

    return Promise.reject(error);
  }
);

axiosInstance.interceptors.request.use(
  (config) => {
    if (!isAuthenticated()) {
      const jwt = getJwtFromStorage();
      config.headers.authorization = `Bearer ${jwt}`;
    }

    return config;
  },
  (error) => {
    console.log("Error in axios");
    Promise.reject(error);
  }
);

export { axiosInstance as axios };
