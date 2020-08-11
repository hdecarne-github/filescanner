#!/bin/bash

curl -sL https://github.com/shyiko/jabba/raw/master/install.sh | bash && . ~/.jabba/jabba.sh
jabba ls-remote
jabba install $BUILD_JDK
jabba use $BUILD_JDK
export JAVA_HOME="$HOME/.jabba/jdk/$BUILD_JDK"
export PATH="$JAVA_HOME/bin:$PATH"

sudo -E apt-get update
sudo -E apt-get -yq --no-install-suggests --no-install-recommends install fluxbox libwebkit2gtk-4.0-37
