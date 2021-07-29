import { axios } from "../utils/custom-axios";
import { API_PAYMENT } from "../config/setting";

export const PaymentAPI = {
  /* Get Event */

  getActiveSession: () =>
    axios.get(`${API_PAYMENT}/practice-report/active-session`),

  getSessionById: (sessionId) =>
    axios.get(`${API_PAYMENT}/practice-report/session/${sessionId}`),

  getQuestionById: (questionId) =>
    axios.get(`${API_PAYMENT}/practice-report/question/${questionId}`),

  getAnswerById: (answerId) =>
    axios.get(`${API_PAYMENT}/practice-report/answer/${answerId}`),

  getReportActivity: () => axios.get(`${API_PAYMENT}/me/report-answers`),

  submitAnswer: (data) =>
    axios.post(`${API_PAYMENT}/practice-report/answer`, data),
  editAnswer: (data) =>
    axios.post(`${API_PAYMENT}/practice-report/update`, data),
};
