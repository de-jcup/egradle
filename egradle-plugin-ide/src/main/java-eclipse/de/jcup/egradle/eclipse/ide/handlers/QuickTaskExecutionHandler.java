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
package de.jcup.egradle.eclipse.ide.handlers;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.domain.GradleRootProject;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.core.util.History;
import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.ide.execution.GradleExecutionDelegate;
import de.jcup.egradle.eclipse.ide.execution.GradleExecutionException;
import de.jcup.egradle.eclipse.ide.execution.UIGradleExecutionDelegate;
import de.jcup.egradle.eclipse.ui.QuickLaunchDialog;

public class QuickTaskExecutionHandler extends AbstractEGradleCommandHandler {

    private String lastInput;

    private History<String> history;

    public QuickTaskExecutionHandler() {
        history = new History<>(20);

        /* add defaults last added will be first to select.. */
        history.add("asciidoctor");
        history.add("test");
        history.add("build");
        history.add("tasks");

    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        lastInput = null;
        Shell shell = HandlerUtil.getActiveShellChecked(event);
        GradleRootProject rootProject = IDEUtil.getRootProject();
        if (rootProject == null) {
            return null;
        }
        QuickLaunchDialog dialog = new QuickLaunchDialog(shell, history, " (" + rootProject.getName() + ")");
        dialog.open();
        lastInput = dialog.getValue();
        if (StringUtils.isBlank(lastInput)) {
            return null;
        }
        return super.execute(event);
    }

    @Override
    public void prepare(GradleContext context) {
        context.setAmountOfWorkToDo(2);
        context.setCommands(GradleCommand.build(lastInput));
    }

    @Override
    protected GradleExecutionDelegate createGradleExecution(OutputHandler outputHandler) throws GradleExecutionException {
        UIGradleExecutionDelegate ui = new UIGradleExecutionDelegate(outputHandler, new SimpleProcessExecutor(outputHandler, true, SimpleProcessExecutor.ENDLESS_RUNNING), this);
        ui.setRefreshProjects(false);
        ui.setShowEGradleSystemConsole(true);
        return ui;
    }
}
