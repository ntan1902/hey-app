import React from 'react';
import { Menu, message } from 'antd';
import CustomAvatar from "../../components/custom-avatar";
import { connect } from 'react-redux';
import { changeStateAddFriendPopup } from '../../actions/addressBookAction';
import { loadNewAddFriend } from '../../actions/chatAction';


class ListUser extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      current: 0
    };
  }
  sendFriendRequest = (event) => {
    console.log(event.key);
    this.props.loadNewAddFriend(event.key);
    this.props.changeStateAddFriendPopup(false);
  }
  render() {
    return (
      <div >
        <Menu
          theme="light"
          mode="inline"
          defaultSelectedKeys={[]}
          selectedKeys={this.state.current}
          className="address-book"
          onSelect={this.sendFriendRequest}
          style={{ overflowY: "scroll", height: 400, overflowX: "hidden" }}
        >
          {this.props.users.map(item => (
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
      </div>
    );
  }
}

function mapDispatchToProps(dispatch) {
  return {
    loadNewAddFriend(username) {
      dispatch(loadNewAddFriend(username));
    },
    changeStateAddFriendPopup(state) {
      dispatch(changeStateAddFriendPopup(state));
    },
  };
}
export default connect(null, mapDispatchToProps)(ListUser);