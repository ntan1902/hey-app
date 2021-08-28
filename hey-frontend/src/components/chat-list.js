import React from "react";
import { Menu } from "antd";
import CustomAvatar from "../components/custom-avatar";
import StartChatGroup from "./start-chat-group";
import { connect } from "react-redux";
import {
  changeMessageHeader,
  loadChatContainer,
  loadChatList,
  userSelected,
} from "../actions/chatAction";
import { Scrollbars } from "react-custom-scrollbars";
import { getProfileURL } from "../utils";
import { getUserIdFromStorage } from "../utils/utils";

class ChatList extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      menuaction: 1,
    };
    this.handleChangeChatItem = this.handleChangeChatItem.bind(this);
  }

  componentDidMount() {
    this.props.loadChatList();
  }

  handleChangeChatItem(event) {
    this.props.userSelected(event.key);
    this.props.loadChatContainer(event.key);
    for (var i = 0; i < this.props.chatList.length; i++) {
      if (this.props.chatList[i].sessionId == event.key) {
        this.props.changeMessageHeader(
          this.props.chatList[i].groupName === ""
            ? this.props.chatList[i].name
            : this.props.chatList[i].groupName,
          this.props.chatList[i].avatar,
          this.props.chatList[i].group,
          this.props.chatList[i].userIds
        );
      }
    }
    this.props.scrollToBottom();
  }

  renderListAvatar = (item) => {
    if (item.group == false || item.userIds.length == 1) {
      let singleId = item.userIds[0];
      if (item.userIds.length != 1)
        singleId = item.userIds.filter((e) => e != getUserIdFromStorage());

      return (
        <CustomAvatar
          type="avatar"
          src={getProfileURL(singleId)}
          size={60}
          style={{
            // position: "absolute",
            // left: 10,
            // top: 10,
            border: "0.5px solid white",
            cursor: "pointer",
          }}
        />
      );
    }

    let userIdsSlice = item.userIds;
    if (item.userIds.length == 2) {
      userIdsSlice = item.userIds.slice(0, 2);
      return (
        <div
          style={{
            width: 60,
            height: 60,
            position: "relative",
          }}
        >
          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[0])}
            size={40}
            style={{
              position: "absolute",
              right: 0,
              top: 0,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />

          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[1])}
            size={40}
            style={{
              position: "absolute",
              left: 0,
              bottom: 0,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />
        </div>
      );
    }

    if (item.userIds.length == 3) {
      userIdsSlice = item.userIds.slice(0, 3);
      return (
        <div
          style={{
            width: 60,
            height: 60,
            position: "relative",
          }}
        >
          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[1])}
            size={30}
            style={{
              position: "absolute",
              left: 4,
              bottom: 4,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />

          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[2])}
            size={30}
            style={{
              position: "absolute",
              right: 4,
              bottom: 4,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />
          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[0])}
            size={30}
            style={{
              position: "absolute",
              left: 15,
              top: 4,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />
        </div>
      );
    }

    if (item.userIds.length == 4) {
      userIdsSlice = item.userIds.slice(0, 4);
      return (
        <div
          style={{
            width: 60,
            height: 60,
            position: "relative",
          }}
        >
          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[0])}
            size={30}
            style={{
              position: "absolute",
              left: 2,
              top: 0,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />

          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[1])}
            size={30}
            style={{
              position: "absolute",
              right: 2,
              top: 0,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />

          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[2])}
            size={30}
            style={{
              position: "absolute",
              right: 2,
              bottom: 4,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />
          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[3])}
            size={30}
            style={{
              position: "absolute",
              left: 2,
              bottom: 4,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />
        </div>
      );
    }

    if (item.userIds.length > 4) {
      userIdsSlice = item.userIds.slice(0, 4);
      return (
        <div
          style={{
            width: 60,
            height: 60,
            position: "relative",
          }}
        >
          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[0])}
            size={30}
            style={{
              position: "absolute",
              left: 2,
              top: 0,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />

          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[1])}
            size={30}
            style={{
              position: "absolute",
              right: 2,
              top: 0,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />
          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[3])}
            size={30}
            style={{
              position: "absolute",
              left: 2,
              bottom: 4,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />
          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[2])}
            size={30}
            style={{
              position: "absolute",
              right: 2,
              bottom: 4,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />

          <div
            style={{
              width: 30,
              height: 30,
              backgroundColor: "rgba(232, 234, 239,0.8)",
              borderRadius: 30,
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              position: "absolute",
              right: 2,
              bottom: 4,
              border: "0.5px solid white",
              cursor: "pointer",
              zIndex: 1000,
            }}
          >
            <p
              style={{
                padding: 0,
                margin: 0,
                color: "#72808e",
                fontWeight: "lighter",
                fontSize: 12,
              }}
            >
              {item.userIds.length - 3}
            </p>
          </div>
        </div>
      );
    }
  };

  render() {
    if (this.props.chatList) {
      return (
        <div className="d-flex flex-column full-height">
          <StartChatGroup />
          <Scrollbars autoHide autoHideTimeout={500} autoHideDuration={200}>
            <Menu
              theme="light"
              mode="inline"
              className="chat-list"
              onSelect={this.handleChangeChatItem}
              selectedKeys={this.props.userSelectedKeys}
              // style={{ marginLeft: 10 }}
            >
              {this.props.chatList.map((item, index) => (
                <Menu.Item key={item.sessionId}>
                  <div
                    style={{
                      width: 60,
                      height: 60,
                      position: "relative",
                    }}
                  >
                    {this.renderListAvatar(item)}
                  </div>
                  {item.unread > 0 ? (
                    <div
                      className="unread-item"
                      style={{ overflow: "hidden", paddingTop: 5 }}
                    >
                      <div className="user-name">
                        {item.groupName === "" ? item.name : item.groupName}
                      </div>
                      <div className="history-message">
                        {JSON.parse(item.lastMessage).content.message}
                      </div>
                    </div>
                  ) : (
                    <div style={{ overflow: "hidden", paddingTop: 5 }}>
                      <div className="user-name">
                        {item.groupName === "" ? item.name : item.groupName}
                      </div>
                      <div className="history-message">
                        {JSON.parse(item.lastMessage).content.message}
                      </div>
                    </div>
                  )}
                  {item.unread > 0 ? (
                    <div className="unread">{item.unread}</div>
                  ) : (
                    ""
                  )}
                </Menu.Item>
              ))}
            </Menu>
          </Scrollbars>
        </div>
      );
    } else {
      return "Loading...";
    }
  }
}

function mapStateToProps(state) {
  return {
    chatList: state.chatReducer.chatList,
    userSelectedKeys: state.chatReducer.userSelectedKeys,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    loadChatList() {
      dispatch(loadChatList());
    },
    loadChatContainer(sessionId) {
      dispatch(loadChatContainer(sessionId));
    },
    changeMessageHeader(avatar, title, groupchat, userIds) {
      dispatch(changeMessageHeader(avatar, title, groupchat, userIds));
    },
    userSelected(sessionId) {
      dispatch(userSelected(sessionId));
    },
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(ChatList);
