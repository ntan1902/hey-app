import React from "react";
import { Modal, Input, Alert, Button } from "antd";
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

class VerifyPIN extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      handleSoftToken: false,
      softToken: "",
    };
  }

  showPinModal = () => {
    this.props.paymentActions.onOpenPinPopup();
  };

  handlePinCancel = (e) => {
    this.props.paymentActions.onClosePinPopup();
  };

  handleTransferCancel = (e) => {
    this.setState({ softToken: "", handleSoftToken: false });
    // this.props.paymentActions.onClosePinPopup();
  };

  handleTransferOK = (e) => {
    // this.props.paymentActions.onClosePinPopup();
  };

  handleOk = (e) => {
    console.log(e);
    var un = $("#add-user-name").val();
    $("#add-user-name").val("");
    this.props.paymentActions.verifyPin(un).then((res) => {
      this.setState({ handleSoftToken: true, softToken: res.softToken });
    });
  };

  render() {
    return (
      <div>
        <div onClick={this.showPinModal}>
          <Button
            style={{
              backgroundColor: "white",
              borderColor: "black",
              color: "black",
              width: 250,
              height: 50,
              margin: 0,
            }}
            onClick={() => {}}
            type="primary"
          >
            Continue
          </Button>
        </div>
        <Modal
          width="420px"
          title="Verify your PIN"
          visible={this.props.verifyPin}
          onOk={this.handleOk}
          onCancel={this.handlePinCancel}
          okText="Ok"
          cancelText="Cancel"
        >
          {/* {this.props.addFriendError ? (
            <Alert message={this.props.addFriendErrorMessage} type="error" />
          ) : (
            ""
          )} */}
          <p className="model-label">Please Enter PIN:</p>
          <Input
            id="add-user-name"
            className="add-user-name"
            onPressEnter={this.handleOk}
          />
        </Modal>
        <Modal
          width="420px"
          title="Accept your transfer"
          visible={
            this.state.handleSoftToken == true && this.state.softToken != ""
          }
          onOk={this.handleTransferOK}
          onCancel={this.handleTransferCancel}
          okText="Ok"
          cancelText="Cancel"
        >
          {/* {this.props.addFriendError ? (
            <Alert message={this.props.addFriendErrorMessage} type="error" />
          ) : (
            ""
          )} */}
          <p className="model-label">Your softToken expired in 60s</p>
          <p className="model-label" style={{ fontWeight: "bold" }}>
            Amount {this.props.amount}
          </p>
          {this.props.message ? (
            <p className="model-label">Message {this.props.message}</p>
          ) : (
            ""
          )}
        </Modal>
      </div>
    );
  }
}

export default connect(
  (state) => ({
    verifyPin: state.paymentReducer.verifyPin,
  }),
  (dispatch) => channingActions({}, dispatch, bindPaymentActions)
)(VerifyPIN);
