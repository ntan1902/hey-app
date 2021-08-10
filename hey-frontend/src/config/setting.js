/**
 * Basic Setting Variables Define
 */

export const IS_DEV = process.env.REACT_APP_NODE_ENV === "development";

export const SITE_NAME = "Bugs";
export const SITE_URL = IS_DEV
  ? "https://localhost:5050/"
  : "http://103.7.41.159:5050/";

// export const SITE_URL = 'https://www.bugs.vn/';
// export const API_URL = 'http://api.dev.oispyouthunion.vn';
export const API_AUTH = `${SITE_URL}auth`;
export const API_PAYMENT = `${SITE_URL}payment`;
export const API_LUCKY = `${SITE_URL}lucky`;
export const API_CHAT = `${SITE_URL}chat`;
export const API_WS = IS_DEV ? "ws://localhost:8090" : "ws://103.7.41.159:8090";

// export const API_URL = 'http://103.7.41.159:3000/';
// export const AUTH_URL_USER = `${API_URL}api/login/user`;
// export const AUTH_URL_ADMIN = `${API_URL}api/login/admin`;

export const TEST_ID = "Å5d47eae7e95693001bb87397";
export const BUNDLE_ID = "5d4ac413c89214001b880c9e";
export const CAPTCHA_SERVICE = "https://captcha.stdio.vn/";

export const EVENTS_PAGE_SIZE = 24;
export const NEWS_PAGE_SIZE = 24;

export const ROLES = {
  ADMIN: "admin",
  SUPERADMIN: "superadmin",
  USER: "user",
};
