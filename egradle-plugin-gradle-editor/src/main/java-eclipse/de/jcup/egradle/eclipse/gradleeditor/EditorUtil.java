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
package de.jcup.egradle.eclipse.gradleeditor;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.jcup.egradle.core.util.ILogSupport;
import de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorPreferences;
import de.jcup.egradle.eclipse.preferences.AbstractEditorPreferences;

public class EditorUtil implements ILogSupport {

    public static final ILogSupport INSTANCE = new EditorUtil();

    public static AbstractEditorPreferences getPreferences() {
        return GradleEditorPreferences.getInstance();
    }

    @Override
    public void logInfo(String info) {
        getLog().log(new Status(IStatus.INFO, EditorActivator.PLUGIN_ID, info));
    }

    @Override
    public void logWarning(String warning) {
        getLog().log(new Status(IStatus.WARNING, EditorActivator.PLUGIN_ID, warning));
    }

    @Override
    public void logError(String error, Throwable t) {
        getLog().log(new Status(IStatus.ERROR, EditorActivator.PLUGIN_ID, error, t));
    }

    protected ILog getLog() {
        ILog log = EditorActivator.getDefault().getLog();
        return log;
    }
}
