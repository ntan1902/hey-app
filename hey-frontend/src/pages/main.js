import React from "react";
import { Icon, Layout, Menu } from "antd";
import CustomAvatar from "../components/custom-avatar";
import Chat from "./Chat";
import Friend from "./Friend";
import Payment from "./Payment";
import ProfileScreen from "./Profile";
import { Form } from "antd";

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
import { changeStateDrawerProfile } from "../actions/modalAction";
import Profile from "../components/Profile/Profile";
import { setProfile } from "../actions/userAction";

const { Sider } = Layout;

const SHOW_PROFILE = true;
const HIDE_PROFILE = false;
class Main extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      menuaction: 1,
      changePasswordVisible: false,
      changeProfileVisible: false,
      error: false,
      hasPin: false,
    };
    this.handleMainMenuChange = this.handleMainMenuChange.bind(this);
    this.handleMessageEnter = this.handleMessageEnter.bind(this);
    this.handleSendClick = this.handleSendClick.bind(this);
  }

  async componentDidMount() {
    this.props.initialWebSocket();

    let actions = this.props.actions();

    try {
      let getProFileResponse = await actions.authActions.getProfile();
      this.props.setProfile(getProFileResponse.data);
      setUserIdToStorage(getProFileResponse.data.id);

      let checkBalanceResponse = await actions.paymentActions.checkBalance();
      if (checkBalanceResponse.success) {
        actions.paymentActions.getBalance();
      }
    } catch (err) {
      console.log("Err", err);
      clearStorage();
    }
  }

  componentWillUnmount() {}

  handleMainMenuChange(e) {
    this.setState({ menuaction: e.key });
  }

  handleMessageEnter(e) {
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
        return <Friend />;
      case "3":
        return <Payment />;
      case "4":
        return <ProfileScreen />;
      default:
        return <Chat />;
    }
  };

  showDrawerProfile = () => {
    this.props.changeStateDrawerProfile(SHOW_PROFILE);
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
            <div
              onClick={() => {
                this.showDrawerProfile();
              }}
            >
              {this.props.profile.miniAvatar ? (
                <CustomAvatar
                  type="main-avatar"
                  src={this.props.profile.miniAvatar}
                />
              ) : (
                <CustomAvatar type="main-avatar" avatar={this.props.userName} />
              )}
            </div>
            <Profile />
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
    userStatus: state.userReducer.userStatus,
    profile: state.userReducer.profile,
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
    changeStateDrawerProfile(isVisible) {
      dispatch(changeStateDrawerProfile(isVisible));
    },
    setProfile(profile) {
      dispatch(setProfile(profile));
    },
    actions() {
      return channingActions({}, dispatch, bindAuthActions, bindPaymentActions);
    },
  };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Form.create()(Main));
