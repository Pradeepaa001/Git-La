
package gitla

import java.nio.file.{Files, Paths, StandardOpenOption}
import java.nio.charset.StandardCharsets
import scala.util.{Try, Success, Failure}
import scala.jdk.CollectionConverters._
import scala.util.boundary

object JumpTo {
  def jumpto(commitHash: String): Unit = {
    try {
      Messages.printMsg(s"JumpTo called with commit hash: $commitHash")
      
      if (commitHash.isEmpty) {
        Messages.raiseError("No commit hash provided. Cannot jumpto.")
        return
      }

      val commitInfo = Commit.readCommit(commitHash)
      if (commitInfo.isEmpty) {
        Messages.raiseError(s"Commit $commitHash not found in repository.")
        return
      }

      val (_, _, _, indexHash) = commitInfo.get
      Messages.printMsg(s"Found commit. Using index hash: $indexHash")

      val indexPath = Paths.get(".gitla/indexObject", indexHash.substring(0, 2), indexHash.substring(2))
      if (!Files.exists(indexPath)) {
        Messages.raiseError(s"Index blob not found at: $indexPath")
        return
      }

      val indexContent = new String(Files.readAllBytes(indexPath), StandardCharsets.UTF_8)
      val indexEntries = parseIndexContent(indexContent)

      clearWorkingDirectory()

      jumptoFiles(indexEntries)

      Index.writeIndex(indexEntries)      

      Head.updateCurrHash(commitHash)
      
      Messages.printMsg(s"Successfully jumped to commit: $commitHash")
    } catch {
      case e: Exception =>
        Messages.raiseError(s"Error during jumping: ${e.getMessage}")
    }
  }

   private def parseIndexContent(content: String): Map[String, (String, String)] = {
    content.linesIterator
      .filter(_.nonEmpty)
      .map { line =>
        val parts = line.split(" ")
        if (parts.length == 3) {
          (parts(0), (parts(1), parts(2)))
        } else {
          Messages.raiseError(s"Malformed index entry: $line")
        }
      }
      .toMap
  }

  private def clearWorkingDirectory(): Unit = {
    val currentDir = Paths.get(".")
    Try {
      Files.walk(currentDir).iterator().asScala.toList
        .filter(path => 
          !path.toString.contains(".gitla") && 
          !path.toString.equals(".") &&
          Files.isRegularFile(path))
        .foreach(Files.deleteIfExists(_))
    }.recover {
      case e: Exception => 
        Messages.raiseError(s"Error clearing working directory: ${e.getMessage}")
    }
  }

  private def jumptoFiles(indexEntries: Map[String, (String, String)]): Unit = {
    indexEntries.foreach { case (filePath, (hash, _)) =>
      try {
        val blobPath = Paths.get(".gitla/fileObject", hash.substring(0, 2), hash.substring(2))
        if (!Files.exists(blobPath)) {
          Messages.raiseError(s"File blob not found at: $blobPath")
        }

        val content = Files.readAllBytes(blobPath)
        val targetPath = Paths.get(filePath)

        Option(targetPath.getParent).foreach { parent =>
          if (!Files.exists(parent)) {
            Files.createDirectories(parent)
          }
        }

        Files.write(targetPath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
        Messages.printMsg(s"Revived file: $filePath")
      } catch {
        case e: Exception =>
          Messages.raiseError(s"Error reviving file $filePath: ${e.getMessage}")
      }
    }
  }
}