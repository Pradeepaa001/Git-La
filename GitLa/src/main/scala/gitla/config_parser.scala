package gitla

import java.io.{File, PrintWriter, FileOutputStream}
import scala.io.Source

object ConfigParser {

  private def globalConfigFilePath: String = s"${System.getProperty("user.home")}/.gitlaconfig"

  private def createFileIfAbsent(file: File, content: String): Unit = {
    if (!file.exists()) {
      val writer = new PrintWriter(file)
      try {
        writer.println(content)
        println(s"Created new file at ${file.getAbsolutePath}")
      } finally {
        writer.close()
      }
    }
  }

  private def writeToFile(file: File, content: String): Unit = {
    val writer = new PrintWriter(file)
    try writer.print(content)
    finally writer.close()
  }

  private def appendToFile(file: File, content: String): Unit = {
    val writer = new PrintWriter(new FileOutputStream(file, true))
    try writer.println(content)
    finally writer.close()
  }

  private def readFile(file: File): List[String] = {
    if (file.exists()) Source.fromFile(file).getLines().toList else Nil
  }

  private def parseToml(lines: List[String]): Map[String, String] = {
    lines.mkString("\n").split("\\[.*?\\]").drop(1).flatMap { section =>
      section.split("\n").map(_.split("=")).collect {
        case Array(key, value) => key.trim -> value.trim.stripPrefix("\"").stripSuffix("\"")
      }
    }.toMap
  }

  def getGlobalConfig(): Map[String, String] = {
    val globalFile = new File(globalConfigFilePath)
    if (globalFile.exists()) {
      parseToml(readFile(globalFile))
    } else {
      println("Global Config not found. Creating a new one.")
      createGlobalConfig(globalFile)
    }
  }

  def createGlobalConfig(file: File): Map[String, String] = {
    println("Creating a new global config. Please enter your details.")
    val name = promptInput("Enter your name:")
    val email = promptInput("Enter your email:")

    val content =
      s"""
         |[user]
         |    name = "$name"
         |    email = "$email"
         |""".stripMargin

    writeToFile(file, content)
    println(s"Global config created at ${file.getAbsolutePath}")
    parseToml(content.split("\n").toList)
  }

  private def promptInput(prompt: String): String = {
    println(prompt)
    scala.io.StdIn.readLine()
  }

  def updateConfig(repoName: String, repoDir: String): Unit = {
    val localFile = new File(s"$repoDir/.gitla/config")
    val globalConfig = getGlobalConfig()

    val content =
      s"""
         |[core]
         |    repositoryname = "$repoName"
         |${formatSection("user", globalConfig)}
         |""".stripMargin

    writeToFile(localFile, content)
    println(s"Updated .gitla/config in repository: $repoDir")
  }

  private def formatSection(section: String, data: Map[String, String]): String = {
    if (data.isEmpty) "" else s"[$section]\n" + data.map { case (k, v) => s"    $k = \"$v\"" }.mkString("\n")
  }

  def addSection(file: File, sectionName: String): Unit = {
    appendToFile(file, s"[$sectionName]")
    println(s"Added section [$sectionName] to ${file.getAbsolutePath}")
  }

  def updateSection(file: File, sectionName: String, key: String, value: String): Unit = {
    val lines = readFile(file)
    val updatedLines = updateOrAppendSection(lines, sectionName, key, value)
    writeToFile(file, updatedLines.mkString("\n"))
    println(s"Updated [$sectionName] in ${file.getAbsolutePath}")
  }

  private def updateOrAppendSection(lines: List[String], section: String, key: String, value: String): List[String] = {
    val inSection = lines.foldLeft((List.empty[String], false)) {
      case ((updated, inSection), line) =>
        if (line.startsWith(s"[$section]")) (updated :+ line, true)
        else if (inSection && line.startsWith("[") && !line.startsWith(s"[$section]")) (updated :+ s"    $key = \"$value\"", false)
        else if (inSection && line.startsWith(s"    $key =")) (updated :+ s"    $key = \"$value\"", true)
        else (updated :+ line, inSection)
    }
    if (!inSection._2) inSection._1 :+ s"[$section]\n    $key = \"$value\"" else inSection._1
  }

  def viewConfig(file: File): Unit = {
    if (file.exists()) {
      println(s"Contents of ${file.getAbsolutePath}:")
      readFile(file).foreach(println)
    } else {
      println(s"Config file not found: ${file.getAbsolutePath}")
    }
  }

  def handleGlobalConfig(command: String, args: Array[String]): Unit = {
    val globalFile = new File(globalConfigFilePath)

    command match {
      case "view" => viewConfig(globalFile)
      case "add" => validateArgs(args, 1) { addSection(globalFile, args(0)) }
      case "update" => validateArgs(args, 3) { updateSection(globalFile, args(0), args(1), args(2)) }
      case "create" => createGlobalConfig(globalFile)
      case _ => println(s"Unknown global config command: $command")
    }
  }

  def handleLocalConfig(repoDir: String, command: String, args: Array[String]): Unit = {
    val localFile = new File(s"$repoDir/config")

    command match {
      case "view" => viewConfig(localFile)
      case "add" => validateArgs(args, 1) { addSection(localFile, args(0)) }
      case "update" => validateArgs(args, 3) { updateSection(localFile, args(0), args(1), args(2)) }
      case _ => println(s"Unknown local config command: $command")
    }
  }

  private def validateArgs(args: Array[String], expected: Int)(action: => Unit): Unit = {
    if (args.length < expected) println(s"Invalid number of arguments. Expected $expected.") else action
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
        |  gitla config --global view
        |  gitla config --local add user
        |  gitla config --global update user name "GitlaUser"
        |""".stripMargin
    )
  }
}

