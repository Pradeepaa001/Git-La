Gitla's Documentation
====================

.. toctree::
   :maxdepth: 2
   :caption: Contents:

   requirements
   installation
   usage
   configuration
   developer

Overview
--------
Gitla is a Git-like version control system implemented in Scala 3.x. It offers basic Git functionality such as repository initialization, file tracking, and configuration management, all with a minimal and extensible design.

Gitla aims to simulate Git's core features while providing an easy-to-understand, file-based architecture. The system focuses on performance, simplicity, and flexibility, allowing developers to extend and customize it as needed.

Core Features
-------------
- **Repository Initialization**: Supports creating new Gitla repositories with required directory structures.
- **Configuration Management**: Allows both global and local configuration for repositories.
- **File-Based Data Structures**: Mimics Git's use of files and directories to manage version control data.

Data Structures
---------------
Gitla uses several key data structures to manage repositories and configurations:

1. **File and Directory Structure**:
   - Represents the `.gitla` directory and its subdirectories, such as `objects`, `indexObject`, and `fileObject`.
   - Mimics Git's structure for repository management.

2. **Maps**:
   - Used for managing configuration data, such as storing properties like `repositoryformatversion` and `bare`.
   - `Map[String, String]` is used to handle configuration settings and can easily serialize into configuration files.

3. **Recursive Functions**:
   - For tasks like directory deletion, recursive functions ensure proper cleanup of nested files and directories within the `.gitla` folder.

4. **Streams and Strings**:
   - Used for reading from and writing to files, such as the `HEAD` file and configuration files, leveraging `PrintWriter` and `Source` from Scala’s standard library.

Requirements
------------
To run Gitla, the following software is required:

- **Scala**: Version 3.x
- **sbt**: Version 1.x
- **Java**: Version 11 or later

Installation
------------
To install and run Gitla, follow these steps:

1. **Clone the Repository**:
   Clone the Gitla repository to your local machine:
   .. code-block:: bash
      git clone https://gitlab.com/rusla/gitla.git

2. **Navigate to the Project Directory**:
   Change to the directory where you cloned Gitla:
   .. code-block:: bash
      cd gitla

3. **Run the Project**:
   Use sbt to run the project:
   .. code-block:: bash
      sbt run

Usage
-----
To initialize a new Gitla repository, use the following command:

.. code-block:: bash
   sbt run init <repo-name>

Replace `<repo-name>` with the name you wish to assign to the repository. If no repository name is provided, Gitla will use the current directory as the repository name.

Available Commands
------------------
Gitla supports several commands:

- **init**: Initializes a new Gitla repository.
  - Usage: `gitla init <repo-name>`
  - This will create the necessary directory structure and configuration files for the repository.

Configuration
-------------

Gitla supports both global and local configuration files to manage user and repository-specific settings:

- **Global Configuration**:  
   - Stored in the user's home directory (`~/.gitlaconfig`).  
   - Contains user-specific information, such as name and email, which is used across all repositories.  
   - If the global configuration file does not exist, Gitla will prompt you to create one when initializing a repository.  
   - Ensures consistency of user information across all repositories managed by Gitla.

- **Local Configuration**:  
   - Stored within the `.gitla` folder of each repository.  
   - Contains repository-specific settings, such as the repository name and user information.  
   - These settings can be updated manually or through Gitla's CLI commands.  
   - Allows each repository to have its own settings, while still using the global configuration for user details like name and email.
   
Developer Documentation
=======================
To contribute to Gitla or extend its functionality, understanding the core components of the codebase is essential. The following components are the key building blocks of Gitla:

- **GitlaApp**: Contains the main application logic for handling commands such as `init` and creating the repository structure.
- **ConfigParser**: Manages the reading, writing, and updating of configuration files.
- **Gitla**: The entry point for the application, responsible for parsing command-line arguments and invoking the appropriate actions.

Building and Running Gitla
==========================

Follow these steps to build and run Gitla locally:

1. **Clone the repository**:
   .. code-block:: bash
      git clone https://github.com/yourusername/gitla.git

2. **Compile the project**:
   .. code-block:: bash
      sbt compile

3. **Run the project**:
   .. code-block:: bash
      sbt run

For more detailed information on building, testing, and contributing, please refer to the specific module documentation.

License
-------
This project is licensed under the MIT License. See `LICENSE` for more details.
