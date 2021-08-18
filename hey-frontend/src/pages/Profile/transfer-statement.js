import React from "react";
import { connect } from "react-redux";
import { Button, Icon, Input } from "antd";
import NumericInput from "../../components/numberic-input";
import Transfer from "../../components/transfer";
import AddFriendTransfer from "../../components/add-friend-transfer";

class MessagePanel extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      topupType: 1,
      amount: "",
      message: "",
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
              <div
                style={{ fontSize: 20, marginRight: 100, fontWeight: "bold" }}
              >
                Transfer To:
              </div>
              {/* <AddFriendTransfer></AddFriendTransfer> */}
            </div>
            <div
              style={{ display: "flex", flexDirection: "row", marginTop: 50 }}
            >
              <div
                style={{
                  fontSize: 20,
                  // marginRight: 100,
                  fontWeight: "bold",
                  width: 150,
                }}
              >
                Amount
              </div>
              <div
                style={{
                  fontSize: 20,
                  // marginRight: 100,
                  fontWeight: "bold",
                  width: 150,
                }}
              >
                100.000đ
              </div>
            </div>
            <div
              style={{ display: "flex", flexDirection: "row", marginTop: 50 }}
            >
              <div
                style={{
                  fontSize: 20,
                  // marginRight: 100,
                  fontWeight: "bold",
                  width: 150,
                }}
              >
                Message
              </div>
              <div
                style={{
                  fontSize: 20,
                  // marginRight: 100,
                  fontWeight: "bold",
                }}
              >
                Happy New Year!
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    messageItems: state.chatReducer.messageItems,
  };
}

function mapDispatchToProps(dispatch) {
  return {};
}

export default connect(mapStateToProps, mapDispatchToProps)(MessagePanel);
