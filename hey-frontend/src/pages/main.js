import React from "react";
import { Button, Icon, Input, Layout, Menu } from "antd";
import CustomAvatar from "../components/custom-avatar";
import ChatList from "../components/chat-list";
import AddressBook from "../components/address-book";
import ChatHeader from "../components/chat-header";
import Profile from "../components/profile";
import MessagePanel from "../components/message-panel";
import Chat from "./Chat";
import Friend from "./Friend";
import Payment from "./Payment";
import ProfileScreen from "./profile";

import {
  closeWebSocket,
  initialWebSocket,
  loadChatContainer,
  submitChatMessage,
} from "../actions/chatAction";
import { connect } from "react-redux";
import {
  isAuthenticated,
  isEmptyString,
  setUserIdToStorage,
  clearStorage,
} from "../utils/utils";
import { Redirect } from "react-router-dom";
import $ from "jquery";
import { channingActions } from "../utils";
import { bindAuthActions, bindPaymentActions } from "../actions";
const { Header, Content, Footer, Sider } = Layout;
const { TextArea } = Input;

class Main extends React.Component {
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
    this.props.initialWebSocket();

    this.props
      .actions()
      .authActions.getProfile()
      .then((res) => {
        setUserIdToStorage(res.data.id);

        this.props
          .actions()
          .paymentActions.checkBalance()
          .then((res) => {
            this.props.actions().paymentActions.getBalance();
          });
        console.log("Res", res.data);
      })
      .catch((err) => {
        console.log("Err", err);
        clearStorage();
      });
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

  renderMainSlide = () => {
    switch (this.state.menuaction) {
      case "1":
        return <Chat />;
      case "2":
        return <Friend></Friend>;
      case "3":
        return <Payment></Payment>;
      case "4":
        return <ProfileScreen></ProfileScreen>;
      default:
        return <Chat></Chat>;
    }
  };

  render() {
    if (isAuthenticated()) {
      return <Redirect to="/login" />;
    }
    return (
      <div style={{ height: 100 + "vh" }}>
        <Layout>
          <Sider
            width
            breakpoint="lg"
            collapsedWidth="0"
            onBreakpoint={(broken) => {}}
            onCollapse={(collapsed, type) => {}}
            width="80"
            id="main-side-menu"
          >
            <CustomAvatar type="main-avatar" avatar={this.props.userName} />
            <div className="menu-separation" />
            <Menu
              theme="dark"
              mode="inline"
              defaultSelectedKeys={["1"]}
              onSelect={this.handleMainMenuChange}
            >
              <Menu.Item key="1">
                <Icon type="message" style={{ fontSize: 30 }} />
              </Menu.Item>
              <Menu.Item key="2">
                <Icon type="bars" style={{ fontSize: 30 }} />
              </Menu.Item>
              <Menu.Item key="3">
                <Icon type="pay-circle" style={{ fontSize: 30 }} />
              </Menu.Item>
              {/* <Menu.Item key="4">
                <Icon type="user" style={{ fontSize: 30 }} />
              </Menu.Item> */}
            </Menu>
          </Sider>
          {this.renderMainSlide()}
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
    actions() {
      return channingActions({}, dispatch, bindAuthActions, bindPaymentActions);
    },
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(Main);
