// package gitla

// import java.nio.file.{Files, Paths, StandardOpenOption}
// import java.nio.charset.StandardCharsets
// import scala.util.{Try, Success, Failure}
// import scala.jdk.CollectionConverters._

// object Restore {
//   def restore(commitHash: String): Unit = {
//     try {
//       if (commitHash.isEmpty) {
//         println("No commit hash provided. Cannot restore.")
//         return
//       }

//       // Get commit info using your existing Log functionality
//       val commitInfo = Log.readCommit(commitHash)
//       if (commitInfo.isEmpty) {
//         println(s"Commit $commitHash not found.")
//         return
//       }

//       val (_, _, _, indexHash) = commitInfo.get
//       println(s"Restoring to commit: $commitHash")

//       // Read the index content
//       val indexPath = Paths.get(".gitla/indexObject", indexHash.substring(0, 2), indexHash.substring(2))
//       if (!Files.exists(indexPath)) {
//         println(s"Index blob not found at: $indexPath")
//         return
//       }

//       // Clear working directory (except .gitla)
//       clearWorkingDirectory()

//       // Read and parse index
//       val indexEntries = Index.readIndex()
      
//       // Restore files from index
//       restoreFiles(indexEntries)

//       // Update HEAD
//       Head.updateCurrHash(commitHash)
      
//       println("Restore completed successfully")
//     } catch {
//       case e: Exception =>
//         println(s"Error during restore: ${e.getMessage}")
//         e.printStackTrace()
//     }
//   }

//   private def clearWorkingDirectory(): Unit = {
//     val currentDir = Paths.get(".")
//     Try {
//       Files.walk(currentDir).iterator().asScala.toList
//         .filter(path => !path.toString.contains(".gitla") && 
//                        !path.toString.equals(".") &&
//                        Files.isRegularFile(path))
//         .foreach(path => {
//           try {
//             Files.delete(path)
//           } catch {
//             case e: Exception => 
//               println(s"Failed to delete $path: ${e.getMessage}")
//           }
//         })
//     } match {
//       case Success(_) => println("Working directory cleared")
//       case Failure(e) => 
//         println(s"Error clearing working directory: ${e.getMessage}")
//         throw e
//     }
//   }

//   private def restoreFiles(indexEntries: Map[String, (String, String)]): Unit = {
//     indexEntries.foreach { case (filePath, (hash, _)) =>
//       try {
//         val blobPath = Paths.get(".gitla/fileObject", hash.substring(0, 2), hash.substring(2))

//         if (!Files.exists(blobPath)) {
//           println(s"Blob not found for $filePath at $blobPath")
//           return
//         }

//         val content = Files.readAllBytes(blobPath)
//         val targetPath = Paths.get(filePath)

//         // Create parent directories if they don't exist
//         Option(targetPath.getParent).foreach { parent =>
//           if (!Files.exists(parent)) {
//             Try(Files.createDirectories(parent)) match {
//               case Success(_) => println(s"Created directory: $parent")
//               case Failure(e) => 
//                 println(s"Failed to create directory $parent: ${e.getMessage}")
//                 throw e
//             }
//           }
//         }

//         // Write file content
//         Try(Files.write(targetPath, content, StandardOpenOption.CREATE, 
//                        StandardOpenOption.TRUNCATE_EXISTING)) match {
//           case Success(_) => println(s"Restored: $filePath")
//           case Failure(e) => 
//             println(s"Failed to restore $filePath: ${e.getMessage}")
//             throw e
//         }
//       } catch {
//         case e: Exception =>
//           println(s"Error restoring $filePath: ${e.getMessage}")
//           throw e
//       }
//     }
//   }
// }
package gitla

import java.nio.file.{Files, Paths, StandardOpenOption}
import java.nio.charset.StandardCharsets
import scala.util.{Try, Success, Failure}
import scala.jdk.CollectionConverters._

object Restore {
  def restore(commitHash: String): Unit = {
    try {
      println(s"Restore called with commit hash: $commitHash")
      
      if (commitHash.isEmpty) {
        println("No commit hash provided. Cannot restore.")
        return
      }

      // Verify the commit exists
      val commitInfo = Log.readCommit(commitHash)
      if (commitInfo.isEmpty) {
        println(s"Commit $commitHash not found in repository.")
        return
      }

      val (_, _, _, indexHash) = commitInfo.get
      println(s"Found commit. Using index hash: $indexHash")

      // Read the index content
      val indexPath = Paths.get(".gitla/indexObject", indexHash.substring(0, 2), indexHash.substring(2))
      if (!Files.exists(indexPath)) {
        println(s"Index blob not found at: $indexPath")
        return
      }

      val indexContent = new String(Files.readAllBytes(indexPath), StandardCharsets.UTF_8)
      val indexEntries = parseIndexContent(indexContent)

      // Clear working directory (except .gitla)
      clearWorkingDirectory()

      restoreFiles(indexEntries)

      // Restore files
      Index.writeIndex(indexEntries)
      

      // Update HEAD to the specified commit
      Head.updateCurrHash(commitHash)
      
      println(s"Successfully restored to commit: $commitHash")
    } catch {
      case e: Exception =>
        println(s"Error during restore: ${e.getMessage}")
        e.printStackTrace()
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
          throw new IllegalArgumentException(s"Malformed index entry: $line")
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
        println(s"Error clearing working directory: ${e.getMessage}")
        throw e
    }
  }

  private def restoreFiles(indexEntries: Map[String, (String, String)]): Unit = {
    indexEntries.foreach { case (filePath, (hash, _)) =>
      try {
        val blobPath = Paths.get(".gitla/fileObject", hash.substring(0, 2), hash.substring(2))
        if (!Files.exists(blobPath)) {
          println(s"File blob not found at: $blobPath")
          return
        }

        val content = Files.readAllBytes(blobPath)
        val targetPath = Paths.get(filePath)

        // Create parent directories if needed
        Option(targetPath.getParent).foreach { parent =>
          if (!Files.exists(parent)) {
            Files.createDirectories(parent)
          }
        }

        // Write the file
        Files.write(targetPath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
        println(s"Restored file: $filePath")
      } catch {
        case e: Exception =>
          println(s"Error restoring file $filePath: ${e.getMessage}")
          throw e
      }
    }
  }
}