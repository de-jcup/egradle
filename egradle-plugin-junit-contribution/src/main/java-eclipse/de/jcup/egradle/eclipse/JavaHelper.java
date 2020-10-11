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
package de.jcup.egradle.eclipse;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import de.jcup.egradle.eclipse.junit.contribution.JunitUtil;

public class JavaHelper {

    public static final JavaHelper SHARED = new JavaHelper();

    /**
     * Returns currently selected method or <code>null</code>
     * 
     * @param editor
     * @return method or <code>null</code>
     * 
     */
    public IMethod getCurrentSelectedJavaMethod(ITextEditor editor) {
        if (editor == null) {
            return null;
        }

        IEditorInput editorInput = editor.getEditorInput();
        if (editorInput == null) {
            return null;
        }
        IJavaElement elem = JavaUI.getEditorInputJavaElement(editorInput);
        if (elem instanceof ICompilationUnit) {
            ITextSelection sel = (ITextSelection) editor.getSelectionProvider().getSelection();
            IJavaElement selected;
            try {
                selected = ((ICompilationUnit) elem).getElementAt(sel.getOffset());
            } catch (JavaModelException e) {
                JunitUtil.logError("Was not able to get element at selection", e);
                return null;
            }
            if (selected == null) {
                return null;
            }
            if (selected.getElementType() == IJavaElement.METHOD) {
                return (IMethod) selected;
            }
        }
        return null;
    }
}
