import * as actionTypes from "../actions/actionTypes";

const changeStateDrawerProfile = (isVisible) => {
    return {
        type: actionTypes.CHANGE_STATE_DRAWER_PROFILE,
        isVisible
    }
}

const changeVisibleChangePassword = (isVisible) => {
    return {
        type: actionTypes.VISIBLE_CHANGE_PASSWORD_MODAL,
        isVisible
    }
}
const changeVisibleChangePin = (isVisible) => {
    return {
        type: actionTypes.VISIBLE_CHANGE_PIN_MODAL,
        isVisible
    }
}
const changeVisibleChangeProfile = (isVisible) => {
    return {
        type: actionTypes.VISIBLE_CHANGE_PROFILE_MODAL,
        isVisible
    }
}


export {
    changeStateDrawerProfile,
    changeVisibleChangePin,
    changeVisibleChangePassword,
    changeVisibleChangeProfile
}