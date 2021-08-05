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
import { Scrollbars } from "react-custom-scrollbars";

import NumericInput from "./numberic-input";
import Transfer from "./transfer";

import { channingActions } from "../utils";
import { bindPaymentActions } from "../actions";

import { Row, Col } from "antd";

class AddFriend extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      confirmLoading: false,
      ModalText: "Content of the modal",
      isCreate: false,
      data: [{}, {}, {}, {}, {}, {}],
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
    this.setState({ isCreate: false });
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
                Lucky Money type
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
                  this.setState({ moneyEachBag: value });
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
                  this.setState({ numberOfBag: value });
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
              height: "100%",
              justifyContent: "flex-end",
              alignItems: "flex-end",
            }}
          >
            <Transfer amount={this.state.amount}></Transfer>
          </div>
        </div>
      </div>
    );
  };

  luckyMoneyItem = () => {
    return (
      <Col
        style={{
          display: "flex",
          width: 300,
          height: 450,
          padding: 20,
        }}
        span={4}
      >
        <div
          style={{
            width: "100%",
            height: "100%",
            borderWidth: 2,
            borderStyle: "solid",
            display: "flex",
            flexDirection: "column",
            // justifyContent: "center",
            alignItems: "center",
          }}
        >
          <div
            style={{
              position: "absolute",
              top: 10,
              left: 35,
              backgroundColor: "white",
            }}
          >
            From Ly Gioi An
          </div>
          <p
            style={{
              margin: 0,
              padding: 0,
              marginTop: 15,
              fontSize: 20,
              fontWeight: "lighter",
            }}
          >
            Click "Open" to receive
          </p>
          <div
            style={{
              width: "100%",
              height: 150,
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            <div
              style={{
                width: 100,
                height: 100,
                borderRadius: 100,
                borderWidth: 2,
                borderStyle: "solid",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                marginTop: 15,
              }}
            >
              <p
                style={{
                  margin: 0,
                  padding: 0,
                  fontSize: 20,
                  fontWeight: "bolder",
                }}
              >
                Open
              </p>
            </div>
          </div>
          <p
            style={{
              margin: 0,
              padding: 0,
              marginTop: 30,
              fontSize: 20,
              fontWeight: "lighter",
            }}
          >
            Happy New Year
          </p>
          <div
            style={{
              flex: 1,
              display: "flex",
              alignItems: "flex-end",
              paddingBottom: 20,
            }}
          >
            <a
              style={{
                margin: 0,
                padding: 0,
                fontSize: 20,
                fontWeight: "lighter",
                textDecoration: "underline",

                color: "black",
              }}
            >
              Detail
            </a>
          </div>
        </div>
      </Col>
    );
  };

  luckyMoneyReceivedItem = () => {
    return (
      <Col
        style={{
          display: "flex",
          width: 300,
          height: 450,
          padding: 20,
        }}
        span={4}
      >
        <div
          style={{
            width: "100%",
            height: "100%",
            borderWidth: 2,
            borderStyle: "solid",
            display: "flex",
            flexDirection: "column",
            // justifyContent: "center",
            alignItems: "center",
          }}
        >
          <div
            style={{
              position: "absolute",
              top: 10,
              left: 35,
              backgroundColor: "white",
            }}
          >
            From Ly Gioi An
          </div>
          <p
            style={{
              margin: 0,
              padding: 0,
              marginTop: 15,
              fontSize: 20,
              fontWeight: "bold",
            }}
          >
            You Received
          </p>
          <div
            style={{
              width: "100%",
              height: 150,
              paddingTop: 20,
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              flexDirection: "column",
            }}
          >
            <Icon
              style={{
                fontSize: 50,
                color: "black",
                fontWeight: "lighter",
              }}
              type="dollar"
            />
            <p
              style={{
                margin: 0,
                padding: 0,
                marginTop: 20,
                fontSize: 20,
                fontWeight: "bolder",
              }}
            >
              3.000đ
            </p>
          </div>
          <p
            style={{
              margin: 0,
              padding: 0,
              marginTop: 30,
              fontSize: 20,
              fontWeight: "lighter",
            }}
          >
            Happy New Year
          </p>
          <div
            style={{
              flex: 1,
              display: "flex",
              alignItems: "flex-end",
              paddingBottom: 20,
            }}
          >
            <a
              style={{
                margin: 0,
                padding: 0,
                fontSize: 20,
                fontWeight: "lighter",
                textDecoration: "underline",

                color: "black",
              }}
            >
              Detail
            </a>
          </div>
        </div>
      </Col>
    );
  };

  renderLuckyMoneyItems = () => {
    return this.state.data.map((e, index) => {
      if (index == 2 || index == 3) return this.luckyMoneyReceivedItem(e);
      return this.luckyMoneyItem(e);
    });
  };

  renderLuckyMoney = () => {
    return (
      <div
        style={{
          display: "flex",
          width: "100%",
          height: window.screen.height * 0.55,
          // justifyContent: "center",
          // alignItems: "center",
          flexDirection: "column",
          padding: 15,
        }}
      >
        {" "}
        <p
          style={{
            margin: 0,
            padding: 0,
            fontSize: 30,
            fontWeight: "lighter",
          }}
        >
          There are some lucky money from your friends
        </p>
        <Scrollbars autoHide autoHideTimeout={500} autoHideDuration={200}>
          <Row
            style={{ width: "100%", height: "100%" }}
            // type="flex"
            // justify="space-between"
          >
            {this.renderLuckyMoneyItems()}
          </Row>
        </Scrollbars>
        <div
          style={{ width: "100%", display: "flex", justifyContent: "flex-end" }}
        >
          <p
            style={{
              margin: 0,
              padding: 0,
              fontSize: 30,
              fontWeight: "lighter",
              marginRight: 10,
            }}
          >
            Want to create a new one ?
          </p>
          <Icon
            onClick={() => this.setState({ isCreate: true })}
            style={{
              fontSize: 40,
              color: "#ggg",
            }}
            type="plus-circle"
          />
        </div>
      </div>
    );
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
