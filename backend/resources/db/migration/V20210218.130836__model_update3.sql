create table users_subject_areas
(
    user_id bigint not null constraint fk_users_subject_areas_user_id
        references users on update restrict on delete CASCADE,
    subject_area_id bigint not null constraint fk_users_subject_areas_subject_area_id
        references subject_area on update restrict on delete CASCADE
);

alter table projects ADD COLUMN stage int;
