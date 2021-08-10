import "antd/dist/antd.css";
import "./index.css";
import "react-slidedown/lib/slidedown.css";

import React from "react";
import ReactDOM from "react-dom";
import { BrowserRouter as Router, Route } from "react-router-dom";
import Portal from "./pages/portal";
import Main from "./pages/main";
import { Provider } from "react-redux";
import { api } from "./api/api";
import { clearStorage } from "./utils/utils";

import { store } from "./store";

window.store = store;

// api.post(`/api/protected/ping`).then(
//   (data) => {
//     console.log("pong");
//     console.log(data);
//   },
//   (data) => {
//     console.log("not-ping");
//     clearStorage();
//   }
// );

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
