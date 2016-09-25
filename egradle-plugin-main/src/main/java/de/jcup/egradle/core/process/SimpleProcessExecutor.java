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

import static org.apache.commons.lang3.Validate.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class SimpleProcessExecutor implements ProcessExecutor {

	protected OutputHandler handler;
	private boolean handleProcessOutputStream;
	
	/**
	 * Simple process executor implementation
	 * @param outputHandler handle process information output
	 * @param handleProcessOutputStream when true process output stream will be fetched and handled by given {@link OutputHandler} too
	 */
	public SimpleProcessExecutor(OutputHandler outputHandler, boolean handleProcessOutputStream) {
		notNull(outputHandler, "'streamHandler' may not be null");
		this.handler = outputHandler;
		this.handleProcessOutputStream=handleProcessOutputStream;
	}

	@Override
	public int execute(WorkingDirectoryProvider wdProvider, EnvironmentProvider envProvider, String... commands) throws IOException {
		notNull(wdProvider, "'wdProvider' may not be null");
		notNull(envProvider, "'envProvider' may not be null");
		String wd = wdProvider.getWorkingDirectory();
		/* Working directory*/
		File workingDirectory = null;
		if (StringUtils.isNotBlank(wd)){
			workingDirectory=new File(wd);
		}
		if (workingDirectory != null) {
			if (!workingDirectory.exists()) {
				throw new FileNotFoundException("Working directory does not exist:" + workingDirectory);
			}
		}
		/* Create process with dedicated environment*/
		ProcessBuilder pb = new ProcessBuilder(commands);
		Map<String, String> env = envProvider.getEnvironment();
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

		Date started = new Date();
		Process p = pb.start();
		handleProcessStarted(envProvider, p, started, workingDirectory, commands);
		handleOutputStreams(p);
		
		/* wait for execution */
		while (p.isAlive()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				throw new IOException(e);
			}
		}
		/* done */
		int exitValue = p.exitValue();
		handleProcessEnd(p);
		return exitValue;
	}

	protected void handleOutputStreams(Process p) throws IOException {
		if (!handleProcessOutputStream){
			return;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null) {
			handler.output(line);
		}
	}

	/**
	 * Handle process end - process can have failed (result != 0...)
	 * @param p process
	 */
	protected void handleProcessEnd(Process p) {
		/* per default nothing special to do*/
	}


	protected void handleProcessStarted(EnvironmentProvider context, Process p, Date started, File workingDirectory,
			String[] commands) {

	}

}
