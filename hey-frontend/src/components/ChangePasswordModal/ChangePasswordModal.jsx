import React from 'react';
import { Modal, Form, Input, message } from 'antd';
import { connect } from 'react-redux';
import $ from 'jquery';
import { AuthAPI } from '../../api';
import { changeVisibleChangePassword } from '../../actions/modalAction';
const HIDE_CHANGE_PASSWORD_MODAL = false;
class ChangePasswordModal extends React.Component {
  constructor(props) {
    super(props);
  }

  onChangePassword = async () => {
    let data = {
      oldPassword: $("#old-password").val(),
      password: $("#new-password").val(),
      confirmPassword: $("#confirm-password").val(),
    };
    $("#old-password").val("");
    $("#new-password").val("");
    $("#confirm-password").val("");
    try {
      let res = await AuthAPI.changePassword(data);
      this.props.changeVisibleChangePassword(HIDE_CHANGE_PASSWORD_MODAL);
      message.success("Change password success !!!");
    } catch (err) {
      console.log(err.response);
      message.error("Invalid current password !!!");
    }
  };

  render() {
    const { getFieldDecorator } = this.props.form;

    return (
      <Modal
        visible={this.props.visibleChangePassword}
        title="Change Password"
        okText="Ok"
        onCancel={() => this.props.changeVisibleChangePassword(HIDE_CHANGE_PASSWORD_MODAL)}
        onOk={this.onChangePassword}
      >
        <Form layout="vertical">
          <Form.Item label="Old Password">
            {getFieldDecorator("old-password", {
              rules: [
                {
                  required: true,
                  message: "Please input Old Password!",
                },
              ],
            })(
              <Input.Password allowClear placeholder="Your current password" />
            )}
          </Form.Item>
          <Form.Item label="New Password">
            {getFieldDecorator("new-password", {
              rules: [
                {
                  required: true,
                  message: "Please input New Password!",
                },
              ],
            })(<Input.Password allowClear placeholder="new password" />)}
          </Form.Item>
          <Form.Item
            label="Confirm Password"
          >
            {getFieldDecorator("confirm-password", {
              rules: [
                {
                  required: true,
                  message: "Please input Confirm Password!",
                },
                {
                  validator: (rule, value, callback) => {
                    const { form } = this.props;
                    if (value && value !== form.getFieldValue("new-password")) {
                      callback("Two passwords that you enter is inconsistent!");
                    } else {
                      callback();
                    }
                  },
                },
              ],
            })(<Input.Password allowClear placeholder="confirm password" />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  };
}

function mapStateToProps(state) {
  return {
    visibleChangePassword: state.modalReducer.visibleChangePassword,
  }
}

function mapDispatchToProps(dispatch) {
  return {
    changeVisibleChangePassword: (isVisible) => dispatch(changeVisibleChangePassword(isVisible))
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Form.create()(ChangePasswordModal));