import React, { useEffect, useRef } from "react";

const RemoteVideo = ({ stream, changeCurrenStream }) => {
  var videoTag = useRef();
  useEffect(() => {
    videoTag.current.srcObject = stream;
  });

  return (
    <div className="current-user" onClick={() => changeCurrenStream(stream)}>
      <video ref={videoTag} autoPlay={true}></video>
    </div>
  );
};

export default RemoteVideo;
