import { axios } from "./custom-axios";
import DocTien from "./docTien";
import { SITE_URL } from "../config/setting";
export function channingActions(currentActions, dispatch, ...actionGenerators) {
  return actionGenerators.reduce((accActions, actionGenerator) => {
    return {
      ...actionGenerator(accActions, dispatch),
    };
  }, currentActions);
}

export function getProfileURL(userId, isBig = false) {
  if (!isBig) userId = userId + "_400";
  return `${SITE_URL}auth/api/v1/users/images/${userId}.png`;
}

const formatToCurrency = (amount) => {
  if (amount === "") return "0";
  return parseInt(amount)
    .toFixed(2)
    .replace(/\d(?=(\d{3})+\.)/g, "$&,")
    .split(".")[0];
};

const currencyToString = (amount) => {
  if (amount === "") return "";
  return amount.replace(/,/g, "");
};

const currency = "vnđ";

export { axios, DocTien, formatToCurrency, currencyToString, currency };
