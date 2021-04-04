alter table messages
    rename column datetime to date_time;
alter table messages
    alter column date_time type timestamp using date_time::timestamp;
