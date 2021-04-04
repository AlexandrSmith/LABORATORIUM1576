alter table tasks ADD COLUMN title text;
alter table projects ADD COLUMN create_date date;
alter table projects ADD COLUMN learner_id bigint constraint fk_projects_learner_id
    references users on update restrict on delete CASCADE;

