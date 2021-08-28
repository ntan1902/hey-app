import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import Typography from "@material-ui/core/Typography";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import Grid from "@material-ui/core/Grid";
import {
  channingActions,
  currencyToString,
  formatToCurrency,
  currency,
} from "../../../utils";
import { bindPaymentActions } from "../../../actions";
import { connect } from "react-redux";
import { message } from "antd";

const useStyles = makeStyles((theme) => ({
  listItem: {
    padding: theme.spacing(1, 0),
  },
  total: {
    fontWeight: 700,
  },
  title: {
    marginTop: theme.spacing(2),
  },
}));

function Review(props) {
  const classes = useStyles();
  const payments = [
    { name: "Card type", detail: "Visa" },
    { name: "Card holder", detail: props.profile.fullName },
    { name: "Card number", detail: "xxxx-xxxx-xxxx-1234" },
    { name: "Expiry date", detail: "04/2024" },
  ];
  return (
    <React.Fragment>
      <Typography variant="h6" gutterBottom>
        Topup summary
      </Typography>
      <List disablePadding>
        <ListItem className={classes.listItem} key={"1"}>
          <ListItemText primary={"Topup"} secondary={"Topup from BIDV"} />
          <Typography variant="body2">
            {formatToCurrency(props.amount) + currency}
          </Typography>
        </ListItem>
        <ListItem className={classes.listItem}>
          <ListItemText primary="Total" />
          <Typography variant="subtitle1" className={classes.total}>
            {formatToCurrency(props.amount) + currency}
          </Typography>
        </ListItem>
      </List>
      <Grid container spacing={2}>
        <Grid item container direction="column" xs={12} sm={12} lg={12} md={12}>
          <Typography variant="h6" gutterBottom className={classes.title}>
            Payment details
          </Typography>
          <Grid container>
            {payments.map((payment) => (
              <React.Fragment key={payment.name}>
                <Grid item xs={6}>
                  <Typography gutterBottom>{payment.name}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography gutterBottom>{payment.detail}</Typography>
                </Grid>
              </React.Fragment>
            ))}
          </Grid>
        </Grid>
      </Grid>
    </React.Fragment>
  );
}

export default connect(
  (state) => ({
    messageItems: state.chatReducer.messageItems,
    offset: state.paymentReducer.offset,
    profile: state.userReducer.profile,
  }),
  (dispatch) => channingActions({}, dispatch, bindPaymentActions)
)(Review);
