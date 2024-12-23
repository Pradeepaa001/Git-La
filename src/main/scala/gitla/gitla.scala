package gitla

object Gitla {
  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      println("No command provided!")
      return
    }

    args(0) match {
      case "init" => 
        println("Running init command")
        GitlaApp.gitInit(args.tail.headOption)
      case "add" =>
        if (args.length < 2) {
          println("Usage: gitla add <file>")
        } else {
          Add.gitAdd(args(1))
        }
      case _ =>
        println(s"Unknown command: ${args(0)}")
    }
  }
}