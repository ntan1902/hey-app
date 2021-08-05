create table users
(
    id        char(50),
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
    id               char(50),
    system_name      varchar(50),
    system_key       varchar(255),
    number_of_wallet int,
    primary key (id),
    unique index uq_system_name (system_name ASC)
);

insert into systems(id, system_name, system_key, number_of_wallet)
values ('e8984aa8-b1a5-4c65-8c5e-036851ec780c', 'payment', '$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2', 0),
       ('e8984aa8-b1a5-4c65-8c5e-036851ec781c', 'chat', '$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2', 0),
       ('e8984aa8-b1a5-4c65-8c5e-036851ec782c', 'lucky_money', '$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2', 10),
       ('e8984aa8-b1a5-4c65-8c5e-036851ec783c', 'bank', '$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2', 1);

