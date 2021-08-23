import React, { useEffect, useRef } from "react";

const LocalVideo = ({ stream }) => {
  let videoTag = useRef();
  useEffect(() => {
    videoTag.current.srcObject = stream;
    videoTag.current.volume = 0;
  }, [stream]);
  return (
    <div className="me">
      <video ref={videoTag} autoPlay={true} />
    </div>
  );
};

export default LocalVideo;
