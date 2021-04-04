create table notifications
(
    id bigserial not null
        constraint notifications_pkey
            primary key,
    user_id bigint not null constraint fk_notifications_user_id
        references users on update restrict on delete CASCADE,
    projects_id bigint not null constraint fk_notifications_projects_id
        references projects on update restrict on delete CASCADE,
    type varchar(255) not null,
    quantity int not null
)