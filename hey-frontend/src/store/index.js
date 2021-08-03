import thunk from "redux-thunk";
import logger from "redux-logger";
import rootReducer from "../reducers";
import { applyMiddleware, createStore } from "redux";

let middleware = [thunk];
if (process.env.NODE_ENV === `development`) {
  middleware.push(logger);
}

export const store = createStore(rootReducer, applyMiddleware(...middleware));
