import {combineReducers} from "redux";
import chatReducer from "./chatReducer";
import addressBookReducer from "./addressBookReducer";
import userReducer from "./userReducer";
import paymentReducer from "./paymentReducer";
import modalReducer from "./modalReducer";

const appReducer = combineReducers({
  chatReducer,
  addressBookReducer,
  userReducer,
  paymentReducer,
  modalReducer
});

const rootReducer = (state, action) => {
  if (action.type === "USER_LOGOUT") {
    state = undefined;
  }
  return appReducer(state, action);
};

export default rootReducer;
