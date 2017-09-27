package de.jcup.egradle.eclipse.ui;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.texteditor.ITextEditor;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.util.ILogSupport;
import de.jcup.egradle.eclipse.preferences.IEditorPreferences;

public interface IExtendedEditor extends ITextEditor{
	public IDocument getDocument();

	public void openSelectedTreeItemInEditor(ISelection selection, boolean grabFocus, boolean fullSelection);

	public ILogSupport getLogSupport();

	public IEditorPreferences getPreferences();

	public Item getItemAtCarretPosition();
}
