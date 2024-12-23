gitla config --global view           # View the global config
gitla config --local view            # View the local config
gitla config --global add <section>  # Add a section to the global config
gitla config --local add <section>   # Add a section to the local config
gitla config --global update <sec> <key> <value>  # Update a section in the global config
gitla config --local update <sec> <key> <value>   # Update a section in the local config
gitla config --global create         # Create a new global config
gitla config add <section>           # Default behavior (local config, add section)
gitla config update <sec> <key> <value>  # Default behavior (local config, update section)
