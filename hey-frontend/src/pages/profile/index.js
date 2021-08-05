import React from "react";
import { Button, Icon, Input, Layout, Menu } from "antd";
// import CustomAvatar from "../components/custom-avatar";
import ChatList from "../../components/chat-list";
import Payment from "./payment";
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
import Topup from "./topup";
import Transfer from "./transfer";
import TransferStatement from "./transfer-statement";

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

  renderMainSide = () => {
    console.log(this.props.layoutType);
    switch (this.props.layoutType) {
      case "topup":
        return <Topup></Topup>;
      case "transfer":
        return <Transfer></Transfer>;
      case "transferStatement":
        return <TransferStatement></TransferStatement>;
      default:
        return <div></div>;
    }
  };

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
            <Payment />
          </Sider>
          <div className="chat-container" style={{ padding: 0 }}>
            {/* <ChatHeader /> */}
            {this.renderMainSide()}
          </div>
        </Layout>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    userName: state.userReducer.userName,
    layoutType: state.paymentReducer.layoutType,
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
