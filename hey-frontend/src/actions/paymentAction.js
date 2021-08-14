import * as actionTypes from "./actionTypes";
import { bindActionCreators } from "redux";
import { PaymentAPI, AuthAPI } from "../api";
/* Get */

const onShow = (screenName, data) => {
  console.log("ON show", screenName);
  return {
    type: actionTypes.ON_SHOW,
    layoutType: screenName,
    mainScreenData: data,
  };
};

const updateBalance = (balance) => {
  console.log("ON balance", balance);
  return {
    type: actionTypes.ON_UPDATE_BALANCE,
    balance: balance,
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

export const changeStateAddFriendPopup = (state) => {
  return {
    type: actionTypes.ADD_FRIEND_TO_SESSION,
    isAddFriendToSession: state,
  };
};

const switchMainScreen = (screenName, data = null) => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      console.log(screenName, data);
      await dispatch(onShow(screenName, data));
      resolve({ success: true });
    } catch (err) {
      reject({ error: err, success: false });
    }
  });
};

const verifyPin = (pin, amount) => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      console.log(pin);
      const res = await AuthAPI.verifyPin({ pin: pin, amount: amount });

      await dispatch(onClosePinPopup());
      resolve({ softToken: res.data.payload.softToken, success: true });
    } catch (err) {
      reject({ error: err, success: false });
    }
  });
};

const getBalance = () => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      const res = await PaymentAPI.getBalance();
      await dispatch(updateBalance(res.data.payload.balance));
      resolve({ success: true });
    } catch (err) {
      reject({ error: err, success: false });
    }
  });
};

const checkBalance = () => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      const res = await PaymentAPI.checkBalance();
      if (res.data.payload.hasWallet == false) {
        console.log(res, "Chekc Balance");
        await PaymentAPI.createBalance();
      }
      resolve({ success: true });
    } catch (err) {
      console.log("Err");
      reject({ error: err, success: false });
    }
  });
};

const topup = (amount) => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      const res = await PaymentAPI.topup({
        amount: amount,
        bankId: "e8984aa8-b1a5-4c65-8c5e-036851ec783c",
      });
      await dispatch(getBalance());
      await dispatch(switchMainScreen("TopupSuccess"));
      resolve({ success: true });
    } catch (err) {
      reject({ error: err, success: false });
    }
  });
};

const transfer = (data) => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      const res = await PaymentAPI.transfer(data);
      await dispatch(getBalance());
      await dispatch(switchMainScreen("TransferSuccess"));
      resolve({ success: true });
    } catch (err) {
      reject({ error: err, success: false });
    }
  });
};

const createLuckymoney = (data) => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      const res = await PaymentAPI.createLuckymoney(data);
      await dispatch(getBalance());
      await dispatch(changeStateLuckyMoneyPopup(true, false));
      resolve({ success: true });
    } catch (err) {
      reject({ error: err, success: false });
    }
  });
};

const getListLuckymoney = (sessionId) => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      const res = await PaymentAPI.getListLuckymoney(sessionId);
      await dispatch(getBalance());
      // await dispatch(switchMainScreen("TransferSuccess"));
      resolve({ data: res.data.payload, success: true });
    } catch (err) {
      reject({ error: err, success: false });
    }
  });
};

const receivedLuckymoney = (data) => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      const res = await PaymentAPI.receivedLuckymoney(data);
      await dispatch(getBalance());
      // await dispatch(switchMainScreen("TransferSuccess"));
      resolve({ data: res.data.payload, success: true });
    } catch (err) {
      reject({ error: err, success: false });
    }
  });
};

const getAllTransferStatement = (data) => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      const res = await PaymentAPI.getAllTransferStatement();
      // await dispatch(getBalance());
      // await dispatch(switchMainScreen("TransferSuccess"));
      resolve({ data: res.data.payload, success: true });
    } catch (err) {
      reject({ error: err, success: false });
    }
  });
};

const changeStateMembersModal = (state) => {
  return {
    type: actionTypes.CHANGE_STATE_MEMBERS_MODAL,
    state
  }
}



export const paymentActions = {
  /* Get Event */
  switchMainScreen,
  onOpenPinPopup,
  onClosePinPopup,
  verifyPin,
  changeStateAddFriendTransferPopup,
  changeStateLuckyMoneyPopup,
  getBalance,
  topup,
  transfer,
  createLuckymoney,
  getListLuckymoney,
  receivedLuckymoney,
  getAllTransferStatement,
  checkBalance,
  changeStateAddFriendPopup,
  changeStateMembersModal,
};

export function bindPaymentActions(currentActions, dispatch) {
  return {
    ...currentActions,
    paymentActions: bindActionCreators(paymentActions, dispatch),
  };
}
