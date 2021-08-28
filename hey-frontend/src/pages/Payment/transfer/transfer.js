import React from "react";
import { connect } from "react-redux";
import { Button, Input } from "antd";
import NumericInput from "../../../components/numberic-input";
import AddFriendTransfer from "../../../components/add-friend-transfer";
import {
  channingActions,
  currencyToString,
  formatToCurrency,
} from "../../../utils";
import { bindPaymentActions } from "../../../actions";

import "./transfer.css";

class MessagePanel extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      topupType: 1,
      amount: "",
      message: "",
      selectedUserId: "",
    };
    this.errorReceiver = React.createRef();
    this.errorAmount = React.createRef();
  }

  showPinModal = () => {
    let hasError = false;
    if (!this.state.selectedUserId) {
      this.errorReceiver.current.innerText = "Please choose receiver";
      hasError = true;
    }
    if (!this.state.amount) {
      this.errorAmount.current.innerText = "Please fill amount";
      hasError = true;
    }
    if (this.state.amount > 50000000) {
      this.errorAmount.current.innerText = "Max amount is 50000000";
      hasError = true;
    }
    if (this.state.amount < 1000) {
      this.errorAmount.current.innerText = "Min amount is 1000";
      hasError = true;
    }
    if (!hasError) {
      // this.props.paymentActions.onOpenPinPopup();
      this.props.setAmount(this.state.amount);
      this.props.setSelectedUserId(this.state.selectedUserId);
      this.props.setSendMessage(this.state.message);

      this.props.cb();
      return true;
    }
    return false;
  };

  renderButtonMoney = (item, index) => {
    return (
      <Button
        key={index}
        className="money-btn"
        onClick={() => {
          this.errorAmount.current.innerText = "";
          this.setState({ amount: item.value });
        }}
        style={{ borderRadius: 300, height: 40 }}
        type="primary"
      >
        {item.title}
      </Button>
    );
  };

  renderListOfDefaultMoney = () => {
    const data1 = [
      { title: "50,000", value: "50000" },
      { title: "100,000", value: "100000" },
      { title: "200,000", value: "200000" },
    ];
    const data2 = [
      { title: "500,000", value: "500000" },
      { title: "1,000,000", value: "1000000" },
      { title: "2,000,000", value: "2000000" },
    ];

    return (
      <div className="wrapper-money-btns">
        <div
          style={{
            height: 50,
            width: "100%",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          {data1.map((e, index) => {
            return this.renderButtonMoney(e, index);
          })}
        </div>
        <div
          style={{
            height: 50,
            width: "100%",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            marginTop: 10,
          }}
        >
          {data2.map((e, index) => {
            return this.renderButtonMoney(e, index);
          })}
        </div>
      </div>
    );
  };

  render() {
    return (
      <div className="wrapper-transfer-form">
        <div>
          <div className="form-group">
            <div
              style={{
                fontSize: 20,
                fontWeight: 200,
                marginLeft: 20,
                width: 120,
              }}
            >
              Transfer To
            </div>
            <AddFriendTransfer
              onChange={(value) => {
                this.errorReceiver.current.innerText = "";
                this.setState({ selectedUserId: value });
              }}
            ></AddFriendTransfer>
          </div>
          <div className="form-group">
            <div style={{ fontSize: 20, width: 150, fontWeight: 200 }}></div>
            <p className="error-small-text" ref={this.errorReceiver}></p>
          </div>
          <div className="form-group" style={{ marginTop: 20 }}>
            <div
              style={{
                fontSize: 20,
                fontWeight: 200,
                marginLeft: 20,
                width: 120,
              }}
            >
              Amount
            </div>
            <div style={{ display: "flex", flexDirection: "column", flex: 1 }}>
              <NumericInput
                style={{ width: "50%" }}
                value={formatToCurrency(this.state.amount)}
                onChange={(value) => {
                  this.errorAmount.current.innerText = "";
                  this.setState({ amount: currencyToString(value) });
                }}
              />
            </div>
          </div>
          <div className="form-group">
            <div style={{ fontSize: 20, width: 150, fontWeight: 200 }}></div>
          </div>
          <div className="form-group">
            <div style={{ fontSize: 20, width: 150, fontWeight: 200 }}></div>
            <p className="error-small-text" ref={this.errorAmount}></p>
          </div>
          <div className="form-group" style={{ marginTop: 20 }}>
            <div
              style={{
                fontSize: 20,
                fontWeight: 200,
                marginLeft: 20,
                width: 120,
              }}
            >
              Message
            </div>
            <div style={{ display: "flex", flexDirection: "column", flex: 1 }}>
              <Input
                style={{ width: "50%" }}
                placeholder="Message to your friend"
                value={this.state.message}
                onChange={(e) => {
                  this.setState({ message: e.target.value });
                }}
              />
            </div>
          </div>
        </div>
        <div style={{ flex: 1, marginTop: 20 }}>
          {this.renderListOfDefaultMoney()}
        </div>
        <div
          style={{
            display: "flex",
            width: "100%",
            height: 50,
          }}
        >
          <div style={{ flex: 1 }}></div>

          <Button
            // variant="contained"
            // color="primary"
            onClick={this.showPinModal}
            style={{
              width: 80,
              height: 30,
              backgroundColor: "#3f51b5",
              color: "white",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              fontSize: 15,
              fontWeight: 500,
              marginTop: 10,
            }}
          >
            Next
          </Button>
        </div>
      </div>
    );
  }
}

export default connect(
  (state) => ({
    messageItems: state.chatReducer.messageItems,
  }),
  (dispatch) => channingActions({}, dispatch, bindPaymentActions)
)(MessagePanel);
