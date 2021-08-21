import React from "react";
import Gravatar from "react-gravatar";
import Ravatar from "react-avatar";
import {Avatar} from "antd";

class CustomAvatar extends React.Component {
  state = { visible: true };

  render() {
    let customClassName = "custom-avatar " + this.props.type;
    if (this.props.src) {
      return (
        <Avatar
          className={customClassName}
          src={this.props.src}
          size={this.props.size}
          style={{ border: "3px solid gray", cursor: "pointer" }}
        />
      );
    }
    switch (this.props.type) {
      case "main-avatar":
        return (
          <Gravatar
            email={this.props.avatar + "@gmail.com"}
            className={customClassName}
            default="identicon"
            style={{
              border: "3px solid gray",

              cursor: "pointer",
            }}
          />
        );
      case "new-avatar":
        return (
          <Avatar
            icon="plus"
            className={customClassName}
            style={{ fontSize: 30 }}
          />
        );
      case "panel-avatar":
        return (
          <Ravatar
            name={this.props.avatar}
            className={customClassName}
            size="50"
          />
        );
      case "panel-group-avatar":
        return (
          <Ravatar
            name="G"
            color="#001529"
            className={customClassName}
            size="50"
          />
        );
      case "user-avatar":
        return (
          <Ravatar
            name={this.props.avatar}
            className={customClassName}
            size="60"
          />
          /*<Gravatar email={this.props.username + '@vng.com.vn'} className={customClassName} default="identicon"/>*/
        );
      case "group-avatar":
        return (
          <Ravatar
            name="G"
            color="#001529"
            className={customClassName}
            size="60"
          />
        );
      case "chat-avatar":
        if (this.props.show) {
          return (
            <Ravatar
              name={this.props.avatar}
              className={customClassName}
              size="40"
            />
          );
        } else {
          return <div className="mock-small-avatar" />;
        }
      default:
        return <Avatar className={customClassName} />;
    }
  }
}

export default CustomAvatar;
