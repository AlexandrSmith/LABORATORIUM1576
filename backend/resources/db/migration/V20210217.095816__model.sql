create table files
(
    id bigserial not null constraint files_pkey primary key,
    filename varchar(255) not null constraint files_filename_unique unique,
    upload_name varchar(255) not null
);

create table projects
(
    id bigserial not null
        constraint projects_pk
            primary key,
    name text not null,
    annotation text,

--  // Тема
    researchTopic text,
--  // Объект
    researchObject text,
--  // Предмет
    researchSubject text,
--  // Цель
    researchPurpose text,
--  // Гипотеза
    researchHypothesis text,
--  // Методы
    researchMethods text,

--  // Результат работы
    workResult text,
--  // Выводы
    conclusion text,
--  // Оценка
    mark int
);

create table projects_files
(
    file_id bigint not null constraint fk_task_files_file_id
        references files on update restrict on delete CASCADE,
    project_id bigint not null constraint fk_projects_files_project_id
        references projects on update restrict on delete CASCADE
);

create table subject_area
(
    id bigserial not null
        constraint subject_area_pk
            primary key,
    name text not null,
    description text not null
);

create table projects_subject_area
(
    project_id bigint constraint fk_projects_subject_area_project_id
        references projects on update restrict on delete CASCADE,
    subject_area_id bigint    not null constraint fk_projects_subject_area_subject_area_id
        references subject_area on update restrict on delete CASCADE
);

create table tasks
(
    id bigserial not null
        constraint tasks_pk
            primary key,
    project_id bigint constraint fk_tasks_project_id
        references projects on update restrict on delete CASCADE,
--     // Задача сделана?
    is_done boolean default false,
--     // Результат
    result text
);

create table task_files
(
    file_id bigint not null constraint fk_task_files_file_id
        references files on update restrict on delete CASCADE,
    task_id bigint not null constraint fk_task_files_task_id
        references tasks on update restrict on delete CASCADE
);

create table members
(
    user_id bigint not null constraint fk_members_user_id
        references users on update restrict on delete CASCADE,
    projects_id bigint not null constraint fk_members_projects_id
        references projects on update restrict on delete CASCADE,
    member_type varchar(32),
    status varchar(32)
);

