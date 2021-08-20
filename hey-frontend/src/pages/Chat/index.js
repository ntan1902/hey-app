import React from "react";
import { Button, Input, Layout } from "antd";
import ChatList from "../../components/chat-list";
import ChatHeader from "../../components/ChatHeader/chat-header";
import Profile from "../../components/profile";
import MessagePanel from "../../components/message-panel";
import {
  closeWebSocket,
  initialWebSocket,
  loadChatContainer,
} from "../../actions/chatAction";
import { connect } from "react-redux";
import { isEmptyString } from "../../utils/utils";
import $ from "jquery";
import LuckyMoney from "../../components/lucky-money";
import AddFriendSession from "../../components/add-friend-session";
import MembersModal from "../../components/MembersModal/MembersModal";
import FormConversation from "../../components/FormConversation/FormConversation";

const { Sider } = Layout;
const { TextArea } = Input;

class Chat extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      menuaction: 1,
    };
    this.handleMainMenuChange = this.handleMainMenuChange.bind(this);
  }

  componentDidMount() {
    // this.props.initialWebSocket();
  }

  componentWillUnmount() {}

  handleMainMenuChange(e) {
    this.setState({ menuaction: e.key });
  }

  render() {
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
            <ChatList />
          </Sider>
          <div className="chat-container" style={{ padding: 0 }}>
            <LuckyMoney />
            <AddFriendSession />
            <MembersModal />
            <ChatHeader />
            <MessagePanel />
            {/* <div className="chat-footer">
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
            </div> */}
            <FormConversation />
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
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(Chat);
