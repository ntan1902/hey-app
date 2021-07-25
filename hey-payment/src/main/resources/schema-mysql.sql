drop table if exists transfer_statements;
drop table if exists wallets;

create table transfer_statements
(
    id            bigint auto_increment,
    transfer_code varchar(100),
    source_id     bigint,
    target_id     bigint,
    amount        bigint,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status        int,
    transfer_fee  bigint,
    transfer_type varchar(100),
    primary key (id)
);

create table wallets
(
    id       bigint auto_increment,
    balance  bigint,
    owner_id bigint,
    ref_from varchar(20),
    primary key (id)
);

alter table transfer_statements
    add constraint fk_source_of_transfer foreign key (source_id) references wallets (id);

alter table transfer_statements
    add constraint fk_target_of_transfer foreign key (target_id) references wallets (id);

