package de.jcup.egradle.core.api;

import java.util.ArrayList;
import java.util.List;

public class MultiFilter implements Filter{

	private List<Filter> filters = new ArrayList<>();
	
	@Override
	public boolean isFiltered(Object object) {
		if (object==null){
			return true;
		}
		/* use other filters */
		for (Filter filter: filters){
			if (filter.isFiltered(object)){
				return true;
			}
		}
		return false;
	}

	public void add(Filter otherFilter) {
		if (otherFilter==null){
			return;
		}
		if (filters.contains(otherFilter)){
			return;
		}
		filters.add(otherFilter);
	}

	

}
