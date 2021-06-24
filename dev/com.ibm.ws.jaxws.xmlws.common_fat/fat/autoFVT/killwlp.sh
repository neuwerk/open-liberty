#!/bin/sh
kill -9 $(ps aux|awk '/ws-server.jar/ {print $2}') $(ps aux|awk '/ws-launch.jar/ {print $2}')