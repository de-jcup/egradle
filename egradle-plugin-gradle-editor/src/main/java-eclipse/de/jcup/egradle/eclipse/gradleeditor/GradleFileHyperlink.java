package de.jcup.egradle.eclipse.gradleeditor;

import static org.eclipse.core.runtime.Assert.*;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class GradleFileHyperlink implements IHyperlink {

	private IRegion region;
	private IFile gradleFile;
	private IFileStore fileStore;

	public GradleFileHyperlink(IRegion region, IFile gradleFile) {
		isNotNull(region, "Gradle hyperlink region may not be null!");
		isNotNull(gradleFile, "Gradle file may not be null!");
		this.region = region;
		this.gradleFile = gradleFile;
	}

	public GradleFileHyperlink(IRegion region, IFileStore fileStore) {
		isNotNull(region, "Gradle hyperlink region may not be null!");
		isNotNull(fileStore, "FileStore may not be null!");
		this.region = region;
		this.fileStore = fileStore;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return region;
	}

	@Override
	public String getTypeLabel() {
		return "gradle link";
	}

	@Override
	public String getHyperlinkText() {
		return null;
	}

	@Override
	public void open() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null) {
			return;
		}
		IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null) {
			return;
		}
		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		if (activePage == null) {
			return;
		}
		try {
			if (fileStore != null) {
				IDE.openEditorOnFileStore(activePage, fileStore);
				return;
			}
			if (gradleFile != null) {
				IDE.openEditor(activePage, gradleFile);
				return;
			}
		} catch (PartInitException e) {

		}
	}

}
