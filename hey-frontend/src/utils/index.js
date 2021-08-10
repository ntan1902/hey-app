import axios from "./custom-axios";
import moment from "moment";
import { API_URL } from "../config/setting";
import DocTien from "./docTien";

export function channingActions(currentActions, dispatch, ...actionGenerators) {
  return actionGenerators.reduce((accActions, actionGenerator) => {
    return {
      ...actionGenerator(accActions, dispatch),
    };
  }, currentActions);
}

export { axios, DocTien };
