import React from "react";
import { Button, Icon, Input, Layout, Menu } from "antd";
// import CustomAvatar from "../components/custom-avatar";
import ChatList from "../../components/chat-list";
import AddressBook from "../../components/address-book";
import ChatHeader from "../../components/chat-header";
import Profile from "../../components/profile";
import MessagePanel from "../../components/message-panel";
import {
  closeWebSocket,
  initialWebSocket,
  loadChatContainer,
  submitChatMessage,
} from "../../actions/chatAction";
import { connect } from "react-redux";
import { isAuthenticated, isEmptyString } from "../../utils/utils";
import { Redirect } from "react-router-dom";
import $ from "jquery";

const { Header, Content, Footer, Sider } = Layout;
const { TextArea } = Input;

class Chat extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      menuaction: 1,
    };
    this.handleMainMenuChange = this.handleMainMenuChange.bind(this);
    this.handleMessageEnter = this.handleMessageEnter.bind(this);
    this.handleSendClick = this.handleSendClick.bind(this);
  }

  componentDidMount() {
    // this.props.initialWebSocket();
  }

  componentWillUnmount() {}

  handleMainMenuChange(e) {
    this.setState({ menuaction: e.key });
  }

  handleMessageEnter(e) {
    let charCode = e.keyCode || e.which;
    if (!e.shiftKey) {
      e.preventDefault();
      let message = e.target.value;
      if (!isEmptyString(message)) {
        this.props.submitChatMessage(message);
      }
      e.target.value = "";
    }
  }

  handleSendClick(e) {
    let message = $("#messageTextArea").val();
    if (!isEmptyString(message)) {
      this.props.submitChatMessage(message);
    }
    $("#messageTextArea").val("");
  }

  render() {
    // if (isAuthenticated()) {
    //   return <Redirect to="/login" />;
    // }
    return (
      <div style={{ height: 100 + "vh", width: "100%" }}>
        <Layout>
          <Sider
            breakpoint="lg"
            collapsedWidth="0"
            theme="light"
            onBreakpoint={(broken) => {}}
            onCollapse={(collapsed, type) => {}}
            width="300"
            id="sub-side-menu"
          >
            <Profile />
            <div className="menu-separation" />
            <AddressBook />
          </Sider>
          <div className="chat-container" style={{ padding: 0 }}>
            <ChatHeader />
            <MessagePanel />
            <div className="chat-footer">
              <TextArea
                id="messageTextArea"
                onPressEnter={this.handleMessageEnter}
                rows={1}
                placeholder="Type a new message"
                ref="messageTextArea"
              />
              <Button type="primary" onClick={this.handleSendClick}>
                Send
              </Button>
            </div>
          </div>
        </Layout>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    userName: state.userReducer.userName,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    initialWebSocket() {
      dispatch(initialWebSocket());
    },
    closeWebSocket() {
      dispatch(closeWebSocket());
    },
    loadChatContainer(sessionId) {
      dispatch(loadChatContainer(sessionId));
    },
    submitChatMessage(message) {
      dispatch(submitChatMessage(message));
    },
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(Chat);
