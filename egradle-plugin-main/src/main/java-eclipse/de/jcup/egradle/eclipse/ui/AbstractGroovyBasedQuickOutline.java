package de.jcup.egradle.eclipse.ui;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.ItemTextMatcher;
import de.jcup.egradle.core.text.FilterPatternMatcher;
import de.jcup.egradle.eclipse.util.EclipseDevelopmentSettings;

public abstract class AbstractGroovyBasedQuickOutline extends AbstractFilterableTreeQuickDialog<Item> {

	protected static final int MIN_WIDTH = 400;
	protected static final int MIN_HEIGHT = 300;
	protected IExtendedEditor editor;

	public AbstractGroovyBasedQuickOutline(IAdaptable adaptable, Shell parent, String title, int minWidth,
			int minHeight, String infoText) {
		super(adaptable, parent, title, minWidth, minHeight, infoText);
	}

	@Override
	protected void openSelectionImpl(ISelection selection, String filterText) {
		if (editor == null) {
			if (EclipseDevelopmentSettings.DEBUG_ADD_SPECIAL_LOGGING) {
				System.out.println("No editor available, would select:" + selection);
			}
			return;
		}
		/*
		 * select part in editor - grab focus not necessary, because this will
		 * close quick outline dialog as well, so editor will get focus back
		 */
		editor.openSelectedTreeItemInEditor(selection, false,false);
	}
	
	@Override
	protected ITreeContentProvider createTreeContentProvider(IAdaptable adaptable) {
		return adaptable.getAdapter(ITreeContentProvider.class);
	}


	@Override
	protected Item getInitialSelectedItem() {
		if (editor == null) {
			return null;
		}
		Item item = editor.getItemAtCarretPosition();
		return item;
	}

	@Override
	protected FilterPatternMatcher<Item> createItemMatcher() {
		return new ItemTextMatcher();
	}

	@Override
	protected IBaseLabelProvider createLabelProvider() {
		IStyledLabelProvider labelProvider = createdStyledLabelProvider();
		return new DelegatingStyledCellLabelProvider(labelProvider);
	}

	protected abstract IStyledLabelProvider createdStyledLabelProvider() ;

	@Override
	protected AbstractTreeViewerFilter<Item> createFilter() {
		return new ItemTextViewerFilter();
	}

}