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
  Drawer,
  List,
  Avatar,
  Divider,
  Col,
  Row,
  Radio,
  Form,
  Modal,
  message,
} from "antd";

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
import { AuthAPI } from "../api";
import { api } from "../api/api";
const { Header, Content, Footer, Sider } = Layout;
const { TextArea } = Input;
const pStyle = {
  fontSize: 16,
  color: "rgba(0,0,0,0.85)",
  lineHeight: "24px",
  display: "block",
  marginBottom: 16,
};

const DescriptionItem = ({ title, content }) => (
  <div
    style={{
      fontSize: 14,
      lineHeight: "22px",
      marginBottom: 7,
      color: "rgba(0,0,0,0.65)",
    }}
  >
    <p
      style={{
        marginRight: 8,
        display: "inline-block",
        color: "rgba(0,0,0,0.85)",
      }}
    >
      {title}:
    </p>
    {content}
  </div>
);

class Main extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      menuaction: 1,
      visible: false,
      changePasswordVisible: false,
      changeProfileVisible: false,
      profile: null,
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
      this.setState({ profile: getProFileResponse.data });
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

  showDrawer = () => {
    this.setState({
      visible: true,
    });
  };

  onClose = () => {
    this.setState({
      visible: false,
    });
  };

  onChangePassword = async () => {
    let data = {
      oldPassword: $("#old-password").val(),
      password: $("#new-password").val(),
      confirmPassword: $("#confirm-password").val(),
    };
    $("#old-password").val("");
    $("#new-password").val("");
    $("#confirm-password").val("");
    try {
      let res = await AuthAPI.changePassword(data);
      this.setState({
        changePasswordVisible: false,
      });
      message.success("Change password success !!!");
    } catch (err) {
      console.log(err.response);
      message.error("Invalid current password !!!");
    }
  };

  onChangePasswordCancel = () => {
    this.setState({
      changePasswordVisible: false,
    });
  };

  changePassword = () => {
    const { visible, onCancel, onCreate, form } = this.props;
    const { getFieldDecorator } = form;

    return (
      <Modal
        visible={this.state.changePasswordVisible}
        title="Change Password"
        okText="Ok"
        onCancel={this.onChangePasswordCancel}
        onOk={this.onChangePassword}
      >
        <Form layout="vertical">
          <Form.Item label="Old Password">
            {getFieldDecorator("old-password", {
              rules: [
                {
                  required: true,
                  message: "Please input Old Password!",
                },
              ],
            })(
              <Input.Password allowClear placeholder="Your current password" />
            )}
          </Form.Item>
          <Form.Item label="New Password">
            {getFieldDecorator("new-password", {
              rules: [
                {
                  required: true,
                  message: "Please input New Password!",
                },
              ],
            })(<Input.Password allowClear placeholder="new password" />)}
          </Form.Item>
          <Form.Item
            label="Confirm Password"
            // hasFeedback
            // validateStatus="error"
          >
            {getFieldDecorator("confirm-password", {
              rules: [
                {
                  required: true,
                  message: "Please input Confirm Password!",
                },
                {
                  validator: (rule, value, callback) => {
                    const { form } = this.props;
                    if (value && value !== form.getFieldValue("new-password")) {
                      callback("Two passwords that you enter is inconsistent!");
                    } else {
                      callback();
                    }
                  },
                },
              ],
            })(<Input.Password allowClear placeholder="confirm password" />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  };

  onChangePin = async () => {
    if (!this.state.hasPin) {
      try {
        let res = await AuthAPI.createPin({
          pin: $("#pin").val(),
        });

        this.setState({
          changePinVisible: false,
        });
        message.success("Change pin success !!!");
      } catch (err) {
        console.log(err.response);
        message.error("Invalid current pin !!!");
      }
      return;
    }

    let data = {
      oldPin: $("#old-pin").val(),
      pin: $("#new-pin").val(),
      confirmPin: $("#confirm-pin").val(),
    };
    $("#old-pin").val("");
    $("#new-pin").val("");
    $("#confirm-pin").val("");
    try {
      let res = await AuthAPI.changePin(data);

      this.setState({
        changePinVisible: false,
      });
      message.success("Change pin success !!!");
    } catch (err) {
      console.log(err.response);
      message.error("Invalid current pin !!!");
    }
  };

  onChangePinCancel = () => {
    this.setState({
      changePinVisible: false,
    });
  };

  changePin = () => {
    const { visible, onCancel, onCreate, form } = this.props;
    const { getFieldDecorator } = form;

    return (
      <Modal
        visible={this.state.changePinVisible}
        title={this.state.hasPin ? "Change Pin" : "Create your new PIN"}
        okText="Ok"
        onCancel={this.onChangePinCancel}
        onOk={this.onChangePin}
      >
        <Form layout="vertical">
          {this.state.hasPin ? (
            <div>
              <Form.Item label="Old Pin">
                {getFieldDecorator("old-pin", {
                  rules: [
                    {
                      required: true,
                      message: "Please input Old Pin!",
                    },
                  ],
                })(<Input.Password allowClear placeholder="Old pin" />)}
              </Form.Item>
              <Form.Item label="New Pin">
                {getFieldDecorator("new-pin", {
                  rules: [
                    {
                      required: true,
                      message: "Please input New Pin!",
                    },
                  ],
                })(<Input.Password allowClear placeholder="New pin" />)}
              </Form.Item>
              <Form.Item label="Confirm Pin">
                {getFieldDecorator("confirm-pin", {
                  rules: [
                    {
                      required: true,
                      message: "Please input Confirm Pin!",
                    },
                    {
                      validator: (rule, value, callback) => {
                        const { form } = this.props;
                        if (value && value !== form.getFieldValue("new-pin")) {
                          callback("Two pins that you enter is inconsistent!");
                        } else {
                          callback();
                        }
                      },
                    },
                  ],
                })(<Input.Password allowClear placeholder="Confirm pin" />)}
              </Form.Item>
            </div>
          ) : (
            <Form.Item label="New Pin">
              {getFieldDecorator("pin", {
                rules: [
                  {
                    required: true,
                    message: "Please input Pin!",
                  },
                ],
              })(<Input.Password allowClear placeholder="pin" />)}
            </Form.Item>
          )}
        </Form>
      </Modal>
    );
  };

  onChangeProfile = async () => {
    let data = {
      email: $("#email").val(),
      fullName: $("#full-name").val(),
    };
    $("#email").val("");
    $("#full-name").val("");
    try {
      let res = await api.post("api/protected/editprofile", data);
      this.setState({
        changeProfileVisible: false,
      });
      console.log(res);
      message.success("Change profile success !!!");
    } catch (err) {
      console.log(err.response);
      message.error(err.response.data.message);
    }
  };

  onChangeProfileCancel = () => {
    this.setState({
      changeProfileVisible: false,
    });
  };

  changeProfile = () => {
    const { visible, onCancel, onCreate, form } = this.props;
    const { getFieldDecorator } = form;

    return (
      <Modal
        visible={this.state.changeProfileVisible}
        title="Change Profile"
        okText="Ok"
        onCancel={this.onChangeProfileCancel}
        onOk={this.onChangeProfile}
      >
        <Form layout="vertical">
          <Form.Item label="Email">
            {getFieldDecorator("email", {
              rules: [
                {
                  type: "email",
                  message: "The input is not valid E-mail!",
                },
                {
                  required: true,
                  message: "Please input your E-mail!",
                },
              ],
            })(<Input />)}
          </Form.Item>
          <Form.Item label="Full Name">
            {getFieldDecorator("full-name", {
              rules: [
                {
                  required: true,
                  message: "Please input Full Name!",
                },
              ],
            })(<Input type="textarea" />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  };

  renderModal = () => {
    if (this.state.profile == null) return;
    return (
      <Drawer
        width={640}
        placement="right"
        closable={false}
        onClose={this.onClose}
        visible={this.state.visible}
      >
        <p style={{ ...pStyle, marginBottom: 24 }}>User Profile</p>
        <p style={pStyle}>Personal</p>
        <Row>
          <Col span={12}>
            <DescriptionItem
              title="Full Name"
              content={this.state.profile.fullName}
            />
          </Col>
          <Col span={12}>
            <DescriptionItem
              title="Username"
              content={this.state.profile.username}
            />
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <DescriptionItem title="Email" content={this.state.profile.email} />
          </Col>
          <Col span={12}>
            <DescriptionItem
              title="Balance"
              content={this.props.balance + " Đồng"}
            />
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <DescriptionItem title="Birthday" content="February 2,1900" />
          </Col>
          <Col span={12}>
            <DescriptionItem title="Phone Number" content="0939238329" />
          </Col>
        </Row>
        <Row>
          <Col span={24}>
            <DescriptionItem
              title="Status"
              content="Make things as simple as possible but no simpler."
            />
          </Col>
        </Row>
        <Divider />
        <p style={pStyle}>Settings</p>
        <Row>
          <Col span={8}>
            <Button
              style={{ width: 150 }}
              onClick={() => {
                this.setState({ changePasswordVisible: true });
              }}
              type="primary"
            >
              Change Password
            </Button>
          </Col>
          <Col span={8}>
            <Button
              style={{ width: 150 }}
              onClick={async () => {
                let res = await AuthAPI.hasPin();
                this.setState({
                  changePinVisible: true,
                  hasPin: res.data.payload.hasPin,
                });
              }}
              type="primary"
            >
              Edit Pin
            </Button>{" "}
          </Col>
          <Col span={8}>
            <Button
              style={{ width: 150 }}
              onClick={() => {
                this.setState({ changeProfileVisible: true });
              }}
              type="primary"
            >
              Edit Profile
            </Button>{" "}
          </Col>
        </Row>
        {this.changePassword()}
        {this.changePin()}
        {this.changeProfile()}
      </Drawer>
    );
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
                this.showDrawer();
              }}
            >
              <CustomAvatar type="main-avatar" avatar={this.props.userName} />
            </div>
            {this.renderModal()}
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
    balance: state.paymentReducer.balance,
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

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Form.create()(Main));
