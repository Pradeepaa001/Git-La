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
        throw new IllegalArgumentException(s"File '$filePath' does not exist.")
      }      
    }

    val fileContent = Files.readAllBytes(fileToAdd)
    val newHash = Blob.calculateHash(filePath)

    val addNeeded = indexEntries.get(filePath) match {
        case Some((existingHash, _)) => existingHash != newHash  // Hash mismatch, need to add/update
        case None => true  // File not in index, need to add
    }
    if addNeeded then 
      Blob.createBlob(newHash, fileContent)
      Index.updateIndex(filePath, newHash, "A")
      println(s"File '$filePath' has been staged")
  }

  def gitAddAll(): Unit = {
    val currentDir = Paths.get(".")
    Files.walk(currentDir)
      .filter(path => Files.isRegularFile(path) && !path.startsWith(".gitla"))
      .iterator()
      .asScala
      .foreach { filePath =>
        val relativePath = currentDir.relativize(filePath).toString
        gitAdd(relativePath) // Directly reuse the `gitAdd` function
      }
  }
}
