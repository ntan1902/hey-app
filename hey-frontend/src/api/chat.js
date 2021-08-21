import {axios} from "../utils/custom-axios";
import {API_CHAT} from "../config/setting";

export const ChatAPI = {
  getMembersOfSessionChat: (sessionId) => {
    return axios.post(`${API_CHAT}/api/protected/getUserOfSessionChat`, {
      sessionId,
    });
  },
  kickMember: (sessionId, userId) => {
    return axios.post(`${API_CHAT}/api/protected/kickmember`, {
      sessionId,
      memberId: userId,
    });
  },
  leaveGroup: (sessionId) => {
    return axios.post(`${API_CHAT}/api/protected/outgroup`, { sessionId });
  },
  makeCall: (sessionId, isVideoCall) => {
    return axios.post(`${API_CHAT}/api/protected/makeCall`, {
      sessionId,
      isVideoCall,
    });
  },
  joinCall: (sessionId, peerId) => {
    return axios.post(`${API_CHAT}/api/protected/joinCall`, {
      sessionId,
      peerId,
    });
  },
  getICEServer: () => {
    return axios.post(`${API_CHAT}/api/protected/getICEServer`);
  },
  addFriendRequest: (username) => {
    return axios.post(`${API_CHAT}/api/protected/addfriendrequest`, {
      username,
    });
  },
  addFriendToSession: (sessionId, userId) => {
    return axios.post(`${API_CHAT}/api/protected/addfriendtosession`, {
      sessionId,
      userId,
    });
  },
  getAddressBook: () => {
    return axios.get(`${API_CHAT}/api/protected/addressbook`);
  },
  waittingFriend: () => {
    return axios.get(`${API_CHAT}/api/protected/waitingfriend`);
  },
  getUser: () => {
    return axios.get(`${API_CHAT}/api/protected/user`);
  },
  updateStatus: (req) => {
    return axios.post(`${API_CHAT}/api/protected/status`, req);
  },
  changeGroupName: (req) => {
    return axios.post(`${API_CHAT}/api/protected/editgroupname`, req);
  },
  editProfile: (data) => {
    return axios.post(`${API_CHAT}/api/protected/editprofile`, data);
  },
  getSessionIdByUserId: (req) => {
    return axios.post(`${API_CHAT}/api/protected/sessionidbyuserid`, req);
  },
  closeWaitingFriend: (req) => {
    return axios.post(`${API_CHAT}/api/protected/closewaitingfriend`, req);
  },
  addFriend: (req) => {
    return axios.post(`${API_CHAT}/api/protected/addfriend`, req);
  },
  usernameExisted: (req) => {
    return axios.post(`${API_CHAT}/api/protected/usernameexisted`, req);
  },
  waitingChatHeader: (req) => {
    return axios.post(`${API_CHAT}/api/protected/waitingchatheader`, req);
  },
  getChatList: () => {
    return axios.get(`${API_CHAT}/api/protected/chatlist`);
  },
  rejectCall: (sessionId) => {
    return axios.post(`${API_CHAT}/api/protected/rejectCall`, { sessionId });
  },
};
