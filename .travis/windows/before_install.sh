#!/bin/bash

powershell -ExecutionPolicy Bypass -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-Expression ( Invoke-WebRequest https://raw.githubusercontent.com/shyiko/jabba/master/install.ps1 -UseBasicParsing ).Content"
export PATH="$HOME/.jabba/bin/:$PATH"
jabba ls-remote
jabba install $BUILD_JDK
jabba use $BUILD_JDK
export JAVA_HOME="$HOME/.jabba/jdk/$BUILD_JDK"
export PATH="$JAVA_HOME/bin:$PATH"
