#!/bin/bash

export DISPLAY=":99.0"
export DBUS_SESSION_BUS_ADDRESS="/dev/null"

sudo systemctl start xvfb
sleep 10 # give fb some time to start

fluxbox >/dev/null 2>&1 &
