import React from "react";
import { connect } from "react-redux";
import ChatItem from "./chat-item";
import { Scrollbars } from "react-custom-scrollbars";
import { Menu, Spin } from "antd";
import { changeChatListOffset } from "../actions/chatAction";

class MessagePanel extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      current: [],
      newselect: [],
      isAll: false,
      limit: 20,
      data: [],
    };
    this.divLoadMore = React.createRef();
  }

  scrollToBottom = () => {
    this.messagesEnd.scrollIntoView({ behavior: "smooth" });
  };

  componentDidUpdate() {
    // this.scrollToBottom();
  }

  componentDidMount() {
    console.log("did mount");
    this.observer = new IntersectionObserver(
      (entries) => {
        console.log("Entries");
        entries.forEach((entry) => {
          console.log("Load More");
          const left =
            this.props.messageItems.length -
            this.props.loadSize -
            this.state.limit;
          let isAll = left < 0;
          this.props.changeChatListOffset(
            this.props.loadSize + this.state.limit,
            isAll
          );
        });
      },
      { threshold: 1 }
    );

    this.observer.observe(this.divLoadMore.current);
  }

  render() {
    console.log("MEssage Item", this.props.messageItems);
    return (
      <div className="chat-content">
        <div ref={this.props.refProp}></div>

        {this.props.messageItems
          .slice(0, this.props.loadSize)
          .map((item, index) => (
            <ChatItem
              key={item.id && item.id != "" ? item.id : index}
              type={item.type}
              value={item.message}
              showavatar={item.showavatar}
              avatar={item.avatar}
              date={item.createdDate}
              userId={item.userId}
              name={item.name}
              id={item.id}
            />
          ))}
        <div
          ref={this.divLoadMore}
          id="load_more"
          style={{ display: "flex", justifyContent: "center" }}
        >
          {!this.props.isAll && <Spin size="large" />}
        </div>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    messageItems: state.chatReducer.messageItems,
    isAll: state.chatReducer.isAll,
    loadSize: state.chatReducer.loadSize,
    currentSessionId: state.chatReducer.currentSessionId,
    userSelectedKeys: state.chatReducer.userSelectedKeys,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    changeChatListOffset(loadSize, isAll) {
      dispatch(changeChatListOffset(loadSize, isAll));
    },
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(MessagePanel);
