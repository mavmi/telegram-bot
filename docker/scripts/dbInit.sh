#/bin/bash

pg_hba="$PGDATA/pg_hba.conf";

su postgres -c \
  "
    cd $PGDATA;
    initdb;

    pg_ctl -D $PGDATA start;
    psql -c 'create database shakal_bot_enterprises;'
    pg_ctl -D $PGDATA stop;
  ";
