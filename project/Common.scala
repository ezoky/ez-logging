import com.typesafe.sbt.SbtGit
import sbt.Keys.{publishArtifact, publishTo, _}
import sbt._
import Dependencies.scalaReflectModule

object Common {

  val warnOnUnusedImportsOption = settingKey[String]("'Warn on unused imports' scala compiler option")
  
  def computeWarnOnUnusedImportsVersion(scalaVersionValue: String) =
    CrossVersion.partialVersion(scalaVersionValue) match {
      case Some((2, n)) if n <= 11 =>
        "-Ywarn-unused-import"
      case Some((2, 12)) =>
        "-Ywarn-unused:imports"
      case _ =>
        "-Wunused:imports"
    }

  val defaultSettings = Seq(
    SbtGit.showCurrentGitBranch,
    conflictManager := ConflictManager.strict,
    //    libraryDependencies ++= Dependencies.Test.Minimal,
    //    dependencyOverrides ++= Dependencies.Overrides,
    
    organization := "com.ezoky",
    organizationName := "EZOKY",
    organizationHomepage := Some(url("http://ezoky.com/")),
    licenses := Seq("Apache 2.0 License" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),

    publishTo := {
      if (isSnapshot.value)
        Some(Opts.resolver.sonatypeSnapshots)
      else
        Some(Opts.resolver.sonatypeStaging)
    },
    publishMavenStyle := true,
    // sonatype credentials
    credentials += Credentials(
      "Sonatype Nexus Repository Manager",
      "oss.sonatype.org",
      "<sonatype username>",
      "<sonatype password>"
    ),
    publishArtifact in Test := false,
    pomIncludeRepository := (_ => false),

    scalaReflectModule := Dependencies.`scala-reflect`(scalaVersion.value),

    warnOnUnusedImportsOption := computeWarnOnUnusedImportsVersion(scalaVersion.value),
    scalacOptions += warnOnUnusedImportsOption.value
  )
}
