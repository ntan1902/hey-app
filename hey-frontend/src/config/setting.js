/**
 * Basic Setting Variables Define
 */

export const IS_DEV = process.env.REACT_APP_NODE_ENV === "development";

export const SITE_NAME = "Bugs";
export const SITE_URL = IS_DEV
  ? "http://localhost:5050/"
  : "http://45.117.169.232:5050/";

// export const SITE_URL = 'https://www.bugs.vn/';
// export const API_URL = 'http://api.dev.oispyouthunion.vn';
export const API_AUTH = `auth`;
export const API_PAYMENT = `payment`;
export const API_LUCKY = `lucky`;
export const API_CHAT = `chat`;
export const API_WS = IS_DEV
  ? "ws://localhost:8090"
  : "ws://45.117.169.232:8090";

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
