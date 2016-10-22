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
package de.jcup.egradle.eclipse.handlers;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;

import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.core.process.scraping.ValidationOutputHandler;
import de.jcup.egradle.core.process.scraping.ValidationOutputHandler.ValidationResult;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.api.FileHelper;
import de.jcup.egradle.eclipse.execution.GradleExecutionDelegate;
import de.jcup.egradle.eclipse.execution.GradleExecutionException;
import de.jcup.egradle.eclipse.ui.MarkerHelper;

public class ValidateGradleScriptHandler extends AbstractEGradleCommandHandler  {


	private MarkerHelper markerHelper = new MarkerHelper();
	
	@Override
	public void prepare(GradleContext context) {
		context.setAmountOfWorkToDo(2);
		context.setCommands(GradleCommand.build("tasks"));
	}

	@Override
	protected GradleExecutionDelegate createGradleExecution(OutputHandler outputHandler)
			throws GradleExecutionException {
		ValidationOutputHandler problemOutputHandler = new ValidationOutputHandler();
		GradleExecutionDelegate ui = new GradleExecutionDelegate(outputHandler,
				new SimpleProcessExecutor(problemOutputHandler, true, 10), this) {
			protected void afterExecutionDone(org.eclipse.core.runtime.IProgressMonitor monitor) throws Exception {
				ValidationResult result = problemOutputHandler.getResult();
				if (result.hasScriptEvaluationProblem()){
//					File folder = EGradleUtil.getRootProjectFolder();
					/* FIXME ATR, 22.10.2016 - implemenent file calculation etc. better, this is not fail safe..*/
					String scriptPath = result.getScriptPath();
					String rootFolderPath = EGradleUtil.getRootProjectFolder().getAbsolutePath();
					if (scriptPath.startsWith(rootFolderPath)){
//						scriptPath=scriptPath.substring(rootFolderPath.length());
					}
					File file = new File(scriptPath);
					if (!file.exists()){
						return;
					}
					IResource resource = null;
					
					if (true){
						/* FIXME ATR, just a workaround , because links currently not working*/
					 resource=ResourcesPlugin.getWorkspace().getRoot();
					}else{
						resource = FileHelper.SHARED.toIFile(file);
					}
					if (resource==null){
						return;
					}
					markerHelper.createErrorMarker(resource,result.getErrorMessage(), result.getLine());
				}
			};
		};
		return ui;
	}

	

}
