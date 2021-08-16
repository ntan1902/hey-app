import React from "react";
import { Menu } from "antd";
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
      data: [
        { title: "Edit Profile" },
        { title: "Change Password" },
        { title: "Manage Pin" },
        { title: "Settings" },
        { title: "Logout" },
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
          <div className="sub-menu-title">Profile Management</div>
          <Menu
            theme="light"
            mode="inline"
            defaultSelectedKeys={[]}
            selectedKeys={this.state.current}
            // className="address-book"
            onSelect={this.handleCurrentChange}
          >
            {this.state.data.map((item, index) => (
              <Menu.Item key={index} style={{ height: 40 }}>
                <div style={{ fontSize: 15, fontWeight: "lighter" }}>
                  {item.title}
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
