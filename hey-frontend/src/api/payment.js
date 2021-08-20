import { axios } from "../utils/custom-axios";
import { API_PAYMENT, API_LUCKY } from "../config/setting";

export const PaymentAPI = {
  /* Get Event */

  getBalance: () => axios.get(`${API_PAYMENT}/api/v1/me/wallet`),
  checkBalance: () => axios.get(`${API_PAYMENT}/api/v1/me/hasWallet`),
  createBalance: () => axios.post(`${API_PAYMENT}/api/v1/me/createWallet`),
  topup: (data) => axios.post(`${API_PAYMENT}/api/v1/me/topup`, data),
  transfer: (data) =>
    axios.post(`${API_PAYMENT}/api/v1/me/createTransfer`, data),
  createLuckymoney: (data) =>
    axios.post(`${API_LUCKY}/api/v1/createLuckyMoney`, data),
  getListLuckymoney: (sessionId) =>
    axios.get(`${API_LUCKY}/api/v1/getAllLuckyMoney?sessionId=${sessionId}`),
  receivedLuckymoney: (data) =>
    axios.post(`${API_LUCKY}/api/v1/receiveLuckyMoney`, data),
  getTransferStatements: (offset, limit) =>
    axios.get(
      `${API_PAYMENT}/api/v1/me/getTransferStatements?offset=${offset}&limit=${limit}`
    ),
};
