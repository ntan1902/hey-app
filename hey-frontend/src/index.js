import "antd/dist/antd.css";
import "./index.css";
import "react-slidedown/lib/slidedown.css";
import thunk from "redux-thunk";
import logger from "redux-logger";
import React from "react";
import ReactDOM from "react-dom";
import { BrowserRouter as Router, Route } from "react-router-dom";
import rootReducer from "./reducers";
import Portal from "./pages/portal";
import Main from "./pages/main";
import { applyMiddleware, createStore } from "redux";
import { Provider } from "react-redux";
import { api } from "./api/api";
import { clearStorage } from "./utils/utils";

let middleware = [thunk];
if (process.env.NODE_ENV === `development`) {
  middleware.push(logger);
}

export const store = createStore(rootReducer, applyMiddleware(...middleware));
window.store = store;

api.post(`/api/protected/ping`).then(
  (data) => {
    console.log("pong");
    console.log(data);
  },
  (data) => {
    console.log("not-ping");
    clearStorage();
  }
);

ReactDOM.render(
  <Provider store={store}>
    <Router>
      <div style={{ overflow: "hidden" }}>
        <Route exact path="/login" component={Portal} />
        <Route exact path="/" component={Main} />
      </div>
    </Router>
  </Provider>,
  document.getElementById("root")
);
