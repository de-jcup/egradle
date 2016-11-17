package de.jcup.egradle.core.model;

public interface ItemFilter {

	public boolean isFiltered(Item item);
	
	public static final NoItemsFiltered NO_ITEMS_FILTERED = new NoItemsFiltered();
	
	public static class NoItemsFiltered implements ItemFilter{

		private NoItemsFiltered(){
		}
		
		@Override
		public boolean isFiltered(Item item) {
			return false;
		}
		
	}
}
