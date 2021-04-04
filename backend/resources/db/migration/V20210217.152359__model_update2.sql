alter table members ADD COLUMN id bigserial not null
    constraint members_pk
        primary key;

