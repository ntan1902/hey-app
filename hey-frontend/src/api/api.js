import axios from "axios";
import { getJwtFromStorage, isEmptyString } from "../utils/utils";
import {API_CHAT, API_WS, SITE_URL} from "../config/setting";

export const host = SITE_URL + API_CHAT;
export const ws_host = API_WS;
const auth_type = "Bearer";

// Set config defaults when creating the instance
const instance = axios.create({
  baseURL: host,
});

instance.interceptors.request.use(
  function (config) {
    // Do something before request is sent
    return config;
  },
  function (error) {
    // Do something with request error
    return Promise.reject(error);
  }
);

// Add a response interceptor
instance.interceptors.response.use(
  function (response) {
    // Do something with response data

    return response;
  },
  function (error) {
    //ignore ping
    // if (!error.request.responseURL.endsWith("api/protected/ping")) {
    //   if (error.response.data.error.message) {
    //     message.error(error.response.data.error.message);
    //   } else {
    //     message.error(
    //       "Ooops, The server was unable to complete your request. We will be back soon :("
    //     );
    //   }
    // }
    return Promise.reject(error);
  }
);

export const api = {
  get: (url) => {
    var jwt = getJwtFromStorage();
    if (!isEmptyString(jwt)) {
      jwt = auth_type + " " + jwt;
    } else {
      jwt = "";
    }
    return instance.get(`${url}`, { headers: { Authorization: jwt } });
  },
  post: (url, req) => {
    var jwt = getJwtFromStorage();
    if (!isEmptyString(jwt)) {
      jwt = auth_type + " " + jwt;
    } else {
      jwt = "";
    }
    return instance.post(`${url}`, req, { headers: { Authorization: jwt } });
  },
  put: (url, req) => {
    return instance.put(`${url}`, req);
  },
  patch: (url, req) => {
    return instance.patch(`${url}`, req);
  },
  delete: (url) => {
    return instance.delete(`${url}`);
  },
};
