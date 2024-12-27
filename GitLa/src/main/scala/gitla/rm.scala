package gitla

import java.nio.file.{Files, Paths}

object Remove {
  def gitRemove(filePath: String): Unit = {
    val path = Paths.get(filePath)
    val indexEntries = Index.readIndex()

    if (Files.notExists(path)) {
      Messages.raiseError(s"Error: File $filePath does not exist!")
      return
    }

    if (!indexEntries.contains(filePath)) {
      Messages.raiseError(s"Error: File $filePath is not tracked!")
      return
    }
    Index.removeFromIndex(filePath)
    Messages.printMsg(s"Removed $filePath from index.")

    try {
      Files.delete(path)
      Messages.printMsg(s"Deleted $filePath from working directory.")
    } catch {
      case e: Exception =>
        Messages.raiseError(s"Error: Could not delete $filePath. ${e.getMessage}")
    }
  }

  def gitRemoveCached(filePath: String): Unit = {
    val indexEntries = Index.readIndex()

    if (!indexEntries.contains(filePath)) {
      Messages.raiseError(s"Error: File $filePath is not tracked!")
      return
    }

    Index.removeFromIndex(filePath)
    Messages.printMsg(s"Removed $filePath from index (cached).")
  }
}
