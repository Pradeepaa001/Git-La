package gitla

import java.nio.file.{Files, Paths, StandardOpenOption}
import java.security.MessageDigest
import java.nio.charset.StandardCharsets
import scala.jdk.CollectionConverters._

object Utils:

  val headFilePath = Paths.get(".gitla/head")
  val indexPath = Paths.get(".gitla/index")


  def calculateHash(filePath: String): String = {
    val bytes = Files.readAllBytes(Paths.get(filePath))
    val digest = MessageDigest.getInstance("SHA-1")
    digest.digest(bytes).map("%02x".format(_)).mkString
  }

  def createBlob(hash: String, fileContent: Array[Byte], destination: String): Unit = {
    val repoPath = Paths.get(".gitla")
    val fileObjectsPath = repoPath.resolve(destination)
    val subDir = hash.substring(0, 2)
    val blobFileName = hash.substring(2)

    val subDirPath = fileObjectsPath.resolve(subDir)
    val blobFilePath = subDirPath.resolve(blobFileName)

    if (!Files.exists(subDirPath)) {
      Files.createDirectories(subDirPath)
    }

    val metadata = s"blob ${fileContent.length}\u0000".getBytes("UTF-8")
    val blobContent = metadata ++ fileContent
    //val compressedContent = compress(blobContent)

    Files.write(blobFilePath, fileContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  }

  // def compress(data: Array[Byte]): Array[Byte] = {
  //   val outputStream = new ByteArrayOutputStream()
  //   val deflaterStream = new DeflaterOutputStream(outputStream, new Deflater())
  //   deflaterStream.write(data)
  //   deflaterStream.close()
  //   outputStream.toByteArray
  // }
  def calculateCommitHash(content: String): String = {
    val digest = MessageDigest.getInstance("SHA-1")
    val hashBytes = digest.digest(content.getBytes("UTF-8"))
    hashBytes.map("%02x".format(_)).mkString
  }

  def readIndex(): Map[String, (String, String)] = {
    if (!Files.exists(indexPath)) return Map.empty
    Files.readAllLines(indexPath, StandardCharsets.UTF_8)
      .asScala
      .filter(_.trim.nonEmpty)
      .map { line =>
        val parts = line.split(" ")
        if (parts.length == 3) parts(0) -> (parts(1), parts(2)) else Messages.raiseError(s"Malformed index entry: $line")
      }
      .toMap
  }

  def writeIndex(entries:Map[String, (String, String)]): Unit = {
    val lines = entries.map { case (filePath, (hash, state)) => s"$filePath $hash $state" }.toSeq
    Files.write(indexPath, lines.mkString("\n").getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  }

  def updateIndex(filePath: String, hash: String, state: String): Unit = {
    val updatedEntries = readIndex() + (filePath -> (hash, state))
    writeIndex(updatedEntries)
  }
  def removeFromIndex(filePath: String): Unit = {
    val indexEntries = readIndex()
    val updatedEntries = indexEntries - filePath
    writeIndex(updatedEntries)
  }
  def isFileInIndex(filePath: String, indexEntries: Map[String, (String, String)]): Boolean = {
    indexEntries.contains(filePath)
  }

  def getPrevCommit: Option[String] = {
    if (Files.exists(headFilePath)) {
      val content = new String(Files.readAllBytes(headFilePath), StandardCharsets.UTF_8).trim
      if (content.nonEmpty) Some(content) else None
    } else None
  }

  def updateHead(hash: String): Unit = {
    Files.write(headFilePath, hash.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  }