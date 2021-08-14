import { axios } from "../utils/custom-axios";
import { API_CHAT } from "../config/setting";

export const ChatAPI = {
    getMembersOfSessionChat: (sessionId) => {
        return axios.post(`${API_CHAT}/api/protected/getUserOfSessionChat`, { sessionId })
    }
};