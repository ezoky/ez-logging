import Dependencies.scalaReflectModule
import sbt.Keys.libraryDependencies

Global / onChangedBuildSource := ReloadOnSourceChanges

// used to sign jars
Global / pgpSigningKey := sys.env.get("PGP_SIGNING_KEY")
//Global / pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toCharArray)
//Global / useGpgPinentry := true

name := "ez-logging"
description := "A simple Scala logging toolset that provides with an opinionated API on logging system and some convenient utilities to simplify developer experience."

homepage := Some(url("https://github.com/ezoky/ez-logging"))
scmInfo := Some(ScmInfo(url("https://github.com/ezoky/ez-logging"), "git@github.com:ezoky/ez-logging.git"))
developers := List(Developer("gweinbach", "Grégory Weinbach", "gweinbach@ezoky.com", url("https://github.com/gweinbach")))

lazy val distVersion = sys.props.getOrElse("distVersion", "0.2.0-SNAPSHOT")

ThisBuild / version := distVersion

ThisBuild / scalaVersion := Dependencies.Versions.scala
lazy val supportedScalaVersions = List(
  Dependencies.Versions.scala211,
  Dependencies.Versions.scala212,
  Dependencies.Versions.scala213
)

ThisBuild / scalacOptions ++= Seq(
  "-Yrangepos", // use range positions for syntax trees
  "-language:postfixOps", //  enables postfix operators
  "-language:implicitConversions", // enables defining implicit methods and members
  "-language:existentials", // enables writing existential types
  "-language:reflectiveCalls", // enables reflection
  "-language:higherKinds", // allow higher kinded types without `import scala.language.higherKinds`
  "-encoding", "UTF-8", // source files are in UTF-8
  "-deprecation", // warns about use of deprecated APIs
  "-unchecked", // warns about unchecked type parameters
  "-feature", // warns about misused language features
  "-Xlint", // enables handy linter warnings
//  "-Xfatal-warnings", // turns compiler warnings into errors
)

// Enables SemanticDB compiler for Scalafix
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
// Enables Scalafix complex rules to work with Scala 2.13
ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value)


autoCompilerPlugins := true

addCompilerPlugin(Dependencies.`kind-projector`)
addCompilerPlugin(Dependencies.`better-monadic-for`)



// Define the root project, and make it compile all child projects
lazy val `ez-logging` = project
  .in(file("."))
  .settings(
    Common.defaultSettings ++ Seq(
      crossScalaVersions := supportedScalaVersions,
      libraryDependencies += Dependencies.`slf4j-api`,
      libraryDependencies += scalaReflectModule.value,
      dependencyOverrides += Dependencies.`slf4j-api`, // to force SLF4J version over the one pulled by logback
      libraryDependencies += Dependencies.`logback-classic` % sbt.Test,
      libraryDependencies += Dependencies.Test.scalatest,
      libraryDependencies += Dependencies.Test.`mockito-scala`,
      libraryDependencies += Dependencies.Test.`junit-interface`
    ): _*
  )

