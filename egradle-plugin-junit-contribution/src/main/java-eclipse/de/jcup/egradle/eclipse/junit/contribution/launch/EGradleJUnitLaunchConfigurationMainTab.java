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
package de.jcup.egradle.eclipse.junit.contribution.launch;

import static de.jcup.egradle.eclipse.ide.launch.EGradleLauncherConstants.*;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import de.jcup.egradle.eclipse.ide.launch.EGradleLaunchConfigurationMainTab;
import de.jcup.egradle.eclipse.junit.contribution.JUnitActivator;
import de.jcup.egradle.eclipse.util.EclipseUtil;
import de.jcup.egradle.junit.EGradleJUnitTaskVariableReplacement;

public class EGradleJUnitLaunchConfigurationMainTab extends EGradleLaunchConfigurationMainTab {

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        super.setDefaults(configuration);
        configuration.setAttribute(PROPERTY_TASKS, EGradleJUnitTaskVariableReplacement.TASKS_VARIABLE);
    }

    @Override
    protected TaskUIPartsDelegate createTaskUIPartsDelegate() {
        return new IgnoreTaskUIPartsDelegate();
    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
    }

    @Override
    public Image getImage() {
        return EclipseUtil.getImage("icons/gradle-og-junit.gif", JUnitActivator.PLUGIN_ID);
    }

    @Override
    public String getName() {
        return "Gradle JUnit";
    }

    protected class IgnoreTaskUIPartsDelegate extends TaskUIPartsDelegate {

        @Override
        protected void setTaskFieldText(ILaunchConfiguration configuration) throws CoreException {

        }

        @Override
        protected void addTaskComponents(Composite composite, GridData labelGridData, GridData gridDataTwoLines) {

        }

        @Override
        protected void applyTasks(ILaunchConfigurationWorkingCopy configuration) {

        }

    }

}
