package gitla

import java.nio.file.{Files, Paths}
import java.time.{Instant, ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.nio.charset.StandardCharsets

object Commit {

  def createCommit(message: String): Unit = {
    val indexEntries = Utils.readIndex()

    if (!needCommit(indexEntries)) {
      println("You have no new commitments.")
      return
    }

    val updatedIndex = indexEntries.map { case (filePath, (hash, _)) => filePath -> (hash, "C") }
    Utils.writeIndex(updatedIndex)

    val indexPath = Paths.get(".gitla/index")
    val indexHash = Utils.calculateHash(indexPath.toString)
    val indexContent = Files.readAllBytes(indexPath)
    Utils.createBlob(indexHash, indexContent, "indexObject")

    val prevHash = Utils.getPrevCommit.getOrElse("")
    val timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .format(ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()))
    val commitContent = s"Previous Commit: $prevHash\nCommit Message: $message\nTime: $timestamp\nContent: $indexHash"
    val commitHash = Utils.calculateCommitHash(commitContent)
    Utils.createBlob(commitHash, commitContent.getBytes("UTF-8"), "commitObject")

    Utils.updateHead(commitHash)

    println(s"Commit created with hash: $commitHash")
  }

  private def needCommit(indexEntries: Map[String, (String, String)]): Boolean = {
    indexEntries.values.exists { case (_, state) => state == "A" || state == "M" }
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
    //val decompressedContent = Utils.decompress(Files.readAllBytes(commitFilePath))

    val lines = new String(fileContent, StandardCharsets.UTF_8).split("\n")
    val prevHash = lines.find(_.startsWith("Previous Commit:")).map(_.split(":")(1).trim).getOrElse("")
    val message = lines.find(_.startsWith("Commit Message:")).map(_.split(":")(1).trim).getOrElse("")
    val timestamp = lines.find(_.startsWith("Time:")).map(_.split(":", 2)(1).trim).getOrElse("")
    val contentHash = lines.find(_.startsWith("Content:")).map(_.split(":")(1).trim).getOrElse("")

    Some((prevHash, message, timestamp, contentHash))
  }

}
