import React from 'react';
import { message, Modal, Menu } from 'antd';
import { connect } from 'react-redux';
import CustomAvatar from "../../components/custom-avatar";
import { bindPaymentActions } from '../../actions';
import { channingActions } from '../../utils';
import { ChatAPI } from '../../api/chat';

class MembersModal extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            users: []
        }
    }

    componentDidUpdate() {
        if (this.props.currentSessionId) {
            ChatAPI.getMembersOfSessionChat(this.props.currentSessionId)
                .then(res => {
                    this.setState({
                        users: res.data.payload
                    })
                })
                .catch(err => {
                    message.error(err.error.response.data.message);
                })
        }
    }

    handleCancel = () => {
        this.props.paymentActions.changeStateMembersModal(false);
    }

    kickUser = (userId) => {
        this.props.paymentActions.kickMembers(userId)
    }

    render() {
        console.log("Day ne:" + this.props.currentSessionId);
        return (
            <Modal
                width="500px"
                height="80%"
                title="Members"
                visible={this.props.membersModal}
                onCancel={this.handleCancel}
                footer={null}
            >
                <Menu
                    theme="light"
                    mode="inline"
                    defaultSelectedKeys={[]}
                    selectedKeys={this.state.current}
                    className="address-book"
                    onSelect={this.onClickUser}
                    style={{ overflowY: "scroll", maxHeight: 500, overflowX: "hidden" }}
                >
                    {this.state.users.map(item => (
                        <Menu.Item key={item.username} style={{ display: "flex", alignItems: "center", height: 90 }}>
                            <div>
                                <CustomAvatar type="user-avatar" />
                            </div>
                            <div style={{ overflow: "hidden", paddingTop: 5 }}>
                                <div className="user-name">{item.fullName}</div>
                            </div>
                        </Menu.Item>
                    ))}
                </Menu>
            </Modal>
        );
    }
}

function mapStateToProps(state) {
    return {
        membersModal: state.paymentReducer.membersModal,
        currentSessionId: state.chatReducer.currentSessionId
    }
}


export default connect(
    mapStateToProps,
    (dispatch) => channingActions({}, dispatch, bindPaymentActions)
)(MembersModal);