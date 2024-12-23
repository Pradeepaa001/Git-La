package gitla

import java.io.{File, PrintWriter}
import scala.io.Source
import java.nio.file.{Paths, Files}
import java.io.FileOutputStream


object ConfigParser {

  private def globalConfigFilePath: String = s"${System.getProperty("user.home")}/.gitlaconfig"
  def getGlobalConfig(): Map[String, String] = {
    val homeDir = System.getProperty("user.home")
    val gitlaConfigFile = new File(s"$homeDir/.gitlaconfig")
    if (gitlaConfigFile.exists()) {
      // If the file exists, read and parse it
      parseToml(gitlaConfigFile)
    } else {
      // If the file doesn't exist, create a new one and prompt the user for information
      println("Global Config not found")
      createGlobalConfig(gitlaConfigFile)
    }
  }

  def createGlobalConfig(file: File): Map[String, String] = {
    println("Creating a new global config. Please enter your details.")
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
    parseToml(file)
  }

  def parseToml(file: File): Map[String, String] = {
    val lines = Source.fromFile(file).getLines().toList
    val sections = lines.mkString("\n").split("\\[.*?\\]").drop(1) // Split by sections

    sections.flatMap { section =>
      val keyValues = section.split("\n").map(_.split("=")).collect {
        case Array(key, value) => key.trim -> value.trim.stripPrefix("\"").stripSuffix("\"")
      }
      keyValues
    }.toMap
  }

  def updateConfig(repoName: String, repoDir: String): Unit = {
    val homeDir = System.getProperty("user.home")
    val gitlaConfigFile = new File(s"$homeDir/.gitlaconfig")

    val globalConfig = getGlobalConfig()

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

  def addSection(file: File, sectionName: String): Unit = {
    if (!file.exists()) {
      println(s"Config file not found: ${file.getAbsolutePath}")
      return
    }
    val writer = new PrintWriter(new FileOutputStream(file, true))
    try {
      writer.println(s"[$sectionName]")
      println(s"Added section [$sectionName] to ${file.getAbsolutePath}")
    } finally {
      writer.close()
    }
  }

  def updateSection(file: File, sectionName: String, key: String, value: String): Unit = {
    if (!file.exists()) {
      println(s"Config file not found: ${file.getAbsolutePath}")
      return
    }

    val lines = Source.fromFile(file).getLines().toList
    val updatedLines = if (lines.exists(_.startsWith(s"[$sectionName]"))) {
      val inSection = lines.foldLeft((List.empty[String], false)) {
        case ((updated, inSection), line) =>
          if (line.startsWith(s"[$sectionName]")) (updated :+ line, true)
          else if (inSection && line.startsWith("[") && !line.startsWith(s"[$sectionName]")) (updated :+ s"    $key = \"$value\"", false)
          else if (inSection && line.startsWith(s"    $key =")) (updated :+ s"    $key = \"$value\"", true)
          else (updated :+ line, inSection)
      }._1
      inSection :+ s"    $key = \"$value\""
    } else {
      lines :+ s"[$sectionName]\n    $key = \"$value\""
    }

    val writer = new PrintWriter(file)
    try {
      updatedLines.foreach(writer.println)
      println(s"Updated [$sectionName] in ${file.getAbsolutePath}")
    } finally {
      writer.close()
    }
  }
  def viewConfig(file: File): Unit = {
      if (file.exists()) {
        println(s"Contents of ${file.getAbsolutePath}:")
        Source.fromFile(file).getLines().foreach(println)
      } else {
        println(s"Config file not found: ${file.getAbsolutePath}")
      }
  }
  def handleGlobalConfig(command: String, args: Array[String]): Unit = {
    val globalConfigFile = new File(globalConfigFilePath)

    command match {
      case "view" => viewConfig(globalConfigFile)
      case "add" =>
        if (args.length < 1) {
          println("Usage: gitla config --global add <section-name>")
        } else {
          addSection(globalConfigFile, args(0))
        }
      case "update" =>
        if (args.length < 3) {
          println("Usage: gitla config --global update <section-name> <key> <value>")
        } else {
          updateSection(globalConfigFile, args(0), args(1), args(2))
        }
      case "create" => createGlobalConfig(new File(globalConfigFilePath))
      case _ => println(s"Unknown global config command: $command")
    }
  }

  def handleLocalConfig(repoDir: String, command: String, args: Array[String]): Unit = {
    val localConfigFile = new File(s"$repoDir/config")

    command match {
      case "view" => viewConfig(localConfigFile)
      case "add" =>
        if (args.length < 1) {
          println("Usage: gitla config --local add <section-name>")
        } else {
          addSection(localConfigFile, args(0))
        }
      case "update" =>
        if (args.length < 3) {
          println("Usage: gitla config --local update <section-name> <key> <value>")
        } else {
          updateSection(localConfigFile, args(0), args(1), args(2))
        }
      case _ => println(s"Unknown local config command: $command")
    }
  }

  def showHelp(): Unit = {
    println(
      """
        |Usage: gitla config [--global|--local] <command> [args]
        |
        |Commands:
        |  --global:
        |    view      View the global config file
        |    add       Add a new section to the global config
        |    update    Update a section in the global config
        |    create    Create a new global config file
        |
        |  --local:
        |    view      View the local config file
        |    add       Add a new section to the local config
        |    update    Update a section in the local config
        |
        |Examples:
        |  gitla config view --global
        |  gitla config add user --local
        |  gitla config update user name "RusLa"
        |""".stripMargin
    )
  }
}
