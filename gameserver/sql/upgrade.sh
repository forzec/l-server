#!/bin/sh

for sqlfile in upgrade/*.sql
do
        echo Loading $sqlfile ...
        mysql -h localhost -u root --password=lvbnhbq -D dbL2 < $sqlfile
done
