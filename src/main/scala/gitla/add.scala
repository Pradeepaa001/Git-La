package gitla

import java.nio.file.{Files, Paths, StandardOpenOption}
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import scala.util.{Try, Using}
import java.util.zip.{Deflater, DeflaterOutputStream}
import java.io.{FileOutputStream, ByteArrayOutputStream,File}

object Add {

  // Function to calculate SHA-1 hash of a file's content
  def calculateHash(filePath: String): String = {
    val bytes = Files.readAllBytes(Paths.get(filePath))
    val digest = MessageDigest.getInstance("SHA-1")
    digest.digest(bytes).map("%02x".format(_)).mkString
  }

  // Function to write a blob file to fileobjects directory
  def createBlob(hash: String, fileContent: Array[Byte]): Unit = {
    val repoPath = Paths.get(".gitla")
    val fileObjectsPath = repoPath.resolve("fileObject")
    val subDir = hash.substring(0, 2)
    val blobFileName = hash.substring(2)

    val subDirPath = fileObjectsPath.resolve(subDir)
    val blobFilePath = subDirPath.resolve(blobFileName)

    // Ensure the subdirectory exists
    if (!Files.exists(subDirPath)) {
      Files.createDirectories(subDirPath)
    }
    val metadata = s"blob ${fileContent.length}\u0000".getBytes("UTF-8")
    val blobContent = metadata ++ fileContent

    // Compress the content
    val compressedContent = compress(blobContent)

    // Write the blob file
    Files.write(blobFilePath, compressedContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)

    println(s"Blob created at: $blobFilePath")

    // Write the blob file
  }
  def compress(data: Array[Byte]): Array[Byte] = {
    val outputStream = new ByteArrayOutputStream()
    val deflater = new Deflater()
    val deflaterStream = new DeflaterOutputStream(outputStream, deflater)
    deflaterStream.write(data)
    deflaterStream.close()
    outputStream.toByteArray
    }

  // Function to update the index file
  def updateIndex(filePath: String, hash: String): Unit = {
  val repoPath = Paths.get(".gitla")
  val indexPath = repoPath.resolve("index.toml")

  // Read existing lines or initialize a new TOML file
  val currentLines = Try {
    Files.readAllLines(indexPath, StandardCharsets.UTF_8).toArray.map(_.toString)
  }.getOrElse(Array("[hash]"))

  // Parse the `[hash]` section into a Map
  val (header, keyValueLines) = currentLines.partition(_.startsWith("[hash]"))
  val hashEntries = keyValueLines.map { line =>
    val parts = line.split("=", 2)
    if (parts.length == 2) parts(0).trim -> parts(1).trim.stripPrefix("\"").stripSuffix("\"") else "" -> ""
  }.filter(_._1.nonEmpty).toMap

  // Update or add the new hash entry
  val updatedHashEntries = hashEntries + (filePath -> hash)

  // Rebuild the TOML file content
  val updatedLines = Array("[hash]") ++ updatedHashEntries.map { case (key, value) =>
    s"$key = \"$value\""
  }

  // Write the updated content back to the file
  Files.write(indexPath, updatedLines.mkString("\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
}




  // Main function for the `add` command
  def gitAdd(filePath: String): Unit = {
    val repoPath = Paths.get(".gitla")

    // Ensure .gitla directory exists
    if (!Files.exists(repoPath)) {
      println(".gitla directory not found. Are you in a Gitla repository?")
      return
    }

    val fileToAdd = Paths.get(filePath)
    if (!Files.exists(fileToAdd)) {
      println(s"File '$filePath' does not exist.")
      return
    }

    // Read file content and generate hash
    val fileContent = Files.readAllBytes(fileToAdd)
    val hash = calculateHash(filePath)

    // Create the blob file
    createBlob(hash, fileContent)

    // Update the index
    updateIndex(filePath, hash)

    println(s"File '$filePath' has been staged with hash: $hash")
  }
}
