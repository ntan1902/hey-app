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
  number_of_wallet int,
  primary key(id),
  unique index uq_system_name (system_name ASC)
);

insert into systems(id, system_name, system_key, number_of_wallet) values
(1, 'payment', '$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2', 0),
(2, 'chat', '$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2', 0),
(3, 'luckey_money', '$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2', 10),
(4, 'bank', '$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2', 1);
