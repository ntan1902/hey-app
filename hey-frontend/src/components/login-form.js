import React from "react";
import { Button, Form, Icon, Input } from "antd";
import { withRouter } from "react-router-dom";
import { setJwtToStorage, setRefreshTokenToStorage } from "../utils/utils";

import { connect } from "react-redux";
import { channingActions } from "../utils";
import { bindAuthActions } from "../actions";

const FormItem = Form.Item;

class NormalLoginForm extends React.Component {
  constructor(props) {
    super(props);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  state = {
    serverValidation: {
      visible: false,
      validateStatus: "error",
      errorMsg: "Invalid username or password!",
    },
  };

  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields((error, values) => {
      if (!error) {
        this.props.authActions.authentication(values).then((res) => {
          setJwtToStorage(res.data.token);
          setRefreshTokenToStorage(res.data.refreshToken);
          this.props.history.push("/");
        });
      }
    });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Form onSubmit={this.handleSubmit} className="login-form">
        <FormItem>
          {getFieldDecorator("username", {
            rules: [{ required: true, message: "Please input your username!" }],
          })(
            <Input
              prefix={<Icon type="user" style={{ color: "rgba(0,0,0,.25)" }} />}
              placeholder="Username"
            />
          )}
        </FormItem>
        <FormItem>
          {getFieldDecorator("password", {
            rules: [{ required: true, message: "Please input your Password!" }],
          })(
            <Input
              prefix={<Icon type="lock" style={{ color: "rgba(0,0,0,.25)" }} />}
              type="password"
              placeholder="Password"
            />
          )}
        </FormItem>
        {this.state.serverValidation.visible ? (
          <FormItem
            validateStatus={this.state.serverValidation.validateStatus}
            help={this.state.serverValidation.errorMsg}
          />
        ) : null}
        <FormItem>
          <Button
            type="primary"
            htmlType="submit"
            className="login-form-button"
          >
            Log in
          </Button>
        </FormItem>
      </Form>
    );
  }
}

export default connect(
  (state) => ({
    verifyPin: state.paymentReducer.verifyPin,
  }),
  (dispatch) => channingActions({}, dispatch, bindAuthActions)
)(withRouter(Form.create()(NormalLoginForm)));
