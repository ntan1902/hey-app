import React from "react";
import { connect } from "react-redux";
import { Button, Icon, message } from "antd";
import NumericInput from "../../components/numberic-input";
import { formatToCurrency, currencyToString } from "../../utils";
import { channingActions } from "../../utils";
import { bindPaymentActions } from "../../actions";

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

  handleTopup = () => {
    this.props.paymentActions
      .topup(this.state.amount)
      .then((res) => {
        console.log("Topup Success");
        this.props.paymentActions.getAllTransferStatement();
      })
      .catch((err) => {
        message.error(err.error.response.data.message);
        console.log(err);
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
        <div
          style={{
            width: "100%",
            height: "100%",
            backgroundColor: "white",
            padding: 30,
            display: "flex",
            flexDirection: "column",
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
              <div style={{ fontSize: 20, marginRight: 100, fontWeight: 200 }}>
                Topup From
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
                      this.state.topupType == 1 ? "#1890FF" : "white",
                    borderColor: "black",
                    color: "black",
                    borderRadius: 200,
                    height: 80,
                    width: 80,
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
                    theme="outlined"
                    style={{
                      fontSize: 30,
                      color: this.state.topupType == 2 ? "black" : "white",
                    }}
                    type="bank"
                  />
                </Button>
                <div style={{ fontWeight: 400 }}>Bank</div>
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
                      this.state.topupType == 2 ? "#1890FF" : "white",
                    borderColor: "black",
                    color: "black",
                    borderRadius: 200,
                    height: 80,
                    width: 80,
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
                <div style={{ fontWeight: 400 }}>Card</div>
              </div>
            </div>
            <div
              style={{ display: "flex", flexDirection: "row", marginTop: 50 }}
            >
              <div style={{ fontSize: 20, marginRight: 100, fontWeight: 200 }}>
                Amount
              </div>
              <div
                style={{ display: "flex", flexDirection: "column", flex: 1 }}
              >
                <NumericInput
                  style={{ width: "50%" }}
                  value={formatToCurrency(this.state.amount)}
                  onChange={(value) => {
                    this.setState({ amount: currencyToString(value) });
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
            <Button
              style={{
                borderColor: "black",
                color: "black",
                height: 50,
                width: 250,
                justifyContent: "center",
                alignItems: "center",
                backgroundColor: "white",
                padding: 0,
              }}
              type="primary"
              onClick={this.handleTopup}
            >
              <p
                style={{
                  fontsize: 20,
                  fontWeight: 400,
                  padding: 0,
                  margin: 0,
                }}
              >
                Confirm
              </p>
            </Button>
            {/* <Transfer amount={this.state.amount}></Transfer> */}
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
