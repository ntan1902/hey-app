import React from "react";
import { connect } from "react-redux";
import { Button, Icon, Input } from "antd";
import NumericInput from "../../../components/numberic-input";
import Transfer from "../../../components/transfer";
import AddFriendTransfer from "../../../components/add-friend-transfer";
import { DocTien, formatToCurrency, currencyToString } from "../../../utils";

import { channingActions } from "../../../utils";
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
      this.props.paymentActions.onOpenPinPopup();
    }
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
        <div className="row-money-btns">
          {data1.map((e, index) => {
            return this.renderButtonMoney(e, index);
          })}
        </div>
        <div className="row-money-btns">
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
            <div style={{ fontSize: 20, width: 150, fontWeight: 200 }}>
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
                width: 150,
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
                width: 150,
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
        <div style={{ flex: 1, padding: 30 }}>
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
          <div onClick={this.showPinModal}>
            <Button className="continue-btn" type="primary">
              Continue
            </Button>
          </div>
          <Transfer
            amount={this.state.amount}
            targetId={this.state.selectedUserId}
            message={this.state.message}
          ></Transfer>
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
