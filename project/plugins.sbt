
// SBT 1.4.0 : full dependency tree features as in old sbt-dependency-graph plugin
addDependencyTreePlugin

// Git inside sbt
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.5")

// Scalafix plugin
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.23")

// Scala 3 plugin
addSbtPlugin("ch.epfl.lamp" % "sbt-dotty" % "0.4.4")
