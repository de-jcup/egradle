EGradle main plugin
===================

contains core /common parts of egradle:
- src/main/java        : core parts, no dependencies to eclipse
- src/main/java-groovy : groovy antlr parts: https://github.com/apache/groovy commit : bf3f467 2016-11-17
- src/main/java-eclipse: eclipse parts

- src/test/java-eclipse: contains testcases only executable with "Run as ... junit test "in eclipse IDE, but not by gradle build 