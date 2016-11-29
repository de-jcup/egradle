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
 package de.jcup.egradle.eclipse.execution.validation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import de.jcup.egradle.core.api.GradleConfigurationValidator;
import de.jcup.egradle.core.api.ValidationException;
import de.jcup.egradle.core.config.GradleConfiguration;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.eclipse.api.EGradleUtil;

public class RootProjectValidationProgressRunnable implements IRunnableWithProgress {
		private GradleConfigurationValidator validator;
		private GradleConfiguration configuration;
		private OutputHandler outputHandler;
		private RootProjectValidationObserver observer;

		public RootProjectValidationProgressRunnable(GradleConfiguration configuration, RootProjectValidationObserver observer, OutputHandler outputHandler) {
			this.observer=observer;
			this.outputHandler=outputHandler;
			validator = new GradleConfigurationValidator(new SimpleProcessExecutor(outputHandler, true, 10));
			validator.setOutputHandler(outputHandler);
			this.configuration=configuration;
		}

		@Override
		public void run(IProgressMonitor monitor) {
			/* check more... */
			try {
				validator.validate(configuration);
				EGradleUtil.safeAsyncExec(new Runnable() {

					@Override
					public void run() {
						observer.handleValidationResult(true);
						outputHandler.output("\n\n\n\nOK - your gradle settings are correct and working.");

					}
				});
			} catch (ValidationException e) {
				EGradleUtil.safeAsyncExec(new Runnable() {

					@Override
					public void run() {
						observer.handleValidationResult(false);
						StringBuilder sb = new StringBuilder();
						sb.append(e.getMessage());
						String details = e.getDetails();
						if (details != null) {
							sb.append("\n");
							sb.append(details);
						}
						outputHandler.output("\n\n\n\nFAILED - " + sb.toString());

					}
				});
				
			} finally {
				EGradleUtil.safeAsyncExec(new Runnable() {

					@Override
					public void run() {
						observer.handleValidationRunning(false);
					}
				});
			}
		}

	}