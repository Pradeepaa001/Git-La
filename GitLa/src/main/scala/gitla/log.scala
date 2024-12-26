package gitla

import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets
import scala.util.Try

object Log {

  def displayLog(): Unit = {
    val headHash = Head.getPrevHash.getOrElse("")
    if (headHash.isEmpty) {
      println("You haven't committed yet.")
      return
    }

    var currentHash = headHash

    while (currentHash.nonEmpty) {
      val commitDetails = readCommit(currentHash)
      commitDetails match {
        case Some((prevHash, message, timestamp, contentHash)) =>
          println(s"Commit ID: $currentHash")
          println(s"Commit Message: $message")
          println(s"Timestamp: $timestamp")
          println()

          currentHash = prevHash
        case None =>
          println(s"Error reading commit object for hash: $currentHash")
          return
      }
    }
  }

  def readCommit(commitHash: String): Option[(String, String, String, String)] = {
    val repoPath = Paths.get(".gitla/commitObject")
    val subDir = commitHash.substring(0, 2)
    val fileName = commitHash.substring(2)
    val commitFilePath = repoPath.resolve(subDir).resolve(fileName)

    if (!Files.exists(commitFilePath)) {
      return None
    }
    val fileContent = Files.readAllBytes(commitFilePath)
    //val decompressedContent = Blob.decompress(Files.readAllBytes(commitFilePath))

    // Assuming the commit blob has a fixed format, we parse it line by line.
    val lines = new String(fileContent, StandardCharsets.UTF_8).split("\n")
    val prevHash = lines.find(_.startsWith("Previous Commit:")).map(_.split(":")(1).trim).getOrElse("")
    val message = lines.find(_.startsWith("Commit Message:")).map(_.split(":")(1).trim).getOrElse("")
    val timestamp = lines.find(_.startsWith("Time:")).map(_.split(":", 2)(1).trim).getOrElse("")
    val contentHash = lines.find(_.startsWith("Content:")).map(_.split(":")(1).trim).getOrElse("")

    Some((prevHash, message, timestamp, contentHash))
  }
}
