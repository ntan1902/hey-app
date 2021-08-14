import { axios } from "../utils/custom-axios";
import { API_CHAT } from "../config/setting";

export const ChatAPI = {
    getMembersOfSessionChat: (sessionId) => {
        return axios.post(`${API_CHAT}/api/protected/getUserOfSessionChat`, { sessionId })
    },
    kickMember: (sessionId, userId) => {
        return axios.post(`${API_CHAT}/api/protected/kickmember`, { sessionId, memberId: userId })
    },
    fetchChatList: (sessionId) => {
        return axios.post(`${API_CHAT}/api/protected/fetchChatListBySessionId`, { sessionId })
    }
};