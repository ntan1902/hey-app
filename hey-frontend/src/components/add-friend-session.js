import React from "react";
import {Modal} from "antd";
import {addFriendToSession,} from "../actions/chatAction";

import {connect} from "react-redux";
import {changeStateAddFriendPopup} from "../actions/paymentAction";
import GetFriendList from "./add-friend-transfer";

class AddFriendSession extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            visible: false,
            selectedUserId: null,
        };
        this.handleOk = this.handleOk.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
        this.showModal = this.showModal.bind(this);
    }

    showModal = () => {
        this.props.changeStateAddFriendPopup(true);
    };

    handleOk = (e) => {
        this.props.addFriendToSession(
            this.props.currentSessionId,
            this.state.selectedUserId
        );
        this.props.changeStateAddFriendPopup(false);
    };

    handleCancel = (e) => {
        this.props.changeStateAddFriendPopup(false);
    };

    render() {
        return (
            <div>
                <Modal
                    width="420px"
                    title="Add New Friend"
                    visible={this.props.addFriendPopup}
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                    okText="Add"
                    cancelText="Cancel"
                >
                    <GetFriendList
                        onChange={(value) => {
                            this.setState({selectedUserId: value});
                        }}
                    />
                </Modal>
            </div>
        );
    }
}

function mapStateToProps(state) {
    return {
        addFriendError: state.addressBookReducer.addFriendError,
        addFriendErrorMessage: state.addressBookReducer.addFriendErrorMessage,
        addFriendPopup: state.paymentReducer.isAddFriendToSession,
        currentSessionId: state.chatReducer.currentSessionId,
    };
}

function mapDispatchToProps(dispatch) {
    return {
        addFriendToSession(sessionId, userId) {
            dispatch(addFriendToSession(sessionId, userId));
        },
        changeStateAddFriendPopup(state) {
            dispatch(changeStateAddFriendPopup(state));
        },
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AddFriendSession);
