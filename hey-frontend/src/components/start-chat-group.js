import React from "react";
import { Alert, Button, Input, Modal, Tag } from "antd";
import CustomAvatar from "../components/custom-avatar";
import { connect } from "react-redux";
import {
  addNewUserChatGroup,
  removeUserChatGroup,
  startNewChatGroup,
} from "../actions/chatAction";
import AddFriend from "./add-friend-transfer";

const { Search } = Input;

class StartChatGroup extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      selectedUserId: null,
      groupName: "",
      selectedFullName: "",
    };
    this.addMoreUsername = this.addMoreUsername.bind(this);
    this.handleRemoveUsername = this.handleRemoveUsername.bind(this);
    this.handleOk = this.handleOk.bind(this);
  }

  showModal = () => {
    this.setState({
      visible: true,
    });
  };

  handleOk = (e) => {
    this.setState({
      visible: false,
    });
    this.props.startNewChatGroup(this.state.groupName);
  };

  handleCancel = (e) => {
    this.setState({
      visible: false,
    });
  };

  addMoreUsername = (e) => {
    this.props.addNewUserChatGroup(this.state.selectedUserId);
  };

  handleRemoveUsername = (item) => {
    this.props.removeUserChatGroup(item);
  };

  render() {
    return (
      <div>
        <div className="new-action-menu" onClick={this.showModal}>
          <a href="#">
            <CustomAvatar type="new-avatar" />
            <div className="new-text">Start New Group Chat</div>
          </a>
        </div>
        <Modal
          width="420px"
          title="Start New Chat Group"
          visible={this.state.visible}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          okText="Start"
          cancelText="Cancel"
          className="start-chat-group-modal"
        >
          {this.props.startChatGroupError ? (
            <Alert
              message={this.props.startChatGroupErrorMessage}
              type="error"
            />
          ) : (
            ""
          )}
          {/* <p className="model-label">Please enter user name:</p>
          <div className="first-line">
            <Input
              ref={(ref) => {
                this.ref = ref;
              }}
              id="add-user-name"
              className="add-user-name"
              onPressEnter={this.addMoreUsername}
            />
            <Button
              onClick={this.addMoreUsername}
              type="primary"
              shape="circle"
              icon="plus"
            />
          </div>
           */}
          <Input
            placeholder="Group Name"
            // enterButton="Search"
            // size="large"
            style={{ marginBottom: 10, width: 200 }}
            onChange={(e) => this.setState({ groupName: e.target.value })}
          />

          <div className="first-line">
            <AddFriend
              onChange={(value) => {
                // console.log(`selected ${value}`);
                this.setState({ selectedUserId: value.userId });
              }}
            />{" "}
            <Button
              onClick={this.addMoreUsername}
              type="primary"
              shape="circle"
              icon="plus"
              style={{ marginLeft: 10 }}
            />
          </div>

          {this.props.startChatGroupList.length > 0 ? (
            <p
              className="model-label"
              style={{ marginBottom: 3, marginTop: 10 }}
            >
              Selected:
            </p>
          ) : (
            ""
          )}
          {this.props.startChatGroupList.map((item, index) => (
            <Tag
              key={index}
              closable
              onClose={(e) => {
                this.handleRemoveUsername(item);
                e.preventDefault();
              }}
              color="#f50"
            >
              {item}
            </Tag>
          ))}
        </Modal>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    startChatGroupList: state.chatReducer.startChatGroupList,
    startChatGroupError: state.chatReducer.startChatGroupError,
    startChatGroupErrorMessage: state.chatReducer.startChatGroupErrorMessage,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    addNewUserChatGroup(username) {
      dispatch(addNewUserChatGroup(username));
    },
    removeUserChatGroup(username) {
      dispatch(removeUserChatGroup(username));
    },
    startNewChatGroup(groupName) {
      dispatch(startNewChatGroup(groupName));
    },
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(StartChatGroup);
