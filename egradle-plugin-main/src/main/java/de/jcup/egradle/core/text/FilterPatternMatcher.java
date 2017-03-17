package de.jcup.egradle.core.text;

import de.jcup.egradle.core.api.Matcher;

public interface FilterPatternMatcher<T> extends Matcher<T>{

	void setFilterText(String filterText);

	boolean hasFilterPattern();

}
