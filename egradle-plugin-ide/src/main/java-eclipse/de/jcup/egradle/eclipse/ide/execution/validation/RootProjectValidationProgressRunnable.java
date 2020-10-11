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
package de.jcup.egradle.eclipse.ide.execution.validation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import de.jcup.egradle.core.config.GradleConfiguration;
import de.jcup.egradle.core.domain.CancelStateProvider;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.ProcessExecutor;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.core.util.GradleConfigurationValidator;
import de.jcup.egradle.core.util.ValidationException;
import de.jcup.egradle.eclipse.util.EclipseUtil;

public class RootProjectValidationProgressRunnable implements IRunnableWithProgress {
    private GradleConfigurationValidator validator;
    private GradleConfiguration configuration;
    private OutputHandler outputHandler;
    private RootProjectValidationObserver observer;

    public RootProjectValidationProgressRunnable(CancelStateProvider cancelStateProvider, GradleConfiguration configuration, RootProjectValidationObserver observer, OutputHandler outputHandler) {
        this.observer = observer;
        this.outputHandler = outputHandler;
        // is now canceable so endless running okay
        validator = new GradleConfigurationValidator(new SimpleProcessExecutor(outputHandler, true, ProcessExecutor.ENDLESS_RUNNING));
        validator.setOutputHandler(outputHandler);
        validator.setCancelStateProvider(cancelStateProvider);
        this.configuration = configuration;
    }

    @Override
    public void run(IProgressMonitor monitor) {
        monitor.beginTask("Start validation", IProgressMonitor.UNKNOWN);
        /* check more... */
        try {
            validator.validate(configuration);
            EclipseUtil.safeAsyncExec(new Runnable() {

                @Override
                public void run() {
                    observer.handleValidationResult(true);
                    outputHandler.output("\n\n\n\nOK - your gradle settings are correct and working.");

                }
            });
        } catch (ValidationException e) {
            EclipseUtil.safeAsyncExec(new Runnable() {

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
            EclipseUtil.safeAsyncExec(new Runnable() {

                @Override
                public void run() {
                    monitor.done();
                    observer.handleValidationRunning(false);
                }
            });
        }
    }

}