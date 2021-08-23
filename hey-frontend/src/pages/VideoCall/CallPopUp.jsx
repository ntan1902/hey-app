import React, { useEffect, useRef, useState } from "react";
import { getJwtFromStorage } from "../../utils/utils";
import Peer from "peerjs";
import { Icon } from "antd";

import "./CallPopUp.css";
import RemoteVideo from "../../components/RemoteVideo/RemoteVideo";
import { ChatAPI } from "../../api/chat";
import LocalVideo from "../../components/LocalVideo/LocalVideo";
import { connect } from "react-redux";
import {
  setPeer,
  appendRemoteStreams,
  setLocalStream,
  setScreenStream,
  initSocket,
  answerPeerCall,
  shareScreen,
} from "../../actions/callAction";

const CallPopUp = ({
  remoteStreams,
  localStream,
  peerIds,
  setPeer,
  appendRemoteStreams,
  setLocalStream,
  setScreenStream,
  initSocket,
}) => {
  var [showNavVideo, setShowNavVideo] = useState(true);
  var [enableVoice, setEnableVoice] = useState(true);
  var [enableVideo, setEnableVideo] = useState(true);
  var videoCurrentTag = useRef();

  const jwt = getJwtFromStorage();

  const getMediaStream = (isVideo) => {
    return navigator.mediaDevices.getUserMedia({
      video: isVideo,
      audio: true,
    });
  };

  useEffect(() => {
    (async () => {
      let peer = null;
      initSocket(jwt);
      window.init = (ICEServer) => {
        peer = new Peer({
          host: "peer-server-ngoctrong102.herokuapp.com",
          debug: 1,
          path: "/",
          port: 443,
          secure: true,
          config: {
            iceServers: ICEServer,
          },
        });
        peer.on("open", async (id) => {
          peer.on("call", function (call) {
            answerPeerCall(call);
          });
        });
        setPeer(peer);
      };
      window.makeCall = async (sessionId, isVideoCall) => {
        let _localStream = await getMediaStream(isVideoCall);
        setLocalStream(_localStream);
        setEnableVideo(isVideoCall);
        ChatAPI.makeCall(sessionId, isVideoCall);
      };

      window.answerCall = async (sessionId, isVideoCall) => {
        let _localStream = await getMediaStream(isVideoCall);
        setLocalStream(_localStream);
        if (peer.id) {
          ChatAPI.joinCall(sessionId, peer.id);
        } else {
          peer.on("open", async (id) => {
            ChatAPI.joinCall(sessionId, peer.id);
          });
        }
      };
    })();
  }, []);
  useEffect(() => {
    if (!videoCurrentTag.current.srcObject && remoteStreams[0]) {
      videoCurrentTag.current.srcObject = remoteStreams[0];
    }
  }, [remoteStreams]);

  const changeCurrenStream = (stream) => {
    videoCurrentTag.current.srcObject = stream;
  };
  const renderRemoteVideo = remoteStreams.map((remoteStream, i) => (
    <RemoteVideo
      stream={remoteStream}
      key={i}
      changeCurrenStream={changeCurrenStream}
    />
  ));
  const handleShareScreen = async () => {
    let screenStream = await navigator.mediaDevices.getDisplayMedia();
    appendRemoteStreams(screenStream);
    setScreenStream(screenStream);
    peerIds.forEach((peerId) => {
      shareScreen(peerId);
    });
  };
  const toggleVoice = () => {
    localStream.getAudioTracks()[0].enabled = !enableVoice;
    setEnableVoice(!enableVoice);
  };
  const toggleVideo = () => {
    localStream.getVideoTracks()[0].enabled = !enableVideo;
    setEnableVideo(!enableVideo);
  };
  return (
    <div className="call-pop-up-focus">
      <div className="current-video">
        <video ref={videoCurrentTag} autoPlay={true} />
      </div>
      <div className={`videos-nav ${showNavVideo ? "" : "hidden"}`}>
        <div className="group-video">{renderRemoteVideo}</div>
        <LocalVideo stream={localStream} />
        <button
          className={`toggle-nav ${showNavVideo ? "" : "hidden"}`}
          onClick={() => setShowNavVideo(!showNavVideo)}
        >
          <Icon type="right" />
        </button>
      </div>
      <div className="actions">
        <button onClick={toggleVoice} className={enableVoice ? "" : "disable"}>
          <Icon type="audio" style={{ fontSize: 23 }} />
        </button>
        <button onClick={toggleVideo} className={enableVideo ? "" : "disable"}>
          <Icon type="video-camera" style={{ fontSize: 23 }} />
        </button>
        <button onClick={handleShareScreen}>
          <Icon
            type="import"
            style={{ fontSize: 23, transform: "rotate(90deg)" }}
          />
        </button>
        <button style={{ backgroundColor: "red" }}>
          <Icon type="phone" style={{ fontSize: 23 }} />
        </button>
      </div>
    </div>
  );
};

function mapStateToProps(state) {
  return {
    localStream: state.callReducer.localStream,
    remoteStreams: state.callReducer.remoteStreams,
    screenStream: state.callReducer.screenStream,
    peer: state.callReducer.peer,
    peerIds: state.callReducer.peerIds,
  };
}
function mapDispatchToProps(dispatch) {
  return {
    setPeer: (peer) => dispatch(setPeer(peer)),
    appendRemoteStreams: (remoteStream) =>
      dispatch(appendRemoteStreams(remoteStream)),
    setLocalStream: (localStream) => dispatch(setLocalStream(localStream)),
    setScreenStream: (screenStream) => dispatch(setScreenStream(screenStream)),
    initSocket: (jwt) => dispatch(initSocket(jwt)),
  };
}
export default connect(mapStateToProps, mapDispatchToProps)(CallPopUp);
