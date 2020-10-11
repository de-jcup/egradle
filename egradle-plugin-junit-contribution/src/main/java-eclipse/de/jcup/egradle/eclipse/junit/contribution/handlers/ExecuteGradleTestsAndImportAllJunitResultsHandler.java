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
package de.jcup.egradle.eclipse.junit.contribution.handlers;

import static de.jcup.egradle.eclipse.junit.contribution.preferences.EGradleJUnitPreferences.*;

import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.eclipse.ide.execution.GradleExecutionDelegate;
import de.jcup.egradle.eclipse.ide.execution.GradleExecutionException;
import de.jcup.egradle.eclipse.ide.execution.UIGradleExecutionDelegate;
import de.jcup.egradle.eclipse.ide.handlers.AbstractEGradleCommandHandler;
import de.jcup.egradle.eclipse.junit.contribution.ImportGradleJunitResultsJob;

public class ExecuteGradleTestsAndImportAllJunitResultsHandler extends AbstractEGradleCommandHandler {

    public static final String COMMAND_ID = "de.jcup.egradle.eclipse.ide.junit.contribution.commands.executeAllTestsAndimportTestResultCommand";

    @Override
    public void prepare(GradleContext context) {
        String tasksToExecute = JUNIT_PREFERENCES.getDefaultTestTaskType().getTestTasks();
        context.setCommands(GradleCommand.build(tasksToExecute));
    }

    @Override
    protected GradleExecutionDelegate createGradleExecution(OutputHandler outputHandler) throws GradleExecutionException {
        /* execute tests */
        UIGradleExecutionDelegate ui = new UIGradleExecutionDelegate(outputHandler, new SimpleProcessExecutor(outputHandler, true, SimpleProcessExecutor.ENDLESS_RUNNING), this);
        ui.setRefreshProjects(false);
        ui.setShowEGradleSystemConsole(true);
        /* when tests were executed do the import */
        ui.setAfterExecution(() -> {
            ImportGradleJunitResultsJob job = new ImportGradleJunitResultsJob("Import gradle junit results", null, false);
            job.schedule();
        });
        return ui;
    }

}
