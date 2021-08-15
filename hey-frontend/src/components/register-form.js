import React from "react";
import {Button, Form, Icon, Input} from "antd";
import {connect} from "react-redux";
import {setJwtToStorage, setRefreshTokenToStorage} from "../utils/utils";
import {withRouter} from "react-router-dom";

import {channingActions} from "../utils";
import {bindAuthActions} from "../actions";

const FormItem = Form.Item;

class NormalRegisterForm extends React.Component {
    state = {
        confirmDirty: false,
    };

    handleSubmit = (e) => {
        e.preventDefault();
        this.props.form.validateFields((err, values) => {
            if (!err) {
                const user = {
                    fullName: values.fullName,
                    username: values.userName,
                    password: values.password,
                    email: values.email,
                };

                this.props.authActions.register(user)
                    .then((res) => {
                        this.props.authActions
                            .authentication({
                                username: user.username,
                                password: user.password,
                            })
                            .then((res2) => {
                                setJwtToStorage(res2.data.token);
                                setRefreshTokenToStorage(res2.data.refreshToken);
                                this.props.history.push("/");
                            });
                    });
            }
        });
    };


    handleConfirmBlur = (e) => {
        const value = e.target.value;
        this.setState({confirmDirty: this.state.confirmDirty || !!value});
    };

    compareToFirstPassword = (rule, value, callback) => {
        const form = this.props.form;
        if (value && value !== form.getFieldValue("password")) {
            callback("Two passwords that you enter is inconsistent!");
        } else {
            callback();
        }
    };

    validateToNextPassword = (rule, value, callback) => {
        const form = this.props.form;
        if (value && this.state.confirmDirty) {
            form.validateFields(["rePassword"], {force: true});
        }
        callback();
    };

    render() {
        const {getFieldDecorator} = this.props.form;
        return (
            <Form onSubmit={this.handleSubmit} className="login-form">
                <FormItem>
                    {getFieldDecorator("email", {
                        rules: [{required: true, message: "Please input your Email!"}],
                    })(
                        <Input
                            prefix={
                                <Icon type="idcard" style={{color: "rgba(0,0,0,.25)"}}/>
                            }
                            placeholder="Email"
                        />
                    )}
                </FormItem>
                <FormItem>
                    {getFieldDecorator("fullName", {
                        rules: [
                            {required: true, message: "Please input your Full Name!"},
                        ],
                    })(
                        <Input
                            prefix={
                                <Icon type="idcard" style={{color: "rgba(0,0,0,.25)"}}/>
                            }
                            placeholder="Fullname"
                        />
                    )}
                </FormItem>
                <FormItem>
                    {getFieldDecorator("userName", {
                        rules: [
                            {required: true, message: "Please choose your username!"},
                        ],
                    })(
                        <Input
                            prefix={<Icon type="user" style={{color: "rgba(0,0,0,.25)"}}/>}
                            placeholder="Username"
                        />
                    )}
                </FormItem>
                <FormItem>
                    {getFieldDecorator("password", {
                        rules: [
                            {required: true, message: "Please input your Password!"},
                            {validator: this.validateToNextPassword},
                        ],
                    })(
                        <Input
                            prefix={<Icon type="lock" style={{color: "rgba(0,0,0,.25)"}}/>}
                            type="password"
                            placeholder="Password"
                        />
                    )}
                </FormItem>
                <FormItem>
                    {getFieldDecorator("rePassword", {
                        rules: [
                            {required: true, message: "Please re-input your Password!"},
                            {validator: this.compareToFirstPassword},
                        ],
                    })(
                        <Input
                            prefix={<Icon type="lock" style={{color: "rgba(0,0,0,.25)"}}/>}
                            type="password"
                            placeholder="Confirm Password"
                            onBlur={this.handleConfirmBlur}
                        />
                    )}
                </FormItem>
                <FormItem>
                    <Button
                        type="primary"
                        htmlType="submit"
                        className="login-form-button"
                    >
                        Register
                    </Button>
                </FormItem>
            </Form>
        );
    }
}

export default connect(
    (state) => ({
        user: state.userReducer.user,
    }),
    (dispatch) => channingActions({}, dispatch, bindAuthActions)
)(withRouter(Form.create()(NormalRegisterForm)));
