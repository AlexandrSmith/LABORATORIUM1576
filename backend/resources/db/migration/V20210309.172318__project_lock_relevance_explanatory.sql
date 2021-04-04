alter table projects
    add column is_locked boolean not null default false;
alter table projects
    add column relevance text not null default '';
alter table projects
    add column explanatory text;

alter table projects
    add column problems text;
alter table projects
    add column product text;