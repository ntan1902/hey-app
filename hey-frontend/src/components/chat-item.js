import React from "react";
import CustomAvatar from "./custom-avatar";
import { Popover } from "antd";
import { SlideDown } from "react-slidedown";

class ChatItem extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showDate: false,
    };
    this.handleItemClick = this.handleItemClick.bind(this);
  }

  handleItemClick(e) {
    let newState = this.state.showDate;
    if (newState) {
      newState = false;
    } else {
      newState = true;
    }
    this.setState({
      showDate: newState,
    });
  }

  renderChat = () => {
    var cssContentClass =
      this.props.type == 1
        ? "chat-item-content-owner"
        : "chat-item-content-other";
    const data = JSON.parse(this.props.value);

    return (
      <div
        className={"chat-item-content " + cssContentClass}
        style={{
          padding: 10,
          // paddingLeft: 10,
          // paddingright: 10,
          margin: 0,
          marginLeft: this.props.type == 1 ? 0 : 5,
          marginRight: this.props.type == 1 ? 5 : 0,

          // height: ,
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
        }}
      >
        {data.content.message}
      </div>
    );
  };

  render() {
    var cssClass = this.props.type == 1 ? "chat-item-owner" : "chat-item-other";
    var cssContentClass =
      this.props.type == 1
        ? "chat-item-content-owner"
        : "chat-item-content-other";
    return (
      <div
        onClick={this.handleItemClick}
        className={"chat-item chat-item-outer " + cssClass}
        style={{ margin: 0, marginBottom: 10 }}
      >
        <div className={"chat-item " + cssClass}>
          <CustomAvatar
            type="chat-avatar"
            avatar={this.props.avatar}
            show={this.props.showavatar}
          />
          {this.renderChat()}
        </div>
        {this.state.showDate ? (
          <SlideDown>
            <div className={"chat-item-date"}>{this.props.date}</div>
          </SlideDown>
        ) : (
          ""
        )}
      </div>
    );
  }
}

export default ChatItem;
