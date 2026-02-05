drop table if exists `user`;
create table `user`
(
    id                      bigint primary key,
    username                varchar(255) unique,
    password                varchar(255),
    avatar                  varchar(255),
    authorities             varchar(255),
    enabled                 tinyint(1),
    locked                  tinyint(1),
    expire_time             datetime,
    credentials_expire_time datetime
);
