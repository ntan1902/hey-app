import React from "react";
import { Menu, Spin } from "antd";
import CustomAvatar from "../../components/custom-avatar";
import Topup from "./top-up";

import { connect } from "react-redux";

import { Scrollbars } from "react-custom-scrollbars";
import moment from "moment";
import { channingActions, currency } from "../../utils";
import { bindPaymentActions } from "../../actions";

import Avatar from "@material-ui/core/Avatar";
import { makeStyles, withStyles } from "@material-ui/core/styles";
import { getProfileURL, formatToCurrency } from "../../utils";
import { getUserIdFromStorage } from "../../utils/utils";
import Typography from "@material-ui/core/Typography";

class Payment extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      current: [],
      newselect: [],
      isAll: false,
    };
    this.divLoadMore = React.createRef();
    this.handleCurrentChange = this.handleCurrentChange.bind(this);
    this.handleNewChange = this.handleNewChange.bind(this);
  }

  componentDidMount() {
    this.observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          console.log("Hi Entry");
          this.props.paymentActions
            .getTransferStatements(this.props.offset, this.props.limit)
            .then((res) => {
              this.setState({
                isAll: res.data.length < 10,
              });
            });
        });
      },
      { threshold: 1 }
    );

    this.observer.observe(this.divLoadMore.current);
  }

  handleCurrentChange(event) {
    this.setState({
      ...this.state,
      current: [event.key],
      newselect: [],
    });
    this.props.paymentActions
      .switchMainScreen(
        "transferStatement",
        this.props.transferStatements[event.key]
      )
      .then((res) => {
        console.log("res");
      });
  }

  handleNewChange(event) {
    this.setState({
      ...this.state,
      newselect: [event.key],
      current: [],
    });
    this.props.paymentActions
      .switchMainScreen("transferStatement")
      .then((res) => {
        console.log("res");
      });
  }

  render() {
    if (this.props.transferStatements == []) return;
    console.log("Transfer, ", this.props.transferStatements);
    return (
      <div className="d-flex flex-column full-height address-book-menu">
        <Topup />
        <Scrollbars
          autoHide
          autoHideTimeout={500}
          autoHideDuration={200}
          // onScroll={this.handleScroll}
        >
          <hr className="hr-sub-menu-title" />
          <div className="sub-menu-title">
            Transfer Statements ({this.props.transferStatements.length})
          </div>
          <Menu
            theme="light"
            mode="inline"
            defaultSelectedKeys={[]}
            selectedKeys={this.state.current}
            className="address-book"
            onSelect={this.handleCurrentChange}
            style={{ marginTop: 10 }}
          >
            {this.props.transferStatements.map((item, index) => {
              let title,
                description,
                amount,
                isMinus = false,
                imgUrl;
              if (item.transferType == "topup") {
                title = "Topup to Hey Pay account";
                description = item.createdAt;
                amount = "+" + formatToCurrency(item.amount) + currency;
                imgUrl =
                  "https://banner2.cleanpng.com/20210109/kbt/transparent-payment-icon-top-up-icon-more-icon-5ffa2fb9c84f26.4451108616102317378205.jpg";
              } else if (item.transferType == "transfer") {
                if (item.source.systemName) {
                  title = "Receive lucky money";
                  amount = "+" + formatToCurrency(item.amount) + currency;
                  imgUrl =
                    "https://image.winudf.com/v2/image/Y29tLmVhcm4ubHVja3ltb25leV9pY29uX3RtNTRjbTls/icon.png?w=&fakeurl=1";
                } else if (item.target.systemName) {
                  title = "Create lucky money";
                  amount = "-" + formatToCurrency(item.amount) + currency;
                  isMinus = true;
                  imgUrl =
                    "https://image.winudf.com/v2/image/Y29tLmVhcm4ubHVja3ltb25leV9pY29uX3RtNTRjbTls/icon.png?w=&fakeurl=1";
                } else if (item.source.id != getUserIdFromStorage()) {
                  title = "Receive money from " + item.source.fullName;
                  amount = "+" + formatToCurrency(item.amount) + currency;
                  imgUrl = getProfileURL(item.source.id);
                } else {
                  title = "Send money to " + item.target.fullName;
                  amount = "-" + formatToCurrency(item.amount) + currency;
                  isMinus = true;
                  imgUrl = getProfileURL(item.target.id);
                }
                description = item.createdAt;
              } else {
                if (item.source.id != getUserIdFromStorage()) {
                  title = "Refund lucky money";
                  amount = "+" + formatToCurrency(item.amount) + currency;
                  imgUrl =
                    "https://image.winudf.com/v2/image/Y29tLmVhcm4ubHVja3ltb25leV9pY29uX3RtNTRjbTls/icon.png?w=&fakeurl=1";
                } else {
                  title = "Create lucky money";
                  amount = "-" + formatToCurrency(item.amount) + currency;
                  isMinus = true;
                  imgUrl =
                    "https://image.winudf.com/v2/image/Y29tLmVhcm4ubHVja3ltb25leV9pY29uX3RtNTRjbTls/icon.png?w=&fakeurl=1";
                }
                description = item.createdAt;
              }

              return (
                <Menu.Item
                  key={index}
                  style={{
                    width: "100%",
                    display: "flex",
                    padding: 0,
                    margin: 0,
                  }}
                >
                  <Avatar
                    alt={item.name}
                    src={imgUrl}
                    style={{
                      height: 55,
                      width: 55,
                    }}
                  />

                  <div
                    style={{
                      flex: 1,
                      height: 60,
                      display: "flex",
                      flexDirection: "column",
                      marginLeft: 5,
                    }}
                  >
                    <div
                      style={{
                        // height: 30,
                        width: "100%",
                        display: "flex",
                        flexDirection: "row",
                      }}
                    >
                      <Typography
                        noWrap={true}
                        style={{
                          padding: 0,
                          margin: 0,
                          width: "85%",
                          fontSize: 15,
                        }}
                      >
                        {title}
                      </Typography>
                      <div
                        style={{
                          position: "absolute",
                          right: 5,
                          top: 40,
                          // justifyContent: "flex-end",
                        }}
                      >
                        <Typography
                          noWrap={true}
                          style={{
                            padding: 0,
                            margin: 0,
                            textAlign: "right",
                            fontSize: 12,
                            color: isMinus ? "red" : "blue",
                            fontWeight: 600,
                          }}
                        >
                          {amount}
                        </Typography>
                      </div>
                    </div>
                    {/* <div style={{ width: "100%" }}>
                      <Typography
                        noWrap={true}
                        style={{
                          padding: 0,
                          margin: 0,
                          // textAlign: "right",
                          fontSize: 12,
                          fontWeight: 600,
                          color: isMinus ? "red" : "blue",
                        }}
                      >
                        {amount}
                      </Typography>
                    </div> */}
                    <div style={{ width: "100%" }}>
                      <Typography
                        noWrap={true}
                        style={{
                          padding: 0,
                          margin: 0,
                          // textAlign: "right",
                          fontSize: 12,
                          fontWeight: 600,
                          // color: isMinus ? "red" : "blue",
                        }}
                      >
                        {moment(description, "YYYY/MM/DD HH:mm").format(
                          "HH:mm - DD-MM-YYYY"
                        )}{" "}
                      </Typography>
                    </div>
                  </div>
                </Menu.Item>
              );
            })}
          </Menu>
          <div style={{ height: 10 }} />
          {!this.state.isAll && (
            <div
              ref={this.divLoadMore}
              id="load_more"
              style={{ display: "flex", justifyContent: "center" }}
            >
              <Spin size="large" />
            </div>
          )}
          <div style={{ height: 10 }} />
        </Scrollbars>
      </div>
    );
  }
}

export default connect(
  (state) => ({
    addFriendError: state.addressBookReducer.addFriendError,
    addFriendErrorMessage: state.addressBookReducer.addFriendErrorMessage,
    addFriendPopup: state.addressBookReducer.topup,
    transferStatements: state.paymentReducer.transferStatements,
    offset: state.paymentReducer.offset,
    limit: state.paymentReducer.limit,
  }),
  (dispatch) => channingActions({}, dispatch, bindPaymentActions)
)(Payment);
