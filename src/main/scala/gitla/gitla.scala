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
      case "config" =>
        if (args.length < 2) {
          ConfigParser.showHelp()
        } else {
          if (args.contains("--global")){
              if (args.length < 3) {
                println("Usage: gitla config --global <view|add|update|create>")
              } else {
                val globalCommand = args(1)
                val globalArgs = args.drop(2) // Drop "config", "--global", and the command
                ConfigParser.handleGlobalConfig(globalCommand, globalArgs)
              }
          }else{
            val repoDir = ".gitla"
            val localCommand = args(1)
            val localArgs = args.drop(2) // Drop "config", "--local", and the command
            ConfigParser.handleLocalConfig(repoDir, localCommand, localArgs)
          }
        }

      case _ =>
        println(s"Unknown command: ${args(0)}")
    }
  }
}