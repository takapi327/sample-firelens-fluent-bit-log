import sbt._

name         := "sample-firelens-fluent-bit-log"
organization := "io.github.takapi327"

ThisBuild / organizationName := "Takahiko Tominaga"

ThisBuild / scalaVersion := "2.13.3"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(DockerPlugin)
  .enablePlugins(EcrPlugin)

resolvers ++= Seq(
  "Takapi snapshots" at "https://s3-ap-northeast-1.amazonaws.com/maven.takapi.net/snapshots"
)

libraryDependencies ++= Seq(
  guice,
  "org.uaparser"        %% "uap-scala"          % "0.13.0",
  //"io.github.takapi327" %% "tracking-log"       % "1.0.0-SNAPSHOT",
  //"io.github.takapi327" %% "slick-db-connector" % "1.0.0-SNAPSHOT",

  //"org.slf4j" % "slf4j-log4j12" % "1.5.2"
  //"org.slf4j" % "slf4j-log4j12" % "2.0.0-alpha5" % Test,
  //"org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.4.1",
  //"org.apache.logging.log4j" % "log4j-api" % "2.4.1",
  //"org.apache.logging.log4j" % "log4j-core" % "2.4.1",
  //"ch.qos.logback" % "logback-core" % "1.3.0-alpha10",
  //"ch.qos.logback" % "logback-classic" % "1.3.0-alpha10",
  //"org.slf4j" % "slf4j-simple" % "1.7.26" % Provided
  "net.logstash.logback" % "logstash-logback-encoder" % "6.6"
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

Ecr / region         := Region.getRegion(Regions.AP_NORTHEAST_1)
Ecr / repositoryName := {
  if   (master) { (Docker / packageName).value }
  else          { "stg-" + (Docker / packageName).value }
}
Ecr / repositoryTags   := Seq(version.value, "latest")
Ecr / localDockerImage := (Docker / packageName).value + ":" + (Docker / version).value

/** Setting sbt-release */
import ReleaseTransformations._

releaseVersionBump := sbtrelease.Version.Bump.Bugfix

releaseProcess := {
  if (master) {
    Seq[ReleaseStep](
      inquireVersions,
      runClean,
      setReleaseVersion,
      ReleaseStep(state => Project.extract(state).runTask(Docker / publishLocal, state)._1),
      ReleaseStep(state => Project.extract(state).runTask(Ecr / login, state)._1),
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
