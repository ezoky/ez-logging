import com.typesafe.sbt.SbtGit
import sbt.Keys.{publishArtifact, publishTo, _}
import sbt._

object Common {
  val defaultSettings = Seq(
    SbtGit.showCurrentGitBranch,
    conflictManager := ConflictManager.strict,
//    libraryDependencies ++= Dependencies.Test.Minimal,
//    dependencyOverrides ++= Dependencies.Overrides,
    publishTo := {
      if (isSnapshot.value)
        Some(Opts.resolver.sonatypeSnapshots)
      else
        Some(Opts.resolver.sonatypeStaging)
    },
    publishArtifact in Test := false,
    pomIncludeRepository := (_ => false)
  )
}
