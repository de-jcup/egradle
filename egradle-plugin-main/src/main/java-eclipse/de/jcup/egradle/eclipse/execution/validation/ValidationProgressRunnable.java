package de.jcup.egradle.eclipse.execution.validation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import de.jcup.egradle.core.api.GradleConfigurationValidator;
import de.jcup.egradle.core.api.ValidationException;
import de.jcup.egradle.core.config.GradleConfiguration;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.eclipse.api.EGradleUtil;

public class ValidationProgressRunnable /* extends Job */ implements IRunnableWithProgress {
		private GradleConfigurationValidator validator;
		private GradleConfiguration configuration;
		private OutputHandler outputHandler;
		private ValidationObserver observer;

		public ValidationProgressRunnable(GradleConfiguration configuration, ValidationObserver observer, OutputHandler outputHandler) {
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