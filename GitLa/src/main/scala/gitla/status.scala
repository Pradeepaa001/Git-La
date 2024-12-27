package gitla

import java.nio.file.{Files, Paths}
import scala.jdk.CollectionConverters._

object Status {

  def gitStatus(): Unit = {
    val indexEntries = Index.readIndex()
    val currentDir = Paths.get(".")
    val untrackedFiles = scala.collection.mutable.ListBuffer[String]()
    val changesToBeStaged = scala.collection.mutable.ListBuffer[String]()
    val changesToBeCommitted = scala.collection.mutable.ListBuffer[String]()

    Files.walk(currentDir)
      .filter(path => Files.isRegularFile(path) && !path.toString.contains(".gitla"))
      .iterator()
      .asScala
      .foreach { filePath =>
        val relativePath = currentDir.relativize(filePath).toString
        if (!indexEntries.contains(relativePath)) {
          untrackedFiles += s"\tnew file: $relativePath"
        } else {
          val (fileHash, state) = indexEntries(relativePath)

          if (Files.exists(filePath)) {
            val newHash = Blob.calculateHash(relativePath)
            if (newHash != fileHash) {
              changesToBeStaged += s"\tmodified: $relativePath"
            }
          }
        }
      }
    indexEntries.foreach { case (filePath, (fileHash, state)) =>
      if (Files.notExists(Paths.get(filePath))) {
        changesToBeStaged += s"\tdeleted: $filePath"
      }
    }

    indexEntries.foreach { case (filePath, (fileHash, state)) =>
       if (Files.exists(Paths.get(filePath))) {
        if (state == "A") {
          changesToBeCommitted += s"\tnew file: $filePath"
        }
        if (state == "M") {
          changesToBeCommitted += s"\tmodified: $filePath"
        }
      }
    }

    if (changesToBeCommitted.nonEmpty) {
      Messages.printMsg("Changes to be committed:")
      changesToBeCommitted.foreach(println)
    }
    if (changesToBeStaged.nonEmpty) {
      Messages.printMsg("Changes to be added:")
      changesToBeStaged.foreach(println)    
    }
    if (untrackedFiles.nonEmpty) {
      Messages.printMsg("Untracked files:")
      untrackedFiles.foreach(println)
    } 

    if (!changesToBeCommitted.nonEmpty && !changesToBeStaged.nonEmpty && !untrackedFiles.nonEmpty) {
      
      Messages.printMsg("You are free from Commitments")
    }
  }
}
