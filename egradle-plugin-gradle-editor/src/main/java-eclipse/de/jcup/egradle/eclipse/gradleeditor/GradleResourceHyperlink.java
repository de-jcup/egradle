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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import de.jcup.egradle.eclipse.api.EGradleUtil;

public class GradleResourceHyperlink implements IHyperlink {

	private IRegion region;
	private String resourceName;

	public GradleResourceHyperlink(IRegion region, String resourceName) {
		isNotNull(region, "Gradle hyperlink region may not be null!");
		isNotNull(resourceName, "resourceName may not be null!");
		this.region = region;
		this.resourceName = resourceName;
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
		
		Shell shell = EGradleUtil.getActiveWorkbenchShell();
		if (shell==null){
			return;
		}
		IWorkspaceRoot container = ResourcesPlugin.getWorkspace().getRoot();
		OpenGradleResourceDialog ogd = new OpenGradleResourceDialog(shell, container, IFile.FILE);
		ogd.setInitialPattern(resourceName);
		final int resultCode = ogd.open();
		
		if (resultCode != Window.OK) {
			return;
		}
		Object[] result = ogd.getResult();
		List<IFile> files = new ArrayList<>();
		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				if (result[i] instanceof IFile) {
					files.add((IFile) result[i]);
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
		}

	}

}
