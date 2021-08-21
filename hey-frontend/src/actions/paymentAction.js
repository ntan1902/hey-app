import * as actionTypes from "./actionTypes";
import {bindActionCreators} from "redux";
import {AuthAPI, PaymentAPI} from "../api";
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

export const changeTransferStatements = (transferStatements) => {
  return {
    type: actionTypes.FETCH_TRANSFER_STATEMENT,
    transferStatements: transferStatements,
  };
};

export const newTransferStatement = (transferStatement) => {
  return {
    type: actionTypes.NEW_TRANSFER_STATEMENT,
    transferStatement: transferStatement,
  };
};

export const changeOffset = (offset) => {
  return {
    type: actionTypes.CHANGE_OFFSET,
    offset: offset,
  };
};

const switchMainScreen =
  (screenName, data = null) =>
  async (dispatch) => {
    return new Promise(async (resolve, reject) => {
      try {
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
      if (res.data.payload.hasWallet === false) {
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
      await PaymentAPI.topup({
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
      await PaymentAPI.transfer(data);
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
      await PaymentAPI.createLuckymoney(data);
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
      if (sessionId === -1) return;

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

const getTransferStatements = (offset, limit) => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      const res = await PaymentAPI.getTransferStatements(offset, limit);
      // await dispatch(getBalance());
      // await dispatch(switchMainScreen("TransferSuccess"));
      dispatch(changeTransferStatements(res.data.payload));
      dispatch(changeOffset(offset + res.data.payload.length));

      resolve({ data: res.data.payload, success: true });
    } catch (err) {
      reject({ error: err, success: false });
    }
  });
};

const getNewTransferStatement = (offset) => async (dispatch) => {
  return new Promise(async (resolve, reject) => {
    try {
      const res = await PaymentAPI.getTransferStatements(0, 1);
      // await dispatch(getBalance());
      // await dispatch(switchMainScreen("TransferSuccess"));
      dispatch(newTransferStatement(res.data.payload[0]));
      dispatch(changeOffset(offset + 1));
      resolve({ data: res.data.payload, success: true });
    } catch (err) {
      reject({ error: err, success: false });
    }
  });
};

const changeStateMembersModal = (state) => {
  return {
    type: actionTypes.CHANGE_STATE_MEMBERS_MODAL,
    state,
  };
};

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
  getTransferStatements,
  checkBalance,
  changeStateAddFriendPopup,
  changeStateMembersModal,
  getNewTransferStatement,
};

export function bindPaymentActions(currentActions, dispatch) {
  return {
    ...currentActions,
    paymentActions: bindActionCreators(paymentActions, dispatch),
  };
}
