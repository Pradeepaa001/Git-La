package gitla

import java.nio.file.{Files, Paths, StandardOpenOption}
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import scala.util.Try
import java.util.zip.{Deflater, DeflaterOutputStream}
import java.io.{ByteArrayOutputStream, File}
import scala.jdk.CollectionConverters._

object Add {

  def calculateHash(filePath: String): String = {
    val bytes = Files.readAllBytes(Paths.get(filePath))
    val digest = MessageDigest.getInstance("SHA-1")
    digest.digest(bytes).map("%02x".format(_)).mkString
  }

  def createBlob(hash: String, fileContent: Array[Byte]): Unit = {
    val repoPath = Paths.get(".gitla")
    val fileObjectsPath = repoPath.resolve("fileObject")
    val subDir = hash.substring(0, 2)
    val blobFileName = hash.substring(2)

    val subDirPath = fileObjectsPath.resolve(subDir)
    val blobFilePath = subDirPath.resolve(blobFileName)

    if (!Files.exists(subDirPath)) {
      Files.createDirectories(subDirPath)
    }
    val metadata = s"blob ${fileContent.length}\u0000".getBytes("UTF-8")
    val blobContent = metadata ++ fileContent

    val compressedContent = compress(blobContent)

    Files.write(blobFilePath, compressedContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)

    println(s"Blob created at: $blobFilePath")
  }

  def compress(data: Array[Byte]): Array[Byte] = {
    val outputStream = new ByteArrayOutputStream()
    val deflater = new Deflater()
    val deflaterStream = new DeflaterOutputStream(outputStream, deflater)
    deflaterStream.write(data)
    deflaterStream.close()
    outputStream.toByteArray
  }

  def updateIndex(filePath: String, hash: String): Unit = {
    val repoPath = Paths.get(".gitla")
    val indexPath = repoPath.resolve("index.toml")

    val currentLines = Try {
      Files.readAllLines(indexPath, StandardCharsets.UTF_8).toArray.map(_.toString)
    }.getOrElse(Array("[hash]"))

    val (header, keyValueLines) = currentLines.partition(_.startsWith("[hash]"))
    val hashEntries = keyValueLines.map { line =>
      val parts = line.split("=", 2)
      if (parts.length == 2) parts(0).trim -> parts(1).trim.stripPrefix("\"").stripSuffix("\"") else "" -> ""
    }.filter(_._1.nonEmpty).toMap

    val updatedHashEntries = hashEntries + (filePath -> hash)

    val updatedLines = Array("[hash]") ++ updatedHashEntries.map { case (key, value) =>
      s"$key = \"$value\""
    }

    Files.write(indexPath, updatedLines.mkString("\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  }

  def gitAdd(filePath: String): Unit = {
    val repoPath = Paths.get(".gitla")

    if (!Files.exists(repoPath)) {
      println(".gitla directory not found. Are you in a Gitla repository?")
      return
    }

    val fileToAdd = Paths.get(filePath)
    if (!Files.exists(fileToAdd)) {
      println(s"File '$filePath' does not exist.")
      return
    }

    val fileContent = Files.readAllBytes(fileToAdd)
    val hash = calculateHash(filePath)

    createBlob(hash, fileContent)

    updateIndex(filePath, hash)

    println(s"File '$filePath' has been staged with hash: $hash")
  }

  def gitAddAll(): Unit = {
    val repoPath = Paths.get(".gitla")

    if (!Files.exists(repoPath)) {
      println(".gitla directory not found. Are you in a Gitla repository?")
      return
    }

    val currentDir = Paths.get(".")
    val filesToAdd = Files.walk(currentDir)
      .filter(path => Files.isRegularFile(path) && !path.startsWith(".gitla"))
      .iterator()
      .asScala
      .toSeq

    filesToAdd.foreach { filePath =>
      val relativePath = currentDir.relativize(filePath).toString
      val fileContent = Files.readAllBytes(filePath)
      val hash = calculateHash(relativePath)

      createBlob(hash, fileContent)
      updateIndex(relativePath, hash)

      println(s"File '$relativePath' has been staged with hash: $hash")
    }
  }
}
