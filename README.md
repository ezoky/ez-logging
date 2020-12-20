# ez-logging [![Build Status](https://travis-ci.com/ezoky/ez-logging.svg?branch=master)](https://travis-ci.com/ezoky/ez-logging)

A **fast**, **easy to use** set of logging tools wrapping [SLF4J](http://www.slf4j.org) in a functional manner.


## Getting ez-logging

Usage with SBT, adding a dependency to the latest version of Scala Logging to your sbt build definition file:

```scala
libraryDependencies += "com.ezoky" %% "ez-logging" % "0.1.0"
```

A compatible logging backend is [Logback](http://logback.qos.ch), add it to your sbt build definition:

```scala
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
```
 