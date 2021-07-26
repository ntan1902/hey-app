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

export const switchMainScreen = (screenName) => async (dispatch) => {
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

export const paymentActions = {
  /* Get Event */
  switchMainScreen,
};

export function bindPaymentActions(currentActions, dispatch) {
  return {
    ...currentActions,
    paymentActions: bindActionCreators(paymentActions, dispatch),
  };
}
