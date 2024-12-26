package gitla

import java.nio.file.{Files, Paths}
import java.time.Instant
import java.security.MessageDigest
import java.time.{Instant, ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter

object Commit {

  def createCommit(message: String): Unit = {
    val indexEntries = Index.readIndex()

    // Step 1: Check if a commit is needed
    if (!needCommit(indexEntries)) {
      println("You have no new commitments.")
      return
    }

    // Step 2: Update all entries to state `C`
    val updatedIndex = indexEntries.map { case (filePath, (hash, _)) => filePath -> (hash, "C") }
    Index.writeIndex(updatedIndex)

    // Step 3: Create a hash for the index file
    val indexPath = Paths.get(".gitla/index")
    val indexHash = Blob.calculateHash(indexPath.toString)
    val indexContent = Files.readAllBytes(indexPath)
    Blob.createBlob(indexHash, indexContent, "indexObject")

    // Step 4: Create the commit object
    val prevHash = Head.getPrevHash.getOrElse("")
    val timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .format(ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()))
    val commitContent = s"Previous Commit: $prevHash\nCommit Message: $message\nTime: $timestamp\nContent: $indexHash"
    val commitHash = Blob.calculateCommitHash(commitContent)
    Blob.createBlob(commitHash, commitContent.getBytes("UTF-8"), "commitObject")

    // Step 5: Update the head file with the new commit hash
    Head.updateCurrHash(commitHash)

    println(s"Commit created with hash: $commitHash")
  }

  private def needCommit(indexEntries: Map[String, (String, String)]): Boolean = {
    indexEntries.values.exists { case (_, state) => state == "A" || state == "M" }
  }

}
