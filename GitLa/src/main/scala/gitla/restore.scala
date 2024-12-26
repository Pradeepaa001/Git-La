package gitla

import java.nio.file.{Files, Paths}
import scala.util.Try

object Restore {

  def restore(commitHash: Option[String]): Unit = {
    val targetHash = commitHash.orElse(Head.getPrevHash)

    targetHash match {
      case Some(hash) =>
        println(s"Restoring commit: $hash")
        val folderName = hash.substring(0, 2) // First 2 characters
        val fileName = hash.substring(2) // Rest of the hash

        val commitPath = Paths.get(s".gitla/commitObject/$folderName/$fileName")
        if (!Files.exists(commitPath)) {
          println(s"Commit $hash not found at: $commitPath")
          return
        }

        val commitContent = new String(Files.readAllBytes(commitPath), "UTF-8")
        val indexHash = parseIndexHash(commitContent)

        if (indexHash.isEmpty) {
          println(s"Failed to parse index hash from commit $hash.")
          return
        }

        val indexFolderName = indexHash.substring(0, 2)
        val indexFileName = indexHash.substring(2)
        val indexPath = Paths.get(s".gitla/indexObject/$indexFolderName/$indexFileName")
        println(s"Looking for index file at: $indexPath")
        
        if (!Files.exists(indexPath)) {
          println(s"Index object $indexHash not found at $indexPath.")
          return
        }

        val indexContent = new String(Files.readAllBytes(indexPath), "UTF-8")
        val restoredIndex = parseIndex(indexContent)
        Index.writeIndex(restoredIndex)

        restoreWorkingDirectory(restoredIndex)
        println(s"Restored to commit: $hash")

      case None =>
        println("No previous commit found to restore.")
    }
  }

  private def parseIndexHash(commitContent: String): String = {
    val lines = commitContent.split("\n")
    lines
      .find(_.startsWith("Content:"))
      .map(_.stripPrefix("Content: ").trim)
      .getOrElse {
        println("Error: Commit content missing the index hash.")
        ""
      }
  }

  private def parseIndex(indexContent: String): Map[String, (String, String)] = {
    indexContent.linesIterator.map { line =>
      val parts = line.split(" ", 3) // filePath, hash, state
      if (parts.length == 3) {
        (parts(0), (parts(1), parts(2)))
      } else {
        println(s"Error parsing index line: $line")
        ("", ("", "")) // Placeholder for invalid lines
      }
    }.filter { case (path, _) => path.nonEmpty }.toMap
  }

  private def restoreWorkingDirectory(indexEntries: Map[String, (String, String)]): Unit = {
    indexEntries.foreach { case (filePath, (hash, _)) =>
      val blobFolder = hash.substring(0, 2)
      val blobFile = hash.substring(2)
      val blobPath = Paths.get(s".gitla/objects/$blobFolder/$blobFile")

      // Debugging log to print blob path
      println(s"Looking for blob file at: $blobPath")

      if (Files.exists(blobPath)) {
        try {
          val content = Files.readAllBytes(blobPath)
          val restoredPath = Paths.get(filePath)
          Files.createDirectories(restoredPath.getParent) // Ensure the parent directory exists
          Files.write(restoredPath, content) // Write the content to the restored path
          println(s"Restored file: $filePath")
        } catch {
          case e: Exception => println(s"Error restoring file $filePath: ${e.getMessage}")
        }
      } else {
        println(s"Blob $hash for file $filePath is missing at path: $blobPath. Skipping.")
      }
    }
  }
}
