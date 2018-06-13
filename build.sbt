name := "location-service"
version := "1.1"
organizationName := "io.codeheroes"

scalaVersion := "2.12.3"

enablePlugins(SbtNativePackager)
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

dockerBaseImage := "java:openjdk-8"
daemonUser := "root"
dockerRepository := Some("codeheroes/location-service")

lazy val `location-service` = project.in(file("."))
  .settings(resolvers ++= Dependencies.additionalResolvers)
  .settings(excludeDependencies ++= Dependencies.globalExcludes)
  .settings(libraryDependencies ++= Dependencies.projectDependencies)
