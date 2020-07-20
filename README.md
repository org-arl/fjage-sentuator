[![Build Status](https://travis-ci.org/org-arl/fjage-sentuator.svg?branch=master)](https://travis-ci.org/org-arl/fjage-sentuator)

fjåge-sentuator
===============
**Sensor/actuator framework for fjåge**

Introduction
------------

[fjåge](http://github.com/org-arl/fjage) provides a **lightweight** and **easy-to-learn** framework for [agent-oriented software development](http://en.wikipedia.org/wiki/Agent-oriented_programming) in Java and Groovy. The fjåge-sentuator project builds on top of fjåge to provide a development framework for agents providing sensor & actuator services.

Key Features
------------

* Flexible API for sensor and actuator services
* Support for sensor/actuator configuration & health monitoring
* Distributed framework enabling producers and consumers to be on different nodes
* Lightweight, fast and easy to learn
* Agent development in Java or Groovy
* Interactive Groovy shell and scripting
* fjåge provides APIs for access from Java, Groovy, Python, C, Julia, and Javascript, and a JSON-based protocol to interface with external applications

Documentation
-------------

If you are not already familiar with fjåge, [get familiar with it](https://fjage.readthedocs.io/en/latest/quickstart.html) first!

We illustrate how to develop a sentuator driver through a simple example (`MySentuator.groovy`):
```groovy
import org.arl.fjage.sentuator.*

class MySentuator extends Sentuator {

  // define your measurements
  static class MyMeasurement extends Measurement {
    float x
  }

  void setup() {
    sentuatorName = 'Demo Sentuator'    // name your sentuator
    config.ofs = 0.0                    // setup your configuration parameters
  }

  Measurement measure() {
    // generate a random measurement with the configured offset
    return new MyMeasurement(x: Math.random() + config.ofs)
  }

}
```

Place this agent and `fjage-sentuator-1.0.jar` in the classpath, and fire up fjåge to interact with the agent. On the shell, we load the sentuator agent:
```console
> container.add 'mysen', new MySentuator()
> ps
mysen: MySentuator - IDLE
shell: org.arl.fjage.shell.ShellAgent - RUNNING
```
We then setup the environment for easy interaction with sentuators:
```console
> org.arl.fjage.sentuator.GroovyExtensions.enable()
```
and play with our simple demo sentuator:
```console
> mysen
« Demo Sentuator »

[org.arl.fjage.sentuator.ConfigParam]
  ofs ⤇ 0.0

[org.arl.fjage.sentuator.SentuatorParam]
  enable = false
  poll = 0

> mysen.enable = true
true

> mysen.ofs = 1.0
1.0

> mysen
« Demo Sentuator »

[org.arl.fjage.sentuator.ConfigParam]
  ofs ⤇ 1.0

[org.arl.fjage.sentuator.SentuatorParam]
  enable = true
  poll = 0

> mysen.status
OK

> mysen.measure()
MyMeasurement:INFORM[time:1564864732645 x:1.6736608]

> mysen.measure()
MyMeasurement:INFORM[time:1564864733895 x:1.7872058]

> mysen.measure()
MyMeasurement:INFORM[time:1564864734752 x:1.0283573]

> mysen.measure().x
2.8090699

> mysen.measure().x
2.395348

> mysen.actuate(2)   // we have not defined an actuate() method, so this should fail
REFUSE

> subscribe(topic(mysen))
> mysen.poll = 100   // get us a measurement every 100 ms
100
mysen >> MyMeasurement:INFORM[time:1564865016105 x:2.6327257]
mysen >> MyMeasurement:INFORM[time:1564865016207 x:2.7943153]
mysen >> MyMeasurement:INFORM[time:1564865016307 x:2.8590062]
mysen >> MyMeasurement:INFORM[time:1564865018704 x:2.7272174]

> mysen.poll = 0
0
```

For more information on doing cool stuff with fjåge-sentuators, check out the [API documentation](http://org-arl.github.io/fjage-sentuator/).

Support
-------

* [Project Home](http://github.com/org-arl/fjage-sentuator)
* [Issue Tracking](http://github.com/org-arl/fjage-sentuator/issues)

Maven Central dependency
------------------------

    <dependency>
      <groupId>com.github.org-arl</groupId>
      <artifactId>fjage-sentuator</artifactId>
      <version>1.1.0</version>
    </dependency>

Contributing
------------

Contributions are always welcome! Clone, develop and do a pull request!

Try to stick to the coding style already in use in the repository. Additionally, some guidelines:

* [Commit message style](https://github.com/angular/angular.js/blob/master/DEVELOPERS.md#commits)

Building:

* `gradle` to build the jars
* `gradle check` to run all regression tests (automated through Travis CI)
* `gradle upload` to upload jars to Maven staging (requires credentials)
* `gradle groovydoc` to build the Java API documentation

License
-------

fjåge-sentuator is licensed under the Simplified (3-clause) BSD license.
See [LICENSE](http://github.com/org-arl/fjage-sentuator/blob/master/LICENSE) for more details.
