import React from 'react';
import {DatePicker, Form, Input, message, Modal} from 'antd';
import moment from 'moment';
import {connect} from 'react-redux';
import $ from 'jquery';
import {ChatAPI} from '../../api/chat';
import {changeVisibleChangeProfile} from '../../actions/modalAction';
import {setProfile} from '../../actions/userAction';
import {AuthAPI} from '../../api';

const HIDE_CHANGE_PROFILE_MODAL = false;

class ChangeProfileModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      dateOfBirth: this.props.profile.dob
    }
  }
  onChangeDatePicker = (date, dateString) => {
    this.setState({ dateOfBirth: dateString + "T00:00:00" })
  }
  onChangeProfile = async () => {
    let profile = {
      email: $("#email").val(),
      fullName: $("#fullName").val(),
      phoneNumber: $("#phoneNumber").val(),
      dateOfBirth: this.state.dateOfBirth,
    };
    ChatAPI.editProfile(profile)
      .then(res => {
        this.props.changeVisibleChangeProfile(HIDE_CHANGE_PROFILE_MODAL);
        $("#email").val("");
        $("#fullName").val("");
        $("#phoneNumber").val("");
        $("#birthday").val("");
        AuthAPI.getProfile()
          .then(res => this.props.setProfile(res.data.payload))

        message.success("Change profile success !!!");
      })
      .catch(err => {
        console.log(err.response);
        message.error(err.response.data.message);
      })
  };
  componentDidMount() {
    this.props.form.
      setFieldsValue({
        email: this.props.profile.email,
        fullName: this.props.profile.fullName,
        phoneNumber: this.props.profile.phoneNumber,
        birthday: this.props.profile.dob ? moment(this.props.profile.dob.slice(0, 10), 'YYYY/MM/DD') : ""
      })
  }

  render() {
    const { form } = this.props;
    const { getFieldDecorator } = form;

    return (
      <Modal
        visible={this.props.visibleChangeProfile}
        title="Change Profile"
        okText="Ok"
        onCancel={() => this.props.changeVisibleChangeProfile(HIDE_CHANGE_PROFILE_MODAL)}
        onOk={this.onChangeProfile}
      >
        <Form layout="vertical">
          <Form.Item label="Email">
            {getFieldDecorator("email", {
              rules: [
                {
                  type: "email",
                  message: "The input is not valid E-mail!",
                },
                {
                  required: false,
                  message: "Please input your E-mail!",
                },
              ],
            })(<Input />)}
          </Form.Item>
          <Form.Item label="Full Name">
            {getFieldDecorator("fullName", {
              rules: [
                {
                  required: false,
                  message: "Please input Full Name!",
                },
              ],
            })(<Input />)}
          </Form.Item>
          <Form.Item label="Phone">
            {getFieldDecorator("phoneNumber", {
              rules: [
                {
                  required: false,
                  message: "Please input phone number!",
                },
              ],
            })(<Input />)}
          </Form.Item>
          <Form.Item label="Birthday">
            {getFieldDecorator("birthday", {
              rules: [
                {
                  required: false,
                  message: "Please input your birthday!",
                },
              ],
            })(<DatePicker onChange={this.onChangeDatePicker} />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  };
}

function mapStateToProps(state) {
  return {
    visibleChangeProfile: state.modalReducer.visibleChangeProfile,
    profile: state.userReducer.profile
  }
}
function mapDispatchToProps(dispatch) {
  return {
    changeVisibleChangeProfile: (isVisible) => dispatch(changeVisibleChangeProfile(isVisible)),
    setProfile: (profile) => dispatch(setProfile(profile))
  }
}
export default connect(mapStateToProps, mapDispatchToProps)(Form.create()(ChangeProfileModal));