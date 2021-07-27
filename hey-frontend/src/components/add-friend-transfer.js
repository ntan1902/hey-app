import React from "react";
import { Modal, Input, Alert } from "antd";
import CustomAvatar from "../components/custom-avatar";
import {
  addNewUserChatGroup,
  removeUserChatGroup,
  startNewChatGroup,
} from "../actions/chatAction";
import {
  addNewFriend,
  changeStateAddFriendPopup,
} from "../actions/addressBookAction";
import { connect } from "react-redux";
import $ from "jquery";

import { channingActions } from "../utils";
import { bindPaymentActions } from "../actions";

class AddFriend extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
    };
    this.handleOk = this.handleOk.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.showModal = this.showModal.bind(this);
  }

  showModal = () => {
    this.props.paymentActions.changeStateAddFriendTransferPopup(true);
  };

  handleOk = (e) => {
    console.log(e);
    var un = $("#add-user-name").val();
    $("#add-user-name").val("");
    this.props.paymentActions.changeStateAddFriendTransferPopup(false);
  };

  handleCancel = (e) => {
    this.props.paymentActions.changeStateAddFriendTransferPopup(false);
  };

  render() {
    return (
      <div>
        <div className="new-action-menu" onClick={this.showModal}>
          <a href="#">
            <CustomAvatar type="new-avatar" />
            <div className="new-text">Add friend to transfer</div>
          </a>
        </div>
        <Modal
          width="420px"
          title="Add New Friend"
          visible={this.props.addFriendTransferPopup}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          okText="Add"
          cancelText="Cancel"
        >
          {this.props.addFriendError ? (
            <Alert message={this.props.addFriendErrorMessage} type="error" />
          ) : (
            ""
          )}
          <p className="model-label">Please enter user name:</p>
          <Input
            id="add-user-name"
            className="add-user-name"
            onPressEnter={this.handleOk}
          />
        </Modal>
      </div>
    );
  }
}

export default connect(
  (state) => ({
    addFriendTransferPopup: state.paymentReducer.addFriendTransferPopup,
  }),
  (dispatch) => channingActions({}, dispatch, bindPaymentActions)
)(AddFriend);
