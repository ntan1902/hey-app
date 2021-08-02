import { axios } from "../utils/custom-axios";
import { API_PAYMENT } from "../config/setting";

export const PaymentAPI = {
  /* Get Event */

  getBalance: () => axios.get(`${API_PAYMENT}/api/v1/me/wallet`),
  topup: (data) => axios.post(`${API_PAYMENT}/api/v1/me/topup`, data),
};
