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
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import de.jcup.egradle.core.Constants;
import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.RememberLastLinesOutputHandler;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.core.validation.GradleOutputValidator;
import de.jcup.egradle.core.validation.ValidationResult;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.execution.GradleExecutionDelegate;
import de.jcup.egradle.eclipse.execution.GradleExecutionException;
import de.jcup.egradle.eclipse.ui.UnpersistedMarkerHelper;

public class ValidateGradleScriptHandler extends AbstractEGradleCommandHandler  {


	private static final int LINES_NEEDED_FOR_VALIDATION = 20;
	private static UnpersistedMarkerHelper buildProblemMarkerHelper = new UnpersistedMarkerHelper("de.jcup.egradle.script.problem");
	
	@Override
	public void prepare(GradleContext context) {
		context.setAmountOfWorkToDo(2);
		context.setCommands(GradleCommand.build("tasks"));
	}

	@Override
	protected GradleExecutionDelegate createGradleExecution(OutputHandler outputHandler)
			throws GradleExecutionException {
		try {
			buildProblemMarkerHelper.removeAllErrorMarkers();
		} catch (CoreException e) {
			EGradleUtil.log(e);
		}
		RememberLastLinesOutputHandler validationOutputHandler = new RememberLastLinesOutputHandler(LINES_NEEDED_FOR_VALIDATION);
		validationOutputHandler.setChainedOutputHandler(outputHandler);
		/* no errors, so erase if there were former ...*/
		GradleExecutionDelegate ui = new GradleExecutionDelegate(outputHandler,
				new SimpleProcessExecutor(validationOutputHandler, true, 10), this) {
			protected void afterExecutionDone(org.eclipse.core.runtime.IProgressMonitor monitor) throws Exception {
				/* we do always remove all buildscript problem markers - so we got only new ones remaining!*/
				
				List<String> list = validationOutputHandler.createOutputToValidate();
				GradleOutputValidator validator = new GradleOutputValidator();
				
				ValidationResult result = validator.validate(list);
				
				if (result.hasProblem()){
					IResource resource = null;
					
					String scriptPath = result.getScriptPath();
					String rootFolderPath = EGradleUtil.getRootProjectFolder().getAbsolutePath();
					File file = new File(scriptPath);
					if (!file.exists()){
						resource=ResourcesPlugin.getWorkspace().getRoot();
						buildProblemMarkerHelper.createErrorMarker(resource,"Build file which prodocues error does not exist:"+file.getAbsolutePath(), 0);
						return;
					}
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					resource = workspace.getRoot().getFileForLocation(Path.fromOSString(scriptPath));
					if (resource==null){
						if (scriptPath.startsWith(rootFolderPath)){
							scriptPath=scriptPath.substring(rootFolderPath.length());
						}
						IProject virtualRootProject = workspace.getRoot().getProject(Constants.VIRTUAL_ROOTPROJECT_NAME);
						if (virtualRootProject.exists()){
							resource=virtualRootProject.getFile(scriptPath);
						}
					}
					
					if (resource==null){
						// fall back to workspace root - so at least we can create an error marker...
						resource=ResourcesPlugin.getWorkspace().getRoot();
					}
					buildProblemMarkerHelper.createErrorMarker(resource,result.getErrorMessage(), result.getLine());
				}
			};
		};
		return ui;
	}

	

}
