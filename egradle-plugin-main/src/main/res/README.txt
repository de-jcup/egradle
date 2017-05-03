This is currently only a placeholder - to prevent src/main/res missing build folder on forks.

- GIT does not add empty folders to repository inside
- Gradle needs - per default - always a src/main/res folder and does this also for "gradle eclipse"
  but we do not use this normally in eclipse PDE projects because res folders normally not in build
- To prevent eclipse complaining about this situation (no folder after fork /clone) this text file 
  was created as a simple workaround.
 