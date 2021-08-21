import { combineReducers } from "redux";
import chatReducer from "./chatReducer";
import addressBookReducer from "./addressBookReducer";
import userReducer from "./userReducer";
import paymentReducer from "./paymentReducer";
import modalReducer from "./modalReducer";
import callReducer from "./callReducer";

const appReducer = combineReducers({
    chatReducer,
    addressBookReducer,
    userReducer,
    paymentReducer,
    modalReducer,
    callReducer
});

const rootReducer = (state, action) => {
    if (action.type === "USER_LOGOUT") {
        state = undefined;
    }
    return appReducer(state, action);
};

export default rootReducer;