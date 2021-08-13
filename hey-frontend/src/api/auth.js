import { axios } from "../utils/custom-axios";
import { API_AUTH } from "../config/setting";

export const AuthAPI = {
  /* Get Event */

  login: (data) => axios.post(`${API_AUTH}/api/v1/users/login`, data),
  register: (data) => axios.post(`${API_AUTH}/api/v1/users/register`, data),
  getProfile: () => axios.get(`${API_AUTH}/api/v1/users/getInfo`),
  getUsername: (userId) =>
    axios.get(`${API_AUTH}/api/v1/users/getUsername/${userId}`),

  verifyPin: (data) => axios.post(`${API_AUTH}/api/v1/users/createSoftTokenByPin`, data),

  searchUser: (keyword) => {
    return axios.get(`${API_AUTH}/api/v1/users/searchUser?key=${keyword}`);
  }
};
