create table messages
(
    id         bigserial    not null
        constraint messages_pkey
            primary key,

    project_id bigint       not null
        constraint fk_messages_project_id
            references projects on update restrict on delete CASCADE,

    user_id    bigint       not null
        constraint fk_messages_user_id
            references users on update restrict on delete CASCADE,

    dateTime   date         not null,
    text       varchar(255) not null,
    file_id    bigint
        constraint fk_messages_file_id
            references files on update restrict on delete CASCADE
);