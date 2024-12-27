package gitla

class GitlaException(message: String) extends RuntimeException(message)

object Messages {
  
  def printMsg(message: String): Unit = {
    println(message) 
  }

  def raiseError(errorMessage: String): Nothing = {
    throw new GitlaException(errorMessage)
  }
}