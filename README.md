# README #
### collectd-warp10 ###

* Collectd writer plugin for Warp10.
* First release
* This plugin is inspired by the plugin to connect OpenTSDB and collectd : https://github.com/auxesis/collectd-opentsdb

### Standard - Install ###
  * Install collectd
```
  apt-get install collectd
```
  * Clone the plugin repo
```
  git clone git://github.com/aurrelhebert/collectd-warp10.git
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
  * Configure JAVA plugin to push data on Warp10

```
 LoadPlugin java
  <Plugin java>
    JVMArg "-Djava.class.path=/usr/share/collectd-core/java/collectd-api.jar:/home/czd/tmp/collectd-writewarp10/"
    LoadPlugin "org.collectd.java.WriteWarp10"
    <Plugin "WriteWarp10">
      Server "https://warp.cityzendata.net/dist/api/v0/update" "hn.iXMFqrZSmQzg8y5Tv2lXsKf.N5ifkitno6L3tfVfGs_MABi_aobQfB7.Qx8z5e1UO_1enRJa0tuSoIp8Pq0QVG4tulOeSAYqwERw5FhEePZXTOnwFAVZZtpYTbeB8UjlBr7qjM5uJcM6WK_Kv7iTvQQChNiSMvQ5SNXvMfRN" "testing" 100
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
  git clone git://github.com/aurrelhebert/collectd-warp10.git
```
  * Compile the plugin

  cd collectd-warp10
```
  $JAVA_HOME/bin/javac -classpath /opt/collectd/share/collectd/java/collectd-api.jar org/collectd/java/WriteWarp10.java 
```
### Contacts ###
* contact@cityzendata.com