import React from "react";
import { Alert, Input, Modal } from "antd";
import CustomAvatar from "../components/custom-avatar";
import {
  addNewFriendRequest,
  changeStateAddFriendPopup,
} from "../actions/addressBookAction";
import { connect } from "react-redux";
import $ from "jquery";
import ListUser from "./ListUser/ListUser";
import { AuthAPI } from "../api";

class AddFriend extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      users: [],
      keyword: "",
    };

    this.inputTimeout = null;

    this.inputSearch = React.createRef();

    this.handleOk = this.handleOk.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.showModal = this.showModal.bind(this);
  }

  showModal = () => {
    this.props.changeStateAddFriendPopup(true);
  };

  handleOk = (e) => {
    // console.log(e);
    var un = $("#add-user-name").val();
    $("#add-user-name").val("");
    // this.props.addNewFriend(un);
    this.props.loadNewAddFriend(un);
    this.props.changeStateAddFriendPopup(false);
  };

  handleCancel = (e) => {
    this.props.changeStateAddFriendPopup(false);
  };
  search = () => {
    if (this.state.keyword) {
      AuthAPI.searchUser(this.state.keyword).then((res) => {
        // console.log(res.data.payload);
        this.setState({
          ...this.state,
          users: res.data.payload,
        });
      });
    } else {
      this.setState({
        ...this.state,
        users: [],
      });
    }
  };
  handleChange = (event) => {
    this.setState({
      ...this.state,
      keyword: event.currentTarget.value,
    });
    if (this.inputTimeout) {
      clearTimeout(this.inputTimeout);
    }
    this.inputTimeout = setTimeout(this.search, 300);
  };

  sendFriendRequest = (username) => {
    $("add-user-name").val("");
    this.props.addNewFriendRequest(username);
    this.props.changeStateAddFriendPopup(false);
  };

  render() {
    return (
      <div>
        <div className="new-action-menu" onClick={this.showModal}>
          <a href="#">
            <CustomAvatar type="new-avatar" />
            <div className="new-text"> Add New Friend </div>{" "}
          </a>{" "}
        </div>{" "}
        <Modal
          width="420px"
          title="Add New Friend"
          visible={this.props.addFriendPopup}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          okText="Add"
          cancelText="Cancel"
        >
          {this.props.addFriendError ? (
            <Alert message={this.props.addFriendErrorMessage} type="error" />
          ) : (
            ""
          )}{" "}
          <p className="model-label"> Search by name or email </p>{" "}
          <Input
            id="add-user-name"
            className="add-user-name"
            onChange={this.handleChange}
            ref={this.inputSearch}
          />{" "}
          <ListUser
            users={this.state.users}
            onClickUser={this.sendFriendRequest}
          />{" "}
        </Modal>{" "}
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    addFriendError: state.addressBookReducer.addFriendError,
    addFriendErrorMessage: state.addressBookReducer.addFriendErrorMessage,
    addFriendPopup: state.addressBookReducer.addFriendPopup,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    changeStateAddFriendPopup(state) {
      dispatch(changeStateAddFriendPopup(state));
    },
    addNewFriendRequest(username) {
      dispatch(addNewFriendRequest(username));
    },
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(AddFriend);
