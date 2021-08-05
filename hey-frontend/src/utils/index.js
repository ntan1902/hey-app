import axios from "./custom-axios";
import moment from "moment";
import { API_URL } from "../config/setting";

export function channingActions(currentActions, dispatch, ...actionGenerators) {
  return actionGenerators.reduce((accActions, actionGenerator) => {
    return {
      ...actionGenerator(accActions, dispatch),
    };
  }, currentActions);
}

export { axios };
