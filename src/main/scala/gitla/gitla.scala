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
        GitlaApp.gitInit(args.tail.headOption) // Process the 'trial' argument or other init arguments
      case _ =>
        println(s"Unknown command: ${args(0)}")
    }
  }
}