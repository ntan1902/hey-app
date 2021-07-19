drop table if exists users;
create table users
(
    id        bigint auto_increment,
    wallet_id bigint,
    username  varchar(50),
    password  varchar(255),
    email     varchar(50),
    full_name varchar(50),
    media     varchar(255),
    pin       varchar(6),
    primary key (id),
    unique index uq_username (username ASC),
    unique index uq_email (email ASC)
);