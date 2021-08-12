import React from "react";
import { Modal, Input, Alert, Button, message } from "antd";
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
      time: {},
      seconds: 30,
    };
    this.timer = 0;
  }


  handlePinCancel = (e) => {
    this.props.paymentActions.onClosePinPopup();
  };

  handleTransferCancel = (e) => {
    this.setState({ softToken: "", handleSoftToken: false });
    this.props.paymentActions.onClosePinPopup();
  };

  handleTransferOK = (e) => {
    if (this.props.type == "lm") {
      this.props.paymentActions
        .createLuckymoney({
          softToken: this.state.softToken,
          ...this.props.data,
        })
        .then((res) => {
          if (this.props.cb) this.props.cb();
          this.setState({ handleSoftToken: false, softToken: "" });
        });
      return true;
    }
    this.props.paymentActions
      .transfer({
        targetId: this.props.targetId,
        softToken: this.state.softToken,
        message: this.props.message,
      })
      .then((res) => {
        if (this.props.cb) this.props.cb();
        this.setState({ handleSoftToken: false, softToken: "" });
      })
      .catch(err => {
        message.error(err.error.response.data.message);
      });
  };

  handleOk = (e) => {
    console.log(e);
    var un = $("#add-user-name").val();
    $("#add-user-name").val("");
    this.props.paymentActions.verifyPin(un, this.props.amount).then((res) => {
      console.log(res, "Pin");
      this.setState({ handleSoftToken: true, softToken: res.softToken });
      this.startTimer();
    })
      .catch(err => {
        message.error(err.error.response.data.message)
      });
  };

  secondsToTime = (secs) => {
    let hours = Math.floor(secs / (60 * 60));

    let divisor_for_minutes = secs % (60 * 60);
    let minutes = Math.floor(divisor_for_minutes / 60);

    let divisor_for_seconds = divisor_for_minutes % 60;
    let seconds = Math.ceil(divisor_for_seconds);

    let obj = {
      h: hours,
      m: minutes,
      s: seconds,
    };
    return obj;
  };

  componentDidMount() {
    let timeLeftVar = this.secondsToTime(this.state.seconds);
    this.setState({ time: timeLeftVar });
  }

  startTimer = () => {
    if (this.timer == 0 && this.state.seconds > 0) {
      this.timer = setInterval(this.countDown, 1000);
    }
  };

  countDown = () => {
    // Remove one second, set state so a re-render happens.
    let seconds = this.state.seconds - 1;
    this.setState({
      time: this.secondsToTime(seconds),
      seconds: seconds,
    });

    // Check if we're at zero.
    if (seconds == 0) {
      this.setState({ softToken: "", handleSoftToken: false });
      this.props.paymentActions.onClosePinPopup();
      clearInterval(this.timer);
    }
  };

  render() {
    return (
      <div>
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

          <Input.Password
            id="add-user-name"
            className="add-user-name"
            onPressEnter={this.handleOk}
            placeholder="input pin"
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
          <p className="model-label">
            Your softToken expired in {this.state.time.s}s
          </p>
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
