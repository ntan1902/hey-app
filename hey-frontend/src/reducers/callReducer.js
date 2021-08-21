let initialState = {
    localStream: null,
    remoteStreams: [],
    screenStream: null,
    peer: null,
    peerIds: [],
    socket: null
}



function callReducer(state = initialState, action) {
    switch (action.type) {
        case "SET_PEER":
            return {
                ...state,
                peer: action.peer
            }

        case "SET_LOCAL_STREAM":
            return {
                ...state,
                localStream: action.localStream
            }
        case "APPEND_REMOTE_STREAMS":

            {
                if (!state.remoteStreams.includes(action.remoteStream)) {
                    return {
                        ...state,
                        remoteStreams: [...state.remoteStreams, action.remoteStream]
                    };
                }
                return {
                    ...state,
                    remoteStreams: [...state.remoteStreams]
                };
            }
        case "APPEND_PEER_IDS":
            return {
                ...state,
                peerIds: [...state.peerIds, action.peerId]
            }
        case "INIT_SOCKET":
            return {
                ...state,
                socket: action.socket
            }
        case "SET_SCREEN_STREAM":
            return {
                ...state,
                screenStream: action.screenStream
            }
        case "REMOVE_SCREEN_STREAM":
            return {
                ...state,
                screenStream: null
            }
        default:
            return state;
    }
}

export default callReducer;