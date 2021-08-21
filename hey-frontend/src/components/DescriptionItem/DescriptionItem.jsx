import {Col, Row} from "antd";
import React from "react";

const DescriptionItem = ({ title, content }) => (
  <Row
    style={{
      fontSize: 14,
      lineHeight: "22px",
      marginBottom: 7,
      color: "rgba(0,0,0,0.65)",
    }}
  >
    <Col
      span={12}
      style={{
        // marginRight: 8,
        display: "inline-block",
        color: "rgba(0,0,0,0.85)",
      }}
    >
      <h3>{title}:</h3>
    </Col>

    <Col span={12}>{content}</Col>
  </Row>
);

export default DescriptionItem;
