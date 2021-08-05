import React from "react";
import { Modal, Input, Alert, Button, Icon } from "antd";
import CustomAvatar from "../../components/custom-avatar";
import {
  addNewUserChatGroup,
  removeUserChatGroup,
  startNewChatGroup,
} from "../../actions/chatAction";
import {
  addNewFriend,
  changeStateTopup,
} from "../../actions/addressBookAction";
import { connect } from "react-redux";
import $ from "jquery";

import { channingActions } from "../../utils";
import { bindPaymentActions } from "../../actions";

class Topup extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      paymentType: 0,
    };
  }

  showModal = (screenName) => {
    this.props.paymentActions.switchMainScreen(screenName).then((res) => {
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
      <div
        style={{
          display: "flex",
          flexDirection: "row",
          justifyContent: "center",
          alignContent: "center",
          marginBottom: 20,
        }}
      >
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            alignItems: "center",
            marginRight: 50,
          }}
        >
          <Button
            style={{
              backgroundColor: this.state.paymentType == 1 ? "blue" : "white",
              borderColor: "black",
              color: "black",
              borderRadius: 200,
              height: 50,
              width: 50,
              justifyContent: "center",
              alignItems: "center",
              padding: 0,
            }}
            // onClick={() => this.showModal("topup")}
            type="primary"
          >
            <Icon
              style={{
                fontSize: 30,
                color: this.state.paymentType == 1 ? "white" : "black",
              }}
              type="bank"
            />
          </Button>
          <div style={{ fontWeight: "bold" }}>Link a Bank</div>
        </div>
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          <Button
            style={{
              backgroundColor: this.state.paymentType == 2 ? "blue" : "white",
              borderColor: "black",
              color: "black",
              borderRadius: 200,
              height: 50,
              width: 50,
              justifyContent: "center",
              alignItems: "center",
              padding: 0,
            }}
            type="primary"
            // onClick={() => this.showModal("transfer")}
          >
            <Icon
              style={{
                fontSize: 30,
                color: this.state.paymentType == 2 ? "white" : "black",
              }}
              type="credit-card"
            />
          </Button>
          <div style={{ fontWeight: "bold" }}>Link a Card</div>
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
