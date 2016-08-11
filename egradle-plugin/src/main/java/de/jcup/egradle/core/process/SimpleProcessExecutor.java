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

import static org.apache.commons.lang3.Validate.notNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class SimpleProcessExecutor implements ProcessExecutor {

	private ProcessOutputHandler handler;

	public SimpleProcessExecutor(ProcessOutputHandler streamHandler) {
		notNull(streamHandler, "'streamHandler' may not be null");
		this.handler = streamHandler;
	}

	@Override
	public int execute(File workingDirectory, Map<String, String> env, String... commands) throws IOException {
		if (workingDirectory!=null){
			if (! workingDirectory.exists()){
				throw new FileNotFoundException("Workign directory does not exist:"+workingDirectory);
			}
		}
		ProcessBuilder pb = new ProcessBuilder(commands);
		
		/* init environment */
		if (env != null) {
			Map<String, String> pbEnv = pb.environment();
			for (String key : env.keySet()) {
				pbEnv.put(key, env.get(key));
			}
		}
		/* init working directory */
		pb.directory(workingDirectory);
		pb.redirectErrorStream(true);
		
		Process p = pb.start();
		handleProcessStarted(p);
		handleOutputStreams(p);
		while (p.isAlive()){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				throw new IOException(e);
			}
		}
		handleProcessEnd(p);
		return p.exitValue();
	}

	protected void handleOutputStreams(Process p) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null) {
			handler.output(line);
		}
	}

	protected void handleProcessEnd(Process p) {
		
	}

	protected void handleProcessStarted(Process p) {
		
	}

}
