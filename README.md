# README #
### collectd-warp10 ###

* Collectd writer plugin for Warp 10.
* First release

### Standard - Install ###
  * Install collectd
```
  apt-get install collectd
```
  * Clone the plugin repo
```
  git clone git://github.com/senx/collectd-plugin-warp10.git
```
  * Compile the plugin
```
  cd collectd-warp10
  javac -classpath /usr/share/collectd-core/java/collectd-api.jar org/collectd/java/WriteWarp10.java
```

Insert this into your `collectd.conf` (likely at `/etc/collectd/collectd.conf`):

  * Configure Interval (Time in seconds between 2 values collectd by collectd)
```
  Interval     30
```  
  * Configure Timeout (Time before flushing the buffer)
```
  Timeout         120
```
  * Configure JAVA plugin to push data to Warp 10

```
 LoadPlugin java
  <Plugin java>
    JVMArg "-Djava.class.path=/usr/share/collectd-core/java/collectd-api.jar:/path/to/collectd-plugin-warp10/"
    LoadPlugin "org.collectd.java.WriteWarp10"
    <Plugin "WriteWarp10">
      Server "https://HOST:PORT/api/v0/update" "TOKEN" "testing" 100
    </Plugin>
  </Plugin>
```

* Restart collectd

### JAVA - Install ###
If issues detected with JAVA 

  * Uninstall collectd

  * Clone collectd repo
```
  git clone https://github.com/collectd/collectd.git
```
  * Configure JAVA_HOME environnement variable
```
  export JAVA_HOME=/usr/lib/jvm/java-8-oracle/
```
  * Configure collectd  
```
  ./configure --with-java=$JAVA_HOME
```
  * Install collectd
```
  make
  make install
```
  * Clone the plugin repo
```
  git clone git://github.com/senx/collectd-plugin-warp10.git
```
  * Compile the plugin

  cd collectd-warp10
```
  $JAVA_HOME/bin/javac -classpath /opt/collectd/share/collectd/java/collectd-api.jar org/collectd/java/WriteWarp10.java 
```
### Contacts ###
* contact@senx.io
