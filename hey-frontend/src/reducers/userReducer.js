import {
  CHANGE_STATUS,
  CHANGE_TAB,
  REGISTER_SUCCEEDED,
  USER_PROFILE,
  SET_PROFILE,
  UPDATE_AVATAR,
  HAS_PIN,
} from "../actions/userAction";

const initialState = {
  user: {},
  activeTabKey: "1",
  userFullName: "",
  userName: "",
  userStatus: "",
  profile: {},
  hasPin: false,
};

export default function reduce(state = initialState, action) {
  switch (action.type) {
    case CHANGE_TAB:
      console.log(action.activeTabKey);
      return {
        ...state,
        activeTabKey: action.activeTabKey.toString(),
      };
    case REGISTER_SUCCEEDED:
      return {
        ...state,
        user: action.user,
        activeTabKey: "1",
      };
    case USER_PROFILE:
      return {
        ...state,
        userFullName: action.userFullName,
        userName: action.userName,
        userStatus: action.userStatus,
      };
    case CHANGE_STATUS:
      return {
        ...state,
        userStatus: action.userStatus,
      };
    case SET_PROFILE:
      return {
        ...state,
        profile: action.profile,
      };
    case UPDATE_AVATAR:
      return {
        ...state,
        profile: {
          ...state.profile,
          avatar: action.avatar,
          miniAvatar: action.miniAvatar,
        },
      };
    case HAS_PIN:
      return {
        ...state,
        hasPin: action.hasPin,
      };
    default:
      return state;
  }
}
