import DependencyVersions._
import sbt._

object Dependencies {

  val globalExcludes = Seq(
    SbtExclusionRule("log4j"),
    SbtExclusionRule("log4j2"),
    SbtExclusionRule("commons-logging")
  )

  val loggingDependencies = Seq(
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "ch.qos.logback" % "logback-core" % logbackVersion,
    "org.slf4j" % "jcl-over-slf4j" % slf4jVersion,
    "org.slf4j" % "log4j-over-slf4j" % slf4jVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  )

  val akkaDependencies = Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion
  )

  val akkaHttpDependencies = Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "de.heikoseeberger" %% "akka-http-json4s" % akkaJson4sVersion,
    "org.json4s" %% "json4s-native" % json4sVersion,
    "org.json4s" %% "json4s-core" % json4sVersion
  )

  val testDependencies = Seq(
    "org.scalactic" %% "scalactic" % scalaTestVersion % "test",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test"
  )

  val projectDependencies = Seq(
    loggingDependencies,
    akkaDependencies,
    testDependencies,
    akkaHttpDependencies
  ).reduce(_ ++ _)


  val additionalResolvers = Seq(
    "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
  )

}