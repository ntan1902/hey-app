
create table lucky_moneys
(
    id bigint auto_increment,
    user_id char(50),
    system_wallet_id bigint,
    session_chat_id varchar(100),
    amount bigint,
    rest_money bigint,
    number_bag int,
    rest_bag int,
    type varchar(30),
    wish_message varchar(255),
    created_at timestamp,
    expired_at timestamp,
    primary key (id)
);

create table received_lucky_moneys
(
    id bigint auto_increment,
    lucky_money_id bigint,
    receiver_id char(50),
    amount bigint,
    created_at timestamp,
    primary key (id)
);
