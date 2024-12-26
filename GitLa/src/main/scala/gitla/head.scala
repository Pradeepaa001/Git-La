package gitla

import java.nio.file.{Files, Paths, StandardOpenOption}
import java.nio.charset.StandardCharsets

object Head {

  private val headFilePath = Paths.get(".gitla/head")

  // Reads the previous hash from the head file, returns `None` if the file doesn't exist or is empty
  def getPrevHash: Option[String] = {
    if (Files.exists(headFilePath)) {
      val content = new String(Files.readAllBytes(headFilePath), StandardCharsets.UTF_8).trim
      if (content.nonEmpty) Some(content) else None
    } else None
  }

  // Updates the current hash in the head file
  def updateCurrHash(hash: String): Unit = {
    Files.write(headFilePath, hash.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  }
}
