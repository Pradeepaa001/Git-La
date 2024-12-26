pages = [{"l":"index.html#","e":false,"i":"","n":"Gitla","t":"Gitla","d":"","k":"static","x":""},
{"l":"gitla.html#","e":false,"i":"","n":"gitla","t":"gitla","d":"","k":"package","x":""},
{"l":"gitla/Add$.html#","e":false,"i":"","n":"Add","t":"Add","d":"gitla","k":"object","x":""},
{"l":"gitla/Add$.html#gitAdd-571","e":false,"i":"","n":"gitAdd","t":"gitAdd(filePath: String): Unit","d":"gitla.Add","k":"def","x":""},
{"l":"gitla/Add$.html#gitAddAll-94c","e":false,"i":"","n":"gitAddAll","t":"gitAddAll(): Unit","d":"gitla.Add","k":"def","x":""},
{"l":"gitla/Blob$.html#","e":false,"i":"","n":"Blob","t":"Blob","d":"gitla","k":"object","x":""},
{"l":"gitla/Blob$.html#calculateCommitHash-fffff2ca","e":false,"i":"","n":"calculateCommitHash","t":"calculateCommitHash(content: String): String","d":"gitla.Blob","k":"def","x":""},
{"l":"gitla/Blob$.html#calculateHash-fffff2ca","e":false,"i":"","n":"calculateHash","t":"calculateHash(filePath: String): String","d":"gitla.Blob","k":"def","x":""},
{"l":"gitla/Blob$.html#compress-fffff564","e":false,"i":"","n":"compress","t":"compress(data: Array[Byte]): Array[Byte]","d":"gitla.Blob","k":"def","x":""},
{"l":"gitla/Blob$.html#createBlob-5c8","e":false,"i":"","n":"createBlob","t":"createBlob(hash: String, fileContent: Array[Byte], destination: String): Unit","d":"gitla.Blob","k":"def","x":""},
{"l":"gitla/Commit$.html#","e":false,"i":"","n":"Commit","t":"Commit","d":"gitla","k":"object","x":""},
{"l":"gitla/Commit$.html#createCommit-571","e":false,"i":"","n":"createCommit","t":"createCommit(message: String): Unit","d":"gitla.Commit","k":"def","x":""},
{"l":"gitla/ConfigParser$.html#","e":false,"i":"","n":"ConfigParser","t":"ConfigParser","d":"gitla","k":"object","x":""},
{"l":"gitla/ConfigParser$.html#addSection-d09","e":false,"i":"","n":"addSection","t":"addSection(file: File, sectionName: String): Unit","d":"gitla.ConfigParser","k":"def","x":""},
{"l":"gitla/ConfigParser$.html#createGlobalConfig-fffffec0","e":false,"i":"","n":"createGlobalConfig","t":"createGlobalConfig(file: File): Map[String, String]","d":"gitla.ConfigParser","k":"def","x":""},
{"l":"gitla/ConfigParser$.html#getGlobalConfig-fffff328","e":false,"i":"","n":"getGlobalConfig","t":"getGlobalConfig(): Map[String, String]","d":"gitla.ConfigParser","k":"def","x":""},
{"l":"gitla/ConfigParser$.html#handleGlobalConfig-fffff5f8","e":false,"i":"","n":"handleGlobalConfig","t":"handleGlobalConfig(command: String, args: Array[String]): Unit","d":"gitla.ConfigParser","k":"def","x":""},
{"l":"gitla/ConfigParser$.html#handleLocalConfig-8dd","e":false,"i":"","n":"handleLocalConfig","t":"handleLocalConfig(repoDir: String, command: String, args: Array[String]): Unit","d":"gitla.ConfigParser","k":"def","x":""},
{"l":"gitla/ConfigParser$.html#showHelp-94c","e":false,"i":"","n":"showHelp","t":"showHelp(): Unit","d":"gitla.ConfigParser","k":"def","x":""},
{"l":"gitla/ConfigParser$.html#updateConfig-fffff796","e":false,"i":"","n":"updateConfig","t":"updateConfig(repoName: String, repoDir: String): Unit","d":"gitla.ConfigParser","k":"def","x":""},
{"l":"gitla/ConfigParser$.html#updateSection-753","e":false,"i":"","n":"updateSection","t":"updateSection(file: File, sectionName: String, key: String, value: String): Unit","d":"gitla.ConfigParser","k":"def","x":""},
{"l":"gitla/ConfigParser$.html#viewConfig-fffff0e4","e":false,"i":"","n":"viewConfig","t":"viewConfig(file: File): Unit","d":"gitla.ConfigParser","k":"def","x":""},
{"l":"gitla/Gitla$.html#","e":false,"i":"","n":"Gitla","t":"Gitla","d":"gitla","k":"object","x":""},
{"l":"gitla/Gitla$.html#main-913","e":false,"i":"","n":"main","t":"main(args: Array[String]): Unit","d":"gitla.Gitla","k":"def","x":""},
{"l":"gitla/GitlaApp$.html#","e":false,"i":"","n":"GitlaApp","t":"GitlaApp","d":"gitla","k":"object","x":""},
{"l":"gitla/GitlaApp$.html#createDir-571","e":false,"i":"","n":"createDir","t":"createDir(dirPath: String): Unit","d":"gitla.GitlaApp","k":"def","x":""},
{"l":"gitla/GitlaApp$.html#createFile-571","e":false,"i":"","n":"createFile","t":"createFile(filePath: String): Unit","d":"gitla.GitlaApp","k":"def","x":""},
{"l":"gitla/GitlaApp$.html#createRepoStructure-d89","e":false,"i":"","n":"createRepoStructure","t":"createRepoStructure(repoDir: String, gitlaDir: File): Unit","d":"gitla.GitlaApp","k":"def","x":""},
{"l":"gitla/GitlaApp$.html#gitInit-fffff749","e":false,"i":"","n":"gitInit","t":"gitInit(repoName: Option[String]): Unit","d":"gitla.GitlaApp","k":"def","x":""},
{"l":"gitla/Head$.html#","e":false,"i":"","n":"Head","t":"Head","d":"gitla","k":"object","x":""},
{"l":"gitla/Head$.html#getPrevHash-0","e":false,"i":"","n":"getPrevHash","t":"getPrevHash: Option[String]","d":"gitla.Head","k":"def","x":""},
{"l":"gitla/Head$.html#updateCurrHash-571","e":false,"i":"","n":"updateCurrHash","t":"updateCurrHash(hash: String): Unit","d":"gitla.Head","k":"def","x":""},
{"l":"gitla/Index$.html#","e":false,"i":"","n":"Index","t":"Index","d":"gitla","k":"object","x":""},
{"l":"gitla/Index$.html#isFileInIndex-1b3","e":false,"i":"","n":"isFileInIndex","t":"isFileInIndex(filePath: String, indexEntries: Map[String, (String, String)]): Boolean","d":"gitla.Index","k":"def","x":""},
{"l":"gitla/Index$.html#readIndex-fffff328","e":false,"i":"","n":"readIndex","t":"readIndex(): Map[String, (String, String)]","d":"gitla.Index","k":"def","x":""},
{"l":"gitla/Index$.html#removeFromIndex-571","e":false,"i":"","n":"removeFromIndex","t":"removeFromIndex(filePath: String): Unit","d":"gitla.Index","k":"def","x":""},
{"l":"gitla/Index$.html#updateIndex-fbb","e":false,"i":"","n":"updateIndex","t":"updateIndex(filePath: String, hash: String, state: String): Unit","d":"gitla.Index","k":"def","x":""},
{"l":"gitla/Index$.html#writeIndex-fffffa74","e":false,"i":"","n":"writeIndex","t":"writeIndex(entries: Map[String, (String, String)]): Unit","d":"gitla.Index","k":"def","x":""},
{"l":"gitla/Log$.html#","e":false,"i":"","n":"Log","t":"Log","d":"gitla","k":"object","x":""},
{"l":"gitla/Log$.html#displayLog-94c","e":false,"i":"","n":"displayLog","t":"displayLog(): Unit","d":"gitla.Log","k":"def","x":""},
{"l":"gitla/Log$.html#readCommit-fffffd22","e":false,"i":"","n":"readCommit","t":"readCommit(commitHash: String): Option[(String, String, String, String)]","d":"gitla.Log","k":"def","x":""},
{"l":"gitla/Remove$.html#","e":false,"i":"","n":"Remove","t":"Remove","d":"gitla","k":"object","x":""},
{"l":"gitla/Remove$.html#gitRemove-571","e":false,"i":"","n":"gitRemove","t":"gitRemove(filePath: String): Unit","d":"gitla.Remove","k":"def","x":""},
{"l":"gitla/Remove$.html#gitRemoveCached-571","e":false,"i":"","n":"gitRemoveCached","t":"gitRemoveCached(filePath: String): Unit","d":"gitla.Remove","k":"def","x":""},
{"l":"gitla/Restore$.html#","e":false,"i":"","n":"Restore","t":"Restore","d":"gitla","k":"object","x":""},
{"l":"gitla/Restore$.html#restore-571","e":false,"i":"","n":"restore","t":"restore(commitHash: String): Unit","d":"gitla.Restore","k":"def","x":""},
{"l":"gitla/Status$.html#","e":false,"i":"","n":"Status","t":"Status","d":"gitla","k":"object","x":""},
{"l":"gitla/Status$.html#gitStatus-94c","e":false,"i":"","n":"gitStatus","t":"gitStatus(): Unit","d":"gitla.Status","k":"def","x":""}];