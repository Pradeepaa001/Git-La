package gitla

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets
import java.nio.file.StandardOpenOption
import scala.io.Source

object GitlaApp {

  def gitInit(repoName: Option[String]): Unit = {
    val repoDir = repoName.getOrElse(new File(".").getName)
    val gitlaDir = new File(s"$repoDir/.gitla")

    if (gitlaDir.exists()) {
      println(s"This directory is already a git repository: $repoDir")
    } else {
      createRepoStructure(repoDir, gitlaDir)
      ConfigParser.updateConfig(repoName.getOrElse(""), repoDir)
    }
  }

  def createRepoStructure(repoDir: String, gitlaDir: File): Unit = {
    new File(repoDir).mkdirs()
    gitlaDir.mkdir()
    createFile(s"$repoDir/.gitla/HEAD", "ref: refs/heads/main")
    createDir(s"$repoDir/.gitla/indexObject")
    createDir(s"$repoDir/.gitla/fileObject")
    createFile(s"$repoDir/.gitla/index", "")
  }

  def createFile(filePath: String, content: String): Unit = {
    val file = new File(filePath)
    val writer = new PrintWriter(file)
    try {
      writer.println(content)
    } finally {
      writer.close()
    }
  }

  def createDir(dirPath: String): Unit = {
    val dir = new File(dirPath)
    if (!dir.exists()) {
      dir.mkdir()
    }
  }

}
