create table if not exists "crvProfile"(
    id bigint primary key,
    chatid bigint,
    username varchar,
    firstname varchar,
    lastname varchar
);

create table if not exists request(
    userid bigint,
    message varchar,
    "date" date,
    "time" time,
    constraint fk_request_userid foreign key (userid) references "crvProfile"(id)
);

create or replace function trg_fnc_user_before_insert() returns trigger as
$$
declare
    users int := (select count(*) from "crvProfile" where id = new.id);
begin
    if (users = 1) then
        update "crvProfile"
        set chatid = new.chatid,
            username = new.username,
            firstname = new.firstname,
            lastname = new.lastname
        where id = new.id;
        return null;
    else
        return new;
    end if;
end;
$$ language plpgsql;

drop trigger if exists trg_fnc_user_before_insert on "crvProfile";
create trigger trg_fnc_user_before_insert
before insert on "crvProfile"
for each row
execute procedure trg_fnc_user_before_insert();
