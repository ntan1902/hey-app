import React from "react";
import { Modal, Input, Button, Icon } from "antd";
import { connect } from "react-redux";
import $ from "jquery";
import { Scrollbars } from "react-custom-scrollbars";

import NumericInput from "./numberic-input";
import Transfer from "./transfer";

import {
  channingActions,
  currencyToString,
  formatToCurrency,
  currency,
} from "../utils";
import { bindPaymentActions } from "../actions";

import { Row, Col } from "antd";
import { Card, Avatar } from "antd";
import LuckyAnimation from "./LuckyAnimation/LuckyAnimation";

const { Meta } = Card;

class LuckyMoney extends React.Component {
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
      showAnimation: false,
    };
    this.handleOk = this.handleOk.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.showModal = this.showModal.bind(this);
  }

  componentWillReceiveProps() {
    if (this.props.currentSessionId) {
      this.props.paymentActions
        .getListLuckymoney(this.props.currentSessionId)
        .then((res) => {
          let lm = [
            ...res.data.filter((x) => !x.received),
            ...res.data.filter((x) => !!x.received),
          ];

          this.setState({ data: lm });
        });
    }
  }

  handleReceived = (id) => {
    this.props.paymentActions
      .receivedLuckymoney({ luckyMoneyId: id })
      .then((res) => {
        this.props.paymentActions
          .getListLuckymoney(this.props.currentSessionId)
          .then((res2) => {
            let lm = [
              ...res2.data.filter((x) => !x.received),
              ...res2.data.filter((x) => !!x.received),
            ];

            this.setState({ data: lm, showAnimation: true });
            setTimeout(() => this.setState({ showAnimation: false }), 2000);
          });
      });
  };

  showPinModal = () => {
    this.props.paymentActions.onOpenPinPopup();
  };

  showModal = () => {
    this.props.paymentActions.changeStateLuckyMoneyPopup(true);
  };

  handleOk = (e) => {
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
              <div style={{ fontSize: 20, width: 250, fontWeight: 200 }}>
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
                      this.state.topupType === 1 ? "blue" : "white",
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
                      color: this.state.topupType === 2 ? "black" : "white",
                    }}
                    type="bank"
                  />
                </Button>
                <div style={{ fontWeight: 400 }}>Equally Devided</div>
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
                <div style={{ fontWeight: 400 }}>Random</div>
              </div>
            </div>
            <div
              style={{ display: "flex", flexDirection: "row", marginTop: 50 }}
            >
              <div style={{ fontSize: 20, width: 250, fontWeight: 200 }}>
                {this.state.topupType == 1
                  ? "Money of each bags"
                  : "Total money"}
              </div>
              <NumericInput
                style={{ width: "50%" }}
                value={formatToCurrency(this.state.moneyEachBag)}
                onChange={(value) => {
                  this.setState({ moneyEachBag: currencyToString(value) });
                }}
              />
            </div>
            <div
              style={{ display: "flex", flexDirection: "row", marginTop: 50 }}
            >
              <div style={{ fontSize: 20, width: 250, fontWeight: 200 }}>
                Number of bags
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
              <div style={{ fontSize: 20, width: 250, fontWeight: 200 }}>
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
            <div onClick={this.showPinModal}>
              <Button className="continue-btn" type="primary">
                Continue
              </Button>
            </div>
            <Transfer
              amount={
                this.state.topupType === 1
                  ? this.state.moneyEachBag * this.state.numberOfBag
                  : this.state.moneyEachBag
              }
              type={"lm"}
              data={{
                message: this.state.message,
                numberBag: this.state.numberOfBag,
                type: this.state.topupType === 1 ? "equally" : "random",
                sessionChatId: this.props.currentSessionId,
              }}
              cb={() => {
                this.setState({
                  topupType: 1,
                  moneyEachBag: "",
                  numberOfBag: "",
                  message: "",
                });
              }}
            />
          </div>
        </div>
      </div>
    );
  };

  luckyMoneyItem = (e) => {
    return (
      <Col
        style={{
          display: "flex",
          width: 300,
          height: 352,
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
              zIndex: 100,
            }}
          >
            From {e.senderName}
          </div>
          <Card
            style={{
              width: "100%",
              height: "100%",
              borderRadius: 10,
            }}
            cover={
              <img
                alt="example"
                src="https://png.pngtree.com/thumb_back/fh260/background/20201230/pngtree-fan-shaped-new-year-red-envelopes-for-2021-image_517238.jpg"
              />
            }
            actions={[
              // <Icon type="setting" key="setting" />,
              // <Icon type="edit" key="edit" />,
              <div
                style={{
                  display: "flex",
                  paddingBottom: 20,
                  height: 20,
                  justifyContent: "center",
                  alignContent: "center",
                }}
              >
                <a
                  onClick={() => this.handleReceived(e.luckyMoneyId)}
                  style={{
                    margin: 0,
                    padding: 0,
                    fontSize: 12,
                    fontWeight: 600,
                    color: "black",
                  }}
                >
                  Receive Lucky Money
                </a>
              </div>,
            ]}
          >
            <Meta
              avatar={
                <Avatar src="https://thietbiketnoi.com/wp-content/uploads/2020/12/phong-nen-hinh-nen-background-dep-cho-tet-chuc-mung-nam-moi-36.jpg" />
              }
              description={e.restBag + " bags left, get it now!"}
              title={e.wishMessage}
            />
          </Card>
        </div>
      </Col>
    );
  };

  luckyMoneyReceivedItem = (e) => {
    return (
      <Col
        style={{
          display: "flex",
          width: 300,
          height: 352,
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
              zIndex: 100,
            }}
          >
            From {e.senderName}
          </div>
          <Card
            style={{
              width: "100%",
              height: "100%",
              borderRadius: 10,
            }}
            cover={
              <img
                alt="example"
                src="https://inanlvc.com/wp-content/uploads/2018/11/ec060164843433.5adfa04ae146d-1.jpg"
              />
            }
            actions={[
              <div
                style={{
                  display: "flex",
                  paddingBottom: 20,
                  height: 20,
                  justifyContent: "center",
                  alignContent: "center",
                }}
              >
                <a
                  style={{
                    margin: 0,
                    padding: 0,
                    fontSize: 12,
                    fontWeight: "lighter",
                    color: "black",
                  }}
                >
                  Detail
                  <Icon type="edit" key="edit" />
                </a>
              </div>,
            ]}
          >
            <Meta
              avatar={
                <Avatar src="https://thietbiketnoi.com/wp-content/uploads/2020/12/phong-nen-hinh-nen-background-dep-cho-tet-chuc-mung-nam-moi-36.jpg" />
              }
              description={
                "Received " + formatToCurrency(e.receivedMoney) + currency
              }
              title={e.wishMessage}
            />
          </Card>
          {/* </div> */}
        </div>
      </Col>
    );
  };

  renderLuckyMoneyItems = () => {
    return this.state.data.map((e, index) => {
      if (e.received === true) return this.luckyMoneyReceivedItem(e);
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
            fontSize: 20,
            fontWeight: "lighter",
          }}
        >
          There are some lucky money from your friends
        </p>
        <Scrollbars autoHide autoHideTimeout={500} autoHideDuration={200}>
          <Row style={{ width: "100%", height: "100%" }}>
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
              fontSize: 20,
              fontWeight: "lighter",
              marginRight: 10,
            }}
          >
            Want to create a new one ?
          </p>
          <Icon
            onClick={() => this.setState({ isCreate: true })}
            style={{
              fontSize: 30,
              color: "#ggg",
            }}
            type="plus-circle"
          />
        </div>
        {this.state.showAnimation && <LuckyAnimation />}
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
            : this.state.data.length === 0
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
    currentSessionId: state.chatReducer.currentSessionId,
  }),
  (dispatch) => channingActions({}, dispatch, bindPaymentActions)
)(LuckyMoney);
