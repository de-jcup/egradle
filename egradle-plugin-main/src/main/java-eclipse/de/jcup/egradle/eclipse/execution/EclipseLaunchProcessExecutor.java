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
package de.jcup.egradle.eclipse.execution;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

import de.jcup.egradle.core.api.BuildInfo;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.process.EnvironmentProvider;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.core.process.WorkingDirectoryProvider;
import de.jcup.egradle.eclipse.api.EGradlePostBuildJob;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.launch.EGradleRuntimeProcess;

public class EclipseLaunchProcessExecutor extends SimpleProcessExecutor {
	private ILaunch launch;
	private EGradlePostBuildJob postJob;
	private String cmdLine;

	public EclipseLaunchProcessExecutor(OutputHandler streamHandler, ILaunch launch, EGradlePostBuildJob postJob) {
		super(streamHandler,false,SimpleProcessExecutor.ENDLESS_RUNNING); // not output handled - done by launch mechanism in console!
		this.launch = launch;
		this.postJob=postJob;
	}

	@Override
	public int execute(WorkingDirectoryProvider wdProvider, EnvironmentProvider envprovider, String... commands) throws IOException {
		try{
			return super.execute(wdProvider, envprovider, commands);
		}catch(IOException | RuntimeException e){
				EGradleUtil.log(e);
				/* problem occured - we have to cleanup launch otherwise launches will be kept in UI and not removeable!*/
				if (!launch.isTerminated()){
					try {
						if (launch.canTerminate()){
							launch.terminate();
						}
					} catch (DebugException de) {
						EGradleUtil.log(de);
					}
				}
				throw e;
		}
	}

	@Override
	protected void handleProcessStarted(EnvironmentProvider provider, Process process, Date started, File workingDirectory,
			String[] commands) {
		String label = "<none>";
		if (provider instanceof GradleContext){
			label = ((GradleContext)provider).getCommandString();
		}
		String path = "inside root project";

		Map<String, String> attributes = new HashMap<>();
		String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(started);
		/*
		 * Will be shown in process information dialog - see
		 * org.eclipse.debug.internal.ui.preferences. ProcessPropertyPage
		 */
		StringBuilder sb = new StringBuilder();
		for (String key : provider.getEnvironment().keySet()) {
			String value = provider.getEnvironment().get(key);
			sb.append(key);
			sb.append('=');
			sb.append(value);
			sb.append(System.getProperty("line.separator"));
		}

		attributes.put(DebugPlugin.ATTR_ENVIRONMENT, sb.toString());
		attributes.put(DebugPlugin.ATTR_CONSOLE_ENCODING, "UTF-8");
		attributes.put(DebugPlugin.ATTR_WORKING_DIRECTORY, workingDirectory.getAbsolutePath());
		attributes.put(DebugPlugin.ATTR_LAUNCH_TIMESTAMP, timestamp);
		attributes.put(DebugPlugin.ATTR_PATH, path);

		/*
		 * using an unbreakable space 00A0 to avoid unnecessary breaks in view
		 */
		cmdLine = StringUtils.join(Arrays.asList(commands), '\u00A0');

		attributes.put(IProcess.ATTR_CMDLINE, cmdLine);
		/*
		 * bind process to runtime process, so visible and correct handled in
		 * debug UI
		 */
		EGradleRuntimeProcess rp = new EGradleRuntimeProcess(launch, process, label, attributes);
		// rp.getStreamsProxy().getOutputStreamMonitor().addListener(rp);

		handler.output("Launch started - for details see output of " + label);
		if (!rp.canTerminate()) {
			handler.output("Started process cannot terminate");
		}
	}
	
	@Override
	protected void handleProcessEnd(Process p) {
		if (postJob!=null){
			postJob.setBuildInfo(new BuildInfo(cmdLine, p.exitValue()));
			postJob.schedule();
		}
	}

}
