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
    publishArtifact in Test := false,
    pomIncludeRepository := (_ => false),

    // sonatype credentials
    credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credentials"),
    // GPG key to sign published jars
    //    credentials += Credentials(
//      "GnuPG Key ID",
//      "gpg",
//      "F7701D6ABE8EA22920ACE6D0A983103B964EB5A0", // key identifier
//      "ignored" // this field is ignored; passwords are supplied by pinentry
//    ),

    scalaReflectModule := Dependencies.`scala-reflect`(scalaVersion.value),

    warnOnUnusedImportsOption := computeWarnOnUnusedImportsVersion(scalaVersion.value),
    scalacOptions += warnOnUnusedImportsOption.value
  )
}
