import React from "react";
import { Button, Input, Layout } from "antd";
import AddressBook from "../../components/address-book";
import Profile from "../../components/profile";
import MessagePanel from "../../components/message-panel";
import { connect } from "react-redux";
import {
  closeWebSocket,
  initialWebSocket,
  loadChatContainer,
  userUnSelected,
  submitChatMessage,
} from "../../actions/chatAction";
import { isEmptyString } from "../../utils/utils";
import $ from "jquery";
import FormConversation from "../../components/FormConversation/FormConversation";
import ChatHeader from "../../components/ChatHeader/chat-header";

const { Header, Content, Footer, Sider } = Layout;
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
    this.props.userUnSelected();
  }

  componentWillUnmount() {}

  handleMainMenuChange(e) {
    this.setState({ menuaction: e.key });
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
            {this.props.currentSessionId != null &&
            this.props.userSelectedKeys != [] ? (
              <MessagePanel />
            ) : (
              <div></div>
            )}
            {this.props.currentSessionId != null &&
            this.props.userSelectedKeys != [] ? (
              <FormConversation />
            ) : (
              <div></div>
            )}
          </div>
        </Layout>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    userName: state.userReducer.userName,
    currentSessionId: state.chatReducer.currentSessionId,
    userSelectedKeys: state.chatReducer.userSelectedKeys,
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
    userUnSelected() {
      dispatch(userUnSelected());
    },
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(Chat);
