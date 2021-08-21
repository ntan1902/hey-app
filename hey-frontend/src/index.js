import "antd/dist/antd.css";
import "./index.css";
import "react-slidedown/lib/slidedown.css";

import React from "react";
import {BrowserRouter as Router, Route} from "react-router-dom";
import Portal from "./pages/portal";
import Main from "./pages/main";
import {Provider} from "react-redux";

import {store} from "./store";
import DOM from 'react-dom';
import CallPopUp from "./pages/VideoCall/CallPopUp";

window.store = store;

DOM.render(
  <Provider store={store}>
    <Router>
      <div style={{ overflow: "hidden" }}>
        <Route exact path="/login" component={Portal} />
        <Route exact path="/" component={Main} />
        <Route exact path="/call" component={CallPopUp} />
      </div>
    </Router>
  </Provider>,
  document.getElementById("root")
);
