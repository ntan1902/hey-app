import React from 'react';
import { message, Modal, Menu, Icon, Button } from 'antd';
import { connect } from 'react-redux';
import CustomAvatar from "../../components/custom-avatar";
import { bindPaymentActions, bindChatActions } from '../../actions';
import { channingActions } from '../../utils';
import { ChatAPI } from '../../api/chat';

class MembersModal extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            isOwner: "",
            members: []
        }
    }

    componentDidUpdate() {
        if (this.props.currentSessionId && this.props.currentSessionId !== "-1") {
            ChatAPI.getMembersOfSessionChat(this.props.currentSessionId)
                .then(res => {
                    this.setState({
                        isOwner: res.data.payload.isOwner,
                        members: res.data.payload.members
                    })
                })
                .catch(err => {
                    message.error(err.error.response.data.message);
                })
        }
    }
    shouldComponentUpdate(nextProps, nextState) {
        return nextProps.currentSessionId != this.props.currentSessionId || nextProps.membersModal != this.props.membersModal;
    }

    handleCancel = () => {
        this.props.paymentActions.changeStateMembersModal(false);
    }

    kickUser = (userId) => {
        this.props.chatActions.kickMembers(this.props.currentSessionId, userId)
            .then(res => {
                console.log("Kick user successfully", res);
            })
            .catch(err => {
                console.log(err);
            })
    }

    render() {
        console.log("Day ne:" + this.props.currentSessionId);
        return (
            <Modal
                width="700px"
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
                    className="address-book"
                    style={{ overflowY: "scroll", maxHeight: 500, overflowX: "hidden" }}
                >
                    {this.state.members.map(item => (
                        <Menu.Item key={item.username} style={{ display: "flex", alignItems: "center", height: 90 }}>
                            <div>
                                <CustomAvatar type="user-avatar" />
                            </div>
                            <div style={{ overflow: "hidden", paddingTop: 5 }}>
                                <div className="user-name">{item.fullName}</div>
                            </div>
                            {this.state.isOwner &&
                                < div style={{ position: "absolute", right: 10 }}>
                                    <Icon type="export" onClick={() => this.kickUser(item.userId)} />
                                </div>
                            }
                        </Menu.Item>
                    ))}
                </Menu>
            </Modal >
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
    (dispatch) => channingActions({}, dispatch, bindPaymentActions, bindChatActions)
)(MembersModal);