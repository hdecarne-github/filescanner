#!/bin/bash -x

sh -e /etc/init.d/xvfb start
sleep 10 # give xvfb some time to start
metacity --sm-disable --replace 2>&1 &
sleep 10 # give metacity some time to start
