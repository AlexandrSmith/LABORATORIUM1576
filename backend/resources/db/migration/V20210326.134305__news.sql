create table news
(
    id        bigserial    not null
        constraint news_pkey
            primary key,


    date_time timestamp    not null,
    text      text         not null,
    title     varchar(255) not null,
    author    varchar(255) not null,
    image_id  bigint
        constraint fk_news_image_id
            references files on update restrict on delete CASCADE
);