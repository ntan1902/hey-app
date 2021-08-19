import axios from "axios";
import _ from "lodash";
import queryString from "query-string";

import {
  isAuthenticated,
  getJwtFromStorage,
  getRefreshTokenFromStorage,
  setJwtToStorage,
  clearStorage,
  isEmptyString,
} from "./utils";
import { API_AUTH, SITE_URL } from "../config/setting";

const axiosInstance = axios.create({
  baseURL: SITE_URL,
  headers: {
    "content-type": "application/json",
  },
  paramsSerializer: (params) => queryString.stringify(params),
});

const refresh = () => {
  const _refreshToken = getRefreshTokenFromStorage();
  try {
    return axiosInstance.post(`${API_AUTH}/api/v1/users/refreshToken`, {
      refreshToken: _refreshToken,
    });
  } catch (err) {
    console.log(err);
  }
};

axiosInstance.setToken = (token) => {
  axiosInstance.defaults.headers = {
    Authorization: `Bearer ${token}`,
    Accept: "application/json",
  };

  setJwtToStorage(token);
};

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    const { response } = error;
    if (!_.isEmpty(response) && response.status === 401) {
      const refreshToken = getRefreshTokenFromStorage();

      if (!isEmptyString(refreshToken))
        return refresh()
          .then((rs) => {
            axiosInstance.setToken(rs.data.payload.accessToken);

            const config = error.response.config;
            config.headers = {
              Authorization: `Bearer ${rs.data.payload.accessToken}`,
              Accept: "application/json",
            };

            return axiosInstance(config);
          })
          .catch((err) => {
            // Expired soft token
            console.log(err.response);
            const { status } = err.response;
            status === 400 && clearStorage();
            return Promise.reject(err);
          });
    }

    return Promise.reject(error);
  }
);

axiosInstance.interceptors.request.use(
  (config) => {
    if (!isAuthenticated()) {
      const jwt = getJwtFromStorage();
      config.headers = {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
      };
    }
    return config;
  },
  (error) => {
    console.log("Error in axios");
    return Promise.reject(error);
  }
);

export { axiosInstance as axios };
