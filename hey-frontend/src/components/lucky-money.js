import React from "react";
import { Modal, Input, Alert, Button, Icon } from "antd";
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

import NumericInput from "./numberic-input";
import Transfer from "./transfer";

import { channingActions } from "../utils";
import { bindPaymentActions } from "../actions";

class AddFriend extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      confirmLoading: false,
      ModalText: "Content of the modal",
      isCreate: false,
      data: [],
      topupType: 1,
      moneyEachBag: "",
      numberOfBag: "",
      message: "",
    };
    this.handleOk = this.handleOk.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.showModal = this.showModal.bind(this);
  }

  showModal = () => {
    this.props.paymentActions.changeStateLuckyMoneyPopup(true);
  };

  handleOk = (e) => {
    console.log(e);
    var un = $("#add-user-name").val();
    $("#add-user-name").val("");

    this.setState({
      ModalText: "The modal will be closed after two seconds",
      confirmLoading: true,
    });
    setTimeout(() => {
      this.setState({
        visible: false,
        confirmLoading: false,
      });
      this.props.paymentActions.changeStateLuckyMoneyPopup(false);
    }, 2000);
  };

  handleCancel = (e) => {
    this.props.paymentActions.changeStateLuckyMoneyPopup(false);
  };

  renderEmptyLuckyMoney = () => {
    return (
      <div
        style={{
          display: "flex",
          width: "100%",
          height: window.screen.height * 0.55,
          justifyContent: "center",
          alignItems: "center",
          flexDirection: "column",
        }}
      >
        <p style={{ fontSize: 30, fontWeight: "bold" }}>
          There are no one Lucky Money bag here!
        </p>
        <p style={{ fontSize: 30, fontWeight: "bold" }}>
          Want to create a new one ?
        </p>
        <Button
          style={{
            backgroundColor: "#ddd",
            borderColor: "black",
            color: "black",
            width: 250,
            height: 50,
            margin: 0,
          }}
          onClick={() => {
            this.setState({ isCreate: true });
          }}
          type="primary"
        >
          <p
            style={{
              margin: 0,
              padding: 0,
              fontSize: 20,
              fontWeight: "bold",
              color: "#ggg",
            }}
          >
            Create Lucky Money
          </p>
        </Button>
      </div>
    );
  };

  renderCreateLuckyMoney = () => {
    return (
      <div
        style={{
          display: "flex",
          width: "100%",
          height: window.screen.height * 0.55,
          justifyContent: "center",
          alignItems: "center",
          flexDirection: "column",
        }}
      >
        <div
          style={{
            width: "100%",
            height: "100%",
            backgroundColor: "white",
            padding: 30,
            display: "flex",
            flexDirection: "column",
          }}
        >
          <div>
            <div
              style={{
                display: "flex",
                flexDirection: "row",
                // justifyContent: "center",
                alignItems: "center",
              }}
            >
              <div style={{ fontSize: 20, width: 250, fontWeight: "bold" }}>
                LuckyMoney type
              </div>
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
                      this.state.topupType == 1 ? "blue" : "white",
                    borderColor: "black",
                    color: "black",
                    borderRadius: 200,
                    height: 50,
                    width: 50,
                    justifyContent: "center",
                    alignItems: "center",
                    padding: 0,
                  }}
                  onClick={() => {
                    this.setState({ topupType: 1 });
                  }}
                  type="primary"
                >
                  <Icon
                    style={{
                      fontSize: 30,
                      color: this.state.topupType == 2 ? "black" : "white",
                    }}
                    type="bank"
                  />
                </Button>
                <div style={{ fontWeight: "bold" }}>Equally Devided</div>
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
                      this.state.topupType == 2 ? "blue" : "white",
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
                  onClick={() => {
                    this.setState({ topupType: 2 });
                  }}
                >
                  <Icon
                    style={{
                      fontSize: 30,
                      color: this.state.topupType == 1 ? "black" : "white",
                    }}
                    type="credit-card"
                  />
                </Button>
                <div style={{ fontWeight: "bold" }}>Random</div>
              </div>
            </div>
            <div
              style={{ display: "flex", flexDirection: "row", marginTop: 50 }}
            >
              <div style={{ fontSize: 20, width: 250, fontWeight: "bold" }}>
                Money of each bags
              </div>
              <NumericInput
                style={{ width: "50%" }}
                value={this.state.moneyEachBag}
                onChange={(value) => {
                  this.setState({ amount: value });
                }}
              />
            </div>
            <div
              style={{ display: "flex", flexDirection: "row", marginTop: 50 }}
            >
              <div style={{ fontSize: 20, width: 250, fontWeight: "bold" }}>
                Number of each bags
              </div>
              <NumericInput
                style={{ width: "50%" }}
                value={this.state.numberOfBag}
                onChange={(value) => {
                  this.setState({ amount: value });
                }}
              />
            </div>
            <div
              style={{ display: "flex", flexDirection: "row", marginTop: 50 }}
            >
              <div style={{ fontSize: 20, width: 250, fontWeight: "bold" }}>
                Wish message
              </div>
              <Input
                style={{ width: "50%" }}
                placeholder="Your Wish Message"
                value={this.state.message}
                onChange={(e) => {
                  this.setState({ message: e.target.value });
                }}
              />
            </div>
          </div>
          <div
            style={{
              display: "flex",
              width: "100%",
              height: 50,
            }}
          >
            <div style={{ flex: 1 }}></div>
            <Transfer amount={this.state.amount}></Transfer>
          </div>
        </div>
      </div>
    );
  };

  renderLuckyMoney = () => {
    return <div>LuckyMoney</div>;
  };

  render() {
    return (
      <div>
        <Modal
          width="80%"
          height="80%"
          title="Lucky Money"
          visible={this.props.luckyMoneyPopup}
          // onOk={this.handleOk}
          onCancel={this.handleCancel}
          // confirmLoading={this.state.confirmLoading}
          // okText="Add"
          // cancelText="Cancel"
          footer={null}
        >
          {this.state.isCreate || this.props.isCreate
            ? this.renderCreateLuckyMoney()
            : this.state.data.length == 0
            ? this.renderEmptyLuckyMoney()
            : this.renderLuckyMoney()}
        </Modal>
      </div>
    );
  }
}

export default connect(
  (state) => ({
    luckyMoneyPopup: state.paymentReducer.luckyMoneyPopup,
    isCreate: state.paymentReducer.isCreate,
  }),
  (dispatch) => channingActions({}, dispatch, bindPaymentActions)
)(AddFriend);
