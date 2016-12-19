package de.jcup.egradle.eclipse.gradleeditor.outline;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.ItemFilter;
import de.jcup.egradle.core.model.ItemType;

class GradleOutlineItemFilter implements ItemFilter {

	@Override
	public boolean isFiltered(Item item) {
		/*
		 * we do not show item which are remaining as METHOD_CALL will not
		 * be shown in outline
		 */
		if (ItemType.METHOD_CALL == item.getItemType()) {
			if (item.hasChildren()){
				return false;
			}
			/* no children... so only a normal call and be filtered*/
			return true;
		}
		return false;
	}

}