package de.jcup.egradle.eclipse.gradleeditor.outline;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.IFileEditorInput;

import de.jcup.egradle.core.parser.GradleAST;
import de.jcup.egradle.core.parser.AbstractGradleToken;
import de.jcup.egradle.core.parser.SimpleGradleTokenParser;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.api.EclipseResourceHelper;

public class GradleEditorOutlineContentProvider implements ITreeContentProvider {

	private static Object[] EMPTY = new Object[] {};

	private SimpleGradleTokenParser parser = new SimpleGradleTokenParser();

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IFileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput) inputElement;

			IFile file = fileEditorInput.getFile();
			EclipseResourceHelper resourceHelper = EGradleUtil.getResourceHelper();
			try {
				File normalFile = resourceHelper.toFile(file);
				try (InputStream is = new FileInputStream(normalFile)) {
					GradleAST ast = parser.parse(is);
					return ast.getRootElements().toArray();
				} catch (IOException e) {
					EGradleUtil.log(e);
				}
			} catch (CoreException e) {
				EGradleUtil.log(e);
				return new Object[] { "Cannot convert to normal file:" + fileEditorInput.getFile() };
			}
		}
		return new Object[] { "no content" };
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof AbstractGradleToken){
			AbstractGradleToken parent = (AbstractGradleToken) parentElement;
			return parent.getElements().toArray();
		}
		return EMPTY;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof AbstractGradleToken){
			AbstractGradleToken gradleElement = (AbstractGradleToken) element;
			return gradleElement.getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof AbstractGradleToken){
			AbstractGradleToken gradleElement = (AbstractGradleToken) element;
			return gradleElement.hasChildren();
		}
		return false;
	}

}
