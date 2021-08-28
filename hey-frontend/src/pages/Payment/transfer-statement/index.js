import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import CssBaseline from "@material-ui/core/CssBaseline";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Paper from "@material-ui/core/Paper";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import Button from "@material-ui/core/Button";
import Link from "@material-ui/core/Link";
import Typography from "@material-ui/core/Typography";
import AddressForm from "./AddressForm";
import PaymentForm from "./PaymentForm";
import Review from "./Review";
import { channingActions, currencyToString } from "../../../utils";
import { bindPaymentActions } from "../../../actions";
import { connect } from "react-redux";
import { message } from "antd";
import SuccessTopup from "../success";
import { getProfileURL, formatToCurrency, currency } from "../../../utils";
import { getUserIdFromStorage } from "../../../utils/utils";
const useStyles = makeStyles((theme) => ({
  appBar: {
    position: "relative",
  },
  layout: {
    width: "auto",
    borderRadius: "10%",
    marginLeft: theme.spacing(2),
    marginRight: theme.spacing(2),
    [theme.breakpoints.up(600 + theme.spacing(2) * 2)]: {
      width: 600,
      borderRadius: "10%",

      marginLeft: "auto",
      marginRight: "auto",
    },
  },
  paper: {
    marginTop: theme.spacing(3),
    marginBottom: theme.spacing(3),
    padding: theme.spacing(2),
    [theme.breakpoints.up(600 + theme.spacing(3) * 2)]: {
      marginTop: theme.spacing(6),
      marginBottom: theme.spacing(6),
      padding: theme.spacing(3),
    },
  },
  stepper: {
    padding: theme.spacing(3, 0, 5),
  },
  buttons: {
    display: "flex",
    justifyContent: "flex-end",
  },
  button: {
    marginTop: theme.spacing(3),
    marginLeft: theme.spacing(1),
  },
}));

const steps = ["Topup details", "Review your order"];

function Checkout(props) {
  const classes = useStyles();
  const [activeStep, setActiveStep] = React.useState(0);

  const handleNext = () => {
    if (activeStep == 1) {
    }
    setActiveStep(activeStep + 1);
  };

  const getStepContent = (step) => {
    switch (step) {
      case 0:
        return <Review />;
      default:
        throw new Error("Unknown step");
    }
  };

  const handleBack = () => {
    setActiveStep(activeStep - 1);
  };

  if (props.mainScreenData === null) return;

  return (
    <React.Fragment>
      <CssBaseline />
      <main className={classes.layout}>
        <Paper className={classes.paper}>
          <Typography component="h1" variant="h4" align="center">
            Transaction detail
          </Typography>

          <React.Fragment>
            <Review data={props.mainScreenData} />
          </React.Fragment>
        </Paper>
      </main>
    </React.Fragment>
  );
}

export default connect(
  (state) => ({
    messageItems: state.chatReducer.messageItems,
    offset: state.paymentReducer.offset,
    mainScreenData: state.paymentReducer.mainScreenData,
  }),
  (dispatch) => channingActions({}, dispatch, bindPaymentActions)
)(Checkout);
