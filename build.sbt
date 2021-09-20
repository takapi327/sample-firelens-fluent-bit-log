import sbt._

name         := "sample-firelens-fluent-bit-log"
organization := "io.github.takapi327"

ThisBuild / organizationName := "Takahiko Tominaga"

ThisBuild / scalaVersion := "2.13.3"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(DockerPlugin)
  .enablePlugins(EcrPlugin)

libraryDependencies ++= Seq(
  guice,
  "org.uaparser" %% "uap-scala" % "0.13.0",
)

scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Ywarn-dead-code",
  "-Ymacro-annotations"
)

javaOptions ++= Seq(
  "-Dconfig.file=conf/env.dev/application.conf",
  "-Dlogger.file=conf/env.dev/logback.xml"
)

Universal / javaOptions ++= Seq(
  "-Dpidfile.path=/dev/null"
)

run / fork := true

/**
 * Build mode
 */
import scala.sys.process._
val branch  = ("git branch".lineStream_!).find(_.head == '*').map(_.drop(2)).getOrElse("")
val master  = branch == "master"
val staging = branch == "staging"

/**
 * Setting for Docker Image
 */
Docker / maintainer         := "t.takapi0327+infra-sample-firelens-fluentbit-log@gmail.com"
dockerBaseImage             := "amazoncorretto:8"
Docker / dockerExposedPorts := Seq(9000, 9000)
Docker / daemonUser         := "daemon"

/** setting AWS Ecr */
import com.amazonaws.regions.{ Region, Regions }

Ecr / region           := Region.getRegion(Regions.AP_NORTHEAST_1)
Ecr / repositoryName   := {
  if   (master)  { (Docker / packageName).value }
  else           { "stg-" + (Docker / packageName).value }
}
Ecr / repositoryTags   := Seq(version.value, "latest")
Ecr / localDockerImage := (Docker / packageName).value + ":" + (Docker / version).value

/** Setting sbt-release */
import ReleaseTransformations._

releaseVersionBump := sbtrelease.Version.Bump.Bugfix

releaseProcess := {
  if (master) {
    Seq[ReleaseStep](
      ReleaseStep(state => Project.extract(state).runTask(Ecr / login, state)._1),
      inquireVersions,
      runClean,
      setReleaseVersion,
      ReleaseStep(state => Project.extract(state).runTask(Docker / publishLocal, state)._1),
      ReleaseStep(state => Project.extract(state).runTask(Ecr / push, state)._1),
      commitReleaseVersion,
      tagRelease,
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  } else {
    Seq[ReleaseStep](
      runClean,
      ReleaseStep(state => Project.extract(state).runTask(Docker / publishLocal, state)._1),
      ReleaseStep(state => Project.extract(state).runTask(Ecr / login, state)._1),
      ReleaseStep(state => Project.extract(state).runTask(Ecr / push, state)._1),
    )
  }
}
