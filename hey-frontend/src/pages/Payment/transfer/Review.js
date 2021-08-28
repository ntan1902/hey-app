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

  return (
    <React.Fragment>
      <Typography variant="h6" gutterBottom>
        {`Transfer to ${props.fullName}`}
      </Typography>
      <List disablePadding>
        <ListItem className={classes.listItem} key={"1"}>
          <ListItemText
            primary={"Transfer"}
            secondary={props.message != "" ? `Message: ${props.message}` : ""}
          />
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
