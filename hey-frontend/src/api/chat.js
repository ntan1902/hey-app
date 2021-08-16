import { axios } from "../utils/custom-axios";
import { API_CHAT } from "../config/setting";

export const ChatAPI = {
    getMembersOfSessionChat: (sessionId) => {
        return axios.post(`${API_CHAT}/api/protected/getUserOfSessionChat`, { sessionId })
    },
    kickMember: (sessionId, userId) => {
        return axios.post(`${API_CHAT}/api/protected/kickmember`, { sessionId, memberId: userId })
    },
    leaveGroup: (sessionId) => {
        return axios.post(`${API_CHAT}/api/protected/outgroup`, { sessionId })
    },
    makeCall: (sessionId, isVideoCall) => {
        return axios.post(`${API_CHAT}/api/protected/makeCall`, { sessionId, isVideoCall })
    },
    joinCall: (sessionId, peerId) => {
        return axios.post(`${API_CHAT}/api/protected/joinCall`, { sessionId, peerId })
    },
    getICEServer: () => {
        return axios.post(`${API_CHAT}/api/protected/getICEServer`)
    }
};