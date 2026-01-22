drop table if exists `user`;
create table `user`
(
    id                    bigint primary key,
    username              varchar(255) unique,
    password              varchar(255),
    authorities           varchar(255),
    enabled               tinyint(1),
    locked                tinyint(1),
    expire_at             datetime,
    credentials_expire_at datetime
);
