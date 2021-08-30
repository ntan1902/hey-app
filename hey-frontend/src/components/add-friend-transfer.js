import React from "react";
import { connect } from "react-redux";
import $ from "jquery";

import { channingActions } from "../utils";
import { bindPaymentActions } from "../actions";
import { Select } from "antd";
import { ChatAPI } from "../api/chat";

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
    $("#add-user-name").val("");
    this.props.paymentActions.changeStateAddFriendTransferPopup(false);
  };

  componentDidMount() {
    ChatAPI.getAddressBook().then((e) => {
      // console.log(e);
      this.setState({ friends: e.data.payload.items });
    });
  }

  handleCancel = (e) => {
    this.props.paymentActions.changeStateAddFriendTransferPopup(false);
  };

  onChange = (value) => {
    // console.log(`selected ${value}`);
    this.props.onChange(JSON.parse(value));
  };

  onBlur = () => {
    console.log("blur");
  };

  onFocus = () => {
    console.log("focus");
  };

  onSearch = (val) => {
    // console.log("search:", val);
  };

  render() {
    return (
      <div>
        {/* <div className="new-action-menu" onClick={this.showModal}>
          <a href="#">
            <CustomAvatar type="new-avatar" />
            <div className="new-text">Add friend to transfer</div>
          </a>
        </div> */}
        <Select
          // showSearch
          style={{ width: 200 }}
          placeholder="Select a person"
          optionFilterProp="children"
          onChange={this.onChange}
          onFocus={this.onFocus}
          onBlur={this.onBlur}
          onSearch={this.onSearch}
        >
          {this.state.friends.map((e, index) => {
            return (
              <Option
                value={JSON.stringify({ userId: e.userId, fullName: e.name })}
                key={index}
              >
                {e.name}
              </Option>
            );
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
