package gitla

import java.io.{File, PrintWriter}
import scala.io.Source
import java.nio.file.{Paths, Files}
import java.io.FileOutputStream


object ConfigParser {

  // Function to get the user-specific Gitla config (global config)
  def getGlobalConfig(): Map[String, String] = {
    val homeDir = System.getProperty("user.home")
    val gitlaConfigFile = new File(s"$homeDir/.gitlaconfig")
    if (gitlaConfigFile.exists()) {
      // If the file exists, read and parse it
      parseToml(gitlaConfigFile)
    } else {
      // If the file doesn't exist, create a new one and prompt the user for information
      createGlobalConfig(gitlaConfigFile)
      Map()
    }
  }

  // Function to create a .gitlaconfig file in the user's home directory
  def createGlobalConfig(file: File): Unit = {
    println("No global config found. Please enter your details.")
    println("Enter your name:")
    val name = scala.io.StdIn.readLine()
    println("Enter your email:")
    val email = scala.io.StdIn.readLine()

    val globalConfigContent =
      s"""
         |[user]
         |    name = "$name"
         |    email = "$email"
      """.stripMargin

    val writer = new PrintWriter(file)
    writer.println(globalConfigContent)
    writer.close()
    println(s"Created global config at ${file.getAbsolutePath}")
  }

  // Function to parse a TOML file into a Map
  def parseToml(file: File): Map[String, String] = {
    val lines = Source.fromFile(file).getLines().toList
    val sections = lines.mkString("\n").split("\\[.*?\\]").drop(1) // Split by sections

    // Extract the key-value pairs
    sections.flatMap { section =>
      val keyValues = section.split("\n").map(_.split("=")).collect {
        case Array(key, value) => key.trim -> value.trim.stripPrefix("\"").stripSuffix("\"")
      }
      keyValues
    }.toMap
  }

  // Function to write the updated global configuration to the .gitla config of the repository
  def updateConfig(repoName: String, repoDir: String): Unit = {
    val homeDir = System.getProperty("user.home")
    val gitlaConfigFile = new File(s"$homeDir/.gitlaconfig")

    // Get the global configuration (user details)
    val globalConfig = getGlobalConfig()

    // Read the local configuration (if exists) and append the user data to it
    val localConfigFile = new File(s"$repoDir/.gitla/config")
    val configWriter = new PrintWriter(localConfigFile)
    try {
      configWriter.println("[core]")
      configWriter.println(s"    repositoryname = \"$repoName\"")

      if (globalConfig.nonEmpty) {
        configWriter.println("[user]")
        globalConfig.foreach { case (key, value) =>
          configWriter.println(s"    $key = \"$value\"")
        }
      }
      println(s"Updated .gitla/config in the repo directory: $repoDir")
    } finally {
      configWriter.close()
    }
  }

  // Function to edit an existing config file by adding a new section
  def addSection(file: File, sectionName: String): Unit = {
    val writer = new PrintWriter(new FileOutputStream(file, true))
    writer.println(s"[$sectionName]")
    writer.close()
  }

  // Function to update an existing section with a new key-value pair
  def updateSection(file: File, sectionName: String, key: String, value: String): Unit = {
    val lines = Source.fromFile(file).getLines().toList
    val updatedLines = lines.map {
      case line if line.startsWith(s"[$sectionName]") =>
        val section = lines.dropWhile(!_.startsWith(s"[$sectionName]")).takeWhile(!_.startsWith("["))
        section.mkString("\n") + s"    $key = \"$value\""
      case other => other
    }
    val writer = new PrintWriter(file)
    updatedLines.foreach(writer.println)
    writer.close()
  }

  // Function to view the contents of the config file
  def viewConfig(file: File): Unit = {
    val lines = Source.fromFile(file).getLines().mkString("\n")
    println(s"Config file contents:\n$lines")
  }

  // Function to show CLI help for config-related commands
  def showHelp(): Unit = {
    println(
      """
        |Commands:
        |  --global: Operates on the global config file in the user's home directory (.gitlaconfig).
        |  --local: Operates on the local config file in the repo's .gitla folder (.gitla/config).
        |
        |Commands with --global:
        |  create: Creates the .gitlaconfig file and prompts for user details.
        |  update: Updates the global config with user details.
        |
        |Commands with --local:
        |  view: Views the contents of the local repo config.
        |  add: Adds a section to the local config.
        |  update: Updates a section in the local config.
        """.stripMargin
    )
  }
}
