import * as actionTypes from "../actions/actionTypes";

const initialState = {
  layoutType: "",
  verifyPin: false,
  acceptTransfer: false,
  addFriendTransferPopup: false,
  luckyMoneyPopup: false,
  isCreate: false,
  balance: "0",
  mainScreenData: null,
};

export default (state = initialState, action = {}) => {
  switch (action.type) {
    case actionTypes.ON_SHOW:
      return {
        ...state,
        layoutType: action.layoutType,
        mainScreenData: action.mainScreenData,
      };
    case actionTypes.ON_SHOW_PIN:
      return {
        ...state,
        verifyPin: true,
      };
    case actionTypes.ON_CLOSE_PIN:
      return {
        ...state,
        verifyPin: false,
      };
    case actionTypes.ON_OPEN_ACCEPT_TRANSFER:
      return {
        ...state,
        acceptTransfer: true,
      };
    case actionTypes.ON_CLOSE_ACCEPT_TRANSFER:
      return {
        ...state,
        acceptTransfer: false,
      };
    case actionTypes.ADD_FRIEND_TRANSFER_POPUP:
      return {
        ...state,
        addFriendTransferPopup: action.addFriendTransferPopup,
      };
    case actionTypes.LUCKY_MONEY_POPUP:
      return {
        ...state,
        luckyMoneyPopup: action.luckyMoneyPopup,
        isCreate: action.isCreate,
      };
    case actionTypes.ON_UPDATE_BALANCE:
      return {
        ...state,
        balance: action.balance,
      };
    case actionTypes.HIDE_LOADING:
      return { ...state };
    default:
      return { ...state };
  }
};
