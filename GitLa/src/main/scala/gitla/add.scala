package gitla

import java.nio.file.{Files, Paths, StandardOpenOption}
import java.security.MessageDigest
import java.io.ByteArrayOutputStream
import java.util.zip.{Deflater, DeflaterOutputStream}
import scala.jdk.CollectionConverters._

object Add {

  def gitAdd(filePath: String): Unit = {

    val fileToAdd = Paths.get(filePath)
    val indexEntries = Index.readIndex()

    if (!Files.exists(fileToAdd)) {
      if (indexEntries.contains(filePath)) {
        Index.removeFromIndex(filePath)
      } else {
        Messages.raiseError(s"File '$filePath' does not exist.")
      }      
    }

    val fileContent = Files.readAllBytes(fileToAdd)
    val newHash = Blob.calculateHash(filePath)
    val destFile = "fileObject"
    indexEntries.get(filePath) match {
      case Some((existingHash, _)) =>
        if (existingHash != newHash) {
          Index.updateIndex(filePath, newHash, "M")
          Blob.createBlob(newHash, fileContent,destFile)
          Messages.printMsg(s"Modified '$filePath' in the index.")
        }
      case None =>
        Index.updateIndex(filePath, newHash, "A")
        Blob.createBlob(newHash, fileContent,destFile)
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
