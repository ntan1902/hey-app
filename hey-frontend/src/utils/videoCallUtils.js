import { ChatAPI } from "../api/chat";
import popupWindow from "./popupWindow";

const acceptCall = (sessionId, isVideoCall) => {
    var newWindow = popupWindow('/call', "Video call", 600, 800);
    if (newWindow) {
        newWindow.addEventListener('load', async() => {
            var ICEServer = await ChatAPI.getICEServer()
                .then(res => {
                    console.log("data nef" + res.data);
                    return res.data.payload
                })
                .catch(err => {
                    console.error("loi iceServer" + err.response);
                });
            newWindow.init(ICEServer);
            newWindow.answerCall(sessionId, isVideoCall)
        })
    }
}
const rejectCall = (sessionId) => {
    ChatAPI.rejectCall(sessionId);
}

export default {
    acceptCall,
    rejectCall
}