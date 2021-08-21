import React, { useRef, useState } from "react";
import Picker from "emoji-picker-react";
import { connect } from "react-redux";
import { submitChatMessage } from "../../actions/chatAction";

import "./FormConversation.css";

const FormConversation = ({ submitChatMessage }) => {
  var [message, setMessage] = useState("");
  var picker = useRef();

  const onEmojiClick = (event, emojiObject) => {
    setMessage(message + emojiObject.emoji);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (message != "") {
      submitChatMessage(message);
    }
    setMessage("");
  };
  const togglePicker = (e) => {
    picker.current.classList.toggle("hidden");
  };
  return (
    <div className="form-conversation">
      <form onSubmit={handleSubmit}>
        <input
          className="mess-content"
          type="text"
          value={message}
          placeholder="Nhập tin nhắn ..."
          onChange={(e) => setMessage(e.target.value)}
        />
        <div className="icon-wrapper">
          <button className="icon" type="button" onClick={togglePicker}>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              stroke="#ffb822"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <circle cx="12" cy="12" r="10"></circle>
              <path d="M8 14s1.5 2 4 2 4-2 4-2"></path>
              <line x1="9" y1="9" x2="9.01" y2="9"></line>
              <line x1="15" y1="9" x2="15.01" y2="9"></line>
            </svg>
          </button>
          <div className="icon-picker hidden" ref={picker}>
            <Picker onEmojiClick={onEmojiClick} />
          </div>
        </div>
        <button className="submit" type="submit">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <line x1="22" y1="2" x2="11" y2="13"></line>
            <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
          </svg>
        </button>
      </form>
    </div>
  );
};
function mapStateToProps(state) {
  return {};
}
function mapDispatchToProps(dispatch) {
  return {
    submitChatMessage(message) {
      dispatch(submitChatMessage(message));
    },
  };
}
export default connect(mapStateToProps, mapDispatchToProps)(FormConversation);
