package gitla

import java.nio.file.{Files, Paths}
import scala.util.Try

object Log {

  def displayLog(): Unit = {
    val headHash = Head.getPrevHash.getOrElse("")
    if (headHash.isEmpty) {
      Messages.printMsg("You haven't committed yet.")
      return
    }

    var currentHash = headHash

    while (currentHash.nonEmpty) {
      val commitDetails = Commit.readCommit(currentHash)
      commitDetails match {
        case Some((prevHash, message, timestamp, contentHash)) =>
          Messages.printMsg(s"Commit ID: $currentHash")
          Messages.printMsg(s"Commit Message: $message")
          Messages.printMsg(s"Timestamp: $timestamp")
          Messages.printMsg("")

          currentHash = prevHash
        case None =>
          Messages.raiseError(s"Error reading commit object for hash: $currentHash")
          return
      }
    }
  }
}
