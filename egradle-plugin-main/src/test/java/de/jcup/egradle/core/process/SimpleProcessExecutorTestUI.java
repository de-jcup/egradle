/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.egradle.core.process;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SimpleProcessExecutorTestUI {

	public static void main(String[] args) throws IOException {
		SimpleProcessExecutor executorToTest = new SimpleProcessExecutor(new SystemOutOutputHandler());

		File folder = new File("./../");
		System.out.println("folder:" + folder.getCanonicalPath());
		Map<String, String> env = new HashMap<>();
		env.put("JAVA_HOME", "C:/dev_custom/java/jdk/jdk8u_25/jre");

		executorToTest.execute(folder, env, "bash", "gradlew", "cleanEclipse", "eclipse");
	}
}
