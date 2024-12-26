Gitla's Documentation
===================

.. toctree::
   :maxdepth: 2
   :caption: Contents:

   requirements
   installation
   usage
   configuration
   developer
   functions
   advanced_features
   data_structures

Gitla is a Git-like version control system implemented in Scala 3.x. It replicates essential Git features with simplicity and extensibility in mind, allowing developers to customize and extend functionality. The focus of Gitla is to provide:

- **Lightweight Version Control**: A streamlined system for managing file changes.
- **Customization-Friendly Design**: Modularity to facilitate extensions.
- **Learning-Oriented Features**: A practical implementation of Git-like concepts.

----------

Core Features
--------------

1. **Repository Initialization**
   - Creates a `.gitla` directory with the necessary structure to manage objects, configurations, and indexes.

2. **Configuration Management**
   - Supports both global (`~/.gitlaconfig`) and local (`.gitla/config`) configurations to manage settings.

3. **Staging Area**
   - Maintains an index file to track files staged for commits.

4. **File Blob Management**
   - Efficiently stores file data using content-based SHA-1 hashes and compression.

5. **File Tracking and Removal**
   - Provides commands for adding files to the staging area and removing them from tracking.

6. **Status Tracking**
   - Displays the repository's status, including tracked, modified, and untracked files.

----------

Advanced Features
-----------------

1. **SHA-1 Hashing**
   - Gitla stores file content as blobs identified by SHA-1 hashes, ensuring efficient deduplication and content integrity.

2. **Compression**
   - Files are compressed before being stored, optimizing disk usage while retaining functionality.

3. **Error Handling**
   - Detailed error messages and edge-case handling ensure robustness.

4. **Modular Design**
   - Gitla's architecture is scalable, allowing easy addition of features like commit history or branching.

----------

Data Structures
---------------

1. **File and Directory Structure**
   - `.gitla/` includes:
     - **objects/**: Stores file blobs.
     - **indexObject/**: Maintains the staging area.
     - **config/**: Holds configuration files.

2. **Maps for Configuration and Indexing**
   - Configuration files use `Map[String, String]`, serialized into TOML format.
   - Index entries use `Map[String, (String, String)]`, representing `filePath -> (hash, status)`.

3. **Recursive Utilities**
   - Handles nested files and directories for operations like staging all files.

----------

Function Documentation
----------------------

1. **Add.scala**

   - **`gitAdd(filePath: String)`**  
     Adds a file to the staging area. Updates the blob if modified or removes it if missing.

   - **`gitAddAll()`**  
     Stages all files in the current directory except `.gitla`.

2. **Blob.scala**

   - **`calculateHash(filePath: String): String`**  
     Computes the SHA-1 hash for file deduplication.

   - **`createBlob(hash: String, fileContent: Array[Byte])`**  
     Compresses and saves file blobs in `.gitla/objects`.

3. **Init.scala**

   - **`initRepository(repoName: Option[String])`**  
     Initializes a new repository, setting up the `.gitla` structure and default configurations.

4. **Config.scala**

   - **`readConfig(path: String): Map[String, String]`**  
     Reads a configuration file into a Scala map.

   - **`writeConfig(path: String, data: Map[String, String])`**  
     Writes configuration data from a map into TOML format.

----------

Developer Notes
---------------

Extending Gitla
- Familiarize yourself with Scala’s file I/O and library functions.
- Follow Gitla’s modular structure for easier implementation of new features.
- Write and run unit tests for all contributions using `sbt test`.

----------

Building and Running Gitla
--------------------------

1. **Step 1: Clone the Repository**
   - Run the following command to clone the repository:

     ```
     git clone https://gitlab.com/rusla/gitla.git
     ```

2. **Step 2: Compile the Project**
   - To compile the project, run:

     ```
     sbt compile
     ```

3. **Step 3: Run the Project**
   - To run the project, execute:

     ```
     sbt run
     ```

For detailed instructions, refer to specific sections in this documentation.

----------

License
-------

This project is licensed under the MIT License. See `LICENSE` for more details.
