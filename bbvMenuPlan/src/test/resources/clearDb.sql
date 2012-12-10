delete from week;
delete from weekday;
delete from menu;

drop table week;
drop table weekday;
alter table menu add DAY_ID Bigint;

select * from WEEKDAY;
select * from WEEK;
select * from menu;