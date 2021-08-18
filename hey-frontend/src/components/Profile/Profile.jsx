import React from "react";
import { connect } from "react-redux";
import { Col, Button, Row, Drawer, Divider, Upload, message } from "antd";
import DescriptionItem from "../DescriptionItem/DescriptionItem";
import ChangePasswordModal from "../ChangePasswordModal/ChangePasswordModal";
import ChangePinModal from "../ChangePinModal/ChangePinModal";
import ChangeProfileModal from "../ChangeProfileModal/ChangeProfileModal";
import CustomAvatar from "../custom-avatar";
import {
  changeStateDrawerProfile,
  changeVisibleChangePassword,
  changeVisibleChangePin,
  changeVisibleChangeProfile,
} from "../../actions/modalAction";
import InputFile from "../InputFile/InputFile";
import { AuthAPI } from "../../api";
import { updateAvatar } from "../../actions/userAction";

const pStyle = {
  fontSize: 16,
  color: "rgba(0,0,0,0.85)",
  lineHeight: "24px",
  display: "block",
  marginBottom: 16,
};

const SHOW_CHANGE_PROFILE_MODAL = true;
const SHOW_CHANGE_PIN_MODAL = true;
const SHOW_CHANGE_PASSWORD_MODAL = true;
const HIDE_PROFILE_DRAWER = false;
class Profile extends React.Component {
  constructor(props) {
    super(props);
  }

  onChangeAvatarInput = (event) => {
    let fileTypes = ["image/png", "image/jpg", "image/jpge"];
    let input = event.currentTarget;
    if (input.files && input.files[0]) {
      let file = input.files[0];
      if (fileTypes.includes(file.type)) {
        this.props.updateAvatar(file);
      } else {
        message.error("Type file must be " + fileTypes.join(", ") + ".");
      }
    }
  };

  render() {
    return (
      <Drawer
        width={640}
        placement="right"
        closable={false}
        onClose={() => this.props.changeStateDrawerProfile(HIDE_PROFILE_DRAWER)}
        visible={this.props.visibleProfile}
      >
        <p style={{ ...pStyle, marginBottom: 24 }}>User Profile</p>
        <Row style={{ display: "flex", justifyContent: "center" }}>
          <InputFile name="file" onChange={this.onChangeAvatarInput}>
            {this.props.profile.miniAvatar ? (
              <CustomAvatar
                type="main-avatar"
                src={this.props.profile.miniAvatar}
                size={100}
              />
            ) : (
              <CustomAvatar
                type="main-avatar"
                avatar={this.props.userName}
                size={100}
              />
            )}
          </InputFile>
        </Row>
        <p style={pStyle}>Personal</p>
        <Row>
          <Col span={12}>
            <DescriptionItem
              title="Full Name"
              content={this.props.profile.fullName}
            />
          </Col>
          <Col span={12}>
            <DescriptionItem
              title="Username"
              content={this.props.profile.username}
            />
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <DescriptionItem title="Email" content={this.props.profile.email} />
          </Col>
          <Col span={12}>
            <DescriptionItem
              title="Balance"
              content={this.props.balance + " Đồng"}
            />
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <DescriptionItem
              title="Birthday"
              content={
                this.props.profile.dob
                  ? this.props.profile.dob.slice(0, 10)
                  : ""
              }
            />
          </Col>
          <Col span={12}>
            <DescriptionItem
              title="Phone Number"
              content={this.props.profile.phoneNumber}
            />
          </Col>
        </Row>
        <Row>
          <Col span={24}>
            <DescriptionItem title="Status" content={this.props.userStatus} />
          </Col>
        </Row>
        <Divider />
        <p style={pStyle}>Settings</p>
        <Row style={{ display: "flex", justifyContent: "space-around" }}>
          <Button
            style={{ width: 150 }}
            onClick={() =>
              this.props.changeVisibleChangePassword(SHOW_CHANGE_PASSWORD_MODAL)
            }
            type="primary"
          >
            Change Password
          </Button>
          <Button
            style={{ width: 150 }}
            onClick={() =>
              this.props.changeVisibleChangePin(SHOW_CHANGE_PIN_MODAL)
            }
            type="primary"
          >
            Edit Pin
          </Button>
          <Button
            style={{ width: 150 }}
            onClick={() =>
              this.props.changeVisibleChangeProfile(SHOW_CHANGE_PROFILE_MODAL)
            }
            type="primary"
          >
            Edit Profile
          </Button>
        </Row>
        <ChangePasswordModal />
        <ChangePinModal />
        <ChangeProfileModal />
      </Drawer>
    );
  }
}

function mapStateToProps(state) {
  return {
    visibleProfile: state.modalReducer.visibleProfile,
    balance: state.paymentReducer.balance,
    profile: state.userReducer.profile,
    userStatus: state.userReducer.userStatus,
  };
}
function mapDispatchToProps(dispatch) {
  return {
    changeStateDrawerProfile: (isVisible) =>
      dispatch(changeStateDrawerProfile(isVisible)),
    changeVisibleChangeProfile: (isVisible) =>
      dispatch(changeVisibleChangeProfile(isVisible)),
    changeVisibleChangePin: (isVisible) =>
      dispatch(changeVisibleChangePin(isVisible)),
    changeVisibleChangePassword: (isVisible) =>
      dispatch(changeVisibleChangePassword(isVisible)),
    updateAvatar: (fileAvatar) => dispatch(updateAvatar(fileAvatar)),
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(Profile);
