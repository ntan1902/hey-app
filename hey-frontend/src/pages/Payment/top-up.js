import React from "react";
import {Button, Icon} from "antd";
import {connect} from "react-redux";
import $ from "jquery";

import {channingActions} from "../../utils";
import {bindPaymentActions} from "../../actions";

class Topup extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      paymentType: 0,
    };
  }

  showModal = (screenName) => {
    this.props.paymentActions.switchMainScreen(screenName).then((res) => {});
  };

  handleOk = (e) => {
    var un = $("#add-user-name").val();
    $("#add-user-name").val("");
    this.props.addNewFriend(un);
  };

  handleCancel = (e) => {
    this.props.paymentActions.switchMainScreen("").then((res) => {});
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
              backgroundColor:
                this.props.layoutType == "topup" ? "#1890FF" : "white",
              borderColor: "black",
              color: "black",
              borderRadius: 200,
              height: 70,
              width: 70,
              justifyContent: "center",
              alignItems: "center",
              padding: 0,
            }}
            onClick={() => this.showModal("topup")}
            type="primary"
          >
            <Icon
              style={{
                fontSize: 30,
                color: this.props.layoutType == "topup" ? "white" : "black",
              }}
              type="bank"
            />
          </Button>
          <div style={{ fontWeight: 500 }}>Topup</div>
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
              backgroundColor:
                this.props.layoutType == "transfer" ? "#1890FF" : "white",
              borderColor: "black",
              color: "black",
              borderRadius: 200,
              height: 70,
              width: 70,
              justifyContent: "center",
              alignItems: "center",
              padding: 0,
            }}
            type="primary"
            onClick={() => this.showModal("transfer")}
          >
            <Icon
              style={{
                fontSize: 30,
                color: this.props.layoutType == "transfer" ? "white" : "black",
              }}
              type="credit-card"
            />
          </Button>
          <div style={{ fontWeight: 500 }}>Transfer</div>
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
    layoutType: state.paymentReducer.layoutType,
  }),
  (dispatch) => channingActions({}, dispatch, bindPaymentActions)
)(Topup);
