create table users
(
    id        bigint auto_increment,
    wallet_id bigint,
    username  varchar(50),
    password  varchar(255),
    email     varchar(50),
    full_name varchar(50),
    pin       varchar(255),
    primary key (id),
    unique index uq_username (username ASC),
    unique index uq_email (email ASC)
);

create table systems
(
  id bigint auto_increment,
  system_name varchar(50),
  system_key varchar(255),
  primary key(id),
  unique index uq_system_name (system_name ASC)
);