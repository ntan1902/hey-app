import React from "react";
import { Icon, Menu } from "antd";
import CustomAvatar from "../components/custom-avatar";
import AddFriend from "./add-friend";
import { connect } from "react-redux";
import {
  addNewFriend,
  handleChangeAddressBook,
  loadAddressBookList,
  loadWaitingFriendList,
  rejectWaitingFriend,
} from "../actions/addressBookAction";
import { Scrollbars } from "react-custom-scrollbars";
import { changeMessageHeader } from "../actions/chatAction";
import { getProfileURL } from "../utils";
import { getUserIdFromStorage } from "../utils/utils";
import Badge from "@material-ui/core/Badge";
import Avatar from "@material-ui/core/Avatar";
import { makeStyles, withStyles } from "@material-ui/core/styles";

const StyledBadge = withStyles((theme) => ({
  badge: {
    backgroundColor: "#44b700",
    color: "#44b700",
    boxShadow: `0 0 0 2px ${theme.palette.background.paper}`,
    width: 15,
    height: 15,
    borderRadius: "50%",

    "&::after": {
      position: "absolute",
      top: 0,
      left: 0,
      width: 15,
      height: 15,
      borderRadius: "50%",
      animation: "$ripple 1.2s infinite ease-in-out",
      border: "1px solid currentColor",
      content: '""',
    },
  },
  "@keyframes ripple": {
    "0%": {
      transform: "scale(.8)",
      opacity: 1,
    },
    "100%": {
      transform: "scale(2.4)",
      opacity: 0,
    },
  },
}))(Badge);

const useStyles = makeStyles((theme) => ({
  root: {
    display: "flex",
    "& > *": {
      margin: theme.spacing(1),
    },
  },
}));

class AddressBook extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      current: [],
      newselect: [],
    };
    this.handleCurrentChange = this.handleCurrentChange.bind(this);
    this.handleNewChange = this.handleNewChange.bind(this);
  }

  componentDidMount() {
    this.props.loadAddressBookList();
    this.props.loadWaitingFriendList();
  }

  handleCurrentChange(event) {
    this.setState({
      ...this.state,
      current: [event.key],
      newselect: [],
    });
    this.props.handleChangeAddressBook(
      this.props.addressBookList[event.key].userId
    );
    this.props.changeMessageHeader(
      this.props.addressBookList[event.key].name,
      this.props.addressBookList[event.key].avatar,
      false,
      [this.props.addressBookList[event.key].userId]
    );
  }

  handleNewChange(event) {
    this.setState({
      ...this.state,
      newselect: [event.key],
      current: [],
    });
    this.props.handleChangeAddressBook(
      this.props.newAddressBookList[event.key].userId
    );
    this.props.changeMessageHeader(
      this.props.newAddressBookList[event.key].name,
      this.props.newAddressBookList[event.key].avatar,
      false,
      [this.props.newAddressBookList[event.key].userId]
    );
  }

  handleAcceptWaitingFriend = (userId) => {
    this.props.addNewFriend(userId);
  };

  handleRejectWaitingFriend = (userId) => {
    this.props.rejectWaitingFriend(userId);
  };

  renderListAvatar = (item) => {
    if (item.group == false || item.userIds.length == 1) {
      let singleId = item.userIds[0];
      if (item.userIds.length != 1)
        singleId = item.userIds.filter((e) => e != getUserIdFromStorage());

      return (
        <CustomAvatar
          type="avatar"
          src={getProfileURL(singleId)}
          size={60}
          style={{
            // position: "absolute",
            // left: 10,
            // top: 10,
            border: "0.5px solid white",
            cursor: "pointer",
          }}
        />
      );
    }

    let userIdsSlice = item.userIds;
    if (item.userIds.length == 2) {
      userIdsSlice = item.userIds.slice(0, 2);
      return (
        <div
          style={{
            width: 60,
            height: 60,
            position: "relative",
          }}
        >
          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[0])}
            size={40}
            style={{
              position: "absolute",
              right: 0,
              top: 0,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />

          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[1])}
            size={40}
            style={{
              position: "absolute",
              left: 0,
              bottom: 0,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />
        </div>
      );
    }

    if (item.userIds.length == 3) {
      userIdsSlice = item.userIds.slice(0, 3);
      return (
        <div
          style={{
            width: 60,
            height: 60,
            position: "relative",
          }}
        >
          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[1])}
            size={30}
            style={{
              position: "absolute",
              left: 4,
              bottom: 4,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />

          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[2])}
            size={30}
            style={{
              position: "absolute",
              right: 4,
              bottom: 4,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />
          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[0])}
            size={30}
            style={{
              position: "absolute",
              left: 15,
              top: 4,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />
        </div>
      );
    }

    if (item.userIds.length == 4) {
      userIdsSlice = item.userIds.slice(0, 4);
      return (
        <div
          style={{
            width: 60,
            height: 60,
            position: "relative",
          }}
        >
          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[0])}
            size={30}
            style={{
              position: "absolute",
              left: 2,
              top: 0,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />

          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[1])}
            size={30}
            style={{
              position: "absolute",
              right: 2,
              top: 0,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />

          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[2])}
            size={30}
            style={{
              position: "absolute",
              right: 2,
              bottom: 4,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />
          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[3])}
            size={30}
            style={{
              position: "absolute",
              left: 2,
              bottom: 4,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />
        </div>
      );
    }

    if (item.userIds.length > 4) {
      userIdsSlice = item.userIds.slice(0, 4);
      return (
        <div
          style={{
            width: 60,
            height: 60,
            position: "relative",
          }}
        >
          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[0])}
            size={30}
            style={{
              position: "absolute",
              left: 2,
              top: 0,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />

          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[1])}
            size={30}
            style={{
              position: "absolute",
              right: 2,
              top: 0,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />
          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[3])}
            size={30}
            style={{
              position: "absolute",
              left: 2,
              bottom: 4,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />
          <CustomAvatar
            type="avatar"
            src={getProfileURL(userIdsSlice[2])}
            size={30}
            style={{
              position: "absolute",
              right: 2,
              bottom: 4,
              border: "0.5px solid white",
              cursor: "pointer",
            }}
          />

          <div
            style={{
              width: 30,
              height: 30,
              backgroundColor: "rgba(232, 234, 239,0.8)",
              borderRadius: 30,
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              position: "absolute",
              right: 2,
              bottom: 4,
              border: "0.5px solid white",
              cursor: "pointer",
              zIndex: 1000,
            }}
          >
            <p
              style={{
                padding: 0,
                margin: 0,
                color: "#72808e",
                fontWeight: "lighter",
                fontSize: 12,
              }}
            >
              {item.userIds.length - 3}
            </p>
          </div>
        </div>
      );
    }
  };

  render() {
    console.log("BÔK", this.props);

    return (
      <div className="d-flex flex-column full-height address-book-menu">
        <AddFriend />
        <Scrollbars autoHide autoHideTimeout={500} autoHideDuration={200}>
          {this.props.waitingFriendList.length > 0 ? (
            <div>
              <hr className="hr-sub-menu-title" />
              <div className="sub-menu-title new-add">
                {" "}
                Friends Request ({this.props.waitingFriendList.length})
              </div>
              <Menu
                theme="light"
                mode="inline"
                defaultSelectedKeys={[]}
                // selectedKeys={this.state.newselect}
                className="address-book new-address-book"
                // onSelect={this.handleNewChange}
              >
                {this.props.waitingFriendList.map((item, index) => (
                  <Menu.Item key={index}>
                    {/* {this.renderListAvatar({ userIds: [item.userId] })} */}
                    <StyledBadge
                      overlap="circular"
                      anchorOrigin={{
                        vertical: "bottom",
                        horizontal: "right",
                      }}
                      variant="dot"
                    >
                      <Avatar
                        sizes="100"
                        alt="Remy Sharp"
                        src={getProfileURL(item.userId)}
                      />
                    </StyledBadge>

                    {item.isOnline ? (
                      <div className="status-point online" />
                    ) : (
                      <div className="status-point offline" />
                    )}
                    <div
                      style={{
                        overflow: "hidden",
                        paddingTop: 5,
                        width: 150,
                        marginRight: 10,
                      }}
                    >
                      <div className="user-name">{item.name}</div>
                      <div className="history-message">{item.status}</div>
                    </div>
                    <div
                      style={{
                        width: 30,
                        display: "flex",
                        justifyContent: "center",
                        alignItems: "center",
                      }}
                    >
                      <Icon
                        type="check-circle"
                        theme="twoTone"
                        twoToneColor="#52c41a"
                        style={{ fontSize: 30 }}
                        onClick={() =>
                          this.handleAcceptWaitingFriend(item.userId)
                        }
                      />
                      <Icon
                        type="stop"
                        theme="twoTone"
                        twoToneColor="#eb2f96"
                        style={{ fontSize: 30, marginLeft: 10 }}
                        onClick={() =>
                          this.handleRejectWaitingFriend(item.userId)
                        }
                      />
                    </div>
                  </Menu.Item>
                ))}
              </Menu>
            </div>
          ) : (
            ""
          )}
          {this.props.newAddressBookList.length > 0 ? (
            <div>
              <hr className="hr-sub-menu-title" />
              <div className="sub-menu-title new-add">
                {" "}
                New Friends ({this.props.newAddressBookList.length})
              </div>
              <Menu
                theme="light"
                mode="inline"
                defaultSelectedKeys={[]}
                selectedKeys={this.state.newselect}
                className="address-book new-address-book"
                onSelect={this.handleNewChange}
              >
                {this.props.newAddressBookList.map((item, index) => (
                  <Menu.Item key={index}>
                    {item.isOnline ? (
                      <StyledBadge
                        overlap="circular"
                        anchorOrigin={{
                          vertical: "bottom",
                          horizontal: "right",
                        }}
                        variant="dot"
                      >
                        <Avatar
                          sizes="100"
                          alt=""
                          src={getProfileURL(item.userId)}
                          style={{ width: 60, height: 60 }}
                        />
                      </StyledBadge>
                    ) : (
                      <StyledBadge
                        overlap="circular"
                        anchorOrigin={{
                          vertical: "bottom",
                          horizontal: "right",
                        }}
                        // variant="dot"
                        showZero={true}
                      >
                        <Avatar
                          alt="Remy Sharp"
                          src={getProfileURL(item.userId)}
                          style={{ width: 60, height: 60 }}
                        />
                      </StyledBadge>
                    )}
                    <div style={{ overflow: "hidden", paddingTop: 5 }}>
                      <div className="user-name">{item.name}</div>
                      <div className="history-message">{item.status}</div>
                    </div>
                  </Menu.Item>
                ))}
              </Menu>
            </div>
          ) : (
            ""
          )}
          <hr className="hr-sub-menu-title" />
          <div className="sub-menu-title">
            Friends ({this.props.addressBookList.length})
          </div>
          <Menu
            theme="light"
            mode="inline"
            defaultSelectedKeys={[]}
            selectedKeys={this.state.current}
            className="address-book"
            onSelect={this.handleCurrentChange}
          >
            {this.props.addressBookList.map((item, index) => (
              <Menu.Item key={index}>
                {/* {this.renderListAvatar({ userIds: [item.userId] })} */}

                {item.isOnline ? (
                  <StyledBadge
                    overlap="circular"
                    anchorOrigin={{
                      vertical: "bottom",
                      horizontal: "right",
                    }}
                    variant="dot"
                  >
                    <Avatar
                      sizes="100"
                      alt=""
                      src={getProfileURL(item.userId)}
                      style={{ width: 60, height: 60 }}
                    />
                  </StyledBadge>
                ) : (
                  <StyledBadge
                    overlap="circular"
                    anchorOrigin={{
                      vertical: "bottom",
                      horizontal: "right",
                    }}
                    // variant="dot"
                    showZero={true}
                  >
                    <Avatar
                      alt="Remy Sharp"
                      src={getProfileURL(item.userId)}
                      style={{ width: 60, height: 60 }}
                    />
                  </StyledBadge>
                )}
                <div style={{ overflow: "hidden", paddingTop: 5 }}>
                  <div className="user-name">{item.name}</div>
                  <div className="history-message">{item.status}</div>
                </div>
              </Menu.Item>
            ))}
          </Menu>
        </Scrollbars>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    addressBookList: state.addressBookReducer.addressBookList,
    newAddressBookList: state.addressBookReducer.newAddressBookList,
    waitingFriendList: state.addressBookReducer.waitingFriendList,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    addNewFriend(username) {
      dispatch(addNewFriend(username));
    },
    rejectWaitingFriend(username) {
      dispatch(rejectWaitingFriend(username));
    },
    loadAddressBookList() {
      dispatch(loadAddressBookList());
    },
    loadWaitingFriendList() {
      dispatch(loadWaitingFriendList());
    },
    changeMessageHeader(avatar, title, groupchat, userIds) {
      dispatch(changeMessageHeader(avatar, title, groupchat, userIds));
    },
    handleChangeAddressBook(userId) {
      dispatch(handleChangeAddressBook(userId));
    },
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(AddressBook);
