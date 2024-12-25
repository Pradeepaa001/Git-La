object Gitla {
  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      println("No command provided!")
      return
    }

    val command = args.headOption.getOrElse("")
    val commandArgs = args.tail

    println(s"Command: $command, Arguments: $commandArgs") // Debugging line

    command match {
      case "init" =>
        println("Running init command")
        GitlaApp.gitInit(commandArgs.headOption)

      case "add" =>
        if (commandArgs.length == 1 && commandArgs(0) == ".") {
          Add.gitAddAll()
        } else if (commandArgs.length == 1) {
          Add.gitAdd(commandArgs(0))
        } else {
          println("Usage: gitla add <file-path> or gitla add .")
        }

      case "config" =>
        if (commandArgs.isEmpty) {
          ConfigParser.showHelp()
        } else {
          if (commandArgs.contains("--global")) {
            if (commandArgs.length < 2) {
              println("Usage: gitla config --global <view|add|update|create>")
            } else {
              val globalCommand = commandArgs(0)
              val globalArgs = commandArgs.drop(1)
              ConfigParser.handleGlobalConfig(globalCommand, globalArgs)
            }
          } else {
            val repoDir = ".gitla"
            val localCommand = commandArgs(0)
            val localArgs = commandArgs.drop(1)
            ConfigParser.handleLocalConfig(repoDir, localCommand, localArgs)
          }
        }

      case "commit" =>
        print("Hello, it is entering the case")
        if (
          commandArgs.isEmpty || commandArgs(
            0
          ) != "-m" || commandArgs.length < 2
        ) {
          println("Usage: gitla commit -m <message>")
        } else {
          val message = commandArgs(1)
          Commit.createCommit(message)
        }

      case _ =>
        println(s"Unknown command: $command")
    }
  }
}
