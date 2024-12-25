package gitla

import java.nio.file.{Files, Paths, StandardOpenOption}
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import scala.collection.JavaConverters._

object Commit {

  val commitObjectsPath = Paths.get(".gitla/commitObject")
  val indexPath = Paths.get(".gitla/index.toml")

  case class CommitNode(
    commitHash: String,
    message: String,
    prevCommit: Option[CommitNode],
    nextCommit: Option[CommitNode]
  )

  var head: Option[CommitNode] = None

  def createCommit(message: String): Unit = {
    if (!Files.exists(indexPath)) {
      println("No files staged for commit. Use 'gitla add' to stage files.")
      return
    }

    val indexEntries = Files.readAllLines(indexPath, StandardCharsets.UTF_8).asScala
    val fileHashes = indexEntries.drop(1).map { line =>
      val parts = line.split("=", 2)
      if (parts.length == 2) parts(0).trim -> parts(1).trim.stripPrefix("\"").stripSuffix("\"") else "" -> ""
    }.filter(_._1.nonEmpty).toMap

    if (fileHashes.isEmpty) {
      println("No files staged for commit.")
      return
    }

    val indexHash = fileHashes.map { case (file, hash) => s"$file=$hash" }.mkString("\n")
    val currentIndexHash = hashString(indexHash)

    val commitContent = CommitNode(commitHash = "temp-hash", message, prevCommit = head, nextCommit = None)

    val commitHash = hashString(commitContent.toString)

    val finalCommitContent = commitContent.copy(commitHash = commitHash)

    saveCommitBlob(commitHash, finalCommitContent)

    head = Some(finalCommitContent)

    updateCommitHistory(commitHash)

    println(s"Commit created with hash: $commitHash")
  }

  private def saveCommitBlob(hash: String, commitContent: CommitNode): Unit = {
    val subDir = hash.substring(0, 2)
    val commitFileName = hash.substring(2)
    val subDirPath = commitObjectsPath.resolve(subDir)
    val commitFilePath = subDirPath.resolve(commitFileName)

    if (!Files.exists(subDirPath)) {
      Files.createDirectories(subDirPath)
    }

    val commitBlobContent = s"Message: ${commitContent.message}\nPrevCommitHash: ${commitContent.prevCommit.map(_.commitHash).getOrElse("None")}\n"
    Files.write(
      commitFilePath,
      commitBlobContent.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE,
      StandardOpenOption.TRUNCATE_EXISTING
    )

    println(s"Commit blob saved at: $commitFilePath")
  }

  private def updateCommitHistory(commitHash: String): Unit = {
    val headFilePath = Paths.get(".gitla/commitHistory")
    Files.write(
      headFilePath,
      commitHash.getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE,
      StandardOpenOption.TRUNCATE_EXISTING
    )
  }

  private def hashString(data: String): String = {
    val digest = MessageDigest.getInstance("SHA-1")
    val bytes = digest.digest(data.getBytes(StandardCharsets.UTF_8))
    bytes.map("%02x".format(_)).mkString
  }

  def traverseCommits(): Unit = {
    var currentCommit = head
    while (currentCommit.isDefined) {
      val commit = currentCommit.get
      println(s"Commit Hash: ${commit.commitHash}, Message: ${commit.message}")
      currentCommit = commit.prevCommit
    }
  }
}
