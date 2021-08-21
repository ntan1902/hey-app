import React from "react";
import { connect } from "react-redux";
import CustomAvatar from "../custom-avatar";

import { Menu, Icon, Button, Input } from "antd";

import { channingActions } from "../../utils";
import { bindChatActions, bindPaymentActions } from "../../actions";
import { ChatAPI } from "../../api/chat";

import popupWindow from "../../utils/popupWindow";

import "./ChatHeader.css";

const { SubMenu } = Menu;

class ChatHeader extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isHover: false,
      current: "",
      status_box_state: false,
      groupTitle: this.props.header.title,
    };
  }
  handleClick = (e) => {
    this.setState({
      current: e.key,
    });
  };

  componentDidUpdate() {
    if (this.refs.statusInput) this.refs.statusInput.focus();
  }

  showLuckyMoneyModal = (isCreate) => {
    this.props.paymentActions.changeStateLuckyMoneyPopup(true, isCreate);
  };

  showAddFriendToSessionModal = () => {
    this.props.paymentActions.changeStateAddFriendPopup(true);
  };
  showMembersModal = () => {
    this.props.chatActions.fetchMember(this.props.currentSessionId);
    this.props.paymentActions.changeStateMembersModal(true);
  };
  leaveGroup = () => {
    ChatAPI.leaveGroup(this.props.currentSessionId).then((res) =>
      console.log(res)
    );
  };
  makeCall = async (isVideoCall) => {
    // window.hadCall = true;
    var newWindow = popupWindow(`/call`, "Video call", 600, 800);
    // if (newWindow) newWindow.makeCall(user, conversation, true);
    if (newWindow) {
      newWindow.addEventListener("load", async () => {
        var ICEServer = await ChatAPI.getICEServer()
          .then((res) => {
            console.log("data nef" + res.data);
            return res.data.payload;
          })
          .catch((err) => {
            console.error("loi iceServer" + err.response);
          });
        // var ICEServer = "key iceServer";
        console.log(ICEServer);
        newWindow.init(ICEServer);
        newWindow.makeCall(this.props.currentSessionId, isVideoCall);
      });
    }
  };

  openChangeTitle = () => {
    this.setState({
      ...this.state,
      status_box_state: true,
      groupTitle: this.props.header.title,
    });
  };

  onChangeTitle = (e) => {
    this.setState({
      ...this.state,
      groupTitle: e.target.value,
    });
  };

  closeStatusBoxStateTitle = () => {
    if (this.state.groupTitle != this.props.header.title)
      this.props.chatActions.changeGroupTitle({
        groupName: this.state.groupTitle,
        sessionId: this.props.currentSessionId,
      });
    this.setState({
      ...this.state,
      status_box_state: false,
    });
  };

  render() {
    console.log("Header", this.props);
    const IconFont = Icon.createFromIconfontCN({
      scriptUrl: "//at.alicdn.com/t/font_8d5l8fzk5b87iudi.js",
    });
    return (
      <div className="chat-header">
        <div style={{ width: 50 }}>
          {this.props.header.group ? (
            <CustomAvatar type="panel-group-avatar" />
          ) : (
            <CustomAvatar
              type="panel-avatar"
              avatar={this.props.header.avatar}
            />
          )}
        </div>
        <div style={{ overflow: "hidden", paddingTop: 5, marginLeft: 5 }}>
          {!this.state.status_box_state || !this.props.header.group ? (
            <div onClick={this.openChangeTitle} className="panel-message">
              {" "}
              {this.props.header.title}{" "}
            </div>
          ) : (
            <Input
              onBlur={this.closeStatusBoxStateTitle}
              onPressEnter={this.closeStatusBoxStatetitle}
              placeholder="Please enter a group title..."
              value={this.state.groupTitle}
              ref="statusInput"
              onChange={this.onChangeTitle}
              onPressEnter={this.closeStatusBoxStateTitle}
              style={{ width: 300, height: 40 }}
            />
          )}
        </div>
        <div
          style={{
            flex: 1,
            display: "flex",
            justifyContent: "flex-end",
            alignItems: "center",
          }}
        >
          <div className="header-chat-action">
            <ul>
              <li>
                <button onClick={() => this.makeCall(false)}>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    viewBox="0 0 24 24"
                    fill="#ffb822"
                    stroke="#ffb822"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  >
                    <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
                  </svg>
                </button>
              </li>
              <li>
                <button onClick={() => this.makeCall(true)}>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    viewBox="0 0 24 24"
                    fill="#1890ff"
                    stroke="#1890ff"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  >
                    <polygon points="23 7 16 12 23 17 23 7"> </polygon>
                    <rect
                      x="1"
                      y="5"
                      width="15"
                      height="14"
                      rx="2"
                      ry="2"
                    ></rect>
                  </svg>
                </button>
              </li>

              <li>
                <button
                  style={{
                    backgroundColor: this.state.isHover
                      ? "rgba(228, 225, 225, 0.733)"
                      : "white",
                  }}
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    viewBox="0 0 24 24"
                    fill="#212529"
                    stroke="#212529"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  >
                    <circle cx="12" cy="12" r="1"></circle>
                    <circle cx="19" cy="12" r="1"></circle>
                    <circle cx="5" cy="12" r="1"></circle>
                  </svg>
                </button>
              </li>
            </ul>
          </div>
        </div>

        {this.props.header.group ? (
          <Menu
            onClick={this.handleClick}
            // selectedKeys={[this.state.current]}
            mode="horizontal"
            style={{
              position: "absolute",
              top: 5,
              right: 10,
              opacity: 0,
            }}
            onMouseEnter={() => this.setState({ isHover: true })}
            onMouseLeave={() => this.setState({ isHover: false })}
          >
            <Menu.ItemGroup title="Lucky Money">
              <Menu.Item
                key="setting:1"
                onClick={() => this.showLuckyMoneyModal(true)}
              >
                <Icon style={{ fontSize: 20 }} type="plus" />
                Create New One
              </Menu.Item>
              <Menu.Item
                key="setting:2"
                onClick={() => this.showLuckyMoneyModal(false)}
              >
                <Icon style={{ fontSize: 20 }} type="money-collect" />
                View List
              </Menu.Item>
            </Menu.ItemGroup>
            <Menu.ItemGroup title="Settings">
              <Menu.Item
                key="setting:3"
                onClick={() => this.showMembersModal()}
              >
                <Icon style={{ fontSize: 20 }} type="usergroup-add" />
                Members
              </Menu.Item>
              <Menu.Item
                key="setting:4"
                onClick={() => this.showAddFriendToSessionModal(true)}
              >
                <Icon style={{ fontSize: 20 }} type="plus" />
                Add More Friend
              </Menu.Item>
              <Menu.Item
                key="setting:5"
                onClick={() => this.leaveGroup()}
                style={{ color: "red" }}
              >
                <IconFont style={{ fontSize: 20 }} type="icon-tuichu" />
                Leave
              </Menu.Item>
            </Menu.ItemGroup>
          </Menu>
        ) : (
          <div></div>
        )}
      </div>
    );
  }
}

export default connect(
  (state) => ({
    header: state.chatReducer.messageHeader,
    luckyMoneyPopup: state.paymentReducer.luckyMoneyPopup,
    currentSessionId: state.chatReducer.currentSessionId,
  }),
  (dispatch) =>
    channingActions({}, dispatch, bindPaymentActions, bindChatActions)
)(ChatHeader);
