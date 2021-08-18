import React from "react";
import { connect } from "react-redux";
import lottie from "lottie-web";
import heart from "../../static/heart2.json";

class LuckyAnimation extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
    lottie
      .loadAnimation({
        container: this._el, // the dom element that will contain the animation
        renderer: "svg",
        autoplay: true,
        loop: true,
        animationData: heart, // the path to the animation json
      })
      .setSpeed(1.5);
  }

  render() {
    return (
      <div
        ref={(el) => (this._el = el)}
        style={{
          position: "absolute",
          width: "100%",
          height: "100%",
          top: 0,
          left: 0,
        }}
      />
    );
  }
}

function mapDispatchToProps(dispatch) {
  return {};
}

export default connect(null, mapDispatchToProps)(LuckyAnimation);
