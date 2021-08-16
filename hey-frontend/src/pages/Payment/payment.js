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
    };
    this.handleCurrentChange = this.handleCurrentChange.bind(this);
    this.handleNewChange = this.handleNewChange.bind(this);
  }

  componentDidMount() {
    // this.props.loadAddressBookList();
    console.log("Did Mount");
    this.props.paymentActions.getAllTransferStatement().then((res) => {
      this.setState({ data: res.data });
      console.log("res");
    });
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
    // this.props.handleChangeAddressBook(
    //   this.props.addressBookList[event.key].userId
    // );
    // this.props.changeMessageHeader(
    //   this.props.addressBookList[event.key].name,
    //   this.props.addressBookList[event.key].avatar,
    //   false
    // );
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

  render() {
    if (this.state.data == []) return;
    return (
      <div className="d-flex flex-column full-height address-book-menu">
        <Topup />
        <Scrollbars autoHide autoHideTimeout={500} autoHideDuration={200}>
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
