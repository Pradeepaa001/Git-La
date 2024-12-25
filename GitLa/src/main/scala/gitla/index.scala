package gitla

import java.nio.file.{Files, Paths, StandardOpenOption}
import java.nio.charset.StandardCharsets
import scala.jdk.CollectionConverters._

object Index {

  private val indexPath = Paths.get(".gitla/index")

  def readIndex(): Map[String, (String, String)] = {
    if (!Files.exists(indexPath)) return Map.empty
    Files.readAllLines(indexPath, StandardCharsets.UTF_8)
      .asScala
      .filter(_.trim.nonEmpty)
      .map { line =>
        val parts = line.split(" ")
        if (parts.length == 3) parts(0) -> (parts(1), parts(2)) else throw new IllegalArgumentException(s"Malformed index entry: $line")
      }
      .toMap
  }

  def writeIndex(entries:Map[String, (String, String)]): Unit = {
    val lines = entries.map { case (filePath, (hash, state)) => s"$filePath $hash $state" }.toSeq
    Files.write(indexPath, lines.mkString("\n").getBytes(StandardCharsets.UTF_8),
      StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  }

  def updateIndex(filePath: String, hash: String, state: String): Unit = {
    val updatedEntries = readIndex() + (filePath -> (hash, state))
    writeIndex(updatedEntries)
  }
  def removeFromIndex(filePath: String): Unit = {
    val indexEntries = readIndex()
    val updatedEntries = indexEntries - filePath
    writeIndex(updatedEntries)
  }

}
