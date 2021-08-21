import { API_WS } from "../config/setting"
import Sockette from "sockette";
import { message } from "antd";
import { store } from "../store";

const socketEvent = {
    REJECT_CALL: "REJECT_CALL",
    JOIN_CALL: "JOIN_CALL",
    MAKE_CALL: "MAKE_CALL",
};

const setLocalStream = (localStream) => {
    return {
        type: "SET_LOCAL_STREAM",
        localStream
    }
}

const setPeer = (peer) => {
    return {
        type: "SET_PEER",
        peer
    }
}
const appendRemoteStreams = (remoteStream) => {
    return {
        type: "APPEND_REMOTE_STREAMS",
        remoteStream
    }
}
const setScreenStream = (screenStream) => {
    return {
        type: "SET_SCREEN_STREAM",
        screenStream
    }
}
const removeScreenStream = () => {
    return {
        type: "REMOVE_SCREEN_STREAM",
    }
}

let joinCallHandle = ({ peerId }) => {
    store.dispatch({
        type: "APPEND_PEER_IDS",
        peerId
    })
    var call = store.getState().callReducer.peer.call(peerId, store.getState().callReducer.localStream);
    call.on("stream", (remoteStream) => {
        store.dispatch({
            type: "APPEND_REMOTE_STREAMS",
            remoteStream
        })
    });
    var screenStream = store.getState().callReducer.screenStream;
    if (screenStream) {
        store.getState().callReducer.peer.call(peerId, screenStream);
    }
};

const initSocket = (jwt) => {
    return {
        type: "INIT_SOCKET",
        socket: new Sockette(API_WS + "?jwt=" + jwt, {
            timeout: 5e3,
            maxAttempts: 100,
            onopen: (e) => {},
            onmessage: (e) => {
                var data = JSON.parse(e.data);
                console.log("New Socket");
                console.log(data, "New Socket");
                switch (data.type) {
                    case socketEvent.REJECT_CALL:
                        {
                            message.warning(`${data.fullName} rejected call.`);
                            if (!data.group) {
                                setTimeout(() => {
                                    window.close();
                                }, 2000);
                            }
                            break;
                        }
                    case socketEvent.JOIN_CALL:
                        {
                            joinCallHandle(data);
                            break;
                        }
                }
            },
            onreconnect: (e) => console.log("Reconnecting...", e),
            onmaximum: (e) => console.log("Stop Attempting!", e),
            onclose: (e) => console.log("Closed!", e),
            onerror: (e) => console.log("Error:", e),
        })
    }
}

const answerPeerCall = (call) => {
    call.answer(store.getState().callReducer.localStream);
    store.dispatch({
        type: "APPEND_PEER_IDS",
        peerId: call.peer
    })
    call.on("stream", (remoteStream) => {
        store.dispatch({
            type: "APPEND_REMOTE_STREAMS",
            remoteStream
        })
    });
}

const shareScreen = (peerId) => {
    store.getState().callReducer.peer.call(peerId, store.getState().callReducer.screenStream);
}

export {
    setLocalStream,
    appendRemoteStreams,
    setPeer,
    setScreenStream,
    removeScreenStream,
    initSocket,
    answerPeerCall,
    shareScreen
}