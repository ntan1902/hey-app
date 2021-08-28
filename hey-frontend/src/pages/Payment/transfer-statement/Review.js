import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import Typography from "@material-ui/core/Typography";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import Grid from "@material-ui/core/Grid";
import { channingActions, currencyToString } from "../../../utils";
import { bindPaymentActions } from "../../../actions";
import { connect } from "react-redux";
import { message } from "antd";
import { getProfileURL, formatToCurrency, currency } from "../../../utils";
import { getUserIdFromStorage } from "../../../utils/utils";
import moment from "moment";

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

  const item = props.data;
  let title,
    description,
    amount,
    isMinus = false,
    imgUrl;
  if (item.transferType == "topup") {
    title = "Topup to Hey Pay account";
    description = item.createdAt;
    amount = "+" + formatToCurrency(item.amount) + currency;
    imgUrl =
      "https://banner2.cleanpng.com/20210109/kbt/transparent-payment-icon-top-up-icon-more-icon-5ffa2fb9c84f26.4451108616102317378205.jpg";
  } else if (item.transferType == "transfer") {
    if (item.source.systemName) {
      title = "Receive lucky money";
      amount = "+" + formatToCurrency(item.amount) + currency;
      imgUrl =
        "https://image.winudf.com/v2/image/Y29tLmVhcm4ubHVja3ltb25leV9pY29uX3RtNTRjbTls/icon.png?w=&fakeurl=1";
    } else if (item.target.systemName) {
      title = "Create lucky money";
      amount = "-" + formatToCurrency(item.amount) + currency;
      isMinus = true;
      imgUrl =
        "https://image.winudf.com/v2/image/Y29tLmVhcm4ubHVja3ltb25leV9pY29uX3RtNTRjbTls/icon.png?w=&fakeurl=1";
    } else if (item.source.id != getUserIdFromStorage()) {
      title = "Receive money from " + item.source.fullName;
      amount = "+" + formatToCurrency(item.amount) + currency;
      imgUrl = getProfileURL(item.source.id);
    } else {
      title = "Send money to " + item.target.fullName;
      amount = "-" + formatToCurrency(item.amount) + currency;
      isMinus = true;
      imgUrl = getProfileURL(item.target.id);
    }
    description = item.createdAt;
  } else {
    if (item.source.id != getUserIdFromStorage()) {
      title = "Receive lucky money";
      amount = "+" + formatToCurrency(item.amount) + currency;
      imgUrl = getProfileURL(item.source.id);
    } else {
      title = "Create lucky money";
      amount = "-" + formatToCurrency(item.amount) + currency;
      isMinus = true;
      imgUrl =
        "https://image.winudf.com/v2/image/Y29tLmVhcm4ubHVja3ltb25leV9pY29uX3RtNTRjbTls/icon.png?w=&fakeurl=1";
    }
    description = item.createdAt;
  }

  const payments = [
    {
      name: "Transaction Code",
      detail: Date.now() + props.data.source.id.slice(0, 5),
    },
    {
      name: "Date",
      detail: moment(description, "YYYY/MM/DD HH:mm").format(
        "HH:mm - DD-MM-YYYY"
      ),
    },
    { name: "Transfer fee", detail: "Free" },
  ];

  console.log("Hiii", title, description, amount, isMinus, imgUrl);
  console.log("Data", props.data);
  return (
    <React.Fragment>
      {/* <Typography variant="h6" gutterBottom>
        {title}
      </Typography> */}
      <List disablePadding>
        <ListItem className={classes.listItem} key={"1"}>
          <ListItemText primary={"Status"} />
          <Typography variant="body2">
            {props.data.status == 2 ? "Success" : "Fail"}
          </Typography>
        </ListItem>
        <ListItem className={classes.listItem}>
          <ListItemText primary="Amount" />
          <Typography variant="subtitle1" className={classes.total}>
            {amount}
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
