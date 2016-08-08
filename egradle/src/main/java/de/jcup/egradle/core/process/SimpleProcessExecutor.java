package de.jcup.egradle.core.process;

import static org.apache.commons.lang3.Validate.*;

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

		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null) {
			handler.output(line);
		}
		while (p.isAlive()){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				throw new IOException(e);
			}
		}
		return p.exitValue();
	}

}
