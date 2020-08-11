#!/bin/bash -x

curl -sL https://github.com/shyiko/jabba/raw/master/install.sh | bash && . ~/.jabba/jabba.sh
jabba ls-remote
jabba install $BUILD_JDK
jabba use $BUILD_JDK
export JAVA_HOME="$HOME/.jabba/jdk/$BUILD_JDK"
export PATH="$JAVA_HOME/bin:$PATH"

sudo apt-get -y install fluxbox
sudo apt-get -y install imagemagick
sudo apt-get -y install libwebkit2gtk
