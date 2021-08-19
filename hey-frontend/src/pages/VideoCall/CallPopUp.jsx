import React, { useEffect, useRef, useState } from "react";
import { getJwtFromStorage } from "../../utils/utils";
import { ws_host } from "../../api/api";
import Sockette from "sockette";
import Peer from "peerjs";

import "./CallPopUp.css";
import RemoteVideo from "../../components/RemoteVideo/RemoteVideo";
import { ChatAPI } from "../../api/chat";

const socketEvent = {
  REFUSE_CALL: "REFUSE_CALL",
  JOIN_CALL: "JOIN_CALL",
  MAKE_CALL: "MAKE_CALL",
};

const CallPopUp = () => {
  var peer;
  var socket;
  var localStream;
  let [remoteStreams, setRemoteStreams] = useState([]);
  var videoTag = useRef();
  var videoCurrentTag = useRef();

  const jwt = getJwtFromStorage();

  let joinCallHandle = ({ peerId }) => {
    console.log("call 2:", peerId);
    var call = peer.call(peerId, localStream);
    call.on("stream", (remoteStream) => {
      console.log("đã bắt được", peerId);
      setRemoteStreams((prev) => {
        if (!prev.includes(remoteStream)) return [...prev, remoteStream];
        return [...prev];
      });
      console.log("remote 2:", remoteStreams);
    });
  };

  useEffect(() => {
    (async () => {
      socket = new Sockette(ws_host + "?jwt=" + jwt, {
        timeout: 5e3,
        maxAttempts: 100,
        onopen: (e) => {},
        onmessage: (e) => {
          var data = JSON.parse(e.data);
          console.log("New Socket");
          console.log(data, "New Socket");
          switch (data.type) {
            case socketEvent.REFUSE_CALL: {
              // do something
              console.log("Từ chối cuộc gọi");
              break;
            }
            case socketEvent.JOIN_CALL: {
              joinCallHandle(data);
              break;
            }
          }
        },
        onreconnect: (e) => console.log("Reconnecting...", e),
        onmaximum: (e) => console.log("Stop Attempting!", e),
        onclose: (e) => console.log("Closed!", e),
        onerror: (e) => console.log("Error:", e),
      });
      window.init = (ICEServer) => {
        peer = new Peer({
          host: "peer-server-ngoctrong102.herokuapp.com",
          debug: 1,
          path: "/",
          port: 443,
          secure: true,
          config: {
            iceServers:
              process.env.REACT_APP_NODE_ENV === "development" ? "" : ICEServer,
          },
        });
        peer.on("open", async (id) => {
          console.log("open 1:", id);
          peer.on("call", function (call) {
            console.log("call 1:");
            call.answer(localStream);
            call.on("stream", (remoteStream) => {
              setRemoteStreams((prev) => {
                if (!prev.includes(remoteStream))
                  return [...prev, remoteStream];
                return [...prev];
              });
              console.log("remote 1:", remoteStreams);
            });
          });
        });
      };
      window.makeCall = async (sessionId, isVideoCall) => {
        localStream = await navigator.mediaDevices.getUserMedia({
          video: isVideoCall,
          audio: true,
        });
        videoTag.current.srcObject = localStream;
        videoTag.current.volume = 0;
        console.log(localStream);
        ChatAPI.makeCall(sessionId, isVideoCall);
      };

      window.answerCall = async (sessionId, isVideoCall) => {
        localStream = await navigator.mediaDevices.getUserMedia({
          video: isVideoCall,
          audio: true,
        });
        videoTag.current.srcObject = localStream;
        videoTag.current.volume = 0;
        if (peer.id) {
          console.log("answer call, my peer id: ", peer.id);
          ChatAPI.joinCall(sessionId, peer.id);
        } else {
          peer.on("open", async (id) => {
            console.log("open 2:", id);
            console.log("answer call, my peer id: ", id);
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
  return (
    <div className="call-pop-up-focus">
      <div className="current-video">
        <video ref={videoCurrentTag} autoPlay={true} />
      </div>
      <div className="me">
        <video ref={videoTag} autoPlay={true} />
      </div>
      <div className="group-video">{renderRemoteVideo}</div>
    </div>
  );
};

export default CallPopUp;
