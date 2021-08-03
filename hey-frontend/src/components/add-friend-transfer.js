import React from "react";
import { Modal, Input, Alert } from "antd";
import CustomAvatar from "../components/custom-avatar";
import {
  addNewUserChatGroup,
  removeUserChatGroup,
  startNewChatGroup,
} from "../actions/chatAction";
import {
  addNewFriend,
  changeStateAddFriendPopup,
} from "../actions/addressBookAction";
import { connect } from "react-redux";
import $ from "jquery";

import { channingActions } from "../utils";
import { bindPaymentActions } from "../actions";
import { api } from "../api/api";
import { Select } from "antd";

const { Option } = Select;

class AddFriend extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      friends: [],
    };
    this.handleOk = this.handleOk.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.showModal = this.showModal.bind(this);
  }

  showModal = () => {
    this.props.paymentActions.changeStateAddFriendTransferPopup(true);
  };

  handleOk = (e) => {
    console.log(e);
    var un = $("#add-user-name").val();
    $("#add-user-name").val("");
    this.props.paymentActions.changeStateAddFriendTransferPopup(false);
  };

  componentDidMount() {
    api.get("/api/protected/addressbook").then((e) => {
      console.log(e);
      this.setState({ friends: e.data.payload.items });
    });
  }

  handleCancel = (e) => {
    this.props.paymentActions.changeStateAddFriendTransferPopup(false);
  };

  onChange = (value) => {
    console.log(`selected ${value}`);
  };

  onBlur = () => {
    console.log("blur");
  };

  onFocus = () => {
    console.log("focus");
  };

  onSearch = (val) => {
    console.log("search:", val);
  };

  render() {
    console.log("Friend", this.state.friends);
    return (
      <div>
        {/* <div className="new-action-menu" onClick={this.showModal}>
          <a href="#">
            <CustomAvatar type="new-avatar" />
            <div className="new-text">Add friend to transfer</div>
          </a>
        </div> */}
        <Select
          showSearch
          style={{ width: 200 }}
          placeholder="Select a person"
          optionFilterProp="children"
          onChange={this.props.onChange}
          onFocus={this.onFocus}
          onBlur={this.onBlur}
          onSearch={this.onSearch}
        >
          {this.state.friends.map((e) => {
            return <Option value={e.userId}>{e.name}</Option>;
          })}
        </Select>
      </div>
    );
  }
}

export default connect(
  (state) => ({
    addFriendTransferPopup: state.paymentReducer.addFriendTransferPopup,
  }),
  (dispatch) => channingActions({}, dispatch, bindPaymentActions)
)(AddFriend);
