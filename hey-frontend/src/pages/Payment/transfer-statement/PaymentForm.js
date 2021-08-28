import React from "react";
import Typography from "@material-ui/core/Typography";
import Grid from "@material-ui/core/Grid";
import TextField from "@material-ui/core/TextField";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";
import Topup from "./topup";
export default function PaymentForm(props) {
  return (
    <React.Fragment>
      {/* <Typography variant="h6" gutterBottom>
        Payment method
      </Typography> */}
      <Topup amount={props.amount} setAmount={props.setAmount} />

      {/* <Grid container spacing={3}></Grid> */}
    </React.Fragment>
  );
}
