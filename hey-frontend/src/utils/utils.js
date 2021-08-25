export function setUserIdToStorage(userId) {
  localStorage.setItem("userId", userId);
}

export function getUserIdFromStorage() {
  var data = localStorage.getItem("userId");
  return data;
}

export function setJwtToStorage(jwt) {
  localStorage.setItem("jwt", jwt);
}

export function getJwtFromStorage() {
  return localStorage.getItem("jwt");
}

export function setRefreshTokenToStorage(jwt) {
  localStorage.setItem("refreshToken", jwt);
}

export function getRefreshTokenFromStorage() {
  return localStorage.getItem("refreshToken");
}

export function clearStorage() {
  localStorage.clear();
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
