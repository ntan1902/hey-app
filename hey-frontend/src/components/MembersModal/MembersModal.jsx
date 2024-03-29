import React from "react";
import { Icon, Menu, Modal } from "antd";
import { connect } from "react-redux";
import CustomAvatar from "../../components/custom-avatar";
import { bindChatActions, bindPaymentActions } from "../../actions";
import { channingActions } from "../../utils";
import { getProfileURL } from "../../utils";

class MembersModal extends React.Component {
  constructor(props) {
    super(props);
  }
  handleCancel = () => {
    this.props.paymentActions.changeStateMembersModal(false);
  };

  kickUser = (userId) => {
    this.props.chatActions
      .kickMembers(this.props.currentSessionId, userId)
      .then((res) => {
        console.log("Kick user successfully", res);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  render() {
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
          {this.props.members.map((item) => (
            <Menu.Item
              key={item.username}
              style={{ display: "flex", alignItems: "center", height: 90 }}
            >
              <div>
                <CustomAvatar
                  type="avatar"
                  src={getProfileURL(item.userId)}
                  size={60}
                  style={{
                    // position: "absolute",
                    // left: 10,
                    // top: 10,
                    border: "0.5px solid white",
                    cursor: "pointer",
                  }}
                />
              </div>
              <div style={{ overflow: "hidden", paddingTop: 5 }}>
                <div className="user-name">{item.fullName}</div>
              </div>
              {this.props.isOwner && (
                <div style={{ position: "absolute", right: 10 }}>
                  <Icon
                    type="export"
                    onClick={() => this.kickUser(item.userId)}
                    theme={"outlined"}
                    style={{ fontSize: 25, color: "red" }}
                  />
                </div>
              )}
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
    currentSessionId: state.chatReducer.currentSessionId,
    members: state.chatReducer.members,
    isOwner: state.chatReducer.isOwner,
  };
}

export default connect(mapStateToProps, (dispatch) =>
  channingActions({}, dispatch, bindPaymentActions, bindChatActions)
)(MembersModal);
