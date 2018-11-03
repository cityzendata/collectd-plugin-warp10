collectd-warp
=================

collectd writer plugin for Warp 10.

Standard - Install
-------------------
  # Install collectd
  apt-get install collectd
  # Clone the plugin repo
  git clone git://github.com/senx/collectd-plugin-warp10.git
  # Compile the plugin
  cd collectd-warp
  javac -classpath /usr/share/collectd-core/java/collectd-api.jar org/collectd/java/WriteWarp.java

Insert this into your `collectd.conf` (likely at `/etc/collectd/collectd.conf`):
  # Configure Interval (Time in seconds between 2 values collectd by collectd)
  Interval     30
  
  # Configure Timeout (Time before flushing the buffer)
  Timeout         120

  # Configure JAVA plugin to push data on Warp
  LoadPlugin java
  <Plugin java>
    JVMArg "-Djava.class.path=/usr/share/collectd-core/java/collectd-api.jar:/path/to/collectd-plugin-warp10/"

    LoadPlugin "org.collectd.java.WriteWarp"
    <Plugin "WriteWarp">
      Server "https://HOST:PORT/api/v0/update" "TOKEN" "testing" 100
    </Plugin>
  </Plugin>

Restart collectd.

If issues detectd with JAVA uninstall collectd

JAVA - Install
--------------
  # Clone collectd repo
  git clone https://github.com/collectd/collectd.git
  # Configure JAVA_HOME environnement variable
  export JAVA_HOME=/usr/lib/jvm/java-8-oracle/
  # Configure collectd  
  ./configure --with-java=$JAVA_HOME
  # Install collectd
  make
  make install
  # Clone the plugin repo
  git clone git://github.com/senx/collectd-plugin-warp10.git
  # Compile the plugin
  cd collectd-warp
  $JAVA_HOME/bin/javac -classpath /opt/collectd/share/collectd/java/collectd-api.jar org/collectd/java/WriteWarp.java 
  
  Start collect
  /opt/collectd/sbin/collectd

