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
    //val deletedFiles = scala.collection.mutable.ListBuffer[String]()

    // Step 1: Find untracked files (files in the directory but not in the index)
    Files.walk(currentDir)
      .filter(path => Files.isRegularFile(path) && !path.startsWith(".gitla"))
      .iterator()
      .asScala
      .foreach { filePath =>
        val relativePath = currentDir.relativize(filePath).toString
        if (!indexEntries.contains(relativePath)) {
          untrackedFiles += s"\tnew file: $relativePath"
        } else {
          // Step 2: Files in index but modified or deleted
          val (fileHash, state) = indexEntries(relativePath)

          if (Files.exists(filePath)) {
            // File exists, check if the hash differs
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

    // Step 3: Files with state "A" or "M" in the index are ready to be committed
    indexEntries.foreach { case (filePath, (fileHash, state)) =>
       if (Files.exists(Paths.get(filePath))) {
        // If the file exists, check its state
        if (state == "A") {
          changesToBeCommitted += s"\tnew file: $filePath"
        }
        if (state == "M") {
          changesToBeCommitted += s"\tmodified: $filePath"
        }
      }
    }

    // Step 4: Output status
    if (changesToBeCommitted.nonEmpty) {
      println("Changes to be committed:")
      changesToBeCommitted.foreach(println)
    }
    if (changesToBeStaged.nonEmpty) {
      println("Changes to be added:")
      changesToBeStaged.foreach(println)    
    }
    if (untrackedFiles.nonEmpty) {
      println("Untracked files:")
      untrackedFiles.foreach(println)
    } 

    if (!changesToBeCommitted.nonEmpty && !changesToBeStaged.nonEmpty && !untrackedFiles.nonEmpty) {
      
      println("You are free from Commitments")
    }
  }
}
