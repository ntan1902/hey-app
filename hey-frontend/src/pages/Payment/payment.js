import React from "react";
import { Menu, Spin } from "antd";
import CustomAvatar from "../../components/custom-avatar";
import Topup from "./top-up";

import { connect } from "react-redux";

import { Scrollbars } from "react-custom-scrollbars";

import { channingActions } from "../../utils";
import { bindPaymentActions } from "../../actions";
import toIsoString from "../../utils/dateISO";

class AddressBook extends React.Component {
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
          let createdAt = this.props.transferStatements.length
            ? this.props.transferStatements[
                this.props.transferStatements.length - 1
              ].createdAt
            : toIsoString(new Date());
          this.props.paymentActions
            .loadMoreTransferStatement(createdAt)
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
    // console.log(event.key);
    // this.props.handleChangeAddressBook(
    //   this.props.newAddressBookList[event.key].userId
    // );
    // this.props.changeMessageHeader(
    //   this.props.newAddressBookList[event.key].name,
    //   this.props.newAddressBookList[event.key].avatar,
    //   false
    // );
  }

  // handleScroll = (event) => {
  //   let { scrollHeight, clientHeight, scrollTop } = event.currentTarget;
  //   console.log({ scrollHeight, clientHeight, scrollTop });
  //   if (
  //     !this.state.isAll &&
  //     scrollHeight === clientHeight + scrollTop + 1 &&
  //   ) {
  //     this.setState((preState) => ({
  //       page: preState.page + 1,
  //     }));
  //     this.props.paymentActions
  //       .getTransferStatement(this.state.page, this.state.size)
  //       .then((res) => {
  //         this.setState((preState) => ({
  //           data: [...preState.data, ...res.data],
  //           isAll: res.data.length < 10,
  //         }));
  //       });
  //   }
  // };

  render() {
    if (this.props.transferStatements == []) return;
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
          >
            {this.props.transferStatements.map((item, index) => (
              <Menu.Item key={index}>
                <div style={{ width: 60 }}>
                  <CustomAvatar type="user-avatar" avatar={item.avatar} />
                </div>

                <div style={{ overflow: "hidden", paddingTop: 5 }}>
                  <div className="user-name">
                    {item.transferType == "topup"
                      ? "Topup"
                      : item.transferType == "transfer"
                      ? "Transfer"
                      : "LuckyMoney"}
                  </div>
                  <div className="history-message">{item.description}</div>
                  {item.isRead ? (
                    <div
                      className="status-point"
                      style={{ backgroundColor: "#ddd" }}
                    />
                  ) : (
                    <div
                      className="status-point"
                      style={{ backgroundColor: "red" }}
                    />
                  )}
                </div>
              </Menu.Item>
            ))}
          </Menu>
          <div style={{ height: 10 }}></div>
          {!this.state.isAll && (
            <div
              ref={this.divLoadMore}
              id="load_more"
              style={{ display: "flex", justifyContent: "center" }}
            >
              <Spin size="large" />
            </div>
          )}
          <div style={{ height: 10 }}></div>
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
  }),
  (dispatch) => channingActions({}, dispatch, bindPaymentActions)
)(AddressBook);
