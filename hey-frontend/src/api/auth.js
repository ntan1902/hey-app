import { axios } from "../utils/custom-axios";
import { API_AUTH } from "../config/setting";

export const AuthAPI = {
  /* Get Event */

  login: (data) => axios.post(`${API_AUTH}/api/v1/users/login`, data),
  register: (data) => axios.post(`${API_AUTH}/api/v1/users/register`, data),
};
