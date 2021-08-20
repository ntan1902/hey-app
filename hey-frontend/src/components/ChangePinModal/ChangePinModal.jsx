import React from "react";
import { Modal, Form, Input, message } from "antd";
import { connect } from "react-redux";
import $ from "jquery";
import { changeVisibleChangePin } from "../../actions/modalAction";
import { getHasPin } from "../../actions/userAction";
import { AuthAPI } from "../../api";

const HIDE_CHANGE_PIN_MODAL = false;
class ChangePinModal extends React.Component {
  onChangePin = () => {
    if (!this.props.hasPin) {
      AuthAPI.createPin({
        pin: $("#pin").val(),
      })
        .then((res) => {
          this.props.changeVisibleChangePin(HIDE_CHANGE_PIN_MODAL);
          this.props.getHasPin();
          message.success("Change pin success !!!");
        })
        .catch((err) => {
          console.log(err.response);
          message.error(err.response.data.message);
        });
      return;
    }

    let data = {
      oldPin: $("#old-pin").val(),
      pin: $("#new-pin").val(),
      confirmPin: $("#confirm-pin").val(),
    };
    AuthAPI.changePin(data)
      .then((res) => {
        this.props.changeVisibleChangePin(HIDE_CHANGE_PIN_MODAL);
        $("#old-pin").val("");
        $("#new-pin").val("");
        $("#confirm-pin").val("");
        message.success("Change pin success !!!");
      })
      .catch((err) => {
        console.log(err.response);
        message.error(err.response.data.message);
      });
  };

  render() {
    const { form } = this.props;
    const { getFieldDecorator } = form;

    return (
      <Modal
        visible={this.props.visibleChangePin}
        title={this.props.hasPin ? "Change Pin" : "Create your new PIN"}
        okText="Ok"
        onCancel={() =>
          this.props.changeVisibleChangePin(HIDE_CHANGE_PIN_MODAL)
        }
        onOk={this.onChangePin}
      >
        <Form layout="vertical">
          {this.props.hasPin ? (
            <div>
              <Form.Item label="Old Pin">
                {getFieldDecorator("old-pin", {
                  rules: [
                    {
                      required: true,
                      message: "Please input Old Pin!",
                    },
                  ],
                })(<Input.Password allowClear placeholder="Old pin" />)}
              </Form.Item>
              <Form.Item label="New Pin">
                {getFieldDecorator("new-pin", {
                  rules: [
                    {
                      required: true,
                      message: "Please input New Pin!",
                    },
                  ],
                })(<Input.Password allowClear placeholder="New pin" />)}
              </Form.Item>
              <Form.Item label="Confirm Pin">
                {getFieldDecorator("confirm-pin", {
                  rules: [
                    {
                      required: true,
                      message: "Please input Confirm Pin!",
                    },
                    {
                      validator: (rule, value, callback) => {
                        const { form } = this.props;
                        if (value && value !== form.getFieldValue("new-pin")) {
                          callback("Two pins that you enter is inconsistent!");
                        } else {
                          callback();
                        }
                      },
                    },
                  ],
                })(<Input.Password allowClear placeholder="Confirm pin" />)}
              </Form.Item>
            </div>
          ) : (
            <Form.Item label="New Pin">
              {getFieldDecorator("pin", {
                rules: [
                  {
                    required: true,
                    message: "Please input Pin!",
                  },
                ],
              })(<Input.Password allowClear placeholder="pin" />)}
            </Form.Item>
          )}
        </Form>
      </Modal>
    );
  }
}

function mapStateToProps(state) {
  return {
    visibleChangePin: state.modalReducer.visibleChangePin,
    hasPin: state.userReducer.hasPin,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    changeVisibleChangePin: (isVisible) =>
      dispatch(changeVisibleChangePin(isVisible)),
    getHasPin: () => dispatch(getHasPin()),
  };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Form.create()(ChangePinModal));
