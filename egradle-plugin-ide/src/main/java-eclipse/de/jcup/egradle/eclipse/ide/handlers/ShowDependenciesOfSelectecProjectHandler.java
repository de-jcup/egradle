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

import static de.jcup.egradle.eclipse.ide.IDEUtil.*;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.ide.execution.GradleExecutionDelegate;
import de.jcup.egradle.eclipse.ide.execution.GradleExecutionException;
import de.jcup.egradle.eclipse.ide.execution.UIGradleExecutionDelegate;
import de.jcup.egradle.eclipse.ide.ui.SelectedProjectFinder;
import de.jcup.egradle.eclipse.ui.SelectConfigurationDialog;
import de.jcup.egradle.eclipse.util.EclipseUtil;

public class ShowDependenciesOfSelectecProjectHandler extends AbstractEGradleCommandHandler {

    private IProject projectToUse;
    private String configuration;

    private SelectedProjectFinder projectFinder = new SelectedProjectFinder();

    public Object execute(ExecutionEvent event) throws ExecutionException {
        Shell activeWorkbenchShell = EclipseUtil.getActiveWorkbenchShell();
        if (activeWorkbenchShell == null) {
            return null;
        }
        IProject project = projectFinder.findSelectedProject();
        if (project == null) {
            return null;
        }
        SelectConfigurationDialog dialog = new SelectConfigurationDialog(activeWorkbenchShell);
        dialog.setTitleImage(IDEUtil.getImage("icons/gradle-og.png"));
        dialog.setText("Show dependencies");
        dialog.setInput(configuration);
        String config = dialog.open();
        if (config == null) {
            /* cancel ... */
            return null;
        }
        configuration = config;
        projectToUse = project;
        return super.execute(event);
    }

    @Override
    public void prepare(GradleContext context) {
        if (projectToUse == null) {
            return;
        }
        context.setAmountOfWorkToDo(2);
        StringBuilder sb = new StringBuilder();
        if (!hasVirtualRootProjectNature(projectToUse) && !isRootProject(projectToUse)) {
            sb.append(":");
            sb.append(projectToUse.getName()); /*
                                                * TODO ATR, 02.03.2017: check if getName() is correct here - should be
                                                * foldername..
                                                */
            sb.append(":");
        }
        sb.append("dependencies");
        if (configuration != null && configuration.length() > 0) {
            sb.append(" --configuration ");
            sb.append(configuration);
        }
        context.setCommands(GradleCommand.build(sb.toString()));
    }

    @Override
    protected GradleExecutionDelegate createGradleExecution(OutputHandler outputHandler) throws GradleExecutionException {
        UIGradleExecutionDelegate ui = new UIGradleExecutionDelegate(outputHandler, new SimpleProcessExecutor(outputHandler, true, SimpleProcessExecutor.ENDLESS_RUNNING), this);
        ui.setRefreshProjects(false);
        ui.setShowEGradleSystemConsole(true);
        return ui;
    }

}
