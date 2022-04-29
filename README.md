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

  void setup() {
    sentuatorName = 'Demo Sentuator'    // name your sentuator
    config.ofs = 0.0                    // setup your configuration parameters
  }

  Measurement measure() {
    // generate a random measurement with the configured offset
    def m = new GenericMeasurement(type: sentuatorName)
    m.x = new Quantity(Math.random() + config.ofs, 'm')
    return m
  }

}
```

Place this agent and `fjage-sentuator.jar` in the classpath, and fire up fjåge to interact with the agent. On the shell, we load the sentuator agent:
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
GenericMeasurement[type:Demo Sentuator x:1.01437940 m]

> mysen.measure()
GenericMeasurement[type:Demo Sentuator x:1.7872058 m]

> mysen.measure()
GenericMeasurement[type:Demo Sentuator x:1.0283573 m]

> mysen.measure().x
1.8090699

> mysen.measure().x
1.395348

> mysen.actuate(2)   // we have not defined an actuate() method, so this should fail
REFUSE

> subscribe(topic(mysen))
> mysen.poll = 100   // get us a measurement every 100 ms
100
mysen >> GenericMeasurement:INFORM[time:1564865016105 x:2.6327257]
mysen >> GenericMeasurement:INFORM[time:1564865016207 x:2.7943153]
mysen >> GenericMeasurement:INFORM[time:1564865016307 x:2.8590062]
mysen >> GenericMeasurement:INFORM[time:1564865018704 x:2.7272174]

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
      <version>1.3.0</version>
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
