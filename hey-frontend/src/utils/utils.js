export function setUserIdToStorage(userId) {
  sessionStorage.setItem("userId", userId);
}

export function getUserIdFromStorage() {
  var data = sessionStorage.getItem("userId");
  return data;
}

export function setJwtToStorage(jwt) {
  sessionStorage.setItem("jwt", jwt);
}

export function getJwtFromStorage() {
  return sessionStorage.getItem("jwt");
}

export function setRefreshTokenToStorage(jwt) {
  sessionStorage.setItem("refreshToken", jwt);
}

export function getRefreshTokenFromStorage() {
  return sessionStorage.getItem("refreshToken");
}

export function clearStorage() {
  sessionStorage.clear();
}

export function isAuthenticated() {
  var jwt = getJwtFromStorage();
  return isEmptyString(jwt);
}

export function isEmptyString(prop) {
  if (prop == null || prop == "") {
    return true;
  } else {
    return false;
  }
}
