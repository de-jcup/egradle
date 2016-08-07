package de.jcup.egradle.core.process;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface ProcessExecutor {
	
	public static final Integer PROCESS_RESULT_OK = Integer.valueOf(0);
	
	/**
	 * Execute commands in given working directory
	 * @param workingDirectory
	 * @param env
	 * @param commands
	 * @return
	 * @throws IOException
	 */
	public int execute(File workingDirectory, Map<String, String> env, String ...commands) throws IOException;
}