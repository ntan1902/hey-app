create table if not exists transfer_statements
(
    id int
);
create table if not exists wallets
(
    id int
);


alter table transfer_statements
drop foreign key if exists fk_source_of_transfer;
alter table transfer_statements
drop foreign key if exists fk_target_of_transfer;

drop table if exists transfer_statements;
drop table if exists wallets;

create table transfer_statements
(
    id bigint auto_increment,
    transfer_code varchar(100),
    source_id bigint,
    target_id bigint,
    amount int,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status int,
    transfer_fee int,
    transfer_type varchar(100),
    primary key (id)
);

create table wallets
(
    id bigint auto_increment,
    balance int,
    owner_id int,
    ref_from varchar(20),
    primary key (id)
);

alter table transfer_statements
add constraint fk_source_of_transfer foreign key (source_id) references wallets(id);

alter table transfer_statements
add constraint fk_target_of_transfer foreign key (target_id) references wallets(id);
