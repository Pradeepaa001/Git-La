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
        if (args.length == 2 && args(1) == ".") {
          Add.gitAddAll()
        } else if (args.length == 2) {
          Add.gitAdd(args(1))
        } else {
          println("Usage: gitla add <file-path> or gitla add .")
        }

      case _ =>
        println(s"Unknown command: ${args(0)}")
    }
  }
}
