#!/bin/bash -x

sh -e /etc/init.d/xvfb start
sleep 10 # give xvfb some time to start
fluxbox &
sleep 20 # give fluxbox some time to start
