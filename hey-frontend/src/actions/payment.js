import * as actionTypes from "./actionTypes";
import { bindActionCreators } from "redux";

/* Get */

const onShow = (screenName) => {
  console.log("ON show", screenName);
  return {
    type: actionTypes.ON_SHOW,
    layoutType: screenName,
  };
};

const onOpenPinPopup = () => {
  return {
    type: actionTypes.ON_SHOW_PIN,
    verifyPin: true,
  };
};

const onClosePinPopup = () => {
  return {
    type: actionTypes.ON_CLOSE_PIN,
    verifyPin: false,
  };
};

const changeStateAddFriendTransferPopup = (state) => {
  return {
    type: actionTypes.ADD_FRIEND_TRANSFER_POPUP,
    addFriendTransferPopup: state,
  };
};

const changeStateLuckyMoneyPopup = (state, isCreate) => {
  return {
    type: actionTypes.LUCKY_MONEY_POPUP,
    luckyMoneyPopup: state,
    isCreate: isCreate,
  };
};

const switchMainScreen = (screenName) => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      console.log(screenName);
      await dispatch(onShow(screenName));
      resolve({ success: true });
    } catch (err) {
      reject({ error: err, success: false });
    }
  });
};

const verifyPin = (pin) => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      console.log(pin);
      await dispatch(onClosePinPopup());
      resolve({ softToken: "abc", success: true });
    } catch (err) {
      reject({ error: err, success: false });
    }
  });
};

export const paymentActions = {
  /* Get Event */
  switchMainScreen,
  onOpenPinPopup,
  onClosePinPopup,
  verifyPin,
  changeStateAddFriendTransferPopup,
  changeStateLuckyMoneyPopup,
};

export function bindPaymentActions(currentActions, dispatch) {
  return {
    ...currentActions,
    paymentActions: bindActionCreators(paymentActions, dispatch),
  };
}
