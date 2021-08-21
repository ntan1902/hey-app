import React, { useEffect, useRef } from "react";
import { connect } from "react-redux";
import { removeRemoteStream } from "../../actions/callAction";

const RemoteVideo = ({ stream, changeCurrenStream, removeRemoteStream }) => {
  var videoTag = useRef();
  useEffect(() => {
    videoTag.current.srcObject = stream;
  });

  return (
    <div className="current-user" onClick={() => changeCurrenStream(stream)}>
      <video
        ref={videoTag}
        autoPlay={true}
        onEmptied={(event) => {
          console.log("emptyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy", stream);
          // removeRemoteStream(stream);
        }}
        onAbort={() => {
          console.log("aborttttttttttttttttttttttttttt", stream);
        }}
        onPause={() => {
          console.log("Pauseeeeeeeeeeeeeeeeeeeeeeeeeee", stream);
        }}
      ></video>
    </div>
  );
};

export default connect(null, (dispatch) => ({
  removeRemoteStream: (stream) => dispatch(removeRemoteStream(stream)),
}))(RemoteVideo);
