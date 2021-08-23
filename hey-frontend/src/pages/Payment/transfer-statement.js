import React from "react";
import { connect } from "react-redux";
import { Button } from "antd";

import { channingActions } from "../../utils";
import { bindPaymentActions } from "../../actions";

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
      { title: "1,000,000", value: "1000000" },
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
    if (this.props.mainScreenData === null) return;
    const data = this.props.mainScreenData;
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
                style={{
                  fontSize: 20,
                  fontWeight: 200,
                  width: 200,
                }}
              >
                Transfer To:
              </div>
              <div
                style={{
                  fontSize: 20,
                  marginRight: 100,
                  fontWeight: 200,
                }}
              >
                {data.target.fullName}
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
                  fontWeight: 200,
                  width: 200,
                }}
              >
                Amount
              </div>
              <div
                style={{
                  fontSize: 20,
                  // marginRight: 100,
                  fontWeight: "bold",
                  fontWeight: 200,
                }}
              >
                {data.amount}vnđ
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
                  fontWeight: 200,
                  width: 200,
                }}
              >
                Message
              </div>
              <div
                style={{
                  fontSize: 20,
                  // marginRight: 100,
                  fontWeight: 200,
                }}
              >
                {data.description}
              </div>
            </div>
          </div>
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
    mainScreenData: state.paymentReducer.mainScreenData,
  }),
  (dispatch) => channingActions({}, dispatch, bindPaymentActions)
)(MessagePanel);
