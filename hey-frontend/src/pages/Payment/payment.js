import React from "react";
import { Menu } from "antd";
import CustomAvatar from "../../components/custom-avatar";
import AddFriend from "../../components/add-friend";
import Topup from "./top-up";

import { connect } from "react-redux";
import {
  handleChangeAddressBook,
  loadAddressBookList,
} from "../../actions/addressBookAction";
import { Scrollbars } from "react-custom-scrollbars";
import { changeMessageHeader } from "../../actions/chatAction";

import { channingActions } from "../../utils";
import { bindPaymentActions } from "../../actions";

class AddressBook extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      current: [],
      newselect: [],
      data: [
        { type: "Topup", message: "Happy New Year", isRead: false },
        { type: "Transfer", message: "Ahihi", isRead: true },
        { type: "Topup", message: "Happy New Year", isRead: true },
        { type: "Transfer", message: "Ahihi", isRead: true },
        { type: "Topup", message: "Happy New Year", isRead: false },
        { type: "Transfer", message: "Ahihi", isRead: true },
        { type: "Topup", message: "Happy New Year", isRead: true },
        { type: "Transfer", message: "Ahihi", isRead: true },
        { type: "Topup", message: "Happy New Year", isRead: false },
        { type: "Transfer", message: "Ahihi", isRead: false },
        { type: "Topup", message: "Happy New Year", isRead: false },
        { type: "Transfer", message: "Ahihi", isRead: true },
        { type: "Topup", message: "Happy New Year", isRead: false },
        { type: "Transfer", message: "Ahihi", isRead: true },
      ],
    };
    this.handleCurrentChange = this.handleCurrentChange.bind(this);
    this.handleNewChange = this.handleNewChange.bind(this);
  }

  componentDidMount() {
    // this.props.loadAddressBookList();
  }

  handleCurrentChange(event) {
    this.setState({
      ...this.state,
      current: [event.key],
      newselect: [],
    });
    this.props.paymentActions
      .switchMainScreen("transferStatement")
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
                  <div className="user-name">{item.type}</div>
                  <div className="history-message">{item.message}</div>
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
