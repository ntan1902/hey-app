import * as actionTypes from "../actions/actionTypes";

const initialState = {
  layoutType: "",
};

export default (state = initialState, action = {}) => {
  switch (action.type) {
    case actionTypes.ON_SHOW:
      return {
        ...state,
        layoutType: action.layoutType,
      };
    case actionTypes.HIDE_LOADING:
      return { ...initialState };
    default:
      return { ...initialState };
  }
};
