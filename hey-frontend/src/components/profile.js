import React from "react";
import {Icon, Input} from "antd";
import {clearStorage, getRefreshTokenFromStorage, isEmptyString,} from "../utils/utils";
import {withRouter} from "react-router-dom";
import {closeWebSocket} from "../actions/chatAction";
import {currency, formatToCurrency} from "../utils/index";
import {connect} from "react-redux";
import {changeUserStatus, getProfile, logout} from "../actions/userAction";
import {AuthAPI} from "../api";

class Profile extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      status_box_state: true,
    };
    this.logOut = this.logOut.bind(this);
    this.openStatusBoxStateStatus = this.openStatusBoxStateStatus.bind(this);
    this.closeStatusBoxStateStatus = this.closeStatusBoxStateStatus.bind(this);
    this.onChangeStatus = this.onChangeStatus.bind(this);
  }

  componentDidMount() {
    this.props.getProfile();
    this.setState({
      ...this.state,
      status_text: this.props.userStatus,
    });
  }

  componentDidUpdate() {
    if (isEmptyString(this.state.status_text) && this.state.status_box_state) {
      this.state.status_text = "You are online";
      this.setState({
        ...this.state,
        status_text: this.state.status_text,
      });
    }
    if (this.refs.statusInput) this.refs.statusInput.focus();
  }

  logOut(e) {
    const refreshToken = getRefreshTokenFromStorage();

    AuthAPI.logout({ refreshToken })
      .then((res) => {
        clearStorage();
        this.props.closeWebSocket();
        this.props.logout();
        this.props.history.push("/login");
      })
      .catch((err) => {
        console.log(err);
      });
  }

  openStatusBoxStateStatus() {
    if (this.props.userStatus === "You are online") {
      this.state.status_text = "";
    } else {
      this.state.status_text = this.props.userStatus;
    }
    this.setState({
      ...this.state,
      status_box_state: false,
      status_text: this.state.status_text,
    });
  }

  closeStatusBoxStateStatus() {
    this.props.changeUserStatus(this.state.status_text);
    this.setState({
      ...this.state,
      status_box_state: true,
    });
  }

  onChangeStatus = (e) => {
    this.setState({
      ...this.state,
      status_text: e.target.value,
    });
  };

  render() {
    return (
      <div className="wrapper">
        <div className="profile-left">
          <div className="box user-name">
            Hey! - {this.props.profile.fullName}
          </div>
          <div className="box status">
            Balance - {formatToCurrency(this.props.balance) + currency}
          </div>
          <div className="box status">
            {this.state.status_box_state ? (
              <div onClick={this.openStatusBoxStateStatus}>
                Status: {this.props.userStatus}
              </div>
            ) : (
              <Input
                onBlur={this.closeStatusBoxStateStatus}
                onPressEnter={this.closeStatusBoxStateStatus}
                placeholder="Please enter a status..."
                value={this.state.status_text}
                ref="statusInput"
                onChange={this.onChangeStatus}
              />
            )}
          </div>
        </div>
        <div className="profile-right logout">
          <a href="#" className="logout-a" onClick={this.logOut}>
            <Icon type="logout" style={{ fontSize: 36, color: "#444" }} />
          </a>
        </div>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    profile: state.userReducer.profile,
    userStatus: state.userReducer.userStatus,
    balance: state.paymentReducer.balance,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    changeUserStatus(status) {
      dispatch(changeUserStatus(status));
    },
    getProfile() {
      dispatch(getProfile());
    },
    logout() {
      dispatch(logout());
    },
    closeWebSocket() {
      dispatch(closeWebSocket());
    },
  };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(withRouter(Profile));
