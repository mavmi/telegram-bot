create table if not exists "crv"(
    id bigint primary key,
    username varchar,
    passwd varchar
);

create or replace function trg_fnc_crv_before_insert() returns trigger as
$$
begin
    update "crv"
    set passwd = new.passwd
    where id = new.id;
    return null;
end;
$$ language plpgsql;

drop trigger if exists trg_fnc_crv_before_insert on "crv";
create trigger trg_fnc_crv_before_insert
before insert on "crv"
for each row
execute procedure trg_fnc_crv_before_insert();
