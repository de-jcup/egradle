package de.jcup.egradle.eclipse.gradleeditor.outline;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.IFileEditorInput;

import de.jcup.egradle.core.parser.Token;
import de.jcup.egradle.core.parser.TokenParser;
import de.jcup.egradle.core.parser.TokenParserResult;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.api.EclipseResourceHelper;

public class GradleEditorOutlineContentProvider implements ITreeContentProvider {

	private static Object[] EMPTY = new Object[] {};

	private TokenParser parser = new TokenParser();


	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IFileEditorInput) {
//			IFileEditorInput fileEditorInput = (IFileEditorInput) inputElement;
//
//			IFile file = fileEditorInput.getFile();
//			EclipseResourceHelper resourceHelper = EGradleUtil.getResourceHelper();
//			try {
//				File normalFile = resourceHelper.toFile(file);
//				try (InputStream is = new FileInputStream(normalFile)) {
//					TokenParserResult ast = parser.parse(is, file.getCharset());
//					return ast.getRoot().getChildren().toArray();
//				} catch (IOException e) {
//					EGradleUtil.log("Was not able to load file:" + normalFile, e);
//				}
//			} catch (CoreException e) {
//				EGradleUtil.log(e);
//				return new Object[] { "Cannot convert to normal file:" + fileEditorInput.getFile() };
//			}
		} else if (inputElement instanceof IDocument) {
			IDocument document = (IDocument) inputElement;
			String[] lineDelimiters = document.getLegalLineDelimiters();
			String dataAsString = document.get();
			
			try (InputStream is = new ByteArrayInputStream(dataAsString.getBytes())) {
				TokenParserResult ast = parser.parse(is, "UTF-8");
				return ast.getRoot().getChildren().toArray();
			} catch (IOException e) {
				EGradleUtil.log("Was not able to parse string:" + dataAsString, e);
			}
		}
		return new Object[] { "no content" };
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Token) {
			Token parent = (Token) parentElement;
			return parent.getChildren().toArray();
		}
		return EMPTY;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Token) {
			Token gradleElement = (Token) element;
			return gradleElement.getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Token) {
			Token gradleElement = (Token) element;
			return gradleElement.hasChildren();
		}
		return false;
	}

}
