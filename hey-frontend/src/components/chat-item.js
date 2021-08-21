import React from "react";
import CustomAvatar from "./custom-avatar";
import { SlideDown } from "react-slidedown";

import { Avatar, Card } from "antd";
import { currency, formatToCurrency } from "../utils";
import { getProfileURL } from "../utils/";
const { Meta } = Card;

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

    if (data.content.luckyMoneyId) {
      return (
        <Card
          style={{ width: 300, borderRadius: 10 }}
          cover={
            <img
              alt="example"
              src="https://png.pngtree.com/thumb_back/fh260/background/20201230/pngtree-fan-shaped-new-year-red-envelopes-for-2021-image_517238.jpg"
            />
          }
          actions={
            [
              // <Icon type="setting" key="setting" />,
              // <Icon type="edit" key="edit" />,
              // <Icon type="ellipsis" key="ellipsis" />,
            ]
          }
        >
          {data.content.amount ? (
            <Meta
              avatar={
                <Avatar src="https://thietbiketnoi.com/wp-content/uploads/2020/12/phong-nen-hinh-nen-background-dep-cho-tet-chuc-mung-nam-moi-36.jpg" />
              }
              title={
                "Received " + formatToCurrency(data.content.amount) + currency
              }
              description={
                "Lucky Money With the best wishes: " + data.content.message
              }
            />
          ) : (
            <Meta
              avatar={
                <Avatar src="https://thietbiketnoi.com/wp-content/uploads/2020/12/phong-nen-hinh-nen-background-dep-cho-tet-chuc-mung-nam-moi-36.jpg" />
              }
              title="Created a Lucky Money"
              description={data.content.message}
            />
          )}
        </Card>
      );
    }
    if (data.type === "transfer") {
      return (
        <Card
          style={{ width: 300, borderRadius: 10 }}
          cover={
            <img
              alt="example"
              src="https://media.istockphoto.com/photos/modern-keyboard-with-blue-money-transfer-button-picture-id904359234?k=6&m=904359234&s=612x612&w=0&h=DsG3gsI0NEtYd-iGjcn2ICMsXGMLDAdda7jMCf0xSq4="
            />
          }
          actions={
            [
              // <Icon type="setting" key="setting" />,
              // <Icon type="edit" key="edit" />,
              // <Icon type="ellipsis" key="ellipsis" />,
            ]
          }
        >
          <Meta
            // avatar={
            //   <Avatar src="https://thietbiketnoi.com/wp-content/uploads/2020/12/phong-nen-hinh-nen-background-dep-cho-tet-chuc-mung-nam-moi-36.jpg" />
            // }
            title={"Send " + formatToCurrency(data.content.amount) + currency}
            description={data.content.message}
          />
        </Card>
      );
    }
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
          maxWidth: 500,
          height: "auto",
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
        }}
      >
        <p style={{ width: "100%" }}>
          {data.type == "transfer"
            ? "Transfer: " + formatToCurrency(data.content.amount) + currency
            : data.content.message}
        </p>
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
            src={getProfileURL(this.props.userId)}
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
