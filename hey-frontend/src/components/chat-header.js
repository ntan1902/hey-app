import React from "react";
import { connect } from "react-redux";
import CustomAvatar from "./custom-avatar";
import { Menu, Icon } from "antd";

import { channingActions } from "../utils";
import { bindPaymentActions } from "../actions";

const { SubMenu } = Menu;

class ChatHeader extends React.Component {
  handleClick = (e) => {
    console.log("click ", e);
    this.setState({
      current: e.key,
    });
  };

  showLuckyMoneyModal = (isCreate) => {
    this.props.paymentActions.changeStateLuckyMoneyPopup(true, isCreate);
  };

  showAddFriendToSessionModal = () => {
    this.props.paymentActions.changeStateAddFriendPopup(true);
  };

  render() {
    const IconFont = Icon.createFromIconfontCN({
      scriptUrl: "//at.alicdn.com/t/font_8d5l8fzk5b87iudi.js",
    });
    return (
      <div className="chat-header">
        <div style={{ width: 50 }}>
          {this.props.header.groupchat ? (
            <CustomAvatar type="panel-group-avatar" />
          ) : (
            <CustomAvatar
              type="panel-avatar"
              avatar={this.props.header.avatar}
            />
          )}
        </div>
        <div style={{ overflow: "hidden", paddingTop: 5 }}>
          <div className="panel-message">{this.props.header.title}</div>
        </div>
        <div style={{ flex: 1 }}></div>
        <Menu
          onClick={this.handleClick}
          // selectedKeys={[this.state.current]}
          mode="horizontal"
        >
          <SubMenu
            title={
              <span className="submenu-title-wrapper">
                <Icon style={{ fontSize: 20 }} type="menu" />
              </span>
            }
          >
            <Menu.ItemGroup title="Lucky Money">
              <Menu.Item
                key="setting:1"
                onClick={() => this.showLuckyMoneyModal(true)}
              >
                {" "}
                <Icon style={{ fontSize: 20 }} type="plus" />
                Create New One
              </Menu.Item>
              <Menu.Item
                key="setting:2"
                onClick={() => this.showLuckyMoneyModal(false)}
              >
                {" "}
                <Icon style={{ fontSize: 20 }} type="money-collect" />
                View List
              </Menu.Item>
            </Menu.ItemGroup>
            <Menu.ItemGroup title="Settings">
              <Menu.Item
                key="setting:3"
                onClick={() => this.showAddFriendToSessionModal(true)}
              >
                {" "}
                <Icon style={{ fontSize: 20 }} type="plus" />
                Add More Friend
              </Menu.Item>
              <Menu.Item key="setting:4">
                {" "}
                <IconFont style={{ fontSize: 20 }} type="icon-tuichu" />
                Leave
              </Menu.Item>
            </Menu.ItemGroup>
          </SubMenu>
        </Menu>
      </div>
    );
  }
}

export default connect(
  (state) => ({
    header: state.chatReducer.messageHeader,

    luckyMoneyPopup: state.paymentReducer.luckyMoneyPopup,
  }),
  (dispatch) => channingActions({}, dispatch, bindPaymentActions)
)(ChatHeader);
