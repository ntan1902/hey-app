drop table if exists received_lucky_moneys;
drop table if exists lucky_moneys;

create table lucky_moneys
(
    id bigint auto_increment,
    uesr_id bigint,
    system_wallet_id bigint,
    session_chat_id bigint,
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
    receiver_id bigint,
    amount bigint,
    createAt timestamp,
    primary key (id)
);

alter table received_lucky_moneys
add constraint fk_receive_lucky_money foreign key (lucky_money_id) references lucky_moneys(id);