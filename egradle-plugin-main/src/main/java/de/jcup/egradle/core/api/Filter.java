package de.jcup.egradle.core.api;

public interface Filter {

	public boolean isFiltered(Object object);
	
	public static final NothingFiltered NOTHING_FILTERED = new NothingFiltered();
	
	public static class NothingFiltered implements Filter{

		private NothingFiltered(){
		}
		
		@Override
		public boolean isFiltered(Object object) {
			return false;
		}
		
	}
}
