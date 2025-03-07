package gitla

import java.io.{File, PrintWriter}

object GitlaApp {

  def gitInit(repoName: Option[String]): Unit = {
    val repoDir = repoName.getOrElse(new File(".").getName)
    val gitlaDir = new File(s"$repoDir/.gitla")

    if (gitlaDir.exists()) {
      Messages.printMsg(s"This directory is already a git repository: $repoDir")
    } else {
      createRepoStructure(repoDir, gitlaDir)
      ConfigParser.updateConfig(repoName.getOrElse(""), repoDir)
    }
  }

  def createRepoStructure(repoDir: String, gitlaDir: File): Unit = {
    new File(repoDir).mkdirs()
    gitlaDir.mkdir()
    createFile(s"$repoDir/.gitla/head")
    createDir(s"$repoDir/.gitla/indexObject")
    createDir(s"$repoDir/.gitla/fileObject")
    createDir(s"$repoDir/.gitla/commitObject")
    createFile(s"$repoDir/.gitla/index")
  }

  def createFile(filePath: String): Unit = {
    val file = new File(filePath)
    val writer = new PrintWriter(file)
    try {
      writer.print("")
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
