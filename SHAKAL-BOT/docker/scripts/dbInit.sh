#/bin/bash

line="host all postgres 172.23.0.0/16 trust";
pg_hba="$PGDATA/pg_hba.conf";

su postgres -c \
  "
    cd $PGDATA;
    initdb;
    echo '$line' >> '$pg_hba';

    pg_ctl -D $PGDATA start;
    psql -c 'create database shakaldb;'
    pg_ctl -D $PGDATA stop;
  ";
