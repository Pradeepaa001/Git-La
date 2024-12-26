package gitla

//
//gitla rm example.txt
//gitla rm --cached example.txt

import java.nio.file.{Files, Paths}

object Remove {
  def gitRemove(filePath: String): Unit = {
    val path = Paths.get(filePath)
    val indexEntries = Index.readIndex()

    if (Files.notExists(path)) {
      println(s"Error: File $filePath does not exist!")
      return
    }

    if (!indexEntries.contains(filePath)) {
      println(s"Error: File $filePath is not tracked!")
      return
    }
    Index.removeFromIndex(filePath)
    println(s"Removed $filePath from index.")

    try {
      Files.delete(path)
      println(s"Deleted $filePath from working directory.")
    } catch {
      case e: Exception =>
        println(s"Error: Could not delete $filePath. ${e.getMessage}")
    }
  }

  def gitRemoveCached(filePath: String): Unit = {
    val indexEntries = Index.readIndex()

    if (!indexEntries.contains(filePath)) {
      println(s"Error: File $filePath is not tracked!")
      return
    }

    Index.removeFromIndex(filePath)
    println(s"Removed $filePath from index (cached).")
  }
}
