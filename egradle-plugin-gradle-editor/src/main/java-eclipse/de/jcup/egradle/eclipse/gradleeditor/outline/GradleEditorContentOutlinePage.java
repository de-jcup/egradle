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
package de.jcup.egradle.eclipse.gradleeditor.outline;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;

import de.jcup.egradle.eclipse.api.GroovyBasedModelType;
import de.jcup.egradle.eclipse.gradleeditor.EditorActivator;
import de.jcup.egradle.eclipse.ui.AbstractGroovyBasedContentOutlinePage;
import de.jcup.egradle.eclipse.ui.AbstractGroovyBasedEditorOutlineContentProvider;
import de.jcup.egradle.eclipse.util.EclipseDevelopmentSettings;
import de.jcup.egradle.eclipse.util.EclipseUtil;

public class GradleEditorContentOutlinePage extends AbstractGroovyBasedContentOutlinePage {

    private static ImageDescriptor IMG_DESC_LINKED = EclipseUtil.createImageDescriptor("/icons/outline/synced.png", EditorActivator.PLUGIN_ID);
    private static ImageDescriptor IMG_DESC_NOT_LINKED = EclipseUtil.createImageDescriptor("/icons/outline/sync_broken.png", EditorActivator.PLUGIN_ID);

    public GradleEditorContentOutlinePage(IAdaptable adaptable) {
        super(adaptable);
    }

    protected GradleEditorOutlineLabelProvider createStyledLabelProvider() {
        return new GradleEditorOutlineLabelProvider();
    }

    protected ImageDescriptor getImageDescriptionForLinked() {
        return IMG_DESC_LINKED;
    }

    protected ImageDescriptor getImageDescriptionNotLinked() {
        return IMG_DESC_NOT_LINKED;
    }

    protected String getPluginId() {
        return EditorActivator.PLUGIN_ID;
    }

    protected void handleDebugOptions(IMenuManager viewMenuManager) {
        if (EclipseDevelopmentSettings.DEBUG_ADD_SPECIAL_MENUS) {

            ShowGradleOutlineModelAction showGradleOutlineModelAction = new ShowGradleOutlineModelAction();
            ShowGradleOutlineUnfilteredAction showGradleOutlineUnfilteredAction = new ShowGradleOutlineUnfilteredAction();
            ShowGroovyFullAntlrModelAction showGroovyFullAntlrModelAction = new ShowGroovyFullAntlrModelAction();

            viewMenuManager.add(showGroovyFullAntlrModelAction);
            viewMenuManager.add(showGradleOutlineModelAction);
            viewMenuManager.add(showGradleOutlineUnfilteredAction);
        }
    }

    private abstract class ChangeModelTypeAction extends Action {

        protected ChangeModelTypeAction() {
            setText("Reload as:" + changeTo());
        }

        @Override
        public void run() {
            if (contentProvider instanceof AbstractGroovyBasedEditorOutlineContentProvider) {
                AbstractGroovyBasedEditorOutlineContentProvider gcp = (AbstractGroovyBasedEditorOutlineContentProvider) contentProvider;
                gcp.setModelType(changeTo());
                gcp.clearModelCache();
                getTreeViewer().refresh();
            }
        }

        protected abstract GroovyBasedModelType changeTo();
    }

    private class ShowGradleOutlineModelAction extends ChangeModelTypeAction {

        @Override
        protected GroovyBasedModelType changeTo() {
            return GradleModelTypes.GRADLE;
        }

    }

    private class ShowGradleOutlineUnfilteredAction extends ChangeModelTypeAction {

        @Override
        protected GroovyBasedModelType changeTo() {
            return GradleModelTypes.GRADLE__UNFILTERED;
        }

    }

    private class ShowGroovyFullAntlrModelAction extends ChangeModelTypeAction {

        @Override
        protected GroovyBasedModelType changeTo() {
            return GradleModelTypes.GROOVY_FULL_ANTLR;
        }

    }

    @Override
    protected String getOutlineImageRootPath() {
        return "/icons/outline/";
    }

}
