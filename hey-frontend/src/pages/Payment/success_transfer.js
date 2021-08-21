import React from "react";
import {connect} from "react-redux";
import {Button, Result} from "antd";

import {channingActions} from "../../utils";
import {bindPaymentActions} from "../../actions";

class MessagePanel extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      topupType: 1,
      amount: "",
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

  showModal = (screenName) => {
    this.props.paymentActions.switchMainScreen(screenName).then((res) => {
      console.log("res");
    });
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
        <Result
          status="success"
          title="Transfer successfully"
          subTitle="Order number: 2017182818828182881. It may take 1-5 minutes, please wait."
          extra={[
            <Button
              type="primary"
              key="console"
              onClick={() => this.showModal("")}
            >
              Close
            </Button>,
            <Button key="buy" onClick={() => this.showModal("transfer")}>
              Transfer Again
            </Button>,
          ]}
        />
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
