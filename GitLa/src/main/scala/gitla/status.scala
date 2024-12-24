package gitla

import java.nio.file.{Files, Paths, Path}
import java.security.MessageDigest
import scala.jdk.CollectionConverters._
import scala.collection.mutable
import java.io.File
import com.typesafe.config.{Config, ConfigFactory}

object GitlaStatus {

  // Locate .gitla directory
  def findRepoRoot(startPath: Path): Option[Path] = {
    var currentPath = startPath.toAbsolutePath
    while (currentPath != null) {
      if (Files.exists(currentPath.resolve(".gitla"))) {
        return Some(currentPath)
      }
      currentPath = currentPath.getParent
    }
    None
  }

  def hashFile(filePath: Path): String = {
    val content = Files.readAllBytes(filePath)
    val digest = MessageDigest.getInstance("SHA-1")
    digest.digest(content).map("%02x".format(_)).mkString
  }

  def parseIndex(repoDir: Path): Map[String, String] = {
    val indexFile = repoDir.resolve(".gitla/index")
    if (!Files.exists(indexFile)) return Map.empty
    val config: Config = ConfigFactory.parseFile(indexFile.toFile)
    val hashConfig = config.getConfig("hash")
    hashConfig.entrySet().asScala.map { entry =>
      val key = entry.getKey
      val value = hashConfig.getString(key)
      (key, value) // FilePath -> Hash
    }.toMap
  }

  def getWorkingDirectoryFiles(repoDir: Path): List[String] = {
    Files.walk(repoDir)
      .filter(Files.isRegularFile(_))
      .filter(file => !file.startsWith(repoDir.resolve(".gitla"))) // Ignore .gitla files
      .map(repoDir.relativize(_).toString) // Make paths relative to repo
      .iterator()
      .asScala
      .toList
  }

  def status(): Unit = {
    val repoRoot = findRepoRoot(Paths.get("."))
    if (repoRoot.isEmpty) {
      println("Error: No .gitla repository found in this directory or any parent directory.")
      return
    }

    val repoDir = repoRoot.get
    val indexEntries = parseIndex(repoDir)
    val workingFiles = getWorkingDirectoryFiles(repoDir)
    val untracked = mutable.ListBuffer[String]()
    val toBeAdded = mutable.ListBuffer[String]()
    val toBeCommitted = mutable.ListBuffer[String]()

    // Check untracked files
    for (file <- workingFiles) {
      if (!indexEntries.contains(file)) {
        untracked += file
      }
    }

    // Check modified files
    for ((file, oldHash) <- indexEntries) {
      val filePath = repoDir.resolve(file)
      if (workingFiles.contains(file)) {
        val currentHash = hashFile(filePath)
        if (currentHash != oldHash) {
          toBeAdded += file
        }
      }
    }

    // Check for changes to be committed
    val blobDir = repoDir.resolve(".gitla/fileObject")
    for ((file, hash) <- indexEntries) {
      val blobFile = blobDir.resolve(hash)
      if (!Files.exists(blobFile)) {
        toBeCommitted += file
      }
    }

    // Print results
    println("Untracked files:")
    if (untracked.nonEmpty) {      
      untracked.foreach(file => println(s"\t$file"))
    }else{
      println("None")
    }
    println("Changes to be added:")
    if (toBeAdded.nonEmpty) {
      toBeAdded.foreach(file => println(s"\t$file"))
    }else{
      println("None")
    }
    println("Changes to be committed:")
    if (toBeCommitted.nonEmpty) {      
      toBeCommitted.foreach(file => println(s"\t$file"))
    }else{
      println("None")
    }

    if (untracked.isEmpty && toBeAdded.isEmpty && toBeCommitted.isEmpty) {
      println("You have nothing to commit.")
    }
  }
}
