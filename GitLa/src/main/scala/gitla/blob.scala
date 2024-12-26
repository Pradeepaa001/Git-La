package gitla

import java.nio.file.{Files, Paths, StandardOpenOption}
import java.security.MessageDigest
import java.util.zip.{Deflater, DeflaterOutputStream, Inflater, InflaterInputStream}
import java.io.{ByteArrayOutputStream, ByteArrayInputStream}

object Blob {

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

  def compress(data: Array[Byte]): Array[Byte] = {
    val outputStream = new ByteArrayOutputStream()
    val deflaterStream = new DeflaterOutputStream(outputStream, new Deflater())
    deflaterStream.write(data)
    deflaterStream.close()
    outputStream.toByteArray
  }
  def calculateCommitHash(content: String): String = {
    val digest = MessageDigest.getInstance("SHA-1")
    val hashBytes = digest.digest(content.getBytes("UTF-8"))
    hashBytes.map("%02x".format(_)).mkString
  }
  

}
