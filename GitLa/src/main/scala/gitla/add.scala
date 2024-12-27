package gitla

import java.nio.file.{Files, Paths}
import scala.collection.JavaConverters.asScalaIteratorConverter

object Add {

  def gitAdd(filePath: String): Unit = {

    val fileToAdd = Paths.get(filePath)
    val indexEntries = Utils.readIndex()

    if (!Files.exists(fileToAdd)) {
      if (indexEntries.contains(filePath)) {
        Utils.removeFromIndex(filePath)
      } else {
        Messages.raiseError(s"File '$filePath' does not exist.")
      }      
    }

    val fileContent = Files.readAllBytes(fileToAdd)
    val newHash = Utils.calculateHash(filePath)
    val destFile = "fileObject"
    indexEntries.get(filePath) match {
      case Some((existingHash, _)) =>
        if (existingHash != newHash) {
          Utils.updateIndex(filePath, newHash, "M")
          Utils.createBlob(newHash, fileContent,destFile)
          Messages.printMsg(s"Modified '$filePath' in the index.")
        }
      case None =>
        Utils.updateIndex(filePath, newHash, "A")
        Utils.createBlob(newHash, fileContent,destFile)
        Messages.printMsg(s"Added '$filePath' to the index.")
    }
  }

  def gitAddAll(): Unit = {
    val currentDir = Paths.get(".")
    Files.walk(currentDir)
      .filter(path => Files.isRegularFile(path) && !path.toString.contains(".gitla"))
      .iterator()
      .asScala
      .foreach { filePath =>
        val relativePath = currentDir.relativize(filePath).toString
        gitAdd(relativePath)
      }
  }
}
