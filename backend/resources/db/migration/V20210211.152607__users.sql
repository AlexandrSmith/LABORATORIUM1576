create table users
(
    id bigserial not null
        constraint users_pk
            primary key,
    username varchar(255) not null,
    password varchar(255),
    surname varchar(255) not null,
    name varchar(255) not null,
    patronymic varchar(255),
    role varchar(30) not null,
    status varchar(30) default 'Active' not null
);

create unique index users_username_uindex
    on users (username);

insert into users (username, password, surname, name, patronymic, role)
values ('admin@example.com', '$2a$12$Tbf/jdVDeuWLyuZ.6db5BuNCX4VPOgiGdTqeMyc4RRcEUrTCehACO', 'Админов', 'Админ', 'Админович', 'Admin');
