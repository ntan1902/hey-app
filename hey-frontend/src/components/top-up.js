import React from "react";
import { Modal, Input, Alert } from "antd";
import CustomAvatar from "../components/custom-avatar";
import {
  addNewUserChatGroup,
  removeUserChatGroup,
  startNewChatGroup,
} from "../actions/chatAction";
import { addNewFriend, changeStateTopup } from "../actions/addressBookAction";
import { connect } from "react-redux";
import $ from "jquery";

import { channingActions } from "../utils";
import { bindPaymentActions } from "../actions";

class Topup extends React.Component {
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
    console.log(this.props);
    this.props.paymentActions.switchMainScreen("topup").then((res) => {
      console.log("res");
    });
  };

  handleOk = (e) => {
    console.log(e);
    var un = $("#add-user-name").val();
    $("#add-user-name").val("");
    this.props.addNewFriend(un);
  };

  handleCancel = (e) => {
    this.props.paymentActions.switchMainScreen("").then((res) => {
      console.log("res");
    });
  };

  render() {
    return (
      <div>
        <div className="new-action-menu" onClick={this.showModal}>
          <a href="#">
            <CustomAvatar type="new-avatar" />
            <div className="new-text">Topup</div>
          </a>
        </div>
      </div>
    );
  }
}

export default connect(
  (state) => ({
    addFriendError: state.addressBookReducer.addFriendError,
    addFriendErrorMessage: state.addressBookReducer.addFriendErrorMessage,
    addFriendPopup: state.addressBookReducer.topup,
  }),
  (dispatch) => channingActions({}, dispatch, bindPaymentActions)
)(Topup);
