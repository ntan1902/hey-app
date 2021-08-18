import React from "react";
import { Menu } from "antd";
import CustomAvatar from "../../components/custom-avatar";
import Topup from "./top-up";

import { connect } from "react-redux";

import { Scrollbars } from "react-custom-scrollbars";

import { channingActions } from "../../utils";
import { bindPaymentActions } from "../../actions";

class AddressBook extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      current: [],
      newselect: [],
      data: [],
      page: 0,
      size: 10,
      isAll: false,
    };
    this.divLoadMore = React.createRef();
    this.handleCurrentChange = this.handleCurrentChange.bind(this);
    this.handleNewChange = this.handleNewChange.bind(this);
  }

  componentDidMount() {
    this.props.paymentActions
      .getTransferStatement(this.state.page, this.state.size)
      .then((res) => {
        this.setState({ data: res.data, isAll: res.data.length < 10 });
      });

    this.observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        this.setState((preState) => ({
          page: preState.page + 1,
        }));
        this.props.paymentActions
          .getTransferStatement(this.state.page, this.state.size)
          .then((res) => {
            this.setState((preState) => ({
              data: [...preState.data, ...res.data],
              isAll: res.data.length < 10,
            }));
          });
      });
    });
  }
  componentDidUpdate() {
    if (this.divLoadMore.current) {
      this.observer.observe(this.divLoadMore.current);
    }
  }

  handleCurrentChange(event) {
    this.setState({
      ...this.state,
      current: [event.key],
      newselect: [],
    });
    this.props.paymentActions
      .switchMainScreen("transferStatement", this.state.data[event.key])
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

  handleScroll = (event) => {
    let { scrollHeight, clientHeight, scrollTop } = event.currentTarget;
    if (!this.state.isAll && scrollHeight === clientHeight + scrollTop + 1) {
      this.setState((preState) => ({
        page: preState.page + 1,
      }));
      this.props.paymentActions
        .getTransferStatement(this.state.page, this.state.size)
        .then((res) => {
          this.setState((preState) => ({
            data: [...preState.data, ...res.data],
            isAll: res.data.length < 10,
          }));
        });
    }
  };

  render() {
    if (this.state.data == []) return;
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
            Transfer Statements ({this.state.data.length})
          </div>
          <Menu
            theme="light"
            mode="inline"
            defaultSelectedKeys={[]}
            selectedKeys={this.state.current}
            className="address-book"
            onSelect={this.handleCurrentChange}
          >
            {this.state.data.map((item, index) => (
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
          {this.state.data.length !== 0 && !this.state.isAll && (
            <div
              ref={this.divLoadMore}
              id="load_more"
              style={{ height: 5, backgroundColor: "red" }}
            ></div>
          )}
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
  }),
  (dispatch) => channingActions({}, dispatch, bindPaymentActions)
)(AddressBook);
