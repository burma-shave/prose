# --- !Ups

create table "ARTICLES" ("ID" VARCHAR NOT NULL PRIMARY KEY,"AUTHOR_ID" VARCHAR NOT NULL,"TITLE" VARCHAR NOT NULL,"BODY" VARCHAR NOT NULL);
create table "USERS" ("ID" VARCHAR NOT NULL PRIMARY KEY,"FIRSTNAME" VARCHAR NOT NULL,"SURNAME" VARCHAR NOT NULL);

# --- !Downs

drop table "ARTICLES";
drop table "USERS";

