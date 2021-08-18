import * as actionTypes from "../actions/actionTypes";

let initialState = {
    visibleProfile: false,
    visibleChangePassword: false,
    visibleChangeProfile: false,
    visibleChangePin: false
}



function modalReducer(state = initialState, action) {
    switch (action.type) {
        case actionTypes.CHANGE_STATE_DRAWER_PROFILE:
            return {
                ...state,
                visibleProfile: action.isVisible,
            }
        case actionTypes.VISIBLE_CHANGE_PASSWORD_MODAL:
            return {
                ...state,
                visibleChangePassword: action.isVisible,
            }
        case actionTypes.VISIBLE_CHANGE_PIN_MODAL:
            return {
                ...state,
                visibleChangePin: action.isVisible,
            }
        case actionTypes.VISIBLE_CHANGE_PROFILE_MODAL:
            return {
                ...state,
                visibleChangeProfile: action.isVisible,
            }

        default:
            return state;
    }
}

export default modalReducer;