/*
 * Copyright 2017 Albert Tregnaghi
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
package de.jcup.egradle.eclipse.ui;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.texteditor.ITextEditor;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.util.ILogSupport;
import de.jcup.egradle.eclipse.preferences.IEditorPreferences;

public interface IExtendedEditor extends ITextEditor {
    public IDocument getDocument();

    public void openSelectedTreeItemInEditor(ISelection selection, boolean grabFocus, boolean fullSelection);

    public ILogSupport getLogSupport();

    public IEditorPreferences getPreferences();

    public Item getItemAtCarretPosition();
}
