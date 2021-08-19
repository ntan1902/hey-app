/**
 * Basic Setting Variables Define
 */

export const IS_DEV = process.env.REACT_APP_NODE_ENV === "development";

export const SITE_URL = IS_DEV
  ? "http://localhost:5050/"
  : "https://heyapi.oispyouthunion.vn/";

export const API_AUTH = `auth`;
export const API_PAYMENT = `payment`;
export const API_LUCKY = `lucky`;
export const API_CHAT = `chat`;
export const API_WS = IS_DEV
  ? "ws://localhost:8090"
  : "wss://heyws.oispyouthunion.vn";
