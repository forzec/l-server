#!/bin/bash
while :;
do
	java -server -Dfile.encoding=UTF-8 -cp config:./* org.mmocore.gameserver.GameServer > log/stdout.log 2>&1

	[ $? -ne 2 ] && break
	sleep 30;
done