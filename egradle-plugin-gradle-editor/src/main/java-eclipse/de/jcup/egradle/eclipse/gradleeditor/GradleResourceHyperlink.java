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

import static org.eclipse.core.runtime.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.ide.IDE;

import de.jcup.egradle.core.text.JavaImportFinder;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.gradleeditor.jdt.JDTDataAccess;

public class GradleResourceHyperlink implements IHyperlink {

	private IRegion region;
	private String resourceName;
	
	private String fullText;

	public GradleResourceHyperlink(IRegion region, String resourceName, String fullText) {
		isNotNull(region, "Gradle hyperlink region may not be null!");
		isNotNull(resourceName, "resourceName may not be null!");
		this.region = region;
		this.resourceName = resourceName;
		
		this.fullText=fullText;
	}


	@Override
	public IRegion getHyperlinkRegion() {
		return region;
	}

	@Override
	public String getTypeLabel() {
		return "open resource link";
	}

	@Override
	public String getHyperlinkText() {
		return "Opens "+resourceName+" by resource dialog";
	}

	@Override
	public void open() {
		String[] packageNames= fetchImportedPackages(fullText);
		
		Shell shell = EGradleUtil.getActiveWorkbenchShell();
		if (shell==null){
			return;
		}
		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		List<String> typesFound = JDTDataAccess.SHARED.scanForJavaType(resourceName, scope, packageNames);
		SelectionDialog dialog = null;
		if (! typesFound.isEmpty()){
			int style=IJavaElementSearchConstants.CONSIDER_ALL_TYPES;
			try {
				String found = typesFound.get(0);
				dialog = JavaUI.createTypeDialog(EGradleUtil.getActiveWorkbenchShell(), EGradleUtil.getActiveWorkbenchWindow(), scope, style, false,found);
				dialog.setTitle("Potential Java types found:");
			} catch (JavaModelException e) {
				EGradleUtil.log("Cannot create java type dialog", e);
			}
		}else{
			IWorkspaceRoot container = ResourcesPlugin.getWorkspace().getRoot();
			OpenGradleResourceDialog opengradleResourceDialog = new OpenGradleResourceDialog(shell, container, IFile.FILE);
			opengradleResourceDialog.setInitialPattern(resourceName);
			dialog = opengradleResourceDialog;
		}
		
		final int resultCode = dialog.open();
		
		if (resultCode != Window.OK) {
			return;
		}
		Object[] result = dialog.getResult();
		List<IFile> files = new ArrayList<>();
		List<IJavaElement> javaElements = new ArrayList<>();
		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				Object object = result[i];
				if (object instanceof IFile) {
					files.add((IFile) object);
				}else if (object instanceof IJavaElement){
					IJavaElement javaElement = (IJavaElement) object;
					javaElements.add(javaElement);
				}
			}
		}
		
		if (files.size() > 0) {

			final IWorkbenchPage page = EGradleUtil.getActivePage();
			if (page==null){
				return;
			}
			IFile currentFile = null;
			try {
				for (Iterator<IFile> it = files.iterator(); it.hasNext();) {
					currentFile= it.next();
					IDE.openEditor(page, currentFile, true);
				}
			} catch (final PartInitException e) {
				EGradleUtil.log("Cannot open file:"+currentFile,e);
			}
		}else if (javaElements.size()>0){
			IJavaElement javaElement = javaElements.get(0);
			try {
				JavaUI.openInEditor(javaElement);
			} catch (PartInitException | JavaModelException e) {
				EGradleUtil.log("Cannot open java editor with:"+javaElement,e);
			}
		}

	}
	
	private String[] fetchImportedPackages(String fullText) {
		JavaImportFinder javaImportFinder = new JavaImportFinder();
		Set<String> set = javaImportFinder.findImportedPackages(fullText);
		return set.toArray(new String[set.size()]);
	}

}
