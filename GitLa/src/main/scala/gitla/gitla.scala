package gitla

object Gitla {
  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      Messages.printMsg("No command provided!")
      return
    }

    val command = args.headOption.getOrElse("")
    val commandArgs = args.tail

    command match {
      case "init" =>
        Messages.printMsg("Running init command")
        GitlaApp.gitInit(commandArgs.headOption)
      case "add" =>
        if (commandArgs.length == 1 && commandArgs(0) == ".") {
          Add.gitAddAll()
        } else if (commandArgs.length == 1) {
          Add.gitAdd(commandArgs(0))
        } else {
          Messages.printMsg("Usage: gitla add <file-path> or gitla add .")
        }
      case "config" =>
        if (commandArgs.isEmpty) {
          ConfigParser.showHelp()
        } else {
          if (commandArgs.contains("--global")) {
            if (commandArgs.length < 2) {
              Messages.printMsg("Usage: gitla config --global <view|add|update|create>")
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
      case "rm" => 
        if (commandArgs.isEmpty){
          Messages.printMsg("Usage: gitla rm <file-path> or gitla rm --cached <file-path>")
        } else {
            if (commandArgs.length == 2 && commandArgs(0) == "--cached") {
              Remove.gitRemoveCached(commandArgs(1))
            } else if (commandArgs.length == 1) {
              Remove.gitRemove(commandArgs(0))
            } else {
              Messages.printMsg("Invalid usage. Try: gitla rm <file-path> or gitla rm --cached <file-path>")
            }
        }
      case "status" =>
        Messages.printMsg("Running status command")
        Status.gitStatus()
      case "commit" =>
        Messages.printMsg("Running commit command")
        Commit.createCommit(commandArgs(0))
      case "log" =>
        Messages.printMsg("Running log command")
        Log.displayLog()
      case "jumpto" =>
        var commitHash: String = ""
        if (commandArgs.isEmpty) {
          commitHash = Utils.getPrevCommit.getOrElse("")
        } else {
          commitHash = commandArgs(0)
        }
        if (commitHash.isEmpty) {
            Messages.raiseError("Commit hash is empty or invalid.")
        }else{
        Messages.printMsg("Running jumpto command")
        JumpTo.jumpto(commitHash)}
      case _ =>
        Messages.raiseError(s"Unknown command: $command")
    }
  }
}
