@echo off
:start
echo Starting GameServer.
echo.

java -server -Dfile.encoding=UTF-8 -Xmx1G -cp config;./* org.mmocore.gameserver.GameServer

REM Debug ...
REM java -Dfile.encoding=UTF-8 -cp config;./* -Xmx1G -Xnoclassgc -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=7456 org.mmocore.gameserver.GameServer

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Server restarted ...
echo.
goto start
:error
echo.
echo Server terminated abnormaly ...
echo.
:end
echo.
echo Server terminated ...
echo.

pause
