package de.jcup.egradle.core.process;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.jcup.egradle.core.process.SimpleProcessExecutor;


public class SimpleProcessExecutorTestUI {


	public static void main(String[] args) throws IOException {
		SimpleProcessExecutor executorToTest=new SimpleProcessExecutor(new SystemOutOutputHandler());
		
		File folder = new File("./../");
		System.out.println("folder:"+folder.getCanonicalPath());
		Map<String, String> env = new HashMap<>();
		env.put("JAVA_HOME","C:/dev_custom/java/jdk/jdk8u_25/jre");
		
		executorToTest.execute(folder, env, "bash", "gradlew","cleanEclipse","eclipse");
	}
}
