import React from "react";
import { Modal, Input, Alert } from "antd";
import CustomAvatar from "../components/custom-avatar";
import {
  addNewUserChatGroup,
  removeUserChatGroup,
  startNewChatGroup,
  loadNewAddFriend,
  addFriendToSession,
} from "../actions/chatAction";
import {
  addNewFriend,
  // changeStateAddFriendPopup,
} from "../actions/addressBookAction";
import { connect } from "react-redux";
import $ from "jquery";
import { message } from "antd";
import { changeStateAddFriendPopup } from "../actions/paymentAction";
import GetFriendList from "./add-friend-transfer";
class AddFriend extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      selectedUserId: null,
    };
    this.handleOk = this.handleOk.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.showModal = this.showModal.bind(this);
  }

  showModal = () => {
    this.props.changeStateAddFriendPopup(true);
  };

  handleOk = (e) => {
    console.log(e);
    var un = $("#add-user-name").val();
    $("#add-user-name").val("");
    // this.props.addNewFriend(un);
    this.props.addFriendToSession(
      this.props.currentSessionId,
      this.state.selectedUserId
    );
    this.props.changeStateAddFriendPopup(false);
  };

  handleCancel = (e) => {
    this.props.changeStateAddFriendPopup(false);
  };

  render() {
    return (
      <div>
        <Modal
          width="420px"
          title="Add New Friend"
          visible={this.props.addFriendPopup}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          okText="Add"
          cancelText="Cancel"
        >
          <GetFriendList
            onChange={(value) => {
              console.log(`selected ${value}`);

              this.setState({ selectedUserId: value });
            }}
          ></GetFriendList>
        </Modal>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    addFriendError: state.addressBookReducer.addFriendError,
    addFriendErrorMessage: state.addressBookReducer.addFriendErrorMessage,
    addFriendPopup: state.paymentReducer.isAddFriendToSession,
    currentSessionId: state.chatReducer.currentSessionId,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    addFriendToSession(sessionId, userId) {
      dispatch(addFriendToSession(sessionId, userId));
    },
    loadNewAddFriend(username) {
      dispatch(loadNewAddFriend(username));
    },
    changeStateAddFriendPopup(state) {
      dispatch(changeStateAddFriendPopup(state));
    },
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(AddFriend);
