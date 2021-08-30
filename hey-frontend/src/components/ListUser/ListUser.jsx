import React from "react";
import { Menu } from "antd";
import CustomAvatar from "../../components/custom-avatar";
import { connect } from "react-redux";
import { getProfileURL } from "../../utils";

class ListUser extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      current: 0,
    };
  }

  onSelect = (event) => {
    this.props.onClickUser(event.key);
  };

  render() {
    console.log("Hii gi", this.props.users);
    return (
      <div>
        <Menu
          theme="light"
          mode="inline"
          defaultSelectedKeys={[]}
          selectedKeys={this.state.current}
          className="address-book"
          onSelect={this.onSelect}
          style={{ overflowY: "scroll", height: 400, overflowX: "hidden" }}
        >
          {this.props.users.map((item) => (
            <Menu.Item
              key={item.username}
              style={{ display: "flex", alignItems: "center", height: 90 }}
            >
              <div>
                <CustomAvatar
                  type="avatar"
                  src={getProfileURL(item.id)}
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
            </Menu.Item>
          ))}
        </Menu>
      </div>
    );
  }
}

function mapDispatchToProps(dispatch) {
  return {};
}
export default connect(null, mapDispatchToProps)(ListUser);
