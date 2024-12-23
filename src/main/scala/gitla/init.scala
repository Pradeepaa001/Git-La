package gitla

import java.io.{File, PrintWriter}
import scala.io.Source

object GitlaApp {

  def gitInit(repoName: Option[String]): Unit = {
    val repoDir = repoName.getOrElse(new File(".").getName)
    val gitlaDir = new File(s"$repoDir/.gitla")

    if (gitlaDir.exists()) {
      println(s"This directory is already a git repository: $repoDir")
    } else {
      createRepoStructure(repoDir, gitlaDir)
      createConfigFiles(repoDir)
    }
  }

  def createRepoStructure(repoDir: String, gitlaDir: File): Unit = {
    new File(repoDir).mkdirs()
    gitlaDir.mkdir()
    createFile(s"$repoDir/.gitla/HEAD", "ref: refs/heads/master")
    createDir(s"$repoDir/.gitla/objects")
    createDir(s"$repoDir/.gitla/indexObject")
    createDir(s"$repoDir/.gitla/fileObject")
    removeRefsFolder(s"$repoDir/.gitla")
  }

  def createFile(filePath: String, content: String): Unit = {
    val file = new File(filePath)
    val writer = new PrintWriter(file)
    try {
      writer.println(content)
      println(s"Created $filePath.")
    } finally {
      writer.close()
    }
  }

  def createDir(dirPath: String): Unit = {
    val dir = new File(dirPath)
    if (!dir.exists()) {
      dir.mkdir()
      println(s"Created directory: $dirPath.")
    }
  }

  def removeRefsFolder(gitlaDir: String): Unit = {
    val refsDir = new File(s"$gitlaDir/refs")
    if (refsDir.exists()) {
      deleteDirectory(refsDir)
      println("Removed .gitla/refs folder.")
    }
  }

  def deleteDirectory(directory: File): Unit = {
    if (directory.exists()) {
      directory.listFiles().foreach {
        case file if file.isDirectory => deleteDirectory(file)
        case file => file.delete()
      }
      directory.delete()
    }
  }

  def createConfigFiles(repoDir: String): Unit = {
    val coreConfig = Map(
      "repositoryformatversion" -> "0",
      "filemode" -> "true",
      "bare" -> "false"
    )

    val configFile = new File(s"$repoDir/.gitla/config")
    val configWriter = new PrintWriter(configFile)
    try {
      configWriter.println("[core]")
      coreConfig.foreach { case (key, value) =>
        configWriter.println(s"    $key = $value")
      }
      println("Created .gitla/config.")
    } finally {
      configWriter.close()
    }

    createFile(s"$repoDir/.gitla/index", "")
  }
}
