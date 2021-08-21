import {Input, Tooltip} from "antd";
import React from "react";
import {currencyToString, DocTien} from "../utils/index";

export default class NumericInput extends React.Component {
  onChange = (e) => {
    const value = e.target.value;
    const reg = /^-?([0-9]|,)*(\.[0-9]*)?$/;
    if (
      (!isNaN(currencyToString(value)) && reg.test(value)) ||
      value === "" ||
      value === "-"
    ) {
      this.props.onChange(value);
    }
  };

  // '.' at the end or only '-' in the input box.
  onBlur = () => {
    const { value, onBlur, onChange } = this.props;
    let valueTemp = value;
    if (value.charAt(value.length - 1) === "," || value === "-") {
      valueTemp = value.slice(0, -1);
    }
    onChange(valueTemp.replace(/0*(\d+)/, "$1"));
    if (onBlur) {
      onBlur();
    }
  };

  render() {
    const { value } = this.props;
    const title = value ? (
      <span>
        {value !== "-" ? new DocTien().doc(currencyToString(value)) : "-"}
      </span>
    ) : (
      "Input a number"
    );
    return (
      <Tooltip
        trigger={["focus"]}
        title={title}
        placement="topLeft"
        overlayClassName="numeric-input"
      >
        <Input
          {...this.props}
          width={this.props.width}
          onChange={this.onChange}
          onBlur={this.onBlur}
          placeholder="Input a number"
          maxLength={20}
          //   style={{ width: 150 }}
        />
      </Tooltip>
    );
  }
}
