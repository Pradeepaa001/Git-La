package gitla

object Gitla {
  def main(args: Array[String]): Unit = {
    val repoName = if (args.nonEmpty) Some(args(0)) else None
    GitlaApp.gitInit(repoName)
  }
}