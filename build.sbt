name := "Scit"

version := "0.1"

scalaVersion := "3.6.2"

lazy val root = (project in file("."))
  .enablePlugins(AssemblyPlugin)
  .settings(
    assembly / mainClass := Some("gitla.Gitla"),
    assembly / assemblyJarName := "gitla.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )