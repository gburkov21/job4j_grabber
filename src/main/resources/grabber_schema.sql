create database grabber_db;

create table post
(
    id      serial primary key,
    name    varchar(255),
    text    varchar,
    link    varchar unique,
    created timestamp
);