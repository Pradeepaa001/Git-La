import sbt.Keys._

name := "Gitla"

version := "0.1"

scalaVersion := "3.6.2"

libraryDependencies += "com.typesafe" % "config" % "1.4.2"

enablePlugins(AssemblyPlugin, JavaAppPackaging)

lazy val root = (project in file("."))
  .settings(
    mainClass in Compile:= Some("gitla.Gitla"),
    assembly / mainClass := Some("gitla.Gitla"),
    assembly / assemblyJarName := "gitla.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    },
    packageBin / packageOptions += Package.ManifestAttributes("Main-Class" -> "gitla.Gitla")
  )
