import React from "react";
import { connect } from "react-redux";
import { Button, Icon, Input } from "antd";
import NumericInput from "../../components/numberic-input";
import Transfer from "../../components/transfer";
import AddFriendTransfer from "../../components/add-friend-transfer";
import { DocTien } from "../../utils";

import { channingActions } from "../../utils";
import { bindPaymentActions } from "../../actions";

class MessagePanel extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      topupType: 1,
      amount: "",
      message: "",
      selectedUserId: "",
    };
  }

  renderButtonMoney = (item) => {
    return (
      <Button
        style={{
          backgroundColor: "white",
          borderColor: "black",
          color: "black",
          width: "30%",
          height: 50,
          margin: 20,
        }}
        onClick={() => {
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
      { title: "1,000,000", value: "10000000" },
      { title: "2,000,000", value: "2000000" },
    ];

    return (
      <div
        style={{
          flex: 1,
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          flexDirection: "column",
        }}
      >
        <div
          style={{
            width: "100%",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          {data1.map((e) => {
            return this.renderButtonMoney(e);
          })}
        </div>
        <div
          style={{
            width: "100%",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          {data2.map((e) => {
            return this.renderButtonMoney(e);
          })}
        </div>
      </div>
    );
  };

  render() {
    return (
      <div
        style={{
          width: "100%",
          height: "100%",
          padding: 50,
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
            paddingTop: 60,
            borderRadius: 30,
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
              <div style={{ fontSize: 20, width: 150, fontWeight: 200 }}>
                Transfer To
              </div>
              <AddFriendTransfer
                onChange={(value) => {
                  console.log(`selected ${value}`);

                  this.setState({ selectedUserId: value });
                }}
              ></AddFriendTransfer>
            </div>
            <div
              style={{
                display: "flex",
                flexDirection: "row",
                marginTop: 50,
              }}
            >
              <div
                style={{
                  fontSize: 20,
                  // marginRight: 100,
                  fontWeight: 200,
                  width: 150,
                }}
              >
                Amount
              </div>
              <div
                style={{ display: "flex", flexDirection: "column", flex: 1 }}
              >
                <NumericInput
                  style={{ width: "50%" }}
                  value={this.state.amount}
                  onChange={(value) => {
                    this.setState({ amount: value });
                  }}
                />
                <p style={{ marginTop: 5, marginLeft: 10, color: "#ACB1C0" }}>
                  {new DocTien().doc(this.state.amount)}
                </p>
              </div>
            </div>
            <div
              style={{
                display: "flex",
                flexDirection: "row",
                marginTop: 30,
                flex: 1,
              }}
            >
              <div
                style={{
                  fontSize: 20,
                  // marginRight: 100,
                  fontWeight: 200,
                  width: 150,
                }}
              >
                Message
              </div>
              <div
                style={{ display: "flex", flexDirection: "column", flex: 1 }}
              >
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
            <Transfer
              amount={this.state.amount}
              targetId={this.state.selectedUserId}
              message={this.state.message}
            ></Transfer>
          </div>
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
