import React from "react";
import { connect } from "react-redux";
import { Button, Icon, message } from "antd";
import NumericInput from "../../../components/numberic-input";
import {
  channingActions,
  currencyToString,
  formatToCurrency,
} from "../../../utils";
import { bindPaymentActions } from "../../../actions";

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
        className="money-btn"
        onClick={() => {
          this.setState({ amount: item.value });
          this.props.setAmount(item.value);
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
            height: 50,
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
            height: 50,

            width: "100%",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            marginTop: 10,
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
        this.props.paymentActions.getNewTransferStatement(this.props.offset);
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
        }}
      >
        <div
          style={{
            width: "100%",
            height: "100%",
            backgroundColor: "white",
            display: "flex",
            flexDirection: "column",
            borderRadius: 30,
          }}
        >
          <div>
            <div
              style={{ display: "flex", flexDirection: "row", marginTop: 10 }}
            >
              <div
                style={{
                  fontSize: 20,
                  marginRight: 20,
                  marginLeft: 30,
                  fontWeight: 200,
                }}
              >
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
                    this.props.setAmount(this.state.amount);
                  }}
                />
              </div>
            </div>
          </div>
          <div style={{ flex: 1, marginTop: 30 }}>
            {this.renderListOfDefaultMoney()}
          </div>
        </div>
      </div>
    );
  }
}

export default connect(
  (state) => ({
    messageItems: state.chatReducer.messageItems,
    offset: state.paymentReducer.offset,
  }),
  (dispatch) => channingActions({}, dispatch, bindPaymentActions)
)(MessagePanel);
