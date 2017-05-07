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

import static de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorPreferenceConstants.*;
import static de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorPreferences.*;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.eclipse.api.EclipseDevelopmentSettings;
import de.jcup.egradle.eclipse.api.EclipseUtil;
import de.jcup.egradle.eclipse.gradleeditor.EditorActivator;
import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;
import de.jcup.egradle.eclipse.gradleeditor.outline.GradleEditorOutlineContentProvider.ModelType;
import de.jcup.egradle.eclipse.ui.FallbackOutlineContentProvider;
public class GradleEditorContentOutlinePage extends ContentOutlinePage implements IDoubleClickListener {
	
	private static ImageDescriptor IMG_DESC_LINKED = EclipseUtil.createImageDescriptor("/icons/outline/synced.png",EditorActivator.PLUGIN_ID);
	private static ImageDescriptor IMG_DESC_NOT_LINKED = EclipseUtil.createImageDescriptor("/icons/outline/sync_broken.png",EditorActivator.PLUGIN_ID);
	
	private ITreeContentProvider contentProvider;
	
	private GradleEditor gradleEditor;
	private boolean ignoreNextSelectionEvents;
	private GradleEditorOutlineLabelProvider labelProvider;
	private boolean linkingWithEditorEnabled;
	private ToggleLinkingAction toggleLinkingAction;
	private Object input;

	public GradleEditorContentOutlinePage(IAdaptable adaptable) {
		if (adaptable==null){
			contentProvider = new FallbackOutlineContentProvider();
			return;
		}
		this.gradleEditor = adaptable.getAdapter(GradleEditor.class);
		this.contentProvider = adaptable.getAdapter(ITreeContentProvider.class);
		if (contentProvider == null) {
			contentProvider = new FallbackOutlineContentProvider();
		}
	}
	public void createControl(Composite parent) {
		super.createControl(parent);

		labelProvider = new GradleEditorOutlineLabelProvider();

		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(contentProvider);
		viewer.addDoubleClickListener(this);
		viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(labelProvider));
		viewer.addSelectionChangedListener(this);
		
		/* it can happen that input is already updated before control created*/
		if (input!=null){
			viewer.setInput(input);
		}
		BlockSelectionAction blockSelectionAction = new BlockSelectionAction();
		CollapseAllAction collapseAllAction = new CollapseAllAction();
		ExpandAllAction expandAllAction = new ExpandAllAction();
		toggleLinkingAction = new ToggleLinkingAction();
		toggleLinkingAction.setActionDefinitionId(IWorkbenchCommandConstants.NAVIGATE_TOGGLE_LINK_WITH_EDITOR);
		IActionBars actionBars = getSite().getActionBars();

		IToolBarManager toolBarManager = actionBars.getToolBarManager();
		toolBarManager.add(expandAllAction);
		toolBarManager.add(collapseAllAction);
		toolBarManager.add(toggleLinkingAction);
		toolBarManager.add(new Separator("selectionGroup1"));//$NON-NLS-1$
		toolBarManager.add(blockSelectionAction);

		IMenuManager viewMenuManager = actionBars.getMenuManager();
		viewMenuManager.add(new Separator("EndFilterGroup")); //$NON-NLS-1$
		
		if (EclipseDevelopmentSettings.DEBUG_ADD_SPECIAL_MENUS){

			ShowGradleOutlineModelAction showGradleOutlineModelAction = new ShowGradleOutlineModelAction();
			ShowGradleOutlineUnfilteredAction showGradleOutlineUnfilteredAction = new ShowGradleOutlineUnfilteredAction();
			ShowGroovyFullAntlrModelAction showGroovyFullAntlrModelAction = new ShowGroovyFullAntlrModelAction();
			
			viewMenuManager.add(showGroovyFullAntlrModelAction);
			viewMenuManager.add(showGradleOutlineModelAction);
			viewMenuManager.add(showGradleOutlineUnfilteredAction);
		}
		viewMenuManager.add(new Separator("treeGroup")); //$NON-NLS-1$
		viewMenuManager.add(expandAllAction);
		viewMenuManager.add(collapseAllAction);
		viewMenuManager.add(toggleLinkingAction);
		viewMenuManager.add(new Separator("selectionGroup2"));//$NON-NLS-1$
		viewMenuManager.add(blockSelectionAction);
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		if (gradleEditor==null){
			return;
		}
		if (linkingWithEditorEnabled){
			gradleEditor.setFocus();
			// selection itself is already handled by single click
			return; 
		}
		ISelection selection = event.getSelection();
		gradleEditor.openSelectedTreeItemInEditor(selection,true);
	}

	public void ignoreNextSelectionEvents(boolean ignore) {
	
	}

	public void onEditorCaretMoved(int caretOffset) {
		if (!linkingWithEditorEnabled) {
			return;
		}
		ignoreNextSelectionEvents = true;
		if (contentProvider instanceof GradleEditorOutlineContentProvider){
			GradleEditorOutlineContentProvider gcp = (GradleEditorOutlineContentProvider) contentProvider;
			Item item = gcp.tryToFindByOffset(caretOffset);
			if (item != null) {
				StructuredSelection selection = new StructuredSelection(item);
				getTreeViewer().setSelection(selection,true);
			}
		}
		ignoreNextSelectionEvents = false;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		super.selectionChanged(event);
		if (!linkingWithEditorEnabled) {
			return;
		}
	
		if (ignoreNextSelectionEvents) {
			return;
		}
		ISelection selection = event.getSelection();
		gradleEditor.openSelectedTreeItemInEditor(selection,false);
	}

	public void inputChanged(Object input){
		this.input=input;
		if (contentProvider instanceof GradleEditorOutlineContentProvider){
			((GradleEditorOutlineContentProvider)contentProvider).clearModelCache();
		}
		TreeViewer treeViewer = getTreeViewer();
		if (treeViewer==null){
			return;
		}
		treeViewer.setInput(input);
	}
	

	private abstract class ChangeModelTypeAction extends Action {

		protected ChangeModelTypeAction() {
			setText("Reload as:" + changeTo());
		}

		@Override
		public void run() {
			if (contentProvider instanceof GradleEditorOutlineContentProvider){
				GradleEditorOutlineContentProvider gcp = (GradleEditorOutlineContentProvider) contentProvider;
				gcp.setModelType(changeTo());
				gcp.clearModelCache();
				getTreeViewer().refresh();
			}
		}

		protected abstract ModelType changeTo();
	}

	private class CollapseAllAction extends Action {

		private CollapseAllAction() {
			setImageDescriptor(
					EclipseUtil.createImageDescriptor("/icons/outline/collapseall.png", EditorActivator.PLUGIN_ID));
			setText("Collapse all");
		}

		@Override
		public void run() {
			getTreeViewer().collapseAll();
		}
	}

	

	private class ExpandAllAction extends Action {

		private ExpandAllAction() {
			setImageDescriptor(EclipseUtil.createImageDescriptor("/icons/outline/expandall.png", EditorActivator.PLUGIN_ID));
			setText("Expand all");
		}

		@Override
		public void run() {
			getTreeViewer().expandAll();
		}
	}
	
	private class BlockSelectionAction extends Action {

		private BlockSelectionAction() {
			setImageDescriptor(EclipseUtil.createImageDescriptor("/icons/outline/mark_occurrences.png", EditorActivator.PLUGIN_ID));
			setText("Mark selected item full");
		}

		@Override
		public void run() {
			if (gradleEditor==null){
				return;
			}
			TreeViewer treeViewer= getTreeViewer();
			if (treeViewer==null){
				return;
			}
			gradleEditor.openSelectedTreeItemInEditor(treeViewer.getSelection(), true, true);
		}

		
	}

	private class ToggleLinkingAction extends Action {
		
	
		private ToggleLinkingAction() {
			linkingWithEditorEnabled = EDITOR_PREFERENCES.getBooleanPreference(P_LINK_OUTLINE_WITH_EDITOR);
			setDescription("link with editor");
			initImage();
			initText();
		}
	
		@Override
		public void run() {
			linkingWithEditorEnabled = !linkingWithEditorEnabled;
			
			initText();
			initImage();
		}
	
		private void initImage() {
			setImageDescriptor(linkingWithEditorEnabled ? IMG_DESC_LINKED : IMG_DESC_NOT_LINKED);
		}
	
		private void initText() {
			setText(linkingWithEditorEnabled ? "Click to unlink from editor" : "Click to link with editor");
		}
	
	}

	private class ShowGroovyFullAntlrModelAction extends ChangeModelTypeAction {

		@Override
		protected ModelType changeTo() {
			return ModelType.GROOVY_FULL_ANTLR;
		}

	}
	
	private class ShowGradleOutlineModelAction extends ChangeModelTypeAction {

		@Override
		protected ModelType changeTo() {
			return ModelType.GRADLE;
		}

	}
	

	private class ShowGradleOutlineUnfilteredAction extends ChangeModelTypeAction {

		@Override
		protected ModelType changeTo() {
			return ModelType.GRADLE__UNFILTERED;
		}

	}

}
