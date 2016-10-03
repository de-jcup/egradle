package de.jcup.egradle.eclipse;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import de.jcup.egradle.eclipse.api.EGradleUtil;

public class JavaHelper {
	
	public static final JavaHelper SHARED = new JavaHelper();

	/**
	 * Returns currently selected method or <code>null</code>
	 * @param editor
	 * @return method or <code>null</code>
	 * 
	 */
	public IMethod getCurrentSelectedJavaMethod(ITextEditor editor) {
		if (editor==null){
			return null;
		}
		
		IEditorInput editorInput = editor.getEditorInput();
		if (editorInput==null){
			return null;
		}
		IJavaElement elem = JavaUI.getEditorInputJavaElement(editorInput);
		if (elem instanceof ICompilationUnit) {
		    ITextSelection sel = (ITextSelection) editor.getSelectionProvider().getSelection();
		    IJavaElement selected;
			try {
				selected = ((ICompilationUnit) elem).getElementAt(sel.getOffset());
			} catch (JavaModelException e) {
				EGradleUtil.log(e);
				return null;
			}
			if (selected==null){
				return null;
			}
		    if (selected.getElementType() == IJavaElement.METHOD) {
		         return (IMethod) selected;
		    }
		}
		return null;
	}
}

